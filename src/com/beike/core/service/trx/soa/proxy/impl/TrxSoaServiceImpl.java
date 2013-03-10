package com.beike.core.service.trx.soa.proxy.impl;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.bean.trx.OrderInfo;
import com.beike.common.bean.trx.TrxRequestData;
import com.beike.common.entity.coupon.TrxCoupon;
import com.beike.common.enums.trx.ReqChannel;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.TrxOrderException;
import com.beike.core.service.trx.coupon.TrxCouponService;
import com.beike.core.service.trx.soa.proxy.TrxSoaService;
import com.beike.dao.trx.soa.proxy.GoodsSoaDao;
import com.beike.dao.trx.soa.proxy.UserSoaDao;
import com.beike.entity.booking.BookingInfo;
import com.beike.service.booking.BookingService;
import com.beike.util.Amount;
import com.beike.util.DateUtils;
import com.beike.util.StringUtils;
import com.beike.util.TrxConstant;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;

/**
 * <p>
 * Title:交易与其它模块解耦Service
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: Sinobo
 * </p>
 * 
 * @date 2011-11-22 20:34:29
 * @author wenhua.cheng
 * @version 1.0
 */
@Service("trxSoaService")
public class TrxSoaServiceImpl implements TrxSoaService {

	@Autowired
	private UserSoaDao userSoaDao;

	@Autowired
	private GoodsSoaDao goodsSoaDao;
	@Autowired
	private BookingService bookingService;
	@Autowired
	private TrxCouponService trxCouponService;

	private static MemCacheService memCacheService = MemCacheServiceImpl.getInstance();
	
	private final Log logger = LogFactory
	.getLog(TrxSoaServiceImpl.class);
	@Override
	public Long findUserById(Long id) {

		Map<String, Object> userMap = userSoaDao.findById(id);

		if (userMap != null && !userMap.isEmpty()) {
			return (Long) userMap.get("userCount");
		}

		return 0L;

	}

	@Override
	public Map<String, Object> findMobileUserById(Long id) {

		Map<String, Object> userMap = userSoaDao.findMobileById(id);
		
	
		if (userMap != null && !userMap.isEmpty()) {
			
			return userMap;
		}

		return null;

	}
	
	/**
	 * 根据id查找用户手机号相关信息（主库查询）
	 * 
	 * @param id
	 *            主键
	 * @return Long
	 */
	public Map<String, Object> preQryInWtDBMobileUserById(Long id){


		Map<String, Object> userMap = userSoaDao.findMobileById(id);
		
	
		if (userMap != null && !userMap.isEmpty()) {
			
			return userMap;
		}

		return null;

	
		
	}

	/**
	 * 查用户
	 * 
	 * @param id
	 * @return
	 */

	public Map<String, Object> findUserInfoById(Long id) {

		Map<String, Object> userMap = userSoaDao.findUserInfoById(id);

		return userMap;

	}

	/**
	 * 查询商品个人限购信息。
	 * 
	 * @param goodsId
	 *            add by wenhua.cheng
	 * @return
	 */
	public Map<String, Object> getSingleCount(Long goodsId) {
		Map<String, Object> goodsMap = goodsSoaDao.getSingleCount(goodsId);
		return goodsMap;
	}

	/**
	 * 查询分店名称。
	 * 
	 * @param goodsId
	 *            add by renli.yu
	 * @return
	 */
	public Map<String,Object> getMerchantById(Long merchantId) {
		return goodsSoaDao.getMerchantById(merchantId);

	}
	/**
	 * 查询goodsTitle
	 * 
	 * @param goodsIdList
	 * @return
	 */
	public Map<Long, String> findGoodsTitle(List<Long> goodsIdList) {
		Map<Long, String> goodsTitleMap = new HashMap<Long, String>();
		for (Long goodsId : goodsIdList) {
			StringBuilder goodsTitleKeySb = new StringBuilder();
			goodsTitleKeySb.append(TrxConstant.GOODS_TITLE_CACHE_KEY);
			goodsTitleKeySb.append(goodsId);

			String goodsTitle = (String) memCacheService.get(goodsTitleKeySb.toString());

			// 如果缓存里没有，从库里取，然后再放一次
			if (goodsTitle == null || goodsTitle.length() == 0) {
				Map<String, Object> goodsTitleQryMap = goodsSoaDao.findGoodsTitleById(goodsId);
				if(goodsTitleQryMap!=null&&!goodsTitleQryMap.isEmpty()){
					goodsTitle = goodsTitleQryMap.get("goodsTitle") == null ? "": goodsTitleQryMap.get("goodsTitle").toString();
				}
				memCacheService.set(goodsTitleKeySb.toString(), goodsTitle,TrxConstant.GOODS_TITLE_CACHE_TIMEOUT);

			}

			goodsTitleMap.put(goodsId, goodsTitle);

		}

		return goodsTitleMap;

	}
	
	
	/**
	 * 查询goodsTitle和是否支持预定
	 * 
	 * @param goodsIdList
	 * @return
	 */
	public Map<Long, String> findGoodsTitleAndIsscheduled(List<Long> goodsIdList) {
		Map<Long, String> goodsTitleMap = new HashMap<Long, String>();
		for (Long goodsId : goodsIdList) {
			StringBuilder goodsTitleKeySb = new StringBuilder();
			goodsTitleKeySb.append(TrxConstant.GOODS_TITLE_CACHE_AND_SCHEDULED_KEY);
			goodsTitleKeySb.append(goodsId);

			String goodsTitle = "";
			String isScheduled ="";
			String goodsId1 = "";
			String str = (String) memCacheService.get(goodsTitleKeySb.toString());
			// 如果缓存里没有，从库里取，然后再放一次
			if (str == null || str.length() == 0) {
				Map<String, Object> goodsTitleQryMap = goodsSoaDao.findGoodsTitleById(goodsId);
				if(goodsTitleQryMap!=null&&!goodsTitleQryMap.isEmpty()){
					goodsTitle = goodsTitleQryMap.get("goodsTitle") == null ? "": goodsTitleQryMap.get("goodsTitle").toString();
					isScheduled = goodsTitleQryMap.get("isScheduled") == null ? "": goodsTitleQryMap.get("isScheduled").toString();
					goodsId1 = goodsTitleQryMap.get("goodsId") == null ? "": goodsTitleQryMap.get("goodsId").toString();
				}
				str = goodsTitle+"-"+isScheduled+"-"+goodsId1;
				memCacheService.set(goodsTitleKeySb.toString(),str,TrxConstant.GOODS_TITLE_CACHE_TIMEOUT);

			}

			goodsTitleMap.put(goodsId, str);

		}

		return goodsTitleMap;

	}

	/**
	 * 查询品牌
	 * 
	 * @param goodsIdList
	 * @return
	 */
	public Map<String, Object> findMerchantName(List<Long> goodsIdList) {

		return goodsSoaDao.findMerchantName(goodsIdList);

	}
	
	/**
	 * 查询品类
	 * 
	 * @param goodsId
	 * @return
	 */
	public Map<Long, String> findTagByIdName(Long goodsId){
		Map<Long, String> goodsTagMap = new HashMap<Long, String>();
		StringBuilder goodsTagKeySb = new StringBuilder();
		goodsTagKeySb.append(TrxConstant.GOODS_TAG_ID_NAME_KEY);
		goodsTagKeySb.append(goodsId);
		
		String tagStr = (String) memCacheService.get(goodsTagKeySb.toString());
		if (tagStr == null || tagStr.length() == 0) {
			 Map<String, Object> mapTag = goodsSoaDao.findTagByIdName(goodsId);
			 if(mapTag!=null){
				String tagId =  mapTag.get("tagId").toString();
				String tagName =  mapTag.get("tagName").toString();
				tagStr = tagId +"-"+tagName;
			 }
			 memCacheService.set(goodsTagKeySb.toString(),tagStr,TrxConstant.GOODS_TITLE_CACHE_TIMEOUT);
			 
		}
		
		goodsTagMap.put(goodsId,tagStr);
		
		
		return goodsTagMap;

	}
	
	/**
	 * 总量限购和上下架获取
	 * @param goodsId
	 * @return
	 */
	public Map<String, Object> getMaxCountAndIsAvbByIdInMem(Long goodsId){
		Map<String,Object> maxCountAndIsAvbMap =new HashMap<String,Object>();
		StringBuilder maxCountAndIsAvbKeySb = new StringBuilder();
		maxCountAndIsAvbKeySb.append(TrxConstant.GOODS_MAXCOUNT_KEY);
		maxCountAndIsAvbKeySb.append(goodsId);

		String goodsMaxCountAndIsAvbStr = (String) memCacheService.get(maxCountAndIsAvbKeySb.toString());
		if (goodsMaxCountAndIsAvbStr == null) {
			Map<String, Object> map = goodsSoaDao.getmaxCountById(goodsId);
			goodsMaxCountAndIsAvbStr = map.get("maxcount").toString() + "|"
					+ map.get("isavaliable").toString();
			memCacheService.set(maxCountAndIsAvbKeySb.toString(), goodsMaxCountAndIsAvbStr,
					TrxConstant.MaxCountExpTimeout);
			return map;
		}
		String[] goodsMaxCountAndIsAvbAry=goodsMaxCountAndIsAvbStr.split("\\|");
		
		maxCountAndIsAvbMap.put("maxcount",goodsMaxCountAndIsAvbAry[0]);//限购数量
		
		maxCountAndIsAvbMap.put("isavaliable",goodsMaxCountAndIsAvbAry[1]);//是否有效
		
		return maxCountAndIsAvbMap;
	}

	/**
	 * 查询商品logo4
	 * 
	 * @param goodsIdList
	 * @return
	 */
	public Map<Long, String> findGoodsDTPicUrl(List<Long> goodsIdList) {

		Map<Long, String> goodsTitleMap = new HashMap<Long, String>();
		for (Long goodsId : goodsIdList) {
			StringBuilder goodsTitleKeySb = new StringBuilder();
			goodsTitleKeySb.append(TrxConstant.GOODS_LOGO_CACHE_KEY);
			goodsTitleKeySb.append(goodsId);

			String goodsLogo = (String) memCacheService.get(goodsTitleKeySb
					.toString());

			// 如果缓存里没有，从库里取，然后再放一次
			if (goodsLogo == null || goodsLogo.length() == 0) {
				Map<String, Object> goodsTitleQryMap = goodsSoaDao
						.findGoodsPicUrlById(goodsId);

				goodsLogo = goodsTitleQryMap.get("logo") == null ? ""
						: goodsTitleQryMap.get("logo").toString();

				memCacheService.set(goodsTitleKeySb.toString(), goodsLogo,
						TrxConstant.GOODS_TITLE_CACHE_TIMEOUT);

			}

			goodsTitleMap.put(goodsId, goodsLogo);

		}

		return goodsTitleMap;
	}

	/**
	 * 交易请求数据转换
	 * 
	 * @param requestData
	 * @return
	 * @throws TrxOrderException
	 */
	@Override
	public OrderInfo tansTrxReqData(TrxRequestData requestData) throws TrxOrderException {
		
		ReqChannel reqChannel = requestData.getReqChannel();// 请求类型
		Long userId = requestData.getUserId();// 用户Id
		boolean isUseEndDateComLose=requestData.isUseEndDateComLose();//是否使用商品下线时间作为下单时间
		boolean isUseOutPayPrice=requestData.isUseOutPayPrice();//是否使用外部价格
		String outPayPrice=requestData.getPayPrice();//外部价格（对分销商有效）
		String goodsId = requestData.getGoodsId();//
		String[] goodsIdAry = goodsId.split("\\|");
		int goodsIdAryCount = goodsIdAry.length;
		String mobile =  requestData.getMobile();
		String couponId = requestData.getCouponId();
		StringBuilder startComLoseDateDefaultStrSb = new StringBuilder(); // 计算过期时间默认时间点(对快照方式进来的订单，无计算过期时间起始点开关限制)
		
		//计算过期时间默认起始点
		for(int i = 0; i < goodsIdAryCount; i++){
			startComLoseDateDefaultStrSb.append(DateUtils.getCurrentDateStr());
			startComLoseDateDefaultStrSb.append("|");
		}
		
	
		requestData.setStartComLoseDateStr(startComLoseDateDefaultStrSb.deleteCharAt(startComLoseDateDefaultStrSb.length()-1).toString());
		
		if (ReqChannel.MC.equals(reqChannel)||ReqChannel.WAP.equals(reqChannel)||ReqChannel.PARTNER.equals(reqChannel)) {//如果是非WEB端过来的下单请求，则交易内部获取商品信息（对此未彻底耦合，但减少对手机客户端的维护和升级以及后续测试的成本）

			String goodsCount = requestData.getGoodsCount();		
			
			if(ReqChannel.PARTNER.equals(reqChannel)){
				mobile = requestData.getMobile();
			}else{
				Map<String, Object> userMap = userSoaDao.findMobileById(userId);
				mobile = userMap.get("mobile").toString();
			}
			if(!StringUtils.isMobileNo(mobile)){
				throw new TrxOrderException(BaseException.MOBILE_ERROR);
			}
			StringBuilder goodsIdSb=new StringBuilder();//goodsIdSb
			StringBuilder goodsNameSb = new StringBuilder(); // 商品名称
			StringBuilder sourcePriceSb = new StringBuilder(); // 商品原价
			StringBuilder payPriceSb = new StringBuilder(); // 商品支付价
			StringBuilder rebatePriceSb = new StringBuilder(); // 商品返现价格
			StringBuilder dividePriceSb = new StringBuilder(); // 商品分成（结算）价格
			StringBuilder guestIdSb = new StringBuilder(); // 商家ID
			StringBuilder orderLoseAbsDateSb = new StringBuilder(); // 订单过期绝对时间（购买后几天过期）
			StringBuilder orderLoseDateSb = new StringBuilder(); // 订单过期时间点（某某时间点过期过期）
			StringBuilder isRefundSb = new StringBuilder(); // 是否支持自动退款
			StringBuilder isSendMerVouSb = new StringBuilder(); // 是否发送商家码（后升级为：凭证码发送类型(0:平台码；1：商家上传到平台的商家码；2：通过在线API发送的商家码))
			StringBuilder isAdvanceSb = new StringBuilder(); // 是否预付
			StringBuilder miaoshaStr = new StringBuilder(); // 秒杀ID
			StringBuilder startComLoseDateStrSb = new StringBuilder(); // 计算过期时间开始时间点
			StringBuilder payMpSb = new StringBuilder();//支付扩展信息
			StringBuilder trxBizType = new StringBuilder();//是否来源于点菜单
			String goodIdInShop="";//购物车中的goodsId串

			Set<String> goodsIdSet = new HashSet<String>();// goodsId去重过渡Set
			Map<String, String> goodsIdAndCountMap = new HashMap<String, String>();// goodsId和Count映射Map
			StringBuilder goodsIdQrySb = new StringBuilder();// 查询用goodId
		
			String[] goodsCountAry = goodsCount.split("\\|");
			int goodsCountAryCount = goodsCountAry.length;
			double rebateAmount = 0.0;
			for (int i = 0; i < goodsIdAryCount; i++) {
							
				String goodsIdItem = goodsIdAry[i];
				String goodsIdCountItem = goodsCountAry[i];
				goodsIdSet.add(goodsIdItem);// goodsId放入到set，用作去重后长度比较
				goodsIdAndCountMap.put(goodsIdItem, goodsIdCountItem);// 放入到映射Map
				goodsIdQrySb.append(goodsIdItem);
				goodsIdQrySb.append(",");
			}
			
			goodsIdQrySb.deleteCharAt(goodsIdQrySb.length() - 1);
		
			if (goodsIdSet.size() != goodsCountAryCount) {// 如果去重后的goodsIdSet和goodsCountAryCount长度不一致，抛异常

				throw new TrxOrderException(BaseException.GOODS_COUNT_ITEM_LENGTH_NOT_EQULAS);
			}
			List<Map<String, Object>> goodsInfoList = goodsSoaDao.findBatchGoodsInfoByIdStr(goodsIdQrySb.toString());// 商品信息List

			for (Map<String, Object> goodsInfoItemMap : goodsInfoList) {
				String goodsIdItem = goodsInfoItemMap.get("goodsId").toString();// 库里返回的GoodsId
				String goodsName = goodsInfoItemMap.get("goodsName").toString();
				if(goodsName!=null){
					try {
						goodsName = URLEncoder.encode(goodsName, "utf-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				String sourcePrice = goodsInfoItemMap.get("sourcePrice").toString();
				String payPrice = goodsInfoItemMap.get("payPrice").toString();
				String rebatePrice = goodsInfoItemMap.get("rebatePrice").toString();
				String dividePrice = goodsInfoItemMap.get("dividePrice").toString();
				String guestId = goodsInfoItemMap.get("guestId").toString();
				String orderLoseAbsDate = goodsInfoItemMap.get("orderLoseAbsDate").toString();
				String orderLoseDate =goodsInfoItemMap.get("orderLoseDate")==null?"null":goodsInfoItemMap.get("orderLoseDate").toString();
				String isRefund = goodsInfoItemMap.get("isRefund").toString();
				String isSendMerVou = goodsInfoItemMap.get("isSendMerVou").toString();
				String isAdvance = goodsInfoItemMap.get("isAdvance").toString();
				String isMenu = goodsInfoItemMap.get("isMenu").toString();//是否来源于团购下单商品
				if(!"0".equals(isMenu)){
					throw new TrxOrderException(BaseException.GOODS_DATE_TRANSFORM_ERROR);
				}
				String miaoshaId = "0";
				
				//过期时间下单时间点差异处理
				String startComLoseDateStr=isUseEndDateComLose?goodsInfoItemMap.get("endTime").toString():DateUtils.getCurrentDateStr();//TODO  fuck
				//外部价格转换
				payPrice=isUseOutPayPrice?outPayPrice:payPrice;
				int goodsCountItem = Integer.parseInt(goodsIdAndCountMap.get(goodsIdItem));// 从映射Map中找到goodsCount
				for (int i = 0; i < goodsCountItem; i++) {
					
					goodsIdSb.append(goodsIdItem);
					goodsIdSb.append("|");
					goodsNameSb.append(goodsName);
					goodsNameSb.append("|");
					sourcePriceSb.append(sourcePrice);
					sourcePriceSb.append("|");
					payPriceSb.append(payPrice);
					payPriceSb.append("|");
					rebatePriceSb.append(rebatePrice);
					rebatePriceSb.append("|");
					dividePriceSb.append(dividePrice);
					dividePriceSb.append("|");
					guestIdSb.append(guestId);
					guestIdSb.append("|");
					orderLoseAbsDateSb.append(orderLoseAbsDate);
					orderLoseAbsDateSb.append("|");
					orderLoseDateSb.append(orderLoseDate);
					orderLoseDateSb.append("|");
					isSendMerVouSb.append(isSendMerVou);
					isSendMerVouSb.append("|");	
					isRefundSb.append(isRefund);
					isRefundSb.append("|");
					isAdvanceSb.append(isAdvance);	
					isAdvanceSb.append("|");
					miaoshaStr.append(miaoshaId);
					miaoshaStr.append("|");
					trxBizType.append(isMenu);
					trxBizType.append("|");
					startComLoseDateStrSb.append(startComLoseDateStr);
					startComLoseDateStrSb.append("|");
					rebateAmount = rebateAmount + new BigDecimal(rebatePrice).doubleValue();//计算返现总额
				}

				
			}
			goodsIdSb.deleteCharAt(goodsIdSb.length()-1);
			goodsNameSb.deleteCharAt(goodsNameSb.length()-1);
			sourcePriceSb.deleteCharAt(sourcePriceSb.length()-1);
			payPriceSb.deleteCharAt(payPriceSb.length()-1);
			rebatePriceSb.deleteCharAt(rebatePriceSb.length()-1);
			dividePriceSb.deleteCharAt(dividePriceSb.length()-1);
			guestIdSb.deleteCharAt(guestIdSb.length()-1);
			orderLoseAbsDateSb.deleteCharAt(orderLoseAbsDateSb.length()-1);
			orderLoseDateSb.deleteCharAt(orderLoseDateSb.length()-1);
			isSendMerVouSb.deleteCharAt(isSendMerVouSb.length()-1);
			isRefundSb.deleteCharAt(isRefundSb.length()-1);
			isAdvanceSb.deleteCharAt(isAdvanceSb.length()-1);
			miaoshaStr.deleteCharAt(miaoshaStr.length()-1);
			trxBizType.deleteCharAt(trxBizType.length()-1);//是否来源于0：正常下单1：点餐下单2：电影票下单
			startComLoseDateStrSb.deleteCharAt(startComLoseDateStrSb.length()-1);
			rebateAmount = Amount.cutOff(rebateAmount, 2);//获得返现总金额
			
			goodIdInShop=goodsIdQrySb.toString().replace(",",".");//购物车中的goodsId串转换
			payMpSb.append(rebateAmount+"-" + mobile + "-"+goodIdInShop+ "-" + userId);
		
			//重新封装requestData
			requestData.setGoodsId(goodsIdSb.toString());
			requestData.setGoodsName(goodsNameSb.toString());
			requestData.setSourcePrice(sourcePriceSb.toString());
			requestData.setPayPrice(payPriceSb.toString());
			requestData.setRebatePrice(rebatePriceSb.toString());
			requestData.setDividePrice(dividePriceSb.toString());
			requestData.setGuestId(guestIdSb.toString());
			requestData.setOrderLoseAbsDate(orderLoseAbsDateSb.toString());
			logger.info("+++++++++orderLoseAbsDateSb.toString()="+orderLoseAbsDateSb.toString());
			requestData.setOrderLoseDate(orderLoseDateSb.toString());
			logger.info("+++++++++orderLoseDateSb.toString()="+orderLoseDateSb.toString());
			requestData.setIsRefund(isRefundSb.toString());
			requestData.setIsSendMerVou(isSendMerVouSb.toString());
			requestData.setIsRefund(isRefundSb.toString());
			requestData.setIsadvance(isAdvanceSb.toString());
			requestData.setMiaoshaId(miaoshaStr.toString());
			requestData.setTrxBizType(trxBizType.toString());
			requestData.setStartComLoseDateStr(startComLoseDateStrSb.toString());
			logger.info("+++++++++startComLoseDateStrSb.toString()="+startComLoseDateStrSb.toString());
			requestData.setPayMp(payMpSb.toString());
			requestData.setTrxType("NORMAL");
		
			
		}
		
		OrderInfo orderInfo = new OrderInfo();
		orderInfo.setUserId(requestData.getUserId());
		orderInfo.setProviderType(requestData.getProviderType());
		orderInfo.setProviderChannel(requestData.getProviderChannel());
		orderInfo.setGoodsName(requestData.getGoodsName());
		orderInfo.setGoodsId(requestData.getGoodsId());
		orderInfo.setSourcePrice(requestData.getSourcePrice());
		orderInfo.setPayPrice(requestData.getPayPrice());
		orderInfo.setRebatePrice(requestData.getRebatePrice());
		orderInfo.setDividePrice(requestData.getDividePrice());
		orderInfo.setGuestId(requestData.getGuestId());
		orderInfo.setOrderLoseAbsDate(requestData.getOrderLoseAbsDate());
		orderInfo.setOrderLoseDate(requestData.getOrderLoseDate());
		orderInfo.setIsRefund(requestData.getIsRefund());
		orderInfo.setIsSendMerVou(requestData.getIsSendMerVou());
		orderInfo.setIsadvance(requestData.getIsadvance());
		orderInfo.setDescription(requestData.getDescription());
		orderInfo.setExtendInfo(requestData.getPayMp());
		orderInfo.setTrxType(requestData.getTrxType());
		orderInfo.setPrizeId(requestData.getPrizeId());
		orderInfo.setMiaoshaStr(requestData.getMiaoshaId());
		//如果优惠券存在的情况下，设置优惠券ID
		if(!StringUtils.isEmpty(couponId)){
			Long couponIdLong = Long.parseLong(couponId);
			if(couponIdLong.intValue() > 0){
				TrxCoupon coupon = trxCouponService.queryTrxCouponByIdAndUserId(couponIdLong,userId);
				orderInfo.setTrxCoupon(coupon);
			}
		}
		orderInfo.setMobile(mobile);
		orderInfo.setClientIp(requestData.getClientIp());
		orderInfo.setStartComLostDateStr(requestData.getStartComLoseDateStr());  	
		orderInfo.setReqChannel(reqChannel);		//请求渠道
		orderInfo.setTrxBizType(requestData.getTrxBizType());//
		if("1".equals(requestData.getTrxBizType())){//点餐组装相关下单信息
			String menuInfo = requestData.getBizJson();
			String menuArray[] = menuInfo.split("\\|");
			orderInfo.setSubGuestId(menuArray[0]);
			orderInfo.setBizJson(menuArray[1]);
		}
		if("2".equals(requestData.getTrxBizType())){//网票网组装相关信息
			String filmInfo = requestData.getBizJson();
			orderInfo.setBizJson(filmInfo);
		}
		return orderInfo;
	}
	
	
	/**
	 * 销售总量更新
	 */
	public void updateSalesCount(Map<Long, Integer> map) {
		if (map == null || map.size() == 0) {
			throw new IllegalArgumentException(
					"goodId and salesCountStr not null");
		}
		for (Map.Entry<Long, Integer> mapEntry : map.entrySet()) {
			goodsSoaDao.updateSalesCount(mapEntry.getKey(), mapEntry.getValue() + "");
		}

	}
	
	public  Map<String,Object> findBytrxgoodsId(Long trxgoods_id){
		return goodsSoaDao.findBytrxgoodsId(trxgoods_id);
	}
	
	
	@Override
	public boolean processScheduled(Long trxGoodsId,Long trxorderId){
		boolean boo = false;
		try {
			Map<String, Object> map = goodsSoaDao.findBytrxgoodsId(trxGoodsId);
			logger.info("+++++++trxGoodsId:"+trxGoodsId+"+++scheduled++++map = " + map);
			Map<String, Object> trxOrder = userSoaDao.findBytrxorderId(trxorderId);
			if (map!=null&&!map.isEmpty()) {// 预定商品表中有预定记录做预定处理
				BookingInfo boInfo = bookingService.getBookingRecordByID((Long) map.get("id"), (Long) trxOrder.get("user_id"));
				boo = bookingService.cancelBookingForTrx(boInfo);
			}
			logger.info("+++++++trxGoodsId:"+trxGoodsId+"++++scheduled++++boo = " + boo);
		}catch(Exception e){
			logger.info("+++++++trxGoodsId:"+trxGoodsId+"++e:"+e);
			e.printStackTrace();
		}
		return boo;
	}
	
	/**
	 * 根据goodsId批量查询商品详情
	 * @param goodsId
	 * @return
	 */
	public List<Map<String, Object>> findGoodsList(String goodsIdStr) {
		return goodsSoaDao.findBatchGoodsInfoByIdStr(goodsIdStr);
	}
}
