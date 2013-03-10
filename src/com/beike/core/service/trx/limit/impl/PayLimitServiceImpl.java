package com.beike.core.service.trx.limit.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.bean.trx.OrderInfo;
import com.beike.common.entity.trx.TrxOrder;
import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.common.entity.trx.limit.PayLimit;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.PayLimitException;
import com.beike.core.service.trx.limit.PayLimitService;
import com.beike.core.service.trx.soa.proxy.TrxSoaService;
import com.beike.dao.trx.limit.PayLimitDao;
import com.beike.dao.trx.soa.proxy.GoodsSoaDao;
import com.beike.util.DateUtils;
import com.beike.util.TrxConstant;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;

/**
 * @Title: PayLimitService.java
 * @Package com.beike.core.service.trx.limit
 * @Description: 购买限制Service实现类
 * @date May 27, 2011 4:53:09 PM
 * @author wh.cheng
 * @version v1.0
 */
@Service("payLimitService")
public class PayLimitServiceImpl implements PayLimitService {

	private final Log logger = LogFactory.getLog(PayLimitServiceImpl.class);

	@Autowired
	private PayLimitDao payLimitDao;

	@Autowired
	private TrxSoaService trxSoaService;
	@Autowired
	private GoodsSoaDao goodsSoaDao;

	private static MemCacheService memCacheService = MemCacheServiceImpl
			.getInstance();

	/**
	 * 根据用户ID和商品ID，允许用户还可购买多少 如果出现并问题。 导致已购买数量>可购买数量.则可购买0件。不能给转发层响应负数。不然会遭用户鄙视。
	 * 
	 * @param sourceCount
	 * @param Uid
	 * @param GId
	 * @return
	 */
	@Override
	public Long allowPayCount(Long sourceCount, Long UId, Long GId,Long miaoshaId)
	{
		Long resultPayCount = 0L;
		if (sourceCount.longValue() < 0 || sourceCount.longValue() == 0)
		{

			throw new IllegalArgumentException("sourceCount must > 0");

		}
		Long sucPayCount = qryPayCountByUIdAndGId(UId, GId,miaoshaId);

		return sourceCount.longValue() - sucPayCount.longValue() > 0 ? sourceCount.longValue() - sucPayCount.longValue() : resultPayCount;

	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean processPayLimit(TrxOrder trxOrder,List<TrxorderGoods> trxorderGoodsList) {
		
		
		Long userId = trxOrder.getUserId();

		// 前置校验。即为超限后将用户的网银支付进行充值进行前置校验
		Map<Object, Object> rspMap = verifyPayLimitList(trxOrder,trxorderGoodsList);

		boolean isPayPlimit = (Boolean) rspMap.get("isPayPlimit");

		List<Long> limitToPayCountList = (List<Long>) rspMap.get("limitToPayCountList");

		List<Long> limitGoodsIdList = (List<Long>) rspMap.get("limitGoodsIdList");

		Map<Long, PayLimit> payLimitMap = (Map<Long, PayLimit>) rspMap.get("payLimitMap");

		int count = limitGoodsIdList.size();
		if (count > 0 && !isPayPlimit) {// 只有包含限购商品且没有超限了才进入下逻辑（如果超限，则变更为充值，实际为未购买成功,不计数。）

			// 使用传参方式减少与交互，此处并发问题在update语句中控制
			for (int i = 0; i < count; i++) {
				Long toPayCount = limitToPayCountList.get(i);
				Long gId = limitGoodsIdList.get(i);

				// 如果在限购表中已有限购 记录
				PayLimit payLimit = payLimitMap.get(gId);
				if (payLimit == null) {
					PayLimit upPayLimit = new PayLimit();
					upPayLimit.setUserId(userId);
					upPayLimit.setGoodsId(gId);
					upPayLimit.setPayCount(toPayCount);
					payLimitDao.savePayLimit(upPayLimit);
				} else {
					payLimit.setPayCount(toPayCount);
					payLimit.setModifyDate(new Date());
					payLimitDao.updatePayLimit(payLimit);

				}

			}
		}
		return isPayPlimit;
	}

	
	

	/**
	 * @param trxOrder
	 * @param trxorderGoods
	 * @return true：表示还可以买 false表示已经超限
	 */
	@Override
	public boolean isAllowBuyInPayLimit(TrxOrder trxOrder, TrxorderGoods trxorderGoods) {
		Long userId = trxOrder.getUserId();
		Long goodsId = trxorderGoods.getGoodsId();
		Long trxRuleId = trxorderGoods.getTrxRuleId();
		//秒杀商品个人限购
		logger.info("++++++++++++++trxRuleId="+trxRuleId+"+++++++++++++Extend_info="+trxorderGoods.getExtend_info());
		if(null != trxRuleId && 3==trxRuleId.intValue()){
			boolean isOverRun = false;
			// 秒杀相关
			Long miaoshaId = Long.valueOf(trxorderGoods.getExtend_info());
			Map<String,Object> miaoshaMap = goodsSoaDao.findMiaoSha(miaoshaId);
			//如果秒杀商品不存在，返回已经超限
			if(null == miaoshaMap || miaoshaMap.isEmpty()){
				return false;
			}
			//获得秒杀商品个人限购总量
			int maxSingleCount = (Integer)miaoshaMap.get("mSingleCount");
			PayLimit payLimit = payLimitDao.findUseridAndGoodsid(trxOrder.getUserId(), trxorderGoods.getGoodsId(), miaoshaId);
			Long singleSaleCount = null==payLimit ? 0L : payLimit.getPayCount();// 个人已购买数量
			//0表示不限购
			if(0 == maxSingleCount){
				isOverRun = true;
			}else{
				
				//如果个人购买量是否小于最大限购量，则没有超限，否则超限
				if (singleSaleCount.intValue() < maxSingleCount) {
					isOverRun = true;
				}else{
					isOverRun = false;
				}
			}
			if(isOverRun){
				if (null == payLimit) {
					PayLimit upPayLimit = new PayLimit();
					upPayLimit.setUserId(userId);
					upPayLimit.setGoodsId(goodsId);
					upPayLimit.setPayCount(1L);
					upPayLimit.setMiaoshaId(miaoshaId);
					payLimitDao.savePayLimit(upPayLimit);
				} else {
					//payLimit.setPayCount(singleSaleCount+1);
					payLimit.setModifyDate(new Date());
					payLimitDao.updatePayLimit(payLimit);
				}
			}
			return isOverRun;
		}else{//常规商品个人限购
			boolean boo = false;
			Set<Long> setGoodsId = new HashSet<Long>();
			setGoodsId.add(goodsId);
			Map<Long, Integer> mapGoodsId = findSingleCount(setGoodsId);
			int userCount = mapGoodsId.get(goodsId);// 个人限购数量
			if (userCount == 0) {
				boo = true;

			} else {

				PayLimit payLimit = payLimitDao.findUseridAndGoodsid(trxOrder
						.getUserId(), goodsId,0L);
				Long payCount = payLimit == null ? 0L : payLimit.getPayCount();// 个人已购买数量
				if (payCount < userCount) {
					boo = true;

				}
				if (payLimit == null) {
					PayLimit upPayLimit = new PayLimit();
					upPayLimit.setUserId(userId);
					upPayLimit.setGoodsId(goodsId);
					upPayLimit.setPayCount(1L);
					upPayLimit.setMiaoshaId(0L);
					payLimitDao.savePayLimit(upPayLimit);
				} else {
					// payLimit.setPayCount(1L);
					payLimit.setModifyDate(new Date());
					payLimitDao.updatePayLimit(payLimit);

				}
			}
			return boo;
		}
	
/*		Set<Long> goodsIdset = new HashSet<Long>();
		goodsIdset.add(trxorderGoods.getGoodsId());
		Map<Long, Integer> goodsIdMap = findSingleCount(goodsIdset);
		int userCount = goodsIdMap.get(goodsId);// 个人限购数量
		if (userCount == 0) {
			return true;
		}
		return false;*/
	}

	@Override
	public String verifyPayLimit(List<Long> sourcePayCountList, Long uId,
			List<Long> gIdList,List<Long> miaoshaIdList) throws PayLimitException {
		StringBuilder sb = new StringBuilder();
		Set<Long> goodsIdSet = new HashSet<Long>();
		int count = sourcePayCountList.size();// 单独赋变量。后有ForEach.
		// 提高代码执行效率，不用每次循环都去计算SIZE.
		if (count != gIdList.size()) {
			throw new PayLimitException(BaseException.PAYLIMIT_LIST_NOT_EQULAS);
		}

		for (int i = 0; i < count; i++) {
			Long gId = gIdList.get(i);
			Long sourceCount = sourcePayCountList.get(i);
			Long miaoshaId = miaoshaIdList.get(i);
			Long sucPayCount = qryPayCountByUIdAndGId(uId, gIdList.get(i),miaoshaId);
			Long resultPayCount = sourceCount - sucPayCount; // 可买量
			if (resultPayCount.longValue() < 0) {
				resultPayCount = 0L; // 如果超出购买量，则变更为0
			}
			sb.append(gId);// 商品ID
			sb.append("-");
			sb.append(sourceCount);// 限购数量
			sb.append("-");
			sb.append(resultPayCount);// 可买数量
			sb.append("|");

			// 构造第一次放入memcahce的限购信息。
			if (!goodsIdSet.contains(gId)) {
				goodsIdSet.add(gId);
			}
		}
		if (sb.length() > 0 && sb != null) {
			sb.deleteCharAt(sb.length() - 1);
			logger.info("+++++++++++validate Pay limit:" + sb.toString()+ "++++++++++++++");

		}

		findSingleCount(goodsIdSet);// 将限购信息放入memCache。预先放入，为支付成功后以减少支付成功与库的交互。

		return sb.toString();
	}

	/**
	 * 批量限购校验
	 */

	@Override
	public Map<Object, Object> verifyPayLimitList(TrxOrder trxOrder,List<TrxorderGoods> trxorderGoodsList) {

		boolean isPayPlimit = false; // 限购是否生效
		// 初例化list
		List<Long> limitToPayCountList = new ArrayList<Long>();
		List<Long> limitSingleCountCountList = new ArrayList<Long>();
		List<Long> limitGoodsIdList = new ArrayList<Long>();
		Set<Long> limitGoddsIdSet = new HashSet<Long>();
		Long userId = trxOrder.getUserId();
		Map<Long, Long> trxGoodsCountMap = new HashMap<Long, Long>(); // key=goodsId,Value=用户想要购买多少件
		Map<Object, Object> rspMap = new HashMap<Object, Object>();

		// TrxorderGoods中按GoodsId进行合并
		Long itemGoodsCountInit = 1L;
		for (TrxorderGoods itemTgGoods : trxorderGoodsList) {
			
			//只针对普通商品，秒杀商品个人限购量已经在payLimitService.isAllowBuyInPayLimit做过更新,不做处理
			if(3==itemTgGoods.getTrxRuleId().intValue()){
				continue;
			}
			
			Long itemTgGoodsId = itemTgGoods.getGoodsId();

			if (trxGoodsCountMap.containsKey(itemTgGoodsId)) {// 如果map已有此goodsId,则+1，itemGoodsCount为局部变量，否则会有itemGoodsCount覆盖问题
				Long itemGoodsCount = trxGoodsCountMap.get(itemTgGoodsId);
				itemGoodsCount += 1L;
				trxGoodsCountMap.put(itemTgGoodsId, itemGoodsCount);// 必须放if里面
			} else {
				trxGoodsCountMap.put(itemTgGoodsId, itemGoodsCountInit);
			}
		}

		Set<Long> goodsIdSet = trxGoodsCountMap.keySet();

		// 获取限购数量
		Map<Long, Integer> singleCountMap = findSingleCount(goodsIdSet);

		for (Long goodsId : singleCountMap.keySet()) {
			Long singleCount = Long.valueOf(singleCountMap.get(goodsId).toString());
			// 如果限购数量为0，则为不限购
			if (singleCount != null && singleCount.intValue() > 0) {
				Long trxGoodsCount = trxGoodsCountMap.get(goodsId);
				limitToPayCountList.add(trxGoodsCount);// 保存购买量
				limitSingleCountCountList.add(singleCount);// 保存限购数量
				limitGoodsIdList.add(goodsId);// 保存 商品ID
				limitGoddsIdSet.add(goodsId);

			}

		}

		int count = limitGoodsIdList.size();// 单独赋变量。后有ForEach.
		// 如果购买的商品含限购商品，则进下逻辑
		if (count > 0) {

			Long sucedPayCount = 0L;
			Map<Long, PayLimit> payLimitMap = qryPayCountListByUIdAndGId(userId, limitGoddsIdSet);// 获取个人已购买成功的数量

			for (int i = 0; i < count; i++) {
				Long gId = limitGoodsIdList.get(i);
				Long toPayCount = limitToPayCountList.get(i); // 需购买数量
				Long limitCount = limitSingleCountCountList.get(i); // 限购数量

				PayLimit payLimit = payLimitMap.get(gId);
				if (payLimit != null) {
					sucedPayCount = payLimit.getPayCount();
				}

				// 已购买成功数量

				// 如果个人购买上限小于购买数量。直接生效受限
				// 本次欲购买+已购买成功的数量>个人购买上限
				if (limitCount.longValue() < toPayCount.longValue()|| 
						toPayCount.longValue() + sucedPayCount.longValue() > limitCount.longValue()) {
					isPayPlimit = true;
					break;
				}

			}

			rspMap.put("payLimitMap", payLimitMap);
		}
		rspMap.put("isPayPlimit", isPayPlimit);
		rspMap.put("limitToPayCountList", limitToPayCountList);
		rspMap.put("limitSingleCountList", limitSingleCountCountList);
		rspMap.put("limitGoodsIdList", limitGoodsIdList);

		return rspMap;
	}

	@Override
	public Long qryPayCountByUIdAndGId(Long UId, Long GId,Long miaoshaId) {
		PayLimit payLimit = payLimitDao.findUseridAndGoodsid(UId, GId,miaoshaId);

		if (payLimit == null) {

			return 0L;
		}
		return payLimit.getPayCount();
	}

	public Map<Long, PayLimit> qryPayCountListByUIdAndGId(Long uId,
			Set<Long> gIdSet) {

		StringBuilder qryLimitSb = new StringBuilder();
		Map<Long, PayLimit> payLimitMap = new HashMap<Long, PayLimit>();
		for (Long goodsId : gIdSet) {
			qryLimitSb.append(goodsId);
			qryLimitSb.append(",");
		}
		qryLimitSb.deleteCharAt(qryLimitSb.length() - 1);
		List<PayLimit> payLimitList = payLimitDao.findUseridAndGoodsIdStr(uId,
				qryLimitSb.toString());

		if (payLimitList != null && payLimitList.size() != 0) {

			for (PayLimit payLimit : payLimitList) {
				Long goodsId = payLimit.getGoodsId();
				// Long payCount = payLimit.getPayCount();

				payLimitMap.put(goodsId, payLimit);
			}
		}
		return payLimitMap;
	}

	/**
	 * 跟据goodsId获取限购数量
	 * 
	 * @param goodsIdStr
	 * @return
	 */
	public Map<Long, Integer> findSingleCount(Set<Long> goodsIdSet) {
		Map<Long, Integer> singleCountMap = new HashMap<Long, Integer>();
		for (Long goodsId : goodsIdSet) {
			StringBuilder singleCountKeySb = new StringBuilder();
			singleCountKeySb.append(TrxConstant.SINGLE_COUNT_KEY);
			singleCountKeySb.append(goodsId);

			Integer singleCount = (Integer) memCacheService.get(singleCountKeySb.toString());

			// 如果缓存里没有，从库里取，然后再放一次
			if (singleCount == null) {
				Map<String, Object> singleCountQryMap = trxSoaService.getSingleCount(goodsId);

				singleCount = (Integer) singleCountQryMap.get("singleCount");

				memCacheService.set(singleCountKeySb.toString(), singleCount,TrxConstant.singleExpTimeout);

			}

			singleCountMap.put(goodsId, singleCount);

		}

		return singleCountMap;

	}

	/**
	 * 追加已做退款处理的限购信息及缓存处理
	 * @param totalOverRunRfdList
	 * @param singleOverRunRfdList
	 * @param payRequestId
	 */
	public void appendPostPayLimitDes(List<TrxorderGoods> totalOverRunRfdList,List<TrxorderGoods> singleOverRunRfdList,String payRequestId)  {

			
	
		List<Long> goodsIdListForTitle = new ArrayList<Long>();// 商品简称
		Map<Long, Integer> singleCountMap = new HashMap<Long, Integer>();// 个人超限Map
		Map<Long, String> goodsNameMap = new HashMap<Long, String>();// goodsNameMap
		Map<Long, String> payLimitMap = new LinkedHashMap<Long, String>();// 超限Map
		Map<Long, String> goodsMiaoshaMap = new HashMap<Long, String>();// goodsMiaoshaMap
		
	
		if (singleOverRunRfdList != null && singleOverRunRfdList.size() > 0) {
			Set<Long> goodsIdSet = new HashSet<Long>();

			for (TrxorderGoods singleItem : singleOverRunRfdList) {

				Long goodsId = singleItem.getGoodsId();
				goodsIdSet.add(goodsId);
				goodsIdListForTitle.add(goodsId);

				goodsNameMap.put(goodsId, singleItem.getGoodsName());
				if(singleItem.getTrxRuleId()==3){
				goodsMiaoshaMap.put(goodsId, singleItem.getExtend_info());
				}else{
					goodsMiaoshaMap.put(goodsId, "0");
				}
			}

			singleCountMap = findSingleCount(goodsIdSet);// 查询个人限购数量
		}
		
		
		if (totalOverRunRfdList != null && totalOverRunRfdList.size() > 0) {

			for (TrxorderGoods totaleItem : totalOverRunRfdList) {

				Long goodsId = totaleItem.getGoodsId();
				goodsIdListForTitle.add(goodsId);
				goodsNameMap.put(goodsId, totaleItem.getGoodsName());
				if(totaleItem.getTrxRuleId()==3){
					goodsMiaoshaMap.put(goodsId, totaleItem.getExtend_info());
					}else{
						goodsMiaoshaMap.put(goodsId, "0");
					}
			}

		}
		
		Map<Long, String> goodsTitleMap = trxSoaService.findGoodsTitle(goodsIdListForTitle); // 查询商品简称
		try {
			for (Long goodsId : goodsIdListForTitle) {

				if (!payLimitMap.containsKey(goodsId)) {// 组装限购信息，从个人限购开始
					String goodsTitle = goodsTitleMap.get(goodsId); // 商品简称
					String goodsName = goodsNameMap.get(goodsId);// 商品名字
					String miaoshaId = goodsMiaoshaMap.get(goodsId);
					StringBuilder payLimitItem = new StringBuilder();
					payLimitItem.append(goodsId);
					payLimitItem.append("|");
					payLimitItem.append(goodsName);
					payLimitItem.append("|");
					payLimitItem.append(goodsTitle);
					payLimitItem.append("|");
					if (singleCountMap.containsKey(goodsId)) {// 如果是个人超限
						Integer singleCount = singleCountMap.get(goodsId);// 个人超限数量
						payLimitItem.append(String.valueOf(singleCount));
						payLimitItem.append("|");
						payLimitItem.append("1");

					} else {// 总量超限
						payLimitItem.append("0");
						payLimitItem.append("|");
						payLimitItem.append("0");

					}
					payLimitItem.append("|");
					payLimitItem.append(miaoshaId);
					payLimitMap.put(goodsId, payLimitItem.toString());
				}

			}

			if (!payLimitMap.isEmpty()) {

				Set<Long> payLimitSet = payLimitMap.keySet();
				StringBuilder payLimit = new StringBuilder();
				for (Long payLimitGoodsId : payLimitSet) {
					payLimit.append(payLimitMap.get(payLimitGoodsId));
					payLimit.append("||");

				}
				String payLimitDes = payLimit.deleteCharAt(payLimit.length() - 2).toString();

				memCacheService.set(
						TrxConstant.PAY_LIMIT_DES_POST_PAY_CACHE_KEY+ payRequestId, payLimitDes,
						TrxConstant.PAY_LIMIT_DES_POST_PAY_CACHE_TIMEOUT);

			}
		} catch (Exception e) {
			logger.debug("PAY_LIMIT_DES_POST_PAY_CACHE_KEY++++" + payRequestId
					+ "++++++++++" + e);

		}
	}
	
	
	/**
	 * 支付前个人限购和总量限购统一接口
	 * @param orderInfo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String toPayLimitCount(OrderInfo orderInfo){
		Map<String,Integer> gIdCountMap = new HashMap<String, Integer>();//获取goodsid和本次购买数量
		Map<String,String> goodsNameMap = new HashMap<String,String>();//获取goodsName
		List<Long> goodsIdListForTitle = new ArrayList<Long>();// 商品简称
		String goodsIdStr = orderInfo.getGoodsId();
		String miaoshaStr = orderInfo.getMiaoshaStr();
		String goodsNameStr = orderInfo.getGoodsName();
		String[] goodsIdArray = goodsIdStr.split("\\|"); 
		String[] miaoshaIdArray = miaoshaStr.split("\\|");
		String[] goodsNameArray = goodsNameStr.split("\\|");
		int  goodsIdCount=goodsIdArray.length;//先计算长度，再放入for each ，不用每次在循环里都去计算
		for(int i=0;i<goodsIdCount;i++){
			Long miaoshaId = Long.valueOf(miaoshaIdArray[i]);
			Long gId = Long.valueOf(goodsIdArray[i]);
			String gmiaoId = gId+"-"+miaoshaId;
			if(!gIdCountMap.containsKey(gmiaoId)){
				gIdCountMap.put(gmiaoId,1);
				goodsNameMap.put(gmiaoId,goodsNameArray[i]);
				goodsIdListForTitle.add(gId);
			}else{
				Integer gidLong = gIdCountMap.get(gmiaoId);
				gidLong = gidLong+1;
				gIdCountMap.put(gmiaoId, gidLong);
			}
		}
		
		Map<Long, String> goodsTitleMap = trxSoaService.findGoodsTitle(goodsIdListForTitle); // 查询商品简称
		
		
		Iterator iter =  gIdCountMap.entrySet().iterator();
		StringBuilder sbStr = new StringBuilder();
		while(iter.hasNext()){//对本次购买商品循环判断
			
			Map.Entry<String,Integer> entry =(Map.Entry<String,Integer>)iter.next();
			String  gmiaoshaId = entry.getKey();  //
			String goodsId = gmiaoshaId.split("-")[0];//单个商品ID
			String miaoshaId = gmiaoshaId.split("-")[1];//单个秒杀ID
			Integer count = entry.getValue();//本次本商品购买数量
			
			Long maxCount = 0L;//总量限购销售量
			String isAvaliable = "";//是否上下架
			Long singleCount = 0L;//个人限购数量
			Long salesCount  = 0L;//已售出总量
			if(Long.valueOf(miaoshaId)==0){
			//sat总量限购数量和是否已下架
			StringBuilder singleCountKeySb = new StringBuilder();
			singleCountKeySb.append(TrxConstant.GOODS_MAXCOUNT_KEY);
			singleCountKeySb.append(goodsId);
			String goodsMaxCount = (String) memCacheService
			.get(singleCountKeySb.toString());
			if (goodsMaxCount == null) {
				Map<String, Object> map = goodsSoaDao.getmaxCountById(Long.valueOf(goodsId));
				goodsMaxCount = map.get("maxcount").toString() + "|"
				+ map.get("isavaliable").toString();
				memCacheService.set(singleCountKeySb.toString(), goodsMaxCount,
				TrxConstant.MaxCountExpTimeout);

			}
			String[] goodsMaxArray = goodsMaxCount.split("\\|");
			 maxCount = Long.valueOf(goodsMaxArray[0]);//总量限购数量
			  isAvaliable = goodsMaxArray[1];//是否下架
			//end总量限购数量和是否已下架
			Set<Long> setGoodsId = new HashSet<Long>();
			setGoodsId.add(Long.valueOf(goodsId));
			Map<Long, Integer> mapGoodsId = findSingleCount(setGoodsId);
			 singleCount = Long.valueOf(mapGoodsId.get(Long.valueOf(goodsId)));// 个人限购数量
			 
			 List<Map<String, Object>> salesCountMapList = goodsSoaDao.getGoodsProfileByGoodsid(String.valueOf(goodsId));// 商品已经购买量
				 salesCount = Long.valueOf(salesCountMapList.get(0).get("salesCount").toString());
			}else{
				//如果是秒杀商品，获取相应秒杀数据
				Map<String, Object> miaoshaMap = goodsSoaDao.findMiaoSha(Long.valueOf(miaoshaId));
				String isUsed = miaoshaMap.get("isUsed").toString();//秒杀是否作废
				 salesCount = Long.valueOf(miaoshaMap.get("mSaleCount").toString());//实际销量
				 Date endTime =(Date) miaoshaMap.get("mEndTime");//秒杀结束时间
				 Date startTime = (Date)miaoshaMap.get("mStartTime");//秒杀结束时间
				 singleCount = Long.valueOf(miaoshaMap.get("mSingleCount").toString());//秒杀个人限购
				 maxCount = Long.valueOf(miaoshaMap.get("mMaxCount").toString());//秒杀总量限购
				 int msStatus = Integer.valueOf(isUsed);
					boolean booDate = DateUtils.betweenBeginAndEnd(new Date(),startTime,endTime);
						 if(msStatus==1&&booDate){
							 isAvaliable = "1";
						 }else {
							 isAvaliable = "0";
						 }
			}
			
			
			
			
			
			
			Long userId = orderInfo.getUserId();//用户ID
			String goodsName = goodsNameMap.get(gmiaoshaId);//商品名称
			String goodsTitle = goodsTitleMap.get(Long.valueOf(goodsId));//商品简称
			Long allowBuyCount = 0L;
			StringBuilder sb1 = new StringBuilder();
			StringBuilder sb2 = new StringBuilder();
			StringBuilder sb3 = new StringBuilder();
			
			if ("0".equals(isAvaliable))
			{// 若商品已下架，则可购买数量直接置0
				sb1.append(goodsId.toString());
				sb1.append("|");
				sb1.append(goodsName);
				sb1.append("|");
				sb1.append(goodsTitle);
				sb1.append("|0|0|0|0|");
				sb1.append(miaoshaId);
				sbStr.append(sb1.toString());
				sbStr.append("||");
			} else
			{// 上架中。总量限购的限购数量一般为10W，加上总量限购是多用户并发（时效性差），故先判断个人限购商品。如果个人限购已超限，直接返回；否则继续判断总量限购。
				Long singleAllowBuyCount = -1L;// 设个处置，总量限购计算时做差异化判断
				if (singleCount > 0)
				{// 如果是个人限购商品

					singleAllowBuyCount = allowPayCount(singleCount, userId, Long.valueOf(goodsId),Long.valueOf(miaoshaId));// 允许购买的数量（不含下单传入的欲购买数量）

					if (singleAllowBuyCount-count<0)//如果可购数量<欲购买数量
					{
						sb2.append(goodsId.toString());
						sb2.append("|");
						sb2.append(goodsName);
						sb2.append("|");
						sb2.append(goodsTitle);
						sb2.append("|");
						sb2.append(singleCount);
						sb2.append("|");
						sb2.append(singleAllowBuyCount);
						sb2.append("|1|1|");
						sb2.append(miaoshaId);
						sb2.append("||");
						
					}
				}
				// 非个人限购或者个人限购没有超限

				
				allowBuyCount = maxCount - salesCount > 0 ?  maxCount - salesCount  : 0;// 总量限购如果为负，则归零

				if (allowBuyCount -count < 0)
				{
					sb3.append(goodsId.toString());
					sb3.append("|");
					sb3.append(goodsName);
					sb3.append("|");
					sb3.append(goodsTitle);
					sb3.append("|");
					sb3.append(maxCount);
					sb3.append("|");
					sb3.append(allowBuyCount);
					sb3.append("|1|0|");
					sb3.append(miaoshaId);
					sb3.append("||");
				}
				if(singleAllowBuyCount!=-1&&(allowBuyCount > singleAllowBuyCount)){
					sbStr.append(sb2);
				}else{
					sbStr.append(sb3);
				}
			}
		}
		String sbStrRetrun = "";
		if(!"".equals(sbStr.toString())){
			sbStrRetrun = sbStr.substring(0,sbStr.length()-2);
		}
		return sbStrRetrun;
	}
	
	
	/**
	 * 支付商品详情页个人限购和总量限购统一接口（临时购物车，正常购物车）
	 * @param orderInfo
	 * @return
	 */
	public String toPayLimitCountNew(String goodsId,String miaoshaId,String userId){

			Long maxCount = 0L;//总量限购销售量
			Long singleCount = 0L;//个人限购数量
			Long salesCount  = 0L;//已售出总量
			Long maCount = 0L;
			if(Long.valueOf(miaoshaId)==0){
			//sat总量限购数量
			StringBuilder singleCountKeySb = new StringBuilder();
			singleCountKeySb.append(TrxConstant.GOODS_MAXCOUNT_KEY);
			singleCountKeySb.append(goodsId);
			String goodsMaxCount = (String) memCacheService
			.get(singleCountKeySb.toString());
			if (goodsMaxCount == null) {
				Map<String, Object> map = goodsSoaDao.getmaxCountById(Long.valueOf(goodsId));
				goodsMaxCount = map.get("maxcount").toString() + "|"
				+ map.get("isavaliable").toString();
				memCacheService.set(singleCountKeySb.toString(), goodsMaxCount,
				TrxConstant.MaxCountExpTimeout);
			}
			String[] goodsMaxArray = goodsMaxCount.split("\\|");
			 maxCount = Long.valueOf(goodsMaxArray[0]);//总量限购数量
			  List<Map<String, Object>> salesCountMapList = goodsSoaDao.getGoodsProfileByGoodsid(String.valueOf(goodsId));// 商品已经购买量
				 salesCount = Long.valueOf(salesCountMapList.get(0).get("salesCount").toString());
				  maCount = maxCount - salesCount>0?maxCount - salesCount:0;
				  Set<Long> setGoodsId = new HashSet<Long>();
					setGoodsId.add(Long.valueOf(goodsId));
					Map<Long, Integer> mapGoodsId = findSingleCount(setGoodsId);
					 singleCount = Long.valueOf(mapGoodsId.get(Long.valueOf(goodsId)));// 个人限购数量
			//end总量限购数量和是否已下架
			  if("".equals(userId)){
				  Long returnCount = 0L;
				  if(singleCount>0){
				   returnCount = singleCount>maCount?maCount:singleCount;
				  }else{
					  returnCount = maCount;
				  }
				 return  returnCount.toString();
			  }else{
				  
						 if(singleCount==0){
							 return  (maCount)+"";
						 }else{
						Long singleAllowBuyCount = allowPayCount(singleCount, Long.valueOf(userId), Long.valueOf(goodsId),Long.valueOf(miaoshaId));
						return singleAllowBuyCount>(maCount)?(maCount)+"":singleAllowBuyCount.toString();
						 }
			  }
			}else{
				//如果是秒杀商品，获取相应秒杀数据
				Map<String, Object> miaoshaMap = goodsSoaDao.findMiaoSha(Long.valueOf(miaoshaId));
				 salesCount = Long.valueOf(miaoshaMap.get("mSaleCount").toString());//实际销量
				 singleCount = Long.valueOf(miaoshaMap.get("mSingleCount").toString());//秒杀个人限购
				 maxCount = Long.valueOf(miaoshaMap.get("mMaxCount").toString());//秒杀总量限购
				 maCount = maxCount - salesCount;
				 if("".equals(userId)){
					Long returnCount = singleCount>maCount?maCount:singleCount;
				return returnCount.toString();
				 }else{
					 Long sucPayCount = qryPayCountByUIdAndGId(Long.valueOf(userId),  Long.valueOf(goodsId),Long.valueOf(miaoshaId));
					 return (singleCount - sucPayCount)>maCount?maCount+"":(singleCount - sucPayCount)+"";
				 }
			}
		
	}
	
	
}
