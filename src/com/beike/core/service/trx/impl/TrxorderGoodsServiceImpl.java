package com.beike.core.service.trx.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.biz.service.trx.OrderFilmService;
import com.beike.common.bean.trx.TrxResponseData;
import com.beike.common.bean.trx.VmAccountParamInfo;
import com.beike.common.entity.trx.MenuGoodsOrder;
import com.beike.common.entity.trx.TrxLog;
import com.beike.common.entity.trx.TrxOrder;
import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.common.enums.trx.ActHistoryType;
import com.beike.common.enums.trx.AuthStatus;
import com.beike.common.enums.trx.CreditStatus;
import com.beike.common.enums.trx.MerSettleStatus;
import com.beike.common.enums.trx.ReqChannel;
import com.beike.common.enums.trx.TrxLogType;
import com.beike.common.enums.trx.TrxStatus;
import com.beike.common.exception.AccountException;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.PaymentException;
import com.beike.common.exception.ProcessServiceException;
import com.beike.common.exception.RebateException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.common.exception.TrxOrderException;
import com.beike.common.exception.TrxorderGoodsException;
import com.beike.common.exception.VmAccountException;
import com.beike.common.exception.VoucherException;
import com.beike.common.guid.GuidGenerator;
import com.beike.core.service.trx.TrxorderGoodsService;
import com.beike.core.service.trx.TrxorderGoodsSnService;
import com.beike.core.service.trx.soa.proxy.TrxSoaService;
import com.beike.core.service.trx.vm.VmAccountService;
import com.beike.dao.goods.GoodsDao;
import com.beike.dao.merchant.MerchantDao;
import com.beike.dao.trx.MenuGoodsOrderDao;
import com.beike.dao.trx.TrxLogDao;
import com.beike.dao.trx.TrxOrderDao;
import com.beike.dao.trx.TrxorderGoodsDao;
import com.beike.dao.trx.soa.proxy.GoodsSoaDao;
import com.beike.entity.merchant.MerchantEvaluation;
import com.beike.util.Constant;
import com.beike.util.DateUtils;
import com.beike.util.EnumUtil;
import com.beike.util.StringUtils;
import com.beike.util.TrxConstant;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;

/**
 * @Title: TrxorderGoodsServiceImpl.java
 * @Package com.beike.core.service.trx
 * @Description:订单商品CORE service 实现类
 * @date May 17, 2011 6:31:57 PM
 * @author wh.cheng
 * @version v1.0
 */
@Service("trxorderGoodsService")
public class TrxorderGoodsServiceImpl implements TrxorderGoodsService {

	private final Log logger = LogFactory.getLog(TrxorderGoodsServiceImpl.class);
	private final String VACCOUNTID = "1";
	private final String OPERATORID = "0";

	@Autowired
	private VmAccountService vmAccountService;
	@Autowired
	private TrxorderGoodsSnService trxorderGoodsSnService;
	@Autowired
	private TrxorderGoodsDao trxorderGoodsDao;
	@Autowired
	private GuidGenerator guidGenerator;
	@Autowired
	private GoodsSoaDao goodsSoaDao;
	@Autowired
	private TrxOrderDao trxOrderDao;
	@Autowired
	private MerchantDao merchantDao;
	@Autowired
	private GoodsDao goodsDao;
	@Autowired
	private TrxLogDao trxLogDao;
	@Autowired
	private TrxSoaService trxSoaService;
	@Autowired
	private OrderFilmService orderFilmService;
	@Autowired
	private MenuGoodsOrderDao menuGoodsOrderDao;
	

	private static MemCacheService memCacheService = MemCacheServiceImpl.getInstance();

	public Long create(TrxorderGoods trxorderGoods)throws TrxorderGoodsException {

		// 商品订单号生成（包括循环避重）
		//String trxGoodsSn = trxorderGoodsSnService.createTrxGoodsSn();
		String trxGoodsSn = trxorderGoodsSnService.createTrxGoodsSnKing();

		trxorderGoods.setTrxGoodsSn(trxGoodsSn);
		trxorderGoods.setAuthStatus(AuthStatus.INIT);
		trxorderGoods.setMerSettleStatus(MerSettleStatus.INIT);// 不可结算
		trxorderGoods.setTrxStatus(TrxStatus.INIT);
		trxorderGoodsDao.addTrxGoods(trxorderGoods);
		Long trxGoodsId = trxorderGoodsDao.getLastInsertId();

		return trxGoodsId;
		// return trxorderGoodsDao.findBySn(trxorderGoods.getTrxGoodsSn());
	}

	public List<List<TrxorderGoods>> injectGoodsInfo(List<List<TrxorderGoods>> sourceList, String viewType) {

		List<List<TrxorderGoods>> resultList = null;
		for (List<TrxorderGoods> itemList : sourceList) {

			for (TrxorderGoods item : itemList) {
				// Map<String,String>
				// rspMap=goodsDao.findLog3AndMerNameBygoodId(item.getGoodsId());
				// //注入商品补充信息
				// if(rspMap!=null){
				// item.setBelongMerchant(rspMap.get("merchantName"));
				// item.setGoodsPicrl(rspMap.get("log3"));
				// }
				if (Constant.TRX_GOODS_UNUSEED.equals(viewType)) {// 只要不是已使用的就remove--逆反-->未使用
					if (!item.getAuthStatus().isCanUse()) {
						itemList.remove(item);
					}

				}

				if (Constant.TRX_GOODS_UNCOMMENT.equals(viewType)) {
					if (item.getCommentId() > 0|| !AuthStatus.DESTROY.equals(item)) {// 已经评价过或者没有凭证回收--逆反-->未评价
						itemList.remove(item);
					}
				}
			}
		}

		return resultList;
	}

	public boolean verifyBelong(Long trxgoodsId, Long userId,boolean verifyBelong) {
		if(!verifyBelong){
			return true;
		}
		
		boolean result = false;
		TrxorderGoods trxorderGoods = trxorderGoodsDao.findById(trxgoodsId);
		if (trxorderGoods != null) {
			TrxOrder trxOrder = trxOrderDao.findById(trxorderGoods.getTrxorderId());
			
			if (userId.longValue() == trxOrder.getUserId().longValue()) {
				result = true;
			}
		}

		return result;
	}

	public Map<String, String> addComment(Long trxGoodsId, Long userId,double commentPoint, String commentContent)
			throws ProcessServiceException, RebateException, AccountException,TrxOrderException, PaymentException, 
			TrxorderGoodsException,VoucherException, StaleObjectStateException {

		Map<String, String> cmtRspMap = new HashMap<String, String>();
		// 读取商户ID
		TrxorderGoods trxorderGoods = trxorderGoodsDao.findById(trxGoodsId);
		
		if (trxorderGoods.getCommentId() > 0) {
			cmtRspMap.put("result", "SUCCESSED");
			return cmtRspMap;
		}
		Map<String, String> rspMap = goodsDao.getMerchantByGoodId(trxorderGoods.getGoodsId());
		Long merchantId = 0L;
		if (rspMap != null) {
			merchantId = new Long(rspMap.get("id"));
		}

		Long commentId = merchantDao.addEvaluation(userId, merchantId,commentPoint, commentContent, trxorderGoods.getGoodsId(),trxorderGoods.getId());

		logger.info("++++++trxGoodsId:" + trxGoodsId + "->userId:" + userId+ "->commentPoint" + commentPoint + "->commentContent"
				+ commentContent + "->GoodsId:" + trxorderGoods.getGoodsId()+ "+++++++++++");
		trxorderGoods.setCommentId(commentId);
		trxorderGoods.setTrxStatus(TrxStatus.COMMENTED);
		trxorderGoodsDao.updateTrxGoods(trxorderGoods);

		// 加入返现
		// OrderInfo orderInfo=new OrderInfo();
		// orderInfo.setGoodsId(trxorderGoods.getId()+"");
		// orderInfo.setUserId(userId);
		// orderInfo.setTrxAmount(trxorderGoods.getRebatePrice());
		// orderInfo.setBizType("COMMENT");
		// processService.process(orderInfo);
		// 加入运营操作日志

		// 加入评价次数
		// update by ye.tian 增加该品牌的评价次数

		merchantDao.updateMerchantSalesCount(merchantId);
		// 更新平均分数
		MerchantEvaluation mel = merchantDao.findAvgScoreByMerchantId(merchantId);
		if (mel == null) {
			logger.info("===========can't find avg score in table beiker_merchantevaluation by merchantId = " + merchantId);
		} else {
			int checkRs = merchantDao.checkMerchantAcgScore("mc_avg_scores",merchantId);
			if (checkRs == 0) {
				merchantDao.insertMerchantAvgScore(mel.getEvaluationscore(),"mc_avg_scores", merchantId);
			} else {
				merchantDao.updateMerchantAvgScore(mel.getEvaluationscore(),"mc_avg_scores", merchantId);
			}
		}

		try {
			TrxLog trxLog = new TrxLog(trxorderGoods.getTrxGoodsSn(),new Date(), TrxLogType.COMMENTED, "评价成功", "");
			trxLogDao.addTrxLog(trxLog);
		} catch (Exception e) {
			e.printStackTrace();
		}

		cmtRspMap.put("result", "SUCCESS");
		return cmtRspMap;

	}

	/**
	 * 根据时间和是否退款查询过期的订单
	 * 
	 * @param date
	 * @return
	 */
	public List<TrxorderGoods> qryLoseListByIsRefund(Date date, boolean isRefund,int start,int daemonLength) {
		
		List<TrxorderGoods> trxOrderGoodsLlist = trxorderGoodsDao.findByStatusAndDate(TrxStatus.SUCCESS, new Date(),isRefund,start,daemonLength,ReqChannel.PARTNER);
		
		return trxOrderGoodsLlist;
	}

	public int qryLoseListByIsRefundCount(Date date, boolean isRefund) {
		
		int count = trxorderGoodsDao.findByStatusAndDateCount(TrxStatus.SUCCESS, new Date(),isRefund,ReqChannel.PARTNER);
		
		return count;
	}
	
	/**
     * 查询商品订单信息数量
     * map参数名称：
     * guestId商家编号
     * subGuestId分店编号
     * trxGoodsSn商品订单号
     * goodsId商品编号
     * voucherCode服务密码凭证号
     * 购买时间createDateBegin - createDateEnd
     * 消费时间 confirmDateBegin - confirmDateEnd
     */
	public Map<String,Long> qryTrxOrderGoodsForGuestCount(Map<String, String> condition){
		Map<String,Long> queryMap = trxorderGoodsDao.queryTrxOrderGoodsForGuestCountGroupByTrxStatus(condition);
		if(null == queryMap){
			return null;
		}
		Map<String,Long> countMap = new HashMap<String,Long>();
		long totalCount = 0;
		long successCount = 0;
		long consumeCount = 0;
		long refundCount = 0;
		long expiredCount = 0;
		for(Entry<String, Long> entry : queryMap.entrySet()){
			long count = entry.getValue();
			String trxStatus = entry.getKey();
			//未使用
			if(trxStatus.equals(TrxStatus.SUCCESS.name())){  
				successCount += count;
			}
			//已消费
			else if(trxStatus.equals(TrxStatus.USED.name()) || trxStatus.equals(TrxStatus.COMMENTED.name())){
				consumeCount += count;
			}
			//退款
			else if(trxStatus.equals(TrxStatus.REFUNDACCEPT.name()) || trxStatus.equals(TrxStatus.REFUNDTOACT.name()) || 
					trxStatus.equals(TrxStatus.RECHECK.name()) || trxStatus.equals(TrxStatus.REFUNDTOBANK.name())){
				refundCount += count;
			}
			
			else{//已过期不可用
				expiredCount += count;
			}
			//全部
			totalCount += count;
		}
		countMap.put("totalCount", totalCount);   //总数量
		countMap.put("successCount", successCount);	//购买未使用数量
		countMap.put("consumeCount", consumeCount);	//消费数量
		countMap.put("refundCount", refundCount);  // 退款数量
		countMap.put("expiredCount", expiredCount); //失效数量
		return countMap;
	}
	

	/**
     * 查询商品订单信息
     * map参数名称：
     * guestId商家编号
     * subGuestId分店编号
     * trxGoodsSn商品订单号
     * goodsId商品编号
     * voucherCode服务密码凭证号
     * trxStatus订单状态
     * 购买时间createDateBegin - createDateEnd
     * 消费时间 confirmDateBegin - confirmDateEnd
     * 分页参数 startRow offSet
     */
	@Override
	public List<Map<String, Object>> queryTrxOrderGoodsForGuest(Map<String, String> map, int startRow, int pageSize) {

        // 查询商品订单信息,分页后的数据
        List<Map<String,Object>> goodsOrderList = trxorderGoodsDao.queryTrxOrderGoodsForGuest(map, startRow, pageSize);
        if(goodsOrderList == null || goodsOrderList.size()==0){
            return null;
        }
        return goodsOrderList;
	}
	
	/**
	 * 根据状态，是否发送商家验证码、是否支持退款查询
	 * 
	 * @param trxStatus
	 * @param isSendMerVou
	 * @param isRefund
	 * @return
	 */
	public List<TrxorderGoods> findByStatusAndIsMerIsRefund(TrxStatus trxStatus, Long isSendMerVou, Long isRefund) {
		
		List<TrxorderGoods> trxOrderGoodsLlist = trxorderGoodsDao.findByStatusAndIsMerIsRefund(trxStatus, isSendMerVou, isRefund);

		return trxOrderGoodsLlist;
	}

	/**
	 * 查询符合要求的交易ID，并进行组合处理
	 * 
	 * @param userId
	 * @param startRow
	 * @param pageSize
	 * @param viewType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<List<TrxorderGoods>> listPageByUserIdAndType(Long userId,int startRow, int pageSize, String viewType) {
		String trxIdStr = "";
		String goodsIdStr = "";
		String mobile = "";
		List<Map<String, Object>> resultMapList = trxorderGoodsDao.findTrxIdByUserIdAndType(userId, startRow, pageSize, viewType);

		if (resultMapList == null) {
			return null;

		}
		// 二维List
		List<List<TrxorderGoods>> resultList = new ArrayList();
		for (Map<String, Object> itemMap : resultMapList) {

			trxIdStr =String.valueOf(itemMap.get("trxorderId"));
			goodsIdStr =String.valueOf(itemMap.get("goodsId"));
			 mobile = String.valueOf(itemMap.get("mobile"));

			List<TrxorderGoods> trxGoodsList = trxorderGoodsDao.findInTrxId(trxIdStr, goodsIdStr);

			if (trxGoodsList != null && trxGoodsList.size() > 0) {

				List<TrxorderGoods> tempList = new ArrayList();// 临时List
				Long lon = 0L;
				
				// 是否显示全部预定,只要有一笔订单满足，就显示全部预订
				for (TrxorderGoods item : trxGoodsList) {
					item.setMobile(mobile);
					if(item.getBizType()==1){
						Long merchantId = item.getSubGuestId();
						Map<String, Object> merchant =  goodsSoaDao.getMerchantById(merchantId);
						Map<String, Object> log3 = goodsSoaDao.findGoodsPicUrlById(item.getGoodsId());
						if(!"0".equals(merchant.get("parentId")+"")){
							merchant = goodsSoaDao.getMerchantById(Long.parseLong(merchant.get("parentId")+""));
						}
						item.setBelongMerchant(merchant.get("merchantName")+"");
						item.setGoodsPicrl(log3.get("log")+"");
						item.setMerchantId(Long.parseLong(merchant.get("merchantId")+""));
						item.setGoodsTitle("");
						item.setScheduledStatus("");// 预定状态
					}else if(item.getBizType()==0){

						Map<String, String> rspMap = goodsDao.findLogo3AndMerNameBygoodId(item.getGoodsId());
						
						// 加入商品补充信息
						List<Long> trxgoods_id = new ArrayList<Long>();
						trxgoods_id.add(item.getId());
						if (rspMap != null) {
							item.setBelongMerchant(rspMap.get("merchantName"));
							item.setGoodsPicrl(rspMap.get("logo3"));
							item.setMerchantId(Long.parseLong(rspMap.get("merchantId")));
							item.setGoodsTitle(rspMap.get("goodsTitle"));
							Long isScheduled = Long.parseLong(rspMap.get("isScheduled"));
							item.setIsScheduled(isScheduled);// 是否支持预定
							if (isScheduled == 0) {
								item.setScheduledStatus("");// 预定状态
							} else {
								Map<String, Object> map = trxSoaService.findBytrxgoodsId(item.getId());
								logger.info("++++++userId:" + userId + "++++++map:"+ map);
								if (TrxStatus.SUCCESS.equals(item.getTrxStatus())) {// 只有成功状态才显示预定商品
									if (map != null && map.size() > 0) {// 预定商品表中有预定记录做预定处理
										item.setBookingId((Long) map.get("id"));
										String scheduledStatus = (String) map.get("status");
										item.setScheduledStatus(scheduledStatus);// 预定状态
										if ("2".equals(scheduledStatus)|| "3".equals(scheduledStatus)|| "4".equals(scheduledStatus)) {
											lon = 1L;
										}
									} else {// 预定商品表中无预定记录的设置预定状态为未预定，5是自定义未预定状态，为页面展示用
										item.setScheduledStatus("5");
										lon = 1L;
									}
								}
							}
						}

						// 加入评价分数
						Map comentMap = merchantDao.getEvaluationByGoodsId(item.getGoodsId(), item.getId());
						if (comentMap != null) {
							String commentPoint = (String) comentMap.get("evaluationscore");
							item.setCommentPoint(commentPoint);
						}

						
					
					}
					//如果是在线选座 add by ljp 20121212
					if(item.getBizType() == 2){
						if(item.getBizType() == 2){
							try {
								Long	cinemaId = orderFilmService.queryCinemaIdByTrxGoodsId(item.getId());
								item.setCinemaId(cinemaId);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					tempList.add(item);
				}

				for (int j = 0; j < tempList.size(); j++) {
					tempList.get(j).setShowScheduled(lon);
				}
				resultList.add(tempList);
			}

			if (resultList == null || trxGoodsList.size() == 0) {
				return null;
			}
		}

		return resultList;
	}





	@Override
	public TrxorderGoods findById(Long id) {
		TrxorderGoods trxorderGoods = trxorderGoodsDao.findById(id);
		return trxorderGoods;
	}

	@Override
	public TrxorderGoods findById(Long id, Long userId) {
		TrxorderGoods trxorderGoods = trxorderGoodsDao.findById(id, userId);
		return trxorderGoods;
	}
	

	/**
	 * 获取总量超限是否退款商品订单LIST
	 * @param overRunTgList
	 * @param type
	 * @return
	 */
	public Map<String,List<TrxorderGoods>> processGetTotalOverRfdTgListMap(List<TrxorderGoods> unSingleOvRunTgList){
		
		List<TrxorderGoods> overRunRfdTgList = new ArrayList<TrxorderGoods>();// 总量超限退款订单
		List<TrxorderGoods> overRunNoRfdTgList = new ArrayList<TrxorderGoods>();// 总量超限不退款订单
		Map<String,List<TrxorderGoods>> overRfdTgListMap=new HashMap<String,List<TrxorderGoods>>();
		
		//Map<Long,Integer> miaoshaSaleCountMap = new HashMap<Long,Integer>();	//秒杀商品的购买数量
		
		for(TrxorderGoods trxorderGoods : unSingleOvRunTgList){
			boolean boo = true;
			
			/*
			 * 增加秒杀商品的总量限购
			 * modify by wangweijie 2012-08-09 
			 */
			Long trxRuleId = trxorderGoods.getTrxRuleId();
			
			//判断是否为秒杀商品的订单（trx_rule_id=3 为秒杀商品，extend_info字段存储了秒杀商品的id）
			if(null != trxRuleId && 3 == trxRuleId.intValue() && !StringUtils.isEmpty(trxorderGoods.getExtend_info())){
				Long miaoshaId = Long.parseLong(trxorderGoods.getExtend_info());
				boo = isCountLimitForMiaoSha(miaoshaId);//秒杀商品总量限购超限判断，并更新秒杀商品库存
				
			}else{
				boo = isCountLimit(trxorderGoods);// 普通商品调用总量限购接口
			}
			
			//如果商家码充足 或者是在线API发送的商家码订单	
			//(如：maxCount 100，商家码只上传50个,到前已售50个，则需开始退款)
			//总量超限不退款
			//if(trxorderGoods.isMerVouEnough() || TrxConstant.isMerchantVoucherByApi(trxorderGoods.getGuestId())){
			if(trxorderGoods.isMerVouEnough() || trxorderGoods.isSendMerVou()==2){
				overRunNoRfdTgList.add(trxorderGoods);
			
			//平台码订单：设置的默认库存是否超限
			
			}else if (boo){//平台码订单超默认库存
				overRunRfdTgList.add(trxorderGoods);
			}else {//平台码订单未超默认库存
				overRunNoRfdTgList.add(trxorderGoods);
				
			}
		}
		
		overRfdTgListMap.put("TOTAL_OVRUN_AUTO_REFUND",overRunRfdTgList);//总量超限退款
		overRfdTgListMap.put("NO_REFUND", overRunNoRfdTgList);//无需退款
		
		return overRfdTgListMap;
		
	}
	
	
	/**
	 * 判断是否总量限购超限
	 * 
	 * @param trxorderGoods
	 * @return
	 */
	@Override
	public boolean isCountLimit(TrxorderGoods trxorderGoods) {
		
		//普通商品逻辑
		boolean boo = false;
		Long goodsId = trxorderGoods.getGoodsId();
		StringBuilder singleCountKeySb = new StringBuilder();
		singleCountKeySb.append(TrxConstant.GOODS_MAXCOUNT_KEY);
		singleCountKeySb.append(goodsId);

		String goodsMaxCount = (String) memCacheService.get(singleCountKeySb.toString());
		if (goodsMaxCount == null) {
			Map<String, Object> map = goodsSoaDao.getmaxCountById(goodsId);
			goodsMaxCount = map.get("maxcount").toString() + "|"+ map.get("isavaliable").toString();
			memCacheService.set(singleCountKeySb.toString(), goodsMaxCount,	TrxConstant.MaxCountExpTimeout);

		}

		String goodsMaxArray[] = goodsMaxCount.split("\\|");
		Map<String, Object> map = goodsSoaDao.getGoodsProfile(trxorderGoods.getGoodsId());// salesCount
		int count = (Integer) map.get("salesCount");
		if (count > Integer.valueOf(goodsMaxArray[0]) || count == Integer.valueOf(goodsMaxArray[0])) {
			boo = true;
		}
		return boo;
	}

	/**
	 * 判断是否秒杀商品总量限购超限，并且更新库存
	 * @param  miaoshaId
	 * @param miaoshaMap
	 * @return
	 */
	public boolean isCountLimitForMiaoSha(Long miaoshaId){
		Map<String,Object> miaoshaMap = goodsSoaDao.findMiaoSha(miaoshaId);
		if(null != miaoshaMap&& !miaoshaMap.isEmpty()){
			
			int miaoShaMaxCount = (Integer) miaoshaMap.get("mMaxCount"); 		//秒杀库存
			long miaoShaStartTime = ((Date)miaoshaMap.get("mStartTime")).getTime() ;		//秒杀开始时间
			long miaoShaEndTime = ((Date)miaoshaMap.get("mEndTime")).getTime() ;			//秒杀结束时间
			int miaoShaIsUsed = (Integer)miaoshaMap.get("isUsed");				//是否在用（1是，0否）
			
			long currentTime = System.currentTimeMillis();
			//秒杀商品在用（1是，0否）  &&　秒杀结束时间>=当前时间  &&　秒杀开始时间<=当前时间 
			if(1==miaoShaIsUsed && currentTime<=miaoShaEndTime && currentTime>=miaoShaStartTime ){
				
				int saleCount = goodsSoaDao.findMiaoShaSaleCountForUpdate((Long)miaoshaMap.get("id")); //获得当前库存量，并且使用悲观锁，保证库存更新正确
			
				//如果库存数量小于库存上限  &&  
				if(saleCount < miaoShaMaxCount){
					//秒杀表msId商品库存加1
					goodsSoaDao.updateMiaoShaSaleCountById(saleCount + 1 , (Long)miaoshaMap.get("id"));
					return false; 
				}
			}
		}
		return true;
	}
	
	
	
	
	/**
	 * 秒杀商品总量限购，并且更新秒杀商品库存
	 */
	@Override
	public List<TrxorderGoods> processTotalCountLimitForMiaoSha(List<TrxorderGoods> unSingleOverRunTgList) {
		/*
		 * 秒杀商品库存更新，个人限购、总量限购的退款操作 begin
		 */
		List<TrxorderGoods> miaoshaTotalOverRunRfdList = new ArrayList<TrxorderGoods>();	//秒杀总量限购List
		//对秒杀商品需要对个人限购和总量限购的商品订单进行退款 
		for(TrxorderGoods trxorderGoods : unSingleOverRunTgList){
			Long trxRuleId = trxorderGoods.getTrxRuleId();
			//判断是否为秒杀商品的订单（trx_rule_id=3 为秒杀商品，extend_info字段存储了秒杀商品的id）
			if(null != trxRuleId && 3 == trxRuleId.intValue() && !StringUtils.isEmpty(trxorderGoods.getExtend_info())){
				Long miaoshaId = Long.parseLong(trxorderGoods.getExtend_info());
				
				//秒杀商品总量限购超限判断，并更新秒杀商品库存
				boolean isCountLimit = isCountLimitForMiaoSha(miaoshaId);
				 //添加总量限购商品
				if(isCountLimit){
					miaoshaTotalOverRunRfdList.add(trxorderGoods); 
				}
			}
		}
		return miaoshaTotalOverRunRfdList;
	}

	@Override
	public int findCountByUserId(Long userId, String viewType) {
		String idStr = "";
		List<Map<String, Object>> listTrxOrder = trxorderGoodsDao.findTrxOrderIdByUserID(userId);
		if (listTrxOrder == null || listTrxOrder.size() == 0) {
			return 0;
		}
		for (Map<String, Object> trxOrder : listTrxOrder) {
			Long id = (Long) trxOrder.get("id");
			idStr = idStr + id.toString() + ",";
		}

		if (idStr.contains(",")) {
			idStr = idStr.substring(0, idStr.lastIndexOf(","));
		}

		int unUsedCount = trxorderGoodsDao.findCountByUserId(idStr, viewType);
		return unUsedCount;
	}

	@Override
	public int findPageCountByUserId(Long userId, String viewType) {
		int totalRows = trxorderGoodsDao.findPageCountByUserId(userId, viewType);
		return totalRows;
	}

	@Override
	public int findPageCountByUserIdStatus(Long userId, String trxStatus) {
		String sqlStatus = TrxConstant.getSqlStatus(trxStatus);// 订单状态转义
		int totalRows = trxorderGoodsDao.findPageCountByUserIdStatus(userId,sqlStatus);
		return totalRows;
	}

	@Override
	public TrxResponseData findTrxorderGoodsByUserIdStatus(Long userId,int startRow, int pageSize, String trxStatus) {
		String sqlStatus = TrxConstant.getSqlStatus(trxStatus);// 订单状态转义
		// 查询出goodsId和trxorderId信息
		List<Map<String, Object>> trxGoodsList = trxorderGoodsDao.findTrxorderGoodsByUserIdStatus(userId, startRow, pageSize,sqlStatus);
		logger.info("++++++userId:"+userId+"++++++++++++trxStatus:"+trxStatus+"+++++sqlStatus:"+sqlStatus);
		List<Map<String, Object>> togList = new ArrayList<Map<String, Object>>();

		for (Map<String, Object> togMap : trxGoodsList) {
			String goodsId = togMap.get("goodsId").toString();
			String trxorderId = togMap.get("trxorderId").toString();
			List<Map<String, Object>> trxGoodsMapList = trxorderGoodsDao.findTrxorderGoodsByGoodsIdTrxorderId(trxorderId, goodsId);
			if (trxGoodsMapList != null) {
				togList.addAll(trxGoodsMapList);
			}
		}
		String strUserId = "";
		String strTrxorderGoodsId = "";
		String strGoodsId = "";
		String strGoodsName = "";// 商品名称
		String strGoodsPayPrice = "";// 支付金额
		String strGoodsTrxStatus = "";// 订单状态
		String strCreateDate = "";// 下单时间
		String strLoseDate = "";// 订单过期时间
		String strTrxorderGoodsSn = "";// 商品订单号
		String strUsedDate = "";// 使用时间
		String strTrxOrderId = "";// 交易订单
		String strVoucherType = "";// 凭证码类型
		String strMerchantName = "";// 品牌名称
		String strGoodsTitle = "";// 商品简称
		String strLastUpdateDate = "";// 最后更新时间
		String strLogo = "";// 商品链接地址
		String strVoucherCode ="";//凭证码
		if (togList.size() > 0) {
			for (Map<String, Object> togMap : togList) {
				String userIdstr = togMap.get("userId").toString();
				String trxorderGoodsId = togMap.get("trxorderGoodsId").toString();
				String goodsId = togMap.get("goodsId").toString();
				String goodsName = togMap.get("goodsName").toString();
				String goodsPayPrice = togMap.get("payPrice").toString();
				String goodsTrxStatus = togMap.get("trxStatus").toString();
				String createDate = DateUtils.dateToStrLong((Date) togMap.get("createDate"));
				String loseDate = DateUtils.dateToStrLong((Date) togMap.get("loseDate"));
				String trxorderGoodsSn = (String) togMap.get("trxGoodsSn");
				String usedDate = DateUtils.dateToStrLong((Date) togMap.get("confirmDate"));
				String trxOrderId = togMap.get("trxorderId").toString();
				String voucherType = togMap.get("sendMerVou").toString();
				String lastUpdateDate = DateUtils.dateToStrLong(togMap.get("lastUpdateDate")==null?new Date():(Date)togMap.get("lastUpdateDate"));
				String voucherCode =  togMap.get("voucherCode").toString();
				//如果 voucherType 是阳光绿洲则是空串 add by ljp 20121030
				if("2".equals(voucherType)){
					voucherCode="";
				}
				
				List<Long> goodsList = new ArrayList<Long>();
				goodsList.add(Long.valueOf(goodsId));
				Map<String, Object> goodsIdMap = trxSoaService.findMerchantName(goodsList);
				String merchantName = goodsIdMap.get("merchantName").toString();
				Map<Long, String> titleMap = trxSoaService.findGoodsTitle(goodsList);
				String goodsTitle = titleMap.get(Long.valueOf(goodsId));
				Map<Long, String> logoMap = trxSoaService.findGoodsDTPicUrl(goodsList);
				String logo4 = TrxConstant.UPLOAD_IMAGES_PATH + logoMap.get(Long.valueOf(goodsId));

				strUserId = strUserId + userIdstr + "|";
				strTrxorderGoodsId = strTrxorderGoodsId + trxorderGoodsId + "|";
				strGoodsId = strGoodsId + goodsId + "|";
				strGoodsName = strGoodsName + goodsName + "|";
				strGoodsTitle = strGoodsTitle + goodsTitle + "|";
				strGoodsPayPrice = strGoodsPayPrice + goodsPayPrice + "|";
				strLogo = strLogo + logo4 + "|";
				strGoodsTrxStatus = strGoodsTrxStatus + goodsTrxStatus + "|";
				strCreateDate = strCreateDate + createDate + "|";
				strLoseDate = strLoseDate + loseDate + "|";
				strTrxorderGoodsSn = strTrxorderGoodsSn + trxorderGoodsSn + "|";
				strUsedDate = strUsedDate + usedDate + "|";
				strTrxOrderId = strTrxOrderId + trxOrderId + "|";
				strVoucherType = strVoucherType + voucherType + "|";
				strMerchantName = strMerchantName + merchantName + "|";
				strLastUpdateDate = strLastUpdateDate + lastUpdateDate + "|";
				strVoucherCode = strVoucherCode + voucherCode + "|";
			}
		}
		if (strUserId.contains("|")) {
			strUserId = strUserId.substring(0, strUserId.lastIndexOf("|"));
			strTrxorderGoodsId = strTrxorderGoodsId.substring(0,strTrxorderGoodsId.lastIndexOf("|"));
			strGoodsId = strGoodsId.substring(0, strGoodsId.lastIndexOf("|"));
			strGoodsName = strGoodsName.substring(0, strGoodsName.lastIndexOf("|"));
			strGoodsTitle = strGoodsTitle.substring(0, strGoodsTitle.lastIndexOf("|"));
			strGoodsPayPrice = strGoodsPayPrice.substring(0, strGoodsPayPrice.lastIndexOf("|"));
			strLogo = strLogo.substring(0, strLogo.lastIndexOf("|"));
			strGoodsTrxStatus = strGoodsTrxStatus.substring(0,strGoodsTrxStatus.lastIndexOf("|"));
			strCreateDate = strCreateDate.substring(0, strCreateDate.lastIndexOf("|"));
			strLoseDate = strLoseDate.substring(0, strLoseDate.lastIndexOf("|"));
			strTrxorderGoodsSn = strTrxorderGoodsSn.substring(0,strTrxorderGoodsSn.lastIndexOf("|"));
			strUsedDate = strUsedDate.substring(0, strUsedDate.lastIndexOf("|"));
			strTrxOrderId = strTrxOrderId.substring(0, strTrxOrderId.lastIndexOf("|"));
			strVoucherType = strVoucherType.substring(0, strVoucherType.lastIndexOf("|"));
			strMerchantName = strMerchantName.substring(0, strMerchantName.lastIndexOf("|"));
			strLastUpdateDate = strLastUpdateDate.substring(0,strLastUpdateDate.lastIndexOf("|"));
			strVoucherCode = strVoucherCode.substring(0,strVoucherCode.lastIndexOf("|")); 
			
		}
		TrxResponseData responseData = new TrxResponseData(strUserId,strTrxorderGoodsId, strGoodsId, strGoodsName, strGoodsTitle,strGoodsPayPrice, 
				strLogo, strGoodsTrxStatus, strCreateDate,strLoseDate, strTrxorderGoodsSn, strUsedDate, strTrxOrderId,strVoucherType, strMerchantName, strLastUpdateDate);
		responseData.setVoucherCode(strVoucherCode);
		return responseData;

	}



	@Override
	public List<TrxorderGoods> findByDis() {
		List<TrxorderGoods> list = trxorderGoodsDao.findByDis(0);
		return list;
	}

	public void rebateDaemon(TrxorderGoods trxorderGoods)throws VmAccountException, AccountException,StaleObjectStateException {
		if (trxorderGoods.getCreateDate().after(TrxConstant.rebateEndDate)) {// 返现补发时，若购买时间点在暂停返现时间点后，则不做返现补发。暂时保留返现补发，使补发平滑割接。

			logger.info("++++unrebate++trxorderGoodsId:"+ trxorderGoods.getId() + "+++++++createDate:"+ trxorderGoods.getCreateDate() + "+++++isDis="+ trxorderGoods.isDis());
			return;
		}
		
		TrxOrder trxOrder = trxOrderDao.findById(trxorderGoods.getTrxorderId());
		String vmAccountId = VACCOUNTID;
		String amount = String.valueOf(trxorderGoods.getRebatePrice());
		String requestId = guidGenerator.gainCode("DIS");
		String userId = trxOrder.getUserId().toString();
		String operatorId = OPERATORID;

		VmAccountParamInfo vmAccountParamInfo = new VmAccountParamInfo();
		vmAccountParamInfo.setVmAccountId(vmAccountId);
		vmAccountParamInfo.setAmount(amount);
		vmAccountParamInfo.setRequestId(requestId);
		vmAccountParamInfo.setUserId(userId);
		vmAccountParamInfo.setOperatorId(operatorId);
		vmAccountParamInfo.setActHistoryType(ActHistoryType.RABATE);
		vmAccountParamInfo.setDescription(trxorderGoods.getId().toString());

		vmAccountService.dispatchVm(vmAccountParamInfo);
		logger.info("++++re++rebate  dis++trxorderGoods:"+ trxorderGoods.getId() + "+++++++++++++++amount=" + amount+ "++++++++++vmAccountId=" + vmAccountId);
		trxorderGoods.setDis(true);
		try {
			TrxLog trxLogReabte = new TrxLog(trxorderGoods.getTrxGoodsSn(),new Date(), TrxLogType.REBATE, "返现成功", "返现金额：￥"+ trxorderGoods.getRebatePrice());
			trxLogDao.addTrxLog(trxLogReabte);

		} catch (Exception e) {
			logger.info(e);
			e.printStackTrace();
		}
		trxorderGoodsDao.updateTrxGoods(trxorderGoods);

	}
	
	
	/**
	 * 根据tgId单笔或批量评价
	 * @throws StaleObjectStateException 
	 */
	public void commentByTgId(Set<Long> tgIdSet, Long evaluationid) throws StaleObjectStateException {

		if (tgIdSet != null && tgIdSet.size() > 0) {
			StringBuilder sb = new StringBuilder();// tgId串
			for (Long tgId : tgIdSet) {

				sb.append(String.valueOf(tgId));
				sb.append(",");

			}
			sb.deleteCharAt(sb.length() - 1);
			int result = trxorderGoodsDao.updateTrxStatusByIds(sb.toString(),EnumUtil.transEnumToString(TrxStatus.COMMENTED), EnumUtil.transEnumToString(TrxStatus.USED), evaluationid);

			if (result != tgIdSet.size()) {

				throw new StaleObjectStateException(BaseException.OPTIMISTIC_LOCK_ERROR);

			}
		} else {
			throw new StaleObjectStateException(BaseException.OPTIMISTIC_LOCK_ERROR);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List findGonTrxGoodsSnByTrxId(Long trxId) {
		List rs = trxorderGoodsDao.findSnByTrxId(trxId);
		return rs;
	}
	
	@Override
	public List<TrxorderGoods> preQryInWtDBFindByTrxId(long trxid) {
		return trxorderGoodsDao.findByTrxId(trxid);
	}

	/**
	 * 根据trxOrderId查询（主库查询）
	 * @param trxOrderId
	 * @return
	 */
	public  List<Map<String, Object>>  preQryInWtDBByByTrxId(Long trxOrderId){
		return trxorderGoodsDao.findByTrxOrderId(trxOrderId);
	}

	/**
     * 根据trxGoodsId查询（主库查询）
     * @param trxGoodsId
     * @return
     */
    public  TrxorderGoods  preQryInWtDBByByTrxGoodsId(Long trxGoodsId){
        return trxorderGoodsDao.findById(trxGoodsId);
    }
    
	/**
	 * 根据trxOrderId查询券有关信息
	 */
	@Override
	public List<Map<String, Object>> findVoucherInfoList(Long trxOrderId) {
		
		return trxorderGoodsDao.findVoucherInfoByTrxOrderId(trxOrderId);
	}
    /**
     * 更新 入账状态为INIT,结算状态如果为 UNSETTLE改为SETTLEED
     */
   /* @Override
    public boolean updateTrxOrdrGoodsCreditStatusInit(Long trxGoodsId) {
        boolean resutl=false;
        TrxorderGoods trxorderGoods= trxorderGoodsDao.findById(trxGoodsId);
        if(trxorderGoods!=null){
            trxorderGoods.setCreditStatus(CreditStatus.INIT.toString());
            if(trxorderGoods.getMerSettleStatus().equals(MerSettleStatus.UNSETTLE)){
                trxorderGoods.setMerSettleStatus(MerSettleStatus.SETTLEED);
            }
            trxorderGoods.setLastUpdateDate(new Date());
            int cut=trxorderGoodsDao.updateTrxOrderGoodsCreditStatus(trxorderGoods);
            if(cut==1){
                resutl=true;
            }
        }
        return resutl;
    }*/
    /**
     * 更新 入账状态为SUCCESS
     * @throws TrxorderGoodsException 
     * @throws StaleObjectStateException 
     */
    @Override
    public void updateTrxOrdrGoodsCreditStatusSuccess(TrxorderGoods trxorderGoods) throws TrxorderGoodsException, StaleObjectStateException {
        if(null==trxorderGoods){
        	throw new TrxorderGoodsException(BaseException.TRXORDERGOODS_NOT_FOUND);
        }
		
		if (MerSettleStatus.UNSETTLE.equals(trxorderGoods.getMerSettleStatus())) {
			trxorderGoods.setMerSettleStatus(MerSettleStatus.SETTLEED);
			trxorderGoods.setCreditStatus(CreditStatus.SUCCESS.toString());
			trxorderGoodsDao.updateTrxGoods(trxorderGoods);
		}
            
        }
            
	

	/**
	 * 根据voucherId来查询
	 * @param voucherId
	 * @return
	 */
	public TrxorderGoods findByVoucherId(Long voucherId){
		return trxorderGoodsDao.findByVoucherId(voucherId);
	}
   
    
    
	
	/**
	 * 查询商品订单详情
	 * @param trxGoodsId
	 * @param guestId
	 * @return
	 * @throws Exception 
	 */
	public List<MenuGoodsOrder> queryMenuGoodsOrderList(Long trxGoodsId){
		return 	menuGoodsOrderDao.queryByOrderIdAndGuestId(trxGoodsId);
	}

	/**
	 * 查询商品订单结算信息
	 */
	@Override
	public List<Map<String, Object>> querySettleDetailById(String idStr) {
		return trxorderGoodsDao.querySettleDetailByIds(idStr);
	}
    
    /**
     * 查询未入账的商品订单
     * @return
     */
    public  List<TrxorderGoods> qryTrxOrderGoodsByCreditStatus(String startDateStr,String  endDateStr,CreditStatus creditStatus){
    	
    	return 	trxorderGoodsDao.getTrxOrderGoodsByCreditStatus(startDateStr,endDateStr,creditStatus);
    	
    }

    /**
     * 查询商家购买数量
     * 0团购        1点餐        2网票        3网店
     * @param map
     * @return Map<String,Object>
     */
    @Override
    public List<Map<String, Object>> queryTrxGoodsBuyCountForGuest(Map<String, String> map) {
        List<Map<String, Object>> list = trxorderGoodsDao.queryTrxGoodsBuyCountForGuest(map);
        return list;
    }
    /**
     * 查询商家消费数量
     * 0团购        1点餐        2网票        3网店
     * @param map
     * @return Map<String,Object>
     */
    @Override
    public List<Map<String, Object>> queryTrxGoodsUsedCountForGuest(Map<String, String> map) {
        List<Map<String, Object>> list = trxorderGoodsDao.queryTrxGoodsUsedCountForGuest(map);
        return list;
    }
}

