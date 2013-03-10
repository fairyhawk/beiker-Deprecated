package com.beike.core.service.trx.coupon.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.bean.trx.TrxResponseData;
import com.beike.common.bean.trx.VmAccountParamInfo;
import com.beike.common.entity.coupon.CouponActivity;
import com.beike.common.entity.coupon.TrxCoupon;
import com.beike.common.entity.vm.VmAccount;
import com.beike.common.enums.trx.ActHistoryType;
import com.beike.common.enums.trx.BizType;
import com.beike.common.enums.trx.ReqChannel;
import com.beike.common.enums.trx.TrxCouponStatus;
import com.beike.common.enums.vm.VmAccountType;
import com.beike.common.exception.AccountException;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.CouponException;
import com.beike.common.exception.DiscountCouponException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.common.exception.VmAccountException;
import com.beike.core.service.trx.coupon.TrxCouponService;
import com.beike.core.service.trx.vm.VmAccountService;
import com.beike.dao.trx.coupon.CouponActivityDao;
import com.beike.dao.trx.coupon.TrxCouponDao;
import com.beike.dao.trx.soa.proxy.GoodsSoaDao;
import com.beike.util.Amount;
import com.beike.util.DateUtils;
import com.beike.util.Des3Encryption;
import com.beike.util.FileUtils;
import com.beike.util.PropertyUtil;
import com.beike.util.StringUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;

/**   
 * @title: TrxCouponServiceImpl.java
 * @package com.beike.core.service.trx.coupon
 * @description: 优惠券service实现
 * @author wangweijie  
 * @date 2012-10-30 下午02:53:37
 * @version v1.0   
 */
@Service("trxCouponService")
public class TrxCouponServiceImpl implements TrxCouponService {
	private final Log logger = LogFactory.getLog(TrxCouponServiceImpl.class);
	private static final int COUPON_TIMEOUT = 60*60; 
	private final MemCacheService memCacheService = MemCacheServiceImpl.getInstance();
	private static PropertyUtil propertyUtil = PropertyUtil.getInstance("project");
	private static String couponDesKeyFilePath = propertyUtil.getProperty("coupon_triple_des_key_file"); 
	private static String couponDesKey = "";
	@Autowired
	private TrxCouponDao trxCouponDao;
	
	@Autowired
	private CouponActivityDao couponActivityDao;
	@Autowired 
	private GoodsSoaDao goodsSoaDao;
	@Autowired
	private VmAccountService vmAccountService;

	private static final String SPLIT_CHAR = ";";	  //优惠券统一分割符
	private static final String MIAOSHA_TAGID = "100"; //秒杀对应的tagid
	
	/**
	 * 根据ID查询优惠券
	 * @param id
	 * @return
	 */
	public TrxCoupon queryCouponById(Long id){
		return trxCouponDao.queryTrxCouponById(id);
	}
	
	/**
	 * 根据userId 查询所有的优惠券数量
	 * @param userId
	 * @return
	 */
	public int queryCountAllTrxCouponsByUserId(Long userId){
		return trxCouponDao.queryCountAllTrxCouponsByUserId(userId);
	}
	
	/**
	 * 根据ID查询优惠券
	 * @param id
	 * @return
	 */
	@Override
	public List<TrxCoupon> queryAllTrxCouponsByUserId(Long userId,int startRow,int pageSize) {
		List<TrxCoupon> couponList = trxCouponDao.queryAllTrxCouponsByUserIdForPage(userId,startRow,pageSize);
		if(null == couponList || couponList.size()==0){
			return null;
		}
		
		Map<Long,Map<String,Object>> activityMap = new HashMap<Long,Map<String,Object>>();
		for(TrxCoupon coupon : couponList){
			Long activityId = coupon.getActivityId();
			Map<String,Object> activityCachedMap = activityMap.get(activityId);
			if(null==activityCachedMap || activityCachedMap.size()==0){
				activityCachedMap = getCouponActivityMemeryCached(activityId);
				activityMap.put(activityId,activityCachedMap);
			}
			
			String limitInfo = (String)activityCachedMap.get("ACTIVITY_LIMIT_INFO");
			String activityName = (String)activityCachedMap.get("ACTIVITY_NAME");
			coupon.setCouponName(activityName);
			coupon.setLimitInfo(limitInfo);
		}
		return couponList;
	}
	
	/**
	 * 根据ID、userId查询优惠券
	 * @param id
	 * @return
	 */
	public TrxCoupon queryTrxCouponByIdAndUserId(Long id,Long userId){
		return trxCouponDao.queryTrxCouponByIdAndUserId(id, userId);
	}
	
	/**
	 * 查写库根据ID、userId查询优惠券
	 * @param id
	 * @return
	 */
	public TrxCoupon preQryInWtDBTrxCouponByIdAndUserId(Long id,Long userId){
		return trxCouponDao.queryTrxCouponByIdAndUserId(id, userId);
	}
	
	/**
	 * 查询未入账的优惠券
	 * @return
	 */
	public List<TrxCoupon> queryNoCreditActCoupon(){
		return trxCouponDao.queryNoCreditActCoupon();
	}

	/**
	 * 根据userId 查询所有的优惠券
	 * @param userId
	 * @return
	 */
	@Override
	public TrxCoupon preQryInWtDBTrxCouponById(Long id) {
		return trxCouponDao.queryTrxCouponById(id);
	}

	/**
	 * 根据userId和优惠券状态查询优惠券
	 */
	@Override
	public List<TrxCoupon> queryTrxCouponsByUserId(Long userId) {
		return trxCouponDao.queryTrxCouponsByUserId(userId, TrxCouponStatus.INIT);
	}

	
	/**
	 * 优惠券下发
	 */
	public void dispatchCoupon(TrxCoupon coupon)throws CouponException,StaleObjectStateException{
		//已经在sql加入是binding状态的判断
		//		if(coupon.getCouponStatus()!=TrxCouponStatus.BINDING){
		//			throw new CouponException(BaseException.COUPON_NOT_AVAILABLE);
		//		}
		
		String requestId = "C_REQID_" + coupon.getCouponNo();

		
		VmAccountParamInfo vmActParam = new VmAccountParamInfo();
		vmActParam.setVmAccountId(String.valueOf(coupon.getVmAccountId())); // 虚拟款项ID
		vmActParam.setUserId(String.valueOf(coupon.getUserId()));// 用户Id
		vmActParam.setRequestId(requestId);	// 下发请求号
		vmActParam.setAmount(String.valueOf(coupon.getCouponBalance()));// 下发金额
		vmActParam.setOperatorId("0");// 操作人Id
		vmActParam.setActHistoryType(ActHistoryType.VMDIS);// 下发类型
		vmActParam.setBizType(BizType.COUPON_3);		//优惠券
		vmActParam.setDescription("优惠券实时充值");
		boolean disSuccess = true;
		try{
			//使用同步下发
			disSuccess = vmAccountService.dispatchVmForSync(vmActParam);
		}catch (Exception e) {
			logger.error("+++++++coupon topon error++couponId=+"+coupon.getId()+"+++",e);
			throw new CouponException(BaseException.COUPON_TOPON_ERROR);
		}
		if(disSuccess){
			coupon.setIsCreditAct(1);		//设置优惠券已经入账
		}else{
			//优惠券入账异常，优惠券状态更新为未入账，将会执行异步入账操作
			logger.info("+++++++coupon topon StaleObjectStateException++couponId="+coupon.getId()+"+++");
			coupon.setIsCreditAct(0);		//设置优惠券已经入账
		}
		//coupon.setCouponStatus(TrxCouponStatus.USED);	//设置优惠券状态为已使用
		coupon.setRequestId(requestId);
		trxCouponDao.updateTrxCouponForSale(coupon.getId(), coupon.getIsCreditAct(), requestId,"", coupon.getVersion());
	}
	
	
	/**
	 * 返回页面展示需要的优惠券信息
	 * @param userId
	 * @param goodsIds
	 * @param miaoshaIds
	 * @param payAmount
	 * @return
	 */
	public Map<String,List<TrxCoupon>> queryTrxCouponByUserIdForShow(Long userId,String[] goodsIds,String[] miaoshaIds,double payAmount,boolean isUseCoupon){
		List<TrxCoupon> couponList = trxCouponDao.queryTrxCouponsByUserId(userId, TrxCouponStatus.BINDING);
		if(null == couponList || couponList.size()==0){
			return null;
		}
		Map<String,List<TrxCoupon>> couponMap = new HashMap<String,List<TrxCoupon>>(2);
		List<TrxCoupon> noLimitCouponList = new ArrayList<TrxCoupon>();
		List<TrxCoupon> limitCouponList = new ArrayList<TrxCoupon>();
		
		Map<Long,Map<String,Object>> activityMap = new HashMap<Long,Map<String,Object>>();
		for(TrxCoupon coupon : couponList){
			Long activityId = coupon.getActivityId();
			Map<String,Object> activityCachedMap = activityMap.get(activityId);
			if(null==activityCachedMap || activityCachedMap.size()==0){
				activityCachedMap = getCouponActivityMemeryCached(activityId);
				activityMap.put(activityId,activityCachedMap);
			}
			//判断优惠券是否受限
			int limitCode = -1;
			if(isUseCoupon){
				limitCode = isAvailableCoupon(goodsIds,miaoshaIds,coupon,payAmount);
			}
			coupon.setCouponName((String)activityCachedMap.get("ACTIVITY_NAME"));
			coupon.setLimitInfo((String)activityCachedMap.get("ACTIVITY_LIMIT_INFO"));
			coupon.setLimitCode(limitCode);
			if(1==limitCode){
				noLimitCouponList.add(coupon);
			}else{
				limitCouponList.add(coupon);
			}
		}
		couponMap.put("NOT_LIMIT_COUPONLIST", noLimitCouponList);
		couponMap.put("LIMIT_COUPONLIST", limitCouponList);
		return couponMap;
	}
	
	/**
	 * 优惠券是否有效
	 *  0	      表示有效
	 *	2105      优惠券不存在
	 *	2106      优惠券金额限制
	 *	2107      优惠券品类限制
	 *	2108      优惠券日期限制
	 *	2109      优惠券不可用
	 * @return
	 */
	@Override
	public int isAvailableCoupon(String[] goodsIds,String[] miaoshaIds,TrxCoupon trxCoupon,double orderAmount){
		if(null == trxCoupon){
			return BaseException.COUPON_NOT_AVAILABLE;
		}
		//状态限制
		if(trxCoupon.getCouponStatus()!=TrxCouponStatus.BINDING){
			logger.info("++++++++++couponIsAvailable+++trxcoupon="+trxCoupon.getId()+"+++++COUPON_NOT_STATUS_AVAILABLE+++++");
			return BaseException.COUPON_NOT_AVAILABLE;
		}
		logger.info("++++++++++couponIsAvailable+++trxcoupon="+trxCoupon.getId()+"+++++orderAmount="+orderAmount+"+++++");
		
		//查询优惠券所属活动对应的
		Long activityId = trxCoupon.getActivityId();
		
		//从memerychached中取出活动属性
		Map<String,Object> activityMap = getCouponActivityMemeryCached(activityId);
		/*
		 * 判断金额限制
		 */
		Double limitAmount = (Double)activityMap.get("LIMIT_AMOUNT");		//限制金额
		
		if(!Amount.compare(orderAmount, limitAmount)){
			logger.info("++++++++++++trxcoupon="+trxCoupon.getId()+"+++++COUPON_AMOUNT_LIMIT++++++");
			return BaseException.COUPON_AMOUNT_LIMIT; //金额限制
		}
		
		//判断优惠券有效期
		Long currentTime = System.currentTimeMillis();
		Long startDate = (Long)activityMap.get("START_DATE");				//开始时间
		Long endDate = (Long)activityMap.get("END_DATE");					//结束时间
		if(currentTime<startDate||currentTime>endDate){
			logger.info("++++++++++++trxcoupon="+trxCoupon.getId()+"+++++COUPON_DATE_LIMIT++++++currentTime="+currentTime+"++++");
			return BaseException.COUPON_DATE_LIMIT;   //日期限制
		}
		/*
		 * 判断商品的限制
		 * 1、秒杀商品、普通商品必须存在一个
		 * 2、如果存在秒杀商品，判断是否支持秒杀商品
		 * 3、如果存在普通商品，判断普通商品所属的品类是否在优惠券在规定中
		 */
		if((null==miaoshaIds||miaoshaIds.length==0) && (null==goodsIds||goodsIds.length==0)){
			logger.info("++++++++++++trxcoupon="+trxCoupon.getId()+"+++++miaoshaIds="+miaoshaIds+"goodsIds="+goodsIds+"++++");
			return BaseException.COUPON_TAGID_LIMIT;  //秒杀限制
		}
		
		//如果存在秒杀商品则判断秒杀限制
		String[] limitTagid = (String[])activityMap.get("LIMIT_TAGID");
		if(miaoshaIds!=null && miaoshaIds.length>0){
			//秒杀限制字段为100
			if(StringUtils.bisearch(limitTagid,MIAOSHA_TAGID)<0){
				logger.info("++++++++++++trxcoupon="+trxCoupon.getId()+"+++++COUPON_TAGID_MIAOSHA_LIMIT++++");
				return BaseException.COUPON_TAGID_LIMIT;  //秒杀限制
			}
		}
		
		boolean isLimitTagid = true;
		//商品品类限制
		if(goodsIds!=null && goodsIds.length>0){
			for(String goodsId : goodsIds){
				//memerycached 获取数据
				String[] tagids = (String[]) memCacheService.get("GOODS_TAGIDS_"+goodsId);
				if(null==tagids){
					List<Map<String, Object>> catlogList = goodsSoaDao.getCatalogGoods(Long.parseLong(goodsId));
					if(null != catlogList && catlogList.size()>0){
						tagids = new String[catlogList.size()];
						for(int i=0;i<catlogList.size();i++){
							tagids[i] = String.valueOf(catlogList.get(i).get("tagid"));
						}
					}else{
						tagids = new String[]{""};
					}
					memCacheService.set("GOODS_TAGIDS_"+goodsId, tagids,COUPON_TIMEOUT);	// 有效期1小时
				}
				
				//判断商品是否存在限制
				boolean goodsOk = false;
				for(String tagid : tagids){
					if(StringUtils.bisearch(limitTagid,tagid)>=0){
						logger.info("++++++++++++trxcoupon="+trxCoupon.getId()+"+++++goodsId="+goodsId+"++tagid="+tagid+"+valid is OK++++");
						goodsOk = true;
						break;
					}
				}
				
				if(!goodsOk){
					logger.info("++++++++++++trxcoupon="+trxCoupon.getId()+"+++++goodsId="+goodsId+"+COUPON_TAGID_GOODS_LIMIT++++");
					isLimitTagid = false;
					break;
				}
			}
			
			//商品种类限制
			if(!isLimitTagid){
				logger.info("++++++++++++trxcoupon="+trxCoupon.getId()+"+++++COUPON_TAGID_GOODS_LIMIT++++");
				return BaseException.COUPON_TAGID_LIMIT;  //秒杀限制
			}
		}
		
		return 1;
	}
	
	/**
	 * 优惠券异步出账
	 */
	public void processCreditVmAccountAsyn(TrxCoupon trxCoupon) throws AccountException,StaleObjectStateException{
		//执行批量更新
		VmAccount vmAccount = vmAccountService.findById(trxCoupon.getVmAccountId());
		vmAccountService.debitForAsyn(vmAccount,trxCoupon.getCouponBalance(),VmAccountType.DISPATCH,trxCoupon.getRequestId());
		trxCouponDao.updateTrxCouponForCreditAct(trxCoupon.getId(), trxCoupon.getVersion(), "优惠券异步入账");
	}
	
	/**
	 * 优惠券自动下发
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void processOnlineActivityAutoBind(Long userId,String csid){
		Map<String,Set<Long>> csidActivityIdsMap = (Map<String,Set<Long>>)memCacheService.get("CSID_COUPON_ACTIVITY_ONLINE_LIST");
		if(null == csidActivityIdsMap || csidActivityIdsMap.size() <=0 ){
			csidActivityIdsMap = new HashMap<String,Set<Long>>();
			List<CouponActivity> activityList = couponActivityDao.queryCouponActivityByType("MARKETING_ONLINE");	//市场线上后动
			for(CouponActivity activity : activityList){
				Long currentTime = System.currentTimeMillis();
				Long startTime = activity.getStartDate().getTime();	//开始时间
				Long endTime = activity.getEndDate().getTime();		//结束时间
				if(currentTime<startTime || currentTime>endTime){
					continue;
				}
				String[] csidArray = activity.getCsid().split(SPLIT_CHAR);
				if(null != csidArray && csidArray.length>0){
					for(int i=0;i<csidArray.length;i++){
						String csidKey = csidArray[i];
						Set<Long> idSet = csidActivityIdsMap.get(csidKey);
						if(null==idSet){
							idSet = new TreeSet<Long>();
						}
						idSet.add(activity.getId());
						csidActivityIdsMap.put(csidKey, idSet);
					}
				}
			}
			memCacheService.set("CSID_COUPON_ACTIVITY_ONLINE_LIST",csidActivityIdsMap,COUPON_TIMEOUT);
		}
		
		//获得该渠道所有可下发的优惠券活动ID
		Set<Long> activitySet = csidActivityIdsMap.get(csid);
		if(null != activitySet && activitySet.size()>0){
			//获得用户已经获得的优惠券（所属的活动）
			List<Long> joinList = trxCouponDao.queryUserJoinCouponActivity(userId);
			
			//移除用户已经下发过的活动
			if(null != joinList){
				//查看用户是否已经下发该活动
				for(Long joinId : joinList){
					activitySet.remove(joinId);
				}
			}
			
			//寻找一个最id最小的优惠券为用户绑定
			for(Long activityId : activitySet){
				logger.info("++++++++++onlineAutoBind+++userId="+userId+"+++++++++csid="+csid+"+++activityId="+activityId+"+++");
				try {
					Long couponId = processAutoBindCoupon(userId,activityId);
					logger.info("++++++++++onlineAutoBind+++SUCCESS++++userId="+userId+"+++++++++csid="+csid+"+++activityId="+activityId+"+++couponId="+couponId+"++++");
				} catch (StaleObjectStateException e) {
					logger.error("++++{ERROR}onlineAutoBind++userId="+userId+"++:",e);
				}
			}
		}
	}
	
	/**
	 * 系统自动绑定
	 * @param userId
	 * @param activityId
	 * @return
	 * @throws StaleObjectStateException
	 */
	@Override
	public Long processAutoBindCoupon(Long userId,Long activityId) throws StaleObjectStateException{
		TrxCoupon trxCoupon = trxCouponDao.queryMinINITCouponId(activityId);
		if(null == trxCoupon){
			logger.error("+++++++++++++coupon online{ERROR}++++activityId="+activityId+" stock is zero++userId="+userId+" won't get coupon +++");
			throw new StaleObjectStateException();
		}
		trxCouponDao.updateTrxCouponForBind(trxCoupon.getId(), userId, "市场线上优惠券--系统自动绑定", trxCoupon.getVersion());
		return trxCoupon.getId();
	}
	
	/**
	 * 优惠券绑定
	 * @param couponPwd
	 * @param userId
	 */
	public TrxResponseData processBindCoupon(String couponPwd,Long userId,ReqChannel reqChannel)throws CouponException,StaleObjectStateException{
		logger.info("++++++++couponPwd="+couponPwd+"+++++++++userId="+userId+"++++++++reqChannel=" + reqChannel);
			//查询优惠券信息
		couponPwd = getEncryptPwd(couponPwd);
		logger.info("++++++++couponPwd="+couponPwd+"+++++++++userId="+userId+"++++++++");
		TrxCoupon trxCoupon = trxCouponDao.queryTrxCouponByPwd(couponPwd);
		
		//判断优惠券有效性
		checkCoupon(trxCoupon);
		
		trxCouponDao.updateTrxCouponForBind(trxCoupon.getId(), userId, "优惠券激活", trxCoupon.getVersion());
		
		Map<String,Object> activityMap = getCouponActivityMemeryCached(trxCoupon.getActivityId());
		TrxResponseData responseData = new TrxResponseData();
		responseData.setCouponToponType("BINDING");
		responseData.setCouponValue(String.valueOf(trxCoupon.getCouponBalance()));
		responseData.setCouponName((String)activityMap.get("ACTIVITY_NAME"));
		responseData.setCouponLimitInfo((String)activityMap.get("ACTIVITY_LIMIT_INFO"));
		responseData.setCouponvalidDate(DateUtils.formatDate(trxCoupon.getStartDate(),"yyyy-MM-dd")+"——"+DateUtils.formatDate(trxCoupon.getEndDate(),"yyyy-MM-dd"));
		return responseData;
	}
	
	/**
	 * 检查优惠券
	 * @param coupon
	 * @throws DiscountCouponException
	 * @throws VmAccountException
	 */
	private void checkCoupon(TrxCoupon coupon) throws CouponException{
		//优惠券密码无效
		if(null == coupon){
			throw new CouponException(BaseException.DISCOUNTCOUPON_PWD_INVALID);
		}
		
		TrxCouponStatus couponStatus = coupon.getCouponStatus();
		
		if(!TrxCouponStatus.INIT.equals(couponStatus)){
			long currentTime = System.currentTimeMillis();
			if(coupon.getCouponType()!=0){
				throw new CouponException(BaseException.COUPON_NOT_AVAILABLE);
			}
			else if(TrxCouponStatus.TIMEOUT.equals(couponStatus)){
				throw new CouponException(BaseException.DISCOUNTCOUPON_EXPIRED);
			}
			//优惠券已使用
			else if(TrxCouponStatus.BINDING.equals(couponStatus)){
				throw new CouponException(BaseException.COUPON_BOUND);
			}
			//优惠券已使用
			else if(TrxCouponStatus.USED.equals(couponStatus)){
				throw new CouponException(BaseException.DISCOUNTCOUPON_USED);
			}
			//优惠券过期（不判断开始时间）
			else if(currentTime>coupon.getEndDate().getTime()){
				throw new CouponException(BaseException.DISCOUNTCOUPON_EXPIRED);	
			}
			//优惠券无效
			else{
				throw new CouponException(BaseException.DISCOUNTCOUPON_STATUS_INVALID);
			}
		}
		
		
	}
	
	/**
	 * 从缓存中获得优惠券活动的缓存MAP
	 * @param activityId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String,Object> getCouponActivityMemeryCached(Long activityId){
		//从memerychached中取出活动属性
		Map<String,Object> activityMap = (Map<String, Object>) memCacheService.get("COUPON_ACTIVITY_"+activityId);
		if(null == activityMap || activityMap.size()==0){
			CouponActivity couponActivity = couponActivityDao.queryCouponActivityById(activityId);
			Map<String,Object> map = new HashMap<String,Object>(4);
			map.put("ACTIVITY_NAME",couponActivity.getActivityName());
			map.put("LIMIT_AMOUNT", couponActivity.getLimitAmount());
			String[] limitTagid = StringUtils.sortArray(StringUtils.toTrim(couponActivity.getLimitTagid()).split(SPLIT_CHAR));
			map.put("LIMIT_TAGID", limitTagid);
			//打印memerycached limit_tagid字段存放内容
			logger.info("++++++++++++couponActivityId="+activityId+"++ memcached LIMIT_TAGID="+Arrays.toString(limitTagid));
			
			map.put("START_DATE", couponActivity.getCouponStartDate().getTime());
			map.put("END_DATE", couponActivity.getCouponEndDate().getTime());
			
			//获得tagid 对应的种类名称
			Map<String,String> tagMap = (Map<String,String>) memCacheService.get("TAG_PROPERTY_ID_NAME_SESSION");
			if(null == tagMap){
				tagMap = goodsSoaDao.findParentTags();
				if(null == tagMap || tagMap.size()<=0){
					tagMap = new HashMap<String,String>();
					tagMap.put("10100", "美食");
					tagMap.put("10200", "休闲娱乐");
					tagMap.put("10300", "丽人");
					tagMap.put("10400", "生活服务");
					tagMap.put("10500", "酒店旅游");
				}
				tagMap.put(MIAOSHA_TAGID, "秒杀");
				memCacheService.set("TAG_PROPERTY_ID_NAME_SESSION",tagMap,COUPON_TIMEOUT);  //失效时间1小时
			}
			
			//拼接优惠券活动对应的问题提示
			StringBuffer couponLimitInfo = new StringBuffer();
			if(limitTagid.length==0){
				couponLimitInfo.append("不支持任何商品");
			}else{
				couponLimitInfo.append("仅限");
				for(int i=0;i<limitTagid.length;i++){
					if(tagMap.containsKey(limitTagid[i])){
						couponLimitInfo.append(tagMap.get(limitTagid[i]) + "类,");
					}
				}
				couponLimitInfo = couponLimitInfo.deleteCharAt(couponLimitInfo.length()-1);
				couponLimitInfo.append("商品使用");
			}
			map.put("ACTIVITY_LIMIT_INFO", couponLimitInfo.toString());
			memCacheService.set("COUPON_ACTIVITY_"+activityId, map,COUPON_TIMEOUT);	//设置过期时间1小时
			activityMap = map;
			logger.info("++++++++++++couponActivityId="+activityId+"++ memcached value="+activityMap);
		}
		return activityMap;
	}
	
	/**
	 * 获得加密密码
	 * @param password
	 * @return
	 */
	private String getEncryptPwd(String password){
		return Des3Encryption.encode(getCouponKey(), password);// 加密
	}
	
	private String getCouponKey(){
		if (null == couponDesKey || couponDesKey.length()==0) {
			String couponDesKeySeed = StringUtils.toTrim(FileUtils.getFileContent(couponDesKeyFilePath));
			int length = couponDesKeySeed.length();
			if (length < 24) { // 不足24位前面补0
				couponDesKey = couponDesKeySeed;
				for(int i=0;i<24-length;i++){
					couponDesKey += "0";
				}
				logger.debug("++++++++++++++couponDesKeySeed.length < 24,append '0' ++++++++++couponDesKey:" + couponDesKey + "++++++++");

			} else {// 超出或者等于24位，截取前24位

				couponDesKey = couponDesKeySeed.substring(0, 24);
				logger.debug("++++++++++++++couponDesKeySeed.length > 24,cut [0-24]:++++++++++couponDesKey:" + couponDesKey + "++++++++");
			}
		}
		return couponDesKey;
	}

	
	/**
	 * 查询超时优惠券
	 * @return
	 */
	@Override
	public List<TrxCoupon> queryTimeoutTrxCoupon() {
		return trxCouponDao.queryTimeoutTrxCoupon();
	}

	/**
	 * 优惠券过期操作
	 * @param couponId
	 * @param version
	 * @param description
	 * @throws StaleObjectStateException
	 */
	@Override
	public void updateTrxCouponForTimeout(Long couponId, Long version,String description) throws StaleObjectStateException {
		trxCouponDao.updateTrxCouponForTimeout(couponId, version, description);
	}
}
