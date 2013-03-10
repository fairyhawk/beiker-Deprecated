package com.beike.biz.service.hessian.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.beike.biz.service.hessian.TrxHessianService;
import com.beike.biz.service.hessian.TrxHessianServiceGateWay;
import com.beike.biz.service.hessian.VipHessianService;
import com.beike.common.bean.trx.OrderInfo;
import com.beike.common.bean.trx.TrxRequestData;
import com.beike.common.bean.trx.TrxResponseData;
import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.common.enums.trx.AccountType;
import com.beike.common.enums.trx.RefundHandleType;
import com.beike.common.enums.trx.RefundSourceType;
import com.beike.common.enums.trx.ReqChannel;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.PayLimitException;
import com.beike.common.exception.ShoppingCartException;
import com.beike.core.service.trx.limit.PayLimitService;
import com.beike.core.service.trx.soa.proxy.TrxSoaService;
import com.beike.service.shopcart.ShopCartService;
import com.beike.util.DateUtils;
import com.beike.util.EnumUtil;
import com.beike.util.StringUtils;

/**
 * @Title:TrxHessianServiceGateWay.java
 * @Package com.beike.biz.service.hessian
 * @Description: 交易对其它模块提供的交易hessian网关接口实现
 * @author wh.cheng@sinobogroup.com
 * @date May 6, 2011 7:58:06 PM
 * @version V1.0
 */

public class TrxHessianServiceGateWayImpl implements TrxHessianServiceGateWay {

	private TrxHessianService trxHessianService;

	private TrxSoaService trxSoaService;

	private PayLimitService payLimitService;
	
	private ShopCartService shopCartService;
	
	private VipHessianService vipHessianService;

	private static final Log logger = LogFactory.getLog(TrxHessianServiceGateWayImpl.class);

	public static final String STATUS_KEY = "status";
	public static final String RSPCODE_KEY = "rspCode";
	public static final String DESCRIPTION_KEY = "description";
	public static final String TRXORDER_ID_KEY = "trxOrderId";
	public static final String TRXORDER_REQ_ID_KEY = "trxOrderReqId";
	public static final String PAY_REQ_ID_KEY = "payRequestId";
	public static final String PAY_INFO_LINK_KEY = "payLinkInfo";// 网银支付链接
	public static final String PAY_LIMIT_DES_KEY = "payLimitDes";
	public static final String RESPCODE_VALUE_IN_SUCCESS = "1";
	public static final String RESPCODE_VALUE_IN_EXCEPTION = "-1";
	public static final String STATUS_VALUE_IN_EXCEPTION = "FAILED";
	public static final String STATUS_VALUE_IN_SUCCESS = "SUCCESS";

	/**
	 * 账户创建
	 * 
	 * @param userId
	 * @return
	 */
	public Map<String, String> createAccount(Map<String, String> sourceMap) {

		Map<String, String> rspMap = new HashMap<String, String>();

		try {
			printHessianEntryLog("createAccount", sourceMap);

			//请求渠道
			String reqChannel = sourceMap.get("reqChannel") ;
			String userId=sourceMap.get("userId");
			String description=sourceMap.get("description");
			TrxRequestData requestData = new TrxRequestData();
			// 封装请求参数
			requestData.setUserId(Long.parseLong(userId));
			requestData.setReqChannel(ReqChannel.valueOf(reqChannel));
			requestData.setDescription(description);
			
			trxHessianService.createAccount(requestData);
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_SUCCESS);
			rspMap.put(DESCRIPTION_KEY,description);

		} catch (BaseException e) {
			e.printStackTrace();
			rspMap.put(RSPCODE_KEY, String.valueOf(e.getCode()));
		} catch (Exception e2) {
			e2.printStackTrace();
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_EXCEPTION);
		}
		return rspMap;

	}

	/**
	 * 创建交易订单
	 * 
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> createTrxOrder(Map<String, String> sourceMap) {
		Map<String, String> rspMap = new HashMap<String, String>();
		String  payLimitDes="";
		try {
			printHessianEntryLog("createTrxOrder", sourceMap);
			String reqChannel=StringUtils.toTrim(sourceMap.get("reqChannel"));//请求来源
			String providerType = StringUtils.toTrim(sourceMap.get("providerType"));// 支付机构（若账户支付，则可为空）
			String providerChannel = StringUtils.toTrim(sourceMap.get("providerChannel"));// 支付通道（若账户支付，则可为空）
			String userId = sourceMap.get("userId");// 用户ID
			String mobile = sourceMap.get("mobile");// 用户手机号
			String clientIp = sourceMap.get("userIp");// 客户端IP
			String goodsId = sourceMap.get("goodsId");// 商品ID
			String guestId = StringUtils.toTrim(sourceMap.get("guestId"));// 商家ID
			String goodsName = StringUtils.toTrim(sourceMap.get("goodsName"));// 商品名称
			String sourcePrice = StringUtils.toTrim(sourceMap.get("sourcePrice"));// 商品原价
			String payPrice = StringUtils.toTrim(sourceMap.get("payPrice"));// 商品支付价
			String rebatePrice = StringUtils.toTrim(sourceMap.get("rebatePrice"));// 商品返现价格
			String dividePrice = StringUtils.toTrim(sourceMap.get("dividePrice"));// 商品分成（结算）价格
			String orderLoseAbsDate = StringUtils.toTrim(sourceMap.get("orderLoseAbsDate"));// 订单过期绝对时间（购买后几天过期）
			String orderLoseDate = StringUtils.toTrim(sourceMap.get("orderLoseDate"));// 订单过期时间点（某某时间点过期过期）
			String isRefund = StringUtils.toTrim(sourceMap.get("isRefund"));// 是否支持自动退款
			String isSendMerVou = StringUtils.toTrim(sourceMap.get("isSendMerVou"));// 是否发送商家码（后升级为：凭证码发送类型(0:平台码；1：商家上传到平台的商家码；2：通过在线API发送的商家码))
			String isadvance = StringUtils.toTrim(sourceMap.get("isadvance"));// 是否预付
			String miaoshaId = StringUtils.toTrim(sourceMap.get("miaoshaId"));// 秒杀ID
			String couponId = StringUtils.toTrim(sourceMap.get("couponId"));		//优惠券ID（为空或为0表示不使用优惠券）
			String description = StringUtils.toTrim(sourceMap.get("description"));// 订单描述
			String payMp = StringUtils.toTrim(sourceMap.get("payMp"));
			String trxType = StringUtils.toTrim(sourceMap.get("trxType")); // 常规交易/0元抽奖/秒杀/打折引擎区分
			String prizeId = StringUtils.toTrim(sourceMap.get("prizeId"));// 抽奖ID
			String goodsCount=StringUtils.toTrim(sourceMap.get("goodsCount"));//手机客户端专用
			String bizType = StringUtils.toTrim(sourceMap.get("bizType"));//是否来源于 0:正常下单，1：点餐单，2：电影
			String bizInfo = StringUtils.toTrim(sourceMap.get("bizInfo"));//点餐信息json
			TrxRequestData requestData = new TrxRequestData();
			requestData.setReqChannel(ReqChannel.valueOf(reqChannel));
			requestData.setProviderType(providerType);
			requestData.setProviderChannel(providerChannel);
			requestData.setUserId(Long.parseLong(userId));
			requestData.setGoodsId(goodsId);
			requestData.setGuestId(guestId);
			requestData.setGoodsName(goodsName);
			requestData.setSourcePrice(sourcePrice);
			requestData.setPayPrice(payPrice);
			requestData.setRebatePrice(rebatePrice);
			requestData.setDividePrice(dividePrice);
			requestData.setOrderLoseAbsDate(orderLoseAbsDate);
			requestData.setOrderLoseDate(orderLoseDate);
			requestData.setIsRefund(isRefund);
			requestData.setIsSendMerVou(isSendMerVou);
			requestData.setIsadvance(isadvance);
			requestData.setMiaoshaId(miaoshaId);
			requestData.setCouponId(couponId);
			requestData.setDescription(description);
			requestData.setPayMp(payMp);
			requestData.setTrxType(trxType);
			requestData.setPrizeId(prizeId);
			requestData.setGoodsCount(goodsCount);
			requestData.setMobile(mobile);
			requestData.setClientIp(clientIp);
			requestData.setTrxBizType(bizType);
			requestData.setBizJson(bizInfo);

			OrderInfo orderInfo = trxSoaService.tansTrxReqData(requestData);// 手机客户端来的请求会进行商品查询，对订单所需信息的进行封装
			if(!"1".equals(bizType)&&!"2".equals(bizType)){//如果来源是正常商品下单，做支付前限购
		    payLimitDes = payLimitService.toPayLimitCount(orderInfo);//个人限购+总量限购
			if(!"".equals(payLimitDes)){
				throw new PayLimitException(BaseException.PAY_LIMIT_ERROR);
			}
			}
			orderInfo=trxHessianService.noTscCreateTrxOrder(orderInfo);//调用创建订单Service
			if(!"1".equals(bizType)&&!"2".equals(bizType)){
			delShopCartPaySucNew(orderInfo,null,AccountType.VC);//账户支付成功删除购物车
			}
			
			//如果是点菜单商品，参数返回
			if("1".equals(bizType)){
				rspMap.put("menuInfo", orderInfo.getTrxId()+"|"+orderInfo.getTgList().get(0).getId()+"|"+orderInfo.getBizJson());
			}
			
			rspMap.put(STATUS_KEY, orderInfo.getPayResult());
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_SUCCESS);
			rspMap.put(PAY_INFO_LINK_KEY, orderInfo.getPayLinkInfo());
			rspMap.put(PAY_LIMIT_DES_KEY, payLimitDes);//限购表达式
			rspMap.put(TRXORDER_ID_KEY, String.valueOf(orderInfo.getTrxId()));//交易订单ID
			rspMap.put(TRXORDER_REQ_ID_KEY, orderInfo.getRequestId());//交易订单号
			rspMap.put(PAY_REQ_ID_KEY, orderInfo.getPayRequestId());//网银支付请求号
			rspMap.put(DESCRIPTION_KEY, description);//描述信息
			
		}catch(PayLimitException e){
			rspMap.put(STATUS_KEY, STATUS_VALUE_IN_EXCEPTION);
			rspMap.put(RSPCODE_KEY, String.valueOf(e.getCode()));
			rspMap.put(PAY_LIMIT_DES_KEY,payLimitDes);
		}catch (BaseException e) {
			rspMap.put(STATUS_KEY, STATUS_VALUE_IN_EXCEPTION);
			rspMap.put(RSPCODE_KEY, String.valueOf(e.getCode()));
			logger.error(e);
			e.printStackTrace();

		} catch (Exception e) {
			rspMap.put(STATUS_KEY, STATUS_VALUE_IN_EXCEPTION);
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_EXCEPTION);
			logger.info(e);
			e.printStackTrace();

		}

		return rspMap;

	}

	/**
	 * 账户查询
	 * 
	 * @param userId
	 * @param actType
	 * @return
	 */
	public Map<String, String> getActByUserId(Map<String, String> sourceMap) {

		Map<String, String> rspMap = new HashMap<String, String>();
		try {
			printHessianEntryLog("getActByUserId", sourceMap);
			String reqChannel = sourceMap.get("reqChannel");
			String userId = sourceMap.get("userId");
			String isSubAccountLose = sourceMap.get("isSubAccountLose");//是否查询子账户过期信息

			TrxRequestData requestData = new TrxRequestData(Long.parseLong(userId));
			requestData.setReqChannel(ReqChannel.valueOf(reqChannel));
			requestData.setIsSubAccountLose(isSubAccountLose);
			
			TrxResponseData trxResponseData = trxHessianService.getActByUserId(requestData);
			rspMap.put("userId", userId);
			rspMap.put("balance", String.valueOf(trxResponseData.getBalance()));
			rspMap.put(DESCRIPTION_KEY, "");
			rspMap.put("subAccountLose", trxResponseData.getSubAccountLose());
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_SUCCESS);
		} catch (BaseException e) {
			rspMap.put(RSPCODE_KEY, String.valueOf(e.getCode()));
			logger.error(e);
			e.printStackTrace();
		} catch (Exception e) {
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_EXCEPTION);
			logger.info(e);
			e.printStackTrace();
		}

		return rspMap;

	}
	
	/**
	 * 子账户查询
	 * 
	 * @param userId
	 * @param actType
	 * @return
	 */
	public Map<String, String> getSubAccountByUserId(Map<String, String> sourceMap) {

		Map<String, String> rspMap = new HashMap<String, String>();
		try {
			printHessianEntryLog("getSubAccountByUserId", sourceMap);
			String reqChannel = sourceMap.get("reqChannel");
			String userId = sourceMap.get("userId");//用户ID
			String trxAmount = sourceMap.get("trxAmount");//下单金额
			TrxRequestData requestData = new TrxRequestData(Long.parseLong(userId));
			requestData.setReqChannel(ReqChannel.valueOf(reqChannel));
			requestData.setTrxAmount(Double.valueOf(trxAmount).doubleValue());
			TrxResponseData trxResponseData = trxHessianService.getSubAccountByUserId(requestData);
			rspMap.put("userId", userId);
			rspMap.put("balance", String.valueOf(trxResponseData.getBalance()));
			rspMap.put("vmAmountListStr", trxResponseData.getVmAmountListStr());//子账户不找零需求。为了以后扩展优惠券,这里命名为子账户LIST集合
			rspMap.put(DESCRIPTION_KEY, "");
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_SUCCESS);
		} catch (BaseException e) {
			rspMap.put(RSPCODE_KEY, String.valueOf(e.getCode()));
			logger.error(e);
			e.printStackTrace();
		} catch (Exception e) {
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_EXCEPTION);
			logger.info(e);
			e.printStackTrace();
		}

		return rspMap;

	}

	/**
	 * 千品卡充值
	 * 
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> topupCard(Map<String, String> sourceMap) {

		Map<String, String> rspMap = new HashMap<String, String>();
		try {
			printHessianEntryLog("topupCard", sourceMap);

			String userId = sourceMap.get("userId");
			String cardNo = sourceMap.get("cardNo");
			String cardPwd = sourceMap.get("cardPwd");
			String reqChannel = sourceMap.get("reqChannel");
			String description = sourceMap.get("description");
			
			// 构造请求参数
			TrxRequestData requestData = new TrxRequestData();
			requestData.setUserId(Long.parseLong(userId));
			requestData.setCardNo(cardNo);
			requestData.setCardPwd(cardPwd);
			requestData.setReqChannel(ReqChannel.valueOf(reqChannel));
			requestData.setDescription(description);

			TrxResponseData trxResponseData = trxHessianService.topupCard(requestData);

			rspMap.put("userId", userId);
			rspMap.put("cardNo", cardNo);
			rspMap.put("cardValue", trxResponseData.getCardValue());
			rspMap.put("balance", String.valueOf(trxResponseData.getBalance()));
			rspMap.put(DESCRIPTION_KEY, description);
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_SUCCESS);
		} catch (BaseException e) {
			rspMap.put(RSPCODE_KEY, String.valueOf(e.getCode()));
			logger.error(e);
			e.printStackTrace();
		} catch (Exception e) {
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_EXCEPTION);
			logger.info(e);
			e.printStackTrace();
		}

		return rspMap;
	}
	
	/**
	 * 线上活动自动绑定优惠券
	 * @param sourceMap
	 * @return
	 */
	public Map<String,String> autoBindCoupon(Map<String, String> sourceMap) {
		Map<String, String> rspMap = new HashMap<String, String>();
		try {
			printHessianEntryLog("autoBindCoupon", sourceMap);
			String userId = StringUtils.toTrim(sourceMap.get("userId"));
			String csid = StringUtils.toTrim(sourceMap.get("csid"));
			String reqChannel = sourceMap.get("reqChannel");
			
			// 构造请求参数
			TrxRequestData requestData = new TrxRequestData();
			requestData.setUserId(Long.parseLong(userId));
			requestData.setCsid(csid);
			requestData.setReqChannel(ReqChannel.valueOf(reqChannel));
			trxHessianService.autoBindCoupon(requestData);
			
		} catch (BaseException e) {
			rspMap.put(RSPCODE_KEY, String.valueOf(e.getCode()));
			logger.error(e);
			e.printStackTrace();
		} catch (Exception e) {
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_EXCEPTION);
			logger.info(e);
			e.printStackTrace();
		}
	
		return rspMap;
	}
	
	/**
	 * 优惠券激活
	 * 
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> activateCoupon(Map<String, String> sourceMap) {

		Map<String, String> rspMap = new HashMap<String, String>();
		try {
			printHessianEntryLog("activateCoupon", sourceMap);

			String userId = sourceMap.get("userId");
			String couponPwd = sourceMap.get("couponPwd");
			String reqChannel = sourceMap.get("reqChannel");
			String description = sourceMap.get("description");
			
			// 构造请求参数
			TrxRequestData requestData = new TrxRequestData();
			requestData.setUserId(Long.parseLong(userId));
			requestData.setCouponPwd(couponPwd);
			requestData.setReqChannel(ReqChannel.valueOf(reqChannel));

			TrxResponseData trxResponseData = trxHessianService.activateCoupon(requestData);
			
			//优惠券3期TYPE=BINDING  其他为TOPON
			rspMap.put("type", trxResponseData.getCouponToponType());
			rspMap.put("couponValue", trxResponseData.getCouponValue());

			rspMap.put("userId", userId);
			rspMap.put("balance", String.valueOf(trxResponseData.getBalance()));
			rspMap.put("loseDate", trxResponseData.getLoseDate());
			
			//优惠券3期返回数据
			rspMap.put("couponName",trxResponseData.getCouponName());
			rspMap.put("limitInfo",trxResponseData.getCouponLimitInfo());
			rspMap.put("validDate",trxResponseData.getCouponvalidDate());
			rspMap.put(DESCRIPTION_KEY, description);
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_SUCCESS);
		} catch (BaseException e) {
			rspMap.put(RSPCODE_KEY, String.valueOf(e.getCode()));
			logger.error(e);
			e.printStackTrace();
		} catch (Exception e) {
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_EXCEPTION);
			logger.info(e);
			e.printStackTrace();
		}

		return rspMap;
	}

	/**
	 * 千品卡查询
	 * 
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> queryCardInfo(Map<String, String> sourceMap) {

		Map<String, String> rspMap = new HashMap<String, String>();
		try {
			printHessianEntryLog("queryCardInfo", sourceMap);
			//请求渠道
			String reqChannel = sourceMap.get("reqChannel");
			// 卡号
			String cardNo = sourceMap.get("cardNo");
			// 卡密
			String cardPwd = sourceMap.get("cardPwd");

			// 构造请求参数
			TrxRequestData requestData = new TrxRequestData();
			requestData.setCardNo(cardNo);
			requestData.setCardPwd(cardPwd);
			requestData.setReqChannel(ReqChannel.valueOf(reqChannel));

			TrxResponseData trxResponseData = trxHessianService.queryCardInfo(requestData);

			// 返回值
			rspMap.put("cardNo", cardNo); // 卡号
			rspMap.put("cardPwd", cardPwd);// 卡密
			rspMap.put("cardValue", trxResponseData.getCardValue()); // 卡面值
			rspMap.put("loseDate", trxResponseData.getLoseDate()); // 最后更新时间

			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_SUCCESS);
		} catch (BaseException e) {
			rspMap.put(RSPCODE_KEY, String.valueOf(e.getCode()));
			logger.error(e);
			e.printStackTrace();
		} catch (Exception e) {
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_EXCEPTION);
			logger.info(e);
			e.printStackTrace();
		}

		return rspMap;
	}

	/**
	 * 重发凭证码
	 * 
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> reSendVoucher(Map<String, String> sourceMap) {
		printHessianEntryLog("reSendVoucher", sourceMap);
		Map<String, String> rspMap = new HashMap<String, String>();
		try {
			ReqChannel reqChannel = ReqChannel.valueOf(sourceMap.get("reqChannel"));
			Long trxorderGoodsId = Long.valueOf(sourceMap.get("trxorderGoodsId"));
			Long userId=0L;
			if(!(null==sourceMap.get("userId") || "".equals(sourceMap.get("userId")))){
				 userId = Long.valueOf(sourceMap.get("userId"));
			}
			String mobile = sourceMap.get("mobile");
			String email = sourceMap.get("email");
			String description = sourceMap.get("description");
			TrxRequestData requestData = new TrxRequestData(reqChannel,trxorderGoodsId, userId, mobile, email, description);
			trxHessianService.reSendVoucher(requestData);
			rspMap.put(DESCRIPTION_KEY, description);
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_SUCCESS);
		} catch (BaseException e) {
			rspMap.put(RSPCODE_KEY, String.valueOf(e.getCode()));
			logger.error(e);
			e.printStackTrace();
		} catch (Exception e) {
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_EXCEPTION);
			logger.info(e);
			e.printStackTrace();
		}
		return rspMap;
	}

	/**
	 * 支付机构查询接口
	 * 
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> qryPayStauts(Map<String, String> sourceMap) {
		printHessianEntryLog("qryPayStauts", sourceMap);
		Map<String, String> rspMap = new HashMap<String, String>();
		try {
			// 请求渠道
			String reqChannel = sourceMap.get("reqChannel");
			String payRequestId = sourceMap.get("payRequestId");
			String providerType = sourceMap.get("providerType");
			TrxRequestData requestData = new TrxRequestData();
			requestData.setPayRequestId(payRequestId);
			requestData.setProviderType(providerType);
			requestData.setReqChannel(ReqChannel.valueOf(reqChannel));
			TrxResponseData resoponseData = trxHessianService.qryPayStauts(requestData);
			String payResult = resoponseData.getPayStatus();
			String proExternalId = resoponseData.getProExternalId();
			String confirmAmount = resoponseData.getSucTrxAmount();
			if ("SUCCESS".equals(payResult)) {

				rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_SUCCESS);
				rspMap.put("payRequestId", payRequestId);
				rspMap.put("proExternallId", proExternalId);
				rspMap.put("sucTrxAmount", confirmAmount);

			} else {
				throw new Exception();
			}
		}catch(Exception e){
			e.printStackTrace();
			  rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_EXCEPTION);
		}
		return rspMap;
	}

	/**
	 * 支付机构查询接口并补单
	 * 
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> complatePayStauts(Map<String, String> sourceMap) {
		printHessianEntryLog("complatePayStauts", sourceMap);
		Map<String, String>	rspTrxMap = new HashMap<String, String>();
		try {
			// 请求渠道
			String reqChannel = sourceMap.get("reqChannel");
			String payRequestId = sourceMap.get("payRequestId");
			String providerType = sourceMap.get("providerType");
			String createDate = sourceMap.get("createDate");
			TrxRequestData requestData = new TrxRequestData();
			requestData.setPayRequestId(payRequestId);
			requestData.setProviderType(providerType);
			requestData.setReqChannel(ReqChannel.valueOf(reqChannel));
			requestData.setCreateDate(createDate);
			TrxResponseData resoponseData = trxHessianService.qryPayStauts(requestData);
			String payResult = resoponseData.getPayStatus();
			String proExternalId = resoponseData.getProExternalId();
			String confirmAmount = resoponseData.getSucTrxAmount();
			if ("SUCCESS".equals(payResult)) {
				Map<String, String> sourceTrxMap = new HashMap<String, String>();
				sourceTrxMap.put("reqChannel", reqChannel);
				sourceTrxMap.put("payRequestId", payRequestId);
				sourceTrxMap.put("proExternallId", proExternalId);
				sourceTrxMap.put("sucTrxAmount", confirmAmount);
				sourceTrxMap.put("providerType", providerType);
				rspTrxMap=complateTrx(sourceTrxMap);

			} else {
				rspTrxMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_EXCEPTION);
			}
		}catch(BaseException e){
			logger.debug(e);
			e.printStackTrace();
			rspTrxMap.put(RSPCODE_KEY, String.valueOf(e));
		}catch(Exception e){
			logger.debug(e);
			e.printStackTrace();
			rspTrxMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_EXCEPTION);
		}
		return rspTrxMap;

	}

	/**
	 * 完成交易
	 * 
	 * @param sourceMap
	 * @return
	 * @throws BaseException
	 * @throws Exception
	 */
	public Map<String, String> complateTrx(Map<String, String> sourceMap) {

		Map<String, String> rspMap = new HashMap<String, String>();
		//请求渠道
		String reqChannel = sourceMap.get("reqChannel");
		String payRequestId = sourceMap.get("payRequestId");
		String proExternallId = sourceMap.get("proExternallId");
		String providerType = sourceMap.get("providerType");
		String sucTrxAmount = sourceMap.get("sucTrxAmount");

		try {
			printHessianEntryLog("complateTrx", sourceMap);
			TrxRequestData requestData = new TrxRequestData();
			requestData.setPayRequestId(payRequestId);
			requestData.setProviderType(providerType);
			requestData.setSucTrxAmount(sucTrxAmount);
			requestData.setProExternalId(proExternallId);
			requestData.setReqChannel(ReqChannel.valueOf(reqChannel));
			
			TrxResponseData responseData = trxHessianService.noTscCompleteTrx(requestData);

			delShopCartPaySuc(null, responseData, AccountType.CASH);// 网银支付成功删除购物车
			rspMap.put("payLimitDes", responseData.getPayLimitDes());
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_SUCCESS);
		} catch (BaseException e) {
			logger.info(e);
			e.printStackTrace();
			rspMap.put(RSPCODE_KEY, String.valueOf(e.getCode()));

		} catch (Exception e) {
			logger.info(e);
			e.printStackTrace();
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_EXCEPTION);

		}

		return rspMap;

	}
	

	/**
	 * 账户退款申请
	 * 
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> refundApplyToAct(Map<String, String> sourceMap) {
		Map<String, String> rspMap = new HashMap<String, String>();
		try {
			printHessianEntryLog("refundApplyToAct", sourceMap);
			//请求渠道
			String reqChannel = sourceMap.get("reqChannel"); 
			String trxOrderGoodsId = sourceMap.get("trxGoodsId"); // 商品订单id
			String operator = sourceMap.get("operator"); // 操作人
			String refundSourceTypeStr = sourceMap.get("refundSourceType"); // 退款来源类型
			String refundHandleTypeStr = sourceMap.get("refundHandleType"); // 退款处理类型
			String description = sourceMap.get("description"); // 描述
			RefundSourceType refundSourceType = EnumUtil.transStringToEnum(RefundSourceType.class, refundSourceTypeStr);
			RefundHandleType refundHandleType = EnumUtil.transStringToEnum(RefundHandleType.class, refundHandleTypeStr);
			
            if(trxOrderGoodsId.contains(",")){
                logger.info("++++++ refundApplyToAct batch  trxOrderGoodsId:"+trxOrderGoodsId);
                String [] trxGoodsArray= trxOrderGoodsId.split(",");
                if(trxGoodsArray!=null && trxGoodsArray.length>0){
                    for(String trGoodsId:trxGoodsArray){
                        try {
                            // 构造请求参数
                           TrxRequestData requestData = new TrxRequestData();
                           requestData.setTrxorderGoodsId(Long.parseLong(trGoodsId));
                           requestData.setOperator(operator);
                           requestData.setRefundSourceType(refundSourceType);
                           requestData.setRefundHandleType(refundHandleType);
                           requestData.setDescription(description);
                           requestData.setReqChannel(ReqChannel.valueOf(reqChannel));
                           // 调用账户退款申请
                           trxHessianService.refundApplyToAct(requestData);
                           rspMap.put(trGoodsId, RESPCODE_VALUE_IN_SUCCESS);
                        }catch (BaseException e) {
                            rspMap.put(trGoodsId, String.valueOf(e.getCode()));
                            logger.error("++++++ refundApplyToAct batch error trxOrderGoodsId:"+trxOrderGoodsId,e);
                            e.printStackTrace();
                        } catch (Exception e) {
                            rspMap.put(trGoodsId, RESPCODE_VALUE_IN_EXCEPTION);
                            logger.error("++++++ refundApplyToAct batch error trxOrderGoodsId:"+trxOrderGoodsId,e);
                            e.printStackTrace();
                        } 
                    }
                }
            }else{
             // 构造请求参数
                TrxRequestData requestData = new TrxRequestData();
                requestData.setTrxorderGoodsId(Long.parseLong(trxOrderGoodsId));
                requestData.setOperator(operator);
                requestData.setRefundSourceType(refundSourceType);
                requestData.setRefundHandleType(refundHandleType);
                requestData.setDescription(description);
                requestData.setReqChannel(ReqChannel.valueOf(reqChannel));

                // 调用账户退款申请
                trxHessianService.refundApplyToAct(requestData);
            }

			// 构造返回值
			rspMap.put(DESCRIPTION_KEY, description);
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_SUCCESS);
		} catch (BaseException e) {
			rspMap.put(RSPCODE_KEY, String.valueOf(e.getCode()));
			logger.error(e);
			e.printStackTrace();
		} catch (Exception e) {
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_EXCEPTION);
			logger.info(e);
			e.printStackTrace();
		}

		return rspMap;
	}

	/**
	 * 账户退款拒绝
	 * 
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> refundRefuseToAct(Map<String, String> sourceMap) {

		Map<String, String> rspMap = new HashMap<String, String>();
		try {
			printHessianEntryLog("refundRefuseToAct", sourceMap);
			//请求渠道
			String reqChannel = sourceMap.get("reqChannel"); 
			String trxOrderGoodsId = sourceMap.get("trxGoodsId"); // 商品订单id
			String operator = sourceMap.get("operator"); // 操作人
			String description = sourceMap.get("description"); // 描述

			// 构造请求参数
			TrxRequestData requestData = new TrxRequestData();
			requestData.setTrxorderGoodsId(Long.parseLong(trxOrderGoodsId));
			requestData.setOperator(operator);
			requestData.setDescription(description);
			requestData.setReqChannel(ReqChannel.valueOf(reqChannel));

			// 调用账户退款申请

			trxHessianService.refundRefuseToAct(requestData);

			// 构造返回值
			rspMap.put(DESCRIPTION_KEY,description);
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_SUCCESS);
		} catch (BaseException e) {
			rspMap.put(RSPCODE_KEY, String.valueOf(e.getCode()));
			logger.error(e);
			e.printStackTrace();
		} catch (Exception e) {
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_EXCEPTION);
			logger.info(e);
			e.printStackTrace();
		}

		return rspMap;
	}

	/**
	 * 退款到账户
	 * 
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> refundToAct(Map<String, String> sourceMap) {

		Map<String, String> rspMap = new HashMap<String, String>();
		try {
			printHessianEntryLog("refundToAct", sourceMap);
			//请求渠道
			String reqChannel = sourceMap.get("reqChannel"); // 商品订单id
			String trxOrderGoodsId = sourceMap.get("trxGoodsId"); // 商品订单id
			String operator = sourceMap.get("operator"); // 操作人
			String refundSourceTypeStr = sourceMap.get("refundSourceType"); // 退款来源类型
			String refundHandleTypeStr = sourceMap.get("refundHandleType"); // 退款处理类型
			String description = sourceMap.get("description"); // 描述
			RefundSourceType refundSourceType = EnumUtil.transStringToEnum(
					RefundSourceType.class, refundSourceTypeStr);
			RefundHandleType refundHandleType = EnumUtil.transStringToEnum(
					RefundHandleType.class, refundHandleTypeStr);

			// 构造请求参数
			TrxRequestData requestData = new TrxRequestData();
			requestData.setTrxorderGoodsId(Long.parseLong(trxOrderGoodsId));
			requestData.setOperator(operator);
			requestData.setRefundSourceType(refundSourceType);
			requestData.setRefundHandleType(refundHandleType);
			requestData.setDescription(description);
			requestData.setReqChannel(ReqChannel.valueOf(reqChannel));
			
			// 调用账户退款申请
			trxHessianService.refundToAct(requestData);

			// 构造返回值
			rspMap.put(DESCRIPTION_KEY, description);
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_SUCCESS);
		} catch (BaseException e) {
			rspMap.put(RSPCODE_KEY, String.valueOf(e.getCode()));
			logger.error(e);
			e.printStackTrace();
		} catch (Exception e) {
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_EXCEPTION);
			logger.info(e);
			e.printStackTrace();
		}

		return rspMap;
	}

	/**
	 * 银行卡退款申请
	 * 
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> refundApplyToBank(Map<String, String> sourceMap) {
		Map<String, String> rspMap = new HashMap<String, String>();
		try {
			printHessianEntryLog("refundApplyToBank", sourceMap);
			//请求渠道
			String reqChannel = sourceMap.get("reqChannel"); 
			String trxOrderGoodsId = sourceMap.get("trxGoodsId"); // 商品订单id
			String operator = sourceMap.get("operator"); // 操作人
			String refundSourceTypeStr = sourceMap.get("refundSourceType"); // 退款来源类型
			String refundHandleTypeStr = sourceMap.get("refundHandleType"); // 退款处理类型
			String description = sourceMap.get("description"); // 描述
			RefundSourceType refundSourceType = EnumUtil.transStringToEnum(RefundSourceType.class, refundSourceTypeStr);
			RefundHandleType refundHandleType = EnumUtil.transStringToEnum(RefundHandleType.class, refundHandleTypeStr);

			// 构造请求参数
			TrxRequestData requestData = new TrxRequestData();
			requestData.setTrxorderGoodsId(Long.parseLong(trxOrderGoodsId));
			requestData.setOperator(operator);
			requestData.setRefundSourceType(refundSourceType);
			requestData.setRefundHandleType(refundHandleType);
			requestData.setDescription(description);
			requestData.setReqChannel(ReqChannel.valueOf(reqChannel));

			// 调用账户退款申请
			trxHessianService.refundApplyToBank(requestData);

			// 构造返回值
			rspMap.put(DESCRIPTION_KEY, description);
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_SUCCESS);
		} catch (BaseException e) {
			rspMap.put(RSPCODE_KEY, String.valueOf(e.getCode()));
			logger.error(e);
			e.printStackTrace();
		} catch (Exception e) {
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_EXCEPTION);
			logger.info(e);
			e.printStackTrace();
		}

		return rspMap;
	}

	/**
	 * 银行卡退款拒绝
	 * 
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> refundRefuseToBank(Map<String, String> sourceMap) {

		Map<String, String> rspMap = new HashMap<String, String>();
		try {
			printHessianEntryLog("refundRefuseToBank", sourceMap);
			//请求渠道
			String reqChannel = sourceMap.get("reqChannel"); 
			String trxOrderGoodsId = sourceMap.get("trxGoodsId"); // 商品订单id
			String operator = sourceMap.get("operator"); // 操作人
			String description = sourceMap.get("description"); // 描述

			// 构造请求参数
			TrxRequestData requestData = new TrxRequestData();
			requestData.setTrxorderGoodsId(Long.parseLong(trxOrderGoodsId));
			requestData.setOperator(operator);
			requestData.setDescription(description);
			requestData.setReqChannel(ReqChannel.valueOf(reqChannel));

			// 调用账户退款申请

			trxHessianService.refundRefuseToBank(requestData);

			// 构造返回值
			rspMap.put(DESCRIPTION_KEY, description);
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_SUCCESS);
		} catch (BaseException e) {
			rspMap.put(RSPCODE_KEY, String.valueOf(e.getCode()));
			logger.error(e);
			e.printStackTrace();
		} catch (Exception e) {
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_EXCEPTION);
			logger.info(e);
			e.printStackTrace();
		}

		return rspMap;
	}

	/**
	 * 退款到银行卡
	 * 
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> refundToBank(Map<String, String> sourceMap) {

		Map<String, String> rspMap = new HashMap<String, String>();
		try {
			printHessianEntryLog("refundToBank", sourceMap);
			//请求渠道
			String reqChannel = sourceMap.get("reqChannel");
			String trxOrderGoodsId = sourceMap.get("trxGoodsId"); // 商品订单id
			String operator = sourceMap.get("operator"); // 操作人
			String description = sourceMap.get("description"); // 描述

			// 构造请求参数
			TrxRequestData requestData = new TrxRequestData();
			requestData.setTrxorderGoodsId(Long.parseLong(trxOrderGoodsId));
			requestData.setOperator(operator);
			requestData.setDescription(description);
			requestData.setReqChannel(ReqChannel.valueOf(reqChannel));

			// 调用账户退款申请
			trxHessianService.refundToBank(requestData);

			// 构造返回值
			rspMap.put(DESCRIPTION_KEY, description);
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_SUCCESS);
		} catch (BaseException e) {
			rspMap.put(RSPCODE_KEY, String.valueOf(e.getCode()));
			logger.error(e);
			e.printStackTrace();
		} catch (Exception e) {
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_EXCEPTION);
			logger.info(e);
			e.printStackTrace();
		}

		return rspMap;
	}

	/**
	 * 销毁凭证
	 * 
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> destoryVoucherByID(Map<String, String> sourceMap) {

		Map<String, String> rspMap = new HashMap<String, String>();
		try {
			printHessianEntryLog("destoryVoucherByID", sourceMap);
			//请求渠道
			String reqChannel = sourceMap.get("reqChannel"); 
			String voucherId = sourceMap.get("voucherId"); // 凭证id
			String description = sourceMap.get("description"); // 描述

			// 构造请求参数
			TrxRequestData requestData = new TrxRequestData();
			requestData.setVoucherId(voucherId);
			requestData.setReqChannel(ReqChannel.valueOf(reqChannel));
			requestData.setDescription(description);

			// 调用销毁凭证
			trxHessianService.destoryVoucherByID(requestData);

			// 构造返回值
			rspMap.put(DESCRIPTION_KEY, description);
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_SUCCESS);
		} catch (BaseException e) {
			rspMap.put(RSPCODE_KEY, String.valueOf(e.getCode()));
			logger.error(e);
			e.printStackTrace();
		} catch (Exception e) {
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_EXCEPTION);
			logger.info(e);
			e.printStackTrace();
		}

		return rspMap;
	}

	/**
	 * 根据客户号和凭证内容校验凭证及回收
	 * 
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> validateVoucher(Map<String, String> sourceMap) {

		Map<String, String> rspMap = new HashMap<String, String>();
		try {
			printHessianEntryLog("validateVoucher", sourceMap);
			//请求渠道
			String reqChannel = sourceMap.get("reqChannel"); 
			String guestId = sourceMap.get("guestId"); // 商家id
			String voucherCode = sourceMap.get("voucherCode"); // 凭证码
			String voucherVerifySource = sourceMap.get("voucherVerifySource");
			String subGuestId = sourceMap.get("subGuestId");
			String description = sourceMap.get("description"); // 描述

			// 构造请求参数
			TrxRequestData requestData = new TrxRequestData();
			requestData.setGuestId(guestId);
			requestData.setVoucherCode(voucherCode);
			requestData.setVoucherVerifySource(voucherVerifySource);
			requestData.setSubGuestId(subGuestId);
			requestData.setReqChannel(ReqChannel.valueOf(reqChannel));

			 trxHessianService.checkVoucher(requestData);
			
			// 构造返回值
			rspMap.put(DESCRIPTION_KEY, description);
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_SUCCESS);
		} catch (BaseException e) {
			rspMap.put(RSPCODE_KEY, String.valueOf(e.getCode()));
			logger.error(e);
			e.printStackTrace();
		} catch (Exception e) {
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_EXCEPTION);
			logger.info(e);
			e.printStackTrace();
		}

		return rspMap;
	}

	/**
	 * 根据凭证码和商家编号查询订单信息 
	 * @param sourceMap
	 * @return
	 */
	public Map<String,String> qryOrderByVouCodeAndGuestId(Map<String,String> sourceMap){
		Map<String, String> rspMap = new HashMap<String, String>();
		try {
			printHessianEntryLog("qryOrderByVoucherAndGuestId", sourceMap);
			//请求渠道
			String reqChannel = sourceMap.get("reqChannel"); 
			String guestId = sourceMap.get("guestId"); // 商家id
			String voucherCode = sourceMap.get("voucherCode"); // 凭证码

			// 构造请求参数
			TrxRequestData requestData = new TrxRequestData();
			requestData.setGuestId(guestId);
			requestData.setVoucherCode(voucherCode);
			requestData.setReqChannel(ReqChannel.valueOf(reqChannel));

			TrxResponseData responseData = trxHessianService.qryOrderByVouCodeAndGuestId(requestData);
			
			// 构造返回值
			rspMap.put("payPrice",responseData.getGoodsPayPrice());   //订单价格
			rspMap.put("goodsName",responseData.getGoodsName());	  //商品名称
			rspMap.put("goodsId",responseData.getGoodsId());		  //商品ID
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_SUCCESS);
		} catch (BaseException e) {
			rspMap.put(RSPCODE_KEY, String.valueOf(e.getCode()));
			logger.error(e);
			e.printStackTrace();
		} catch (Exception e) {
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_EXCEPTION);
			logger.info(e);
			e.printStackTrace();
		}

		return rspMap;
	}

	
	/**
	 * 提供商家的交易订单查询
	 * @return
	 */
	public Map<String,Object> qryTrxOrderGoodsForGuest(Map<String,String> sourceMap){
		Map<String, Object> rspMap = new HashMap<String, Object>();
		try {
			printHessianEntryLog("qryTrxOrderGoodsForGuest", sourceMap);
           
			Map<String, String> condition = trxHessianService.getQryTrxOrderGoodsForGuestCondition(sourceMap);

			Map<String,String> countMap = trxHessianService.qryTrxOrderGoodsForGuestCount(condition);
			rspMap.put("trxStatusCount",countMap);   //各状态的订单数量（TOTAL为总数量）

            if(null != countMap && !countMap.isEmpty()){
            	long totalCount = Long.parseLong(countMap.get("totalCount"));
            	if(totalCount>0){
            		  List<Map<String,Object>>  dataList = trxHessianService.qryTrxorderGoodsForGuest(condition);
            		  rspMap.put("data", dataList);//具体数据
            	}
            }
			rspMap.put(DESCRIPTION_KEY, "");
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_SUCCESS);
		} catch (Exception e) {
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_EXCEPTION);
			logger.info(e);
			e.printStackTrace();
		}
		return rspMap;
	}
	
	/**
	 * 根据商品订单id查询商品订单结算信息
	 * @param sourceMap
	 * @return
	 */
	public Map<String,Object> qryTrxOrderGoodsDetailForSettle(Map<String,String> sourceMap){
		Map<String, Object> rspMap = new HashMap<String, Object>();
		try {
			
			printHessianEntryLog("qryTrxOrderGoodsDetailForSettle", sourceMap);
//			String reqChannel = sourceMap.get("reqChannel"); 
			String trxOrderGoodsId = sourceMap.get("trxOrderGoodsId"); //商品订单id  以分号分隔
			
            List<Map<String,Object>> tgList = trxHessianService.qryTrxOrderGoodsDetailForSettle(trxOrderGoodsId.trim());
			rspMap.put("data", tgList);
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_SUCCESS);
			
		}catch (Exception e) {
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_EXCEPTION);
			logger.info(e);
			e.printStackTrace();
		}
		return rspMap;
		
	}
	
	/**
	 * 查询订单详情
	 * @param sourceMap
	 * @return
	 */
	public Map<String,Object> qryTgDetailForGuest(Map<String,String> sourceMap){
		Map<String, Object> rspMap = new HashMap<String, Object>();
		try {
			printHessianEntryLog("qryTgDetailForGuest", sourceMap);
			//请求渠道
			String reqChannel = sourceMap.get("reqChannel"); 
			String guestId = sourceMap.get("guestId"); // 商家id
			String trxOrderGoodsId = sourceMap.get("trxOrderGoodsId"); //商品订单id

			// 构造请求参数
			TrxRequestData requestData = new TrxRequestData();
			requestData.setGuestId(guestId);
			requestData.setTrxorderGoodsId(Long.parseLong(trxOrderGoodsId));
			requestData.setReqChannel(ReqChannel.valueOf(reqChannel));

			Map<String,Object> detaiMap = trxHessianService.qryOrderDetailByGuestIdAndTgId(requestData);
			
			rspMap.put("data", detaiMap);
			// 构造返回值
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_SUCCESS);
		} catch (BaseException e) {
			rspMap.put(RSPCODE_KEY, String.valueOf(e.getCode()));
			logger.error(e);
			e.printStackTrace();
		} catch (Exception e) {
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_EXCEPTION);
			logger.info(e);
			e.printStackTrace();
		}

		return rspMap;
	}
	
	
	/**
	 * 创建虚拟款项账户
	 * 
	 * @return
	 */
	public Map<String, String> createVmAccount(Map<String, String> sourceMap) {

		Map<String, String> rspMap = new HashMap<String, String>();
		try {
			printHessianEntryLog("createVmAccount", sourceMap);
			//请求渠道
			String reqChannel = sourceMap.get("reqChannel"); // 创建余额
			String balance = sourceMap.get("balance"); // 创建余额
			String vmAccountSortId = sourceMap.get("vmAccountSortId");// 类别Id
			String loseDateStr = sourceMap.get("loseDate");// 过期时间
			String costBear = sourceMap.get("costBear");// //成本承担方
			String isFund = sourceMap.get("isFund");// 是否有金
			String proposer = sourceMap.get("proposer");// 申请人
			String description = sourceMap.get("description");// 描述
			String operatorId = sourceMap.get("operatorId");// 操作人ID
			String isNotChange = sourceMap.get("isNotChange");//是否找零
			String notChangeRule = sourceMap.get("notChangeRule");//找零金额
			if(notChangeRule==null){
				notChangeRule = "";
			}
			String isRefund =sourceMap.get("isRefund");//是否退款，默认为1退款，优惠券时设置为0不退款
			if(isRefund==null || "".equals(isRefund)){
			    isRefund="1";
			}
			// 构造请求参数
			TrxRequestData requestData = new TrxRequestData();
			requestData.setBalance(balance);
			requestData.setVmAccountSortId(vmAccountSortId);
			requestData.setLoseDate(loseDateStr);
			requestData.setCostBear(costBear);
			requestData.setIsFund(isFund);
			requestData.setProposer(proposer);
			requestData.setDescription(description);
			requestData.setOperator(operatorId);
			requestData.setReqChannel(ReqChannel.valueOf(reqChannel));
			requestData.setIsNotChange(isNotChange);
			requestData.setNotChangeRule(notChangeRule);
			requestData.setIsRefund(isRefund);
			// 调用创建虚拟款项账户

			TrxResponseData trxResponseData = trxHessianService
					.createVmAccount(requestData);

			// 构造返回值
			rspMap.put("vmAccountId", trxResponseData.getVmAccountId().toString());
			rspMap.put(DESCRIPTION_KEY, description);
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_SUCCESS);
		} catch (BaseException e) {
			rspMap.put(RSPCODE_KEY, String.valueOf(e.getCode()));
			logger.error(e);
			e.printStackTrace();
		} catch (Exception e) {
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_EXCEPTION);
			logger.info(e);
			e.printStackTrace();
		}

		return rspMap;

	}

	/**
	 * 往虚拟款项账户追加余额
	 * 
	 * @return
	 */
	public Map<String, String> pursueVmAccount(Map<String, String> sourceMap) {

		Map<String, String> rspMap = new HashMap<String, String>();
		try {
			printHessianEntryLog("pursueVmAccount", sourceMap);
			//请求渠道
			String reqChannel = sourceMap.get("reqChannel");// 虚拟款项Id
			String vmAccountId = sourceMap.get("vmAccountId");// 虚拟款项Id
			String amount = sourceMap.get("amount"); // 追加余额
			String operatorId = sourceMap.get("operatorId");// 操作人ID
			String description = sourceMap.get("description");// 描述

			// 构造请求参数
			TrxRequestData requestData = new TrxRequestData();
			requestData.setVmAccountId(vmAccountId);
			requestData.setAmount(amount);
			requestData.setOperator(operatorId);
			requestData.setReqChannel(ReqChannel.valueOf(reqChannel));

			// 调用追加余额

			trxHessianService.pursueVmAccount(requestData);

			// 构造返回值
			rspMap.put(DESCRIPTION_KEY, description);
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_SUCCESS);
		} catch (BaseException e) {
			rspMap.put(RSPCODE_KEY, String.valueOf(e.getCode()));
			logger.error(e);
			e.printStackTrace();
		} catch (Exception e) {
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_EXCEPTION);
			logger.info(e);
			e.printStackTrace();
		}

		return rspMap;
	}

	/**
	 * 下发虚拟款项
	 * 
	 * @return
	 */
	public Map<String, String> dispatchVm(Map<String, String> sourceMap) {

		Map<String, String> rspMap = new HashMap<String, String>();
		try {
			printHessianEntryLog("dispatchVm", sourceMap);
			//请求渠道
			String reqChannel = StringUtils.toTrim(sourceMap.get("reqChannel"));
			String vmAccountId = StringUtils.toTrim(sourceMap.get("vmAccountId"));// 虚拟款项Id
			String amount = StringUtils.toTrim(sourceMap.get("amount")); // 下发金额
			String requestId = StringUtils.toTrim(sourceMap.get("requestId"));// 下发请求号
			String userId = StringUtils.toTrim(sourceMap.get("userId"));// 接收用户主键Id
			String operatorId = StringUtils.toTrim(sourceMap.get("operatorId"));// 操作人ID
			String description = StringUtils.toTrim(sourceMap.get("description"));// 描述
			String bizType = StringUtils.toTrim(sourceMap.get("bizType"));		//业务类型
			// 构造请求参数
			TrxRequestData requestData = new TrxRequestData();
			requestData.setVmAccountId(vmAccountId);
			requestData.setAmount(amount);
			requestData.setRequestId(requestId);
			requestData.setUserId(Long.parseLong(userId));
			requestData.setOperator(operatorId);
			requestData.setReqChannel(ReqChannel.valueOf(reqChannel));
			requestData.setDescription(description);
			requestData.setBizType(bizType);
			
			// 调用下发虚拟款项

			trxHessianService.dispatchVm(requestData);

			// 构造返回值
			rspMap.put("requestId", requestId);
			rspMap.put(DESCRIPTION_KEY, description);
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_SUCCESS);
		} catch (BaseException e) {
			rspMap.put(RSPCODE_KEY, String.valueOf(e.getCode()));
			logger.error(e);
			e.printStackTrace();
		} catch (Exception e) {
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_EXCEPTION);
			logger.info(e);
			e.printStackTrace();
		}

		return rspMap;

	}

	/**
	 * 查看购物车列表
	 * 
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> qryShoppingCart(Map<String, String> sourceMap) {

		Map<String, String> rspMap = new HashMap<String, String>();
		try {
			printHessianEntryLog("qryShoppingCart", sourceMap);

			String reqChannel = sourceMap.get("reqChannel");  //请求渠道
			String userId = sourceMap.get("userId");      //用户id
			String pageSize = sourceMap.get("pageSize");   // 页面大小
			String rowsOffset = sourceMap.get("rowsOffset");  //记录偏移量

			TrxRequestData requestData = new TrxRequestData(Long.parseLong(userId), Long.parseLong(pageSize), Long.parseLong(rowsOffset));
			requestData.setReqChannel(EnumUtil.transStringToEnum(ReqChannel.class,reqChannel));

			TrxResponseData trxResponseData = trxHessianService.qryShoppingCart(requestData);

			rspMap.put("userId", trxResponseData.getUserId());
			rspMap.put("shoppingCartId", trxResponseData.getShopCartId());
			rspMap.put("goodsId", trxResponseData.getGoodsId());
			rspMap.put("goodsCount", trxResponseData.getGoodsCount());
			rspMap.put("goodsName", trxResponseData.getGoodsName());
			rspMap.put("goodsTitle", trxResponseData.getGoodsTitle());
			rspMap.put("goodsDTPicUrl", trxResponseData.getGoodsDTPicUrl());
			rspMap.put("goodsPayPrice", trxResponseData.getGoodsPayPrice());
			rspMap.put("isAvailable", trxResponseData.getGoodsIsAvailable());
			rspMap.put("merchantName", trxResponseData.getMerchantName());
			rspMap.put("lastUpdateDate", trxResponseData.getGoodsLastUpdate());
			rspMap.put("allowBuyCount", trxResponseData.getAllowBuyCount());
			rspMap.put("totalRows", trxResponseData.getTotalRows().toString());
			rspMap.put("pageSize", pageSize);
			rspMap.put("rowsOffset", rowsOffset);

			rspMap.put(DESCRIPTION_KEY, "");
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_SUCCESS);

		} catch (Exception e) {
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_EXCEPTION);
			logger.error(e);
			e.printStackTrace();
		}

		return rspMap;

	}

	/**
	 * 添加购物车
	 * 
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> addShoppingCart(Map<String, String> sourceMap) {

		Map<String, String> rspMap = new HashMap<String, String>();
		try {
			printHessianEntryLog("qryShoppingCart", sourceMap);
			
			String reqChannel = sourceMap.get("reqChannel");  //请求渠道
			String isFirst  =	sourceMap.get("isFirst");  //首次添加购物车：商品已经存在标识
			String userId = sourceMap.get("userId");  //用户id
			String goodsId = sourceMap.get("goodsId"); //商品id
			String buyCount = sourceMap.get("goodsCount"); //商品数量
			String description = sourceMap.get("description"); //描述
			
			// 商品详情页 参数 isFirst，没有批量，如果此参数存在且值为1，此时如果购物车里已存在该商品，则返回错误码阻止用户重复操作
			if ("1".equals(isFirst))
			{
				int shopItemCount = shopCartService.getShopItemCount(Long.parseLong(userId), Long.parseLong(goodsId));
				if (shopItemCount > 0)
				{
					logger.debug("++++addShoppingCart: ++++reqChannel:" + reqChannel + "+++userId:" + userId + "+++goodsId:" + goodsId + "++buyCount:" + buyCount + "+++++++Exception:" + BaseException.SHOPPINGCART_FIRST_ADD_GOODS_EXIS + "+++++++++");
					throw new ShoppingCartException(BaseException.SHOPPINGCART_FIRST_ADD_GOODS_EXIS);
				}
			}
			
			TrxRequestData requestData = new TrxRequestData(Long.parseLong(userId), goodsId, buyCount);
			
			requestData.setReqChannel(ReqChannel.valueOf(reqChannel));
			requestData.setDescription(description);

			TrxResponseData trxResponseData = trxHessianService.addShoppingCart(requestData);


			rspMap.put("userId", trxResponseData.getUserId());
			rspMap.put("shoppingCartId", trxResponseData.getShopCartId());
			rspMap.put("goodsId", trxResponseData.getGoodsId());
			rspMap.put("goodsName", trxResponseData.getGoodsName());
			rspMap.put("goodsCount", trxResponseData.getGoodsCount());
			rspMap.put("goodsTitle", trxResponseData.getGoodsTitle());
			rspMap.put("goodsDTPicUrl", trxResponseData.getGoodsDTPicUrl());
			rspMap.put("goodsPayPrice", trxResponseData.getGoodsPayPrice());
			rspMap.put("merchantName", trxResponseData.getMerchantName());
			rspMap.put("lastUpdateDate", trxResponseData.getGoodsLastUpdate());

			rspMap.put(DESCRIPTION_KEY, description);
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_SUCCESS);

		} catch (BaseException e) {
			rspMap.put(RSPCODE_KEY, String.valueOf(e.getCode()));
			logger.error(e);
			e.printStackTrace();
		} catch (Exception e) {
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_EXCEPTION);
			logger.error(e);
			e.printStackTrace();
		}

		return rspMap;
	}

	/**
	 * 删除购物车
	 * 
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> delShoppingCartById(Map<String, String> sourceMap) {
		Map<String, String> rspMap = new HashMap<String, String>();

		try {
			printHessianEntryLog("delShoppingCartById", sourceMap);

			String reqChannel = sourceMap.get("reqChannel"); //请求渠道
			String shoppingCartId = sourceMap.get("shoppingCartId");  //购物车id
			String userId = sourceMap.get("userId");  //用户id
			String description = sourceMap.get("description");  //描述
			TrxRequestData requestData = new TrxRequestData(Long.parseLong(userId), shoppingCartId);
			requestData.setReqChannel(ReqChannel.valueOf(reqChannel));
			requestData.setDescription(description) ;

			TrxResponseData trxResponseData = trxHessianService.delShoppingCartById(requestData);

			rspMap.put("userId", trxResponseData.getUserId());
			rspMap.put("shoppingCartId", trxResponseData.getShopCartId());
			rspMap.put(DESCRIPTION_KEY,description);
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_SUCCESS);

		} catch (BaseException e) {
			rspMap.put(RSPCODE_KEY, String.valueOf(e.getCode()));
			logger.error(e);
			e.printStackTrace();
		} catch (Exception e) {
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_EXCEPTION);
			logger.error(e);
			e.printStackTrace();
		}

		return rspMap;
	}

	/**
	 * 查看商品订单列表
	 * 
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> qryTrxorderGoodsByUserId(
			Map<String, String> sourceMap) {
		printHessianEntryLog("qryTrxorderGoodsByUserId", sourceMap);
		Map<String, String> rspMap = new HashMap<String, String>();
		try {
			ReqChannel reqChannel = ReqChannel.valueOf(sourceMap.get("reqChannel"));
			String trxStatus = sourceMap.get("trxStatus");
			Long userId = Long.valueOf(sourceMap.get("userId"));
			Long pageSize = Long.valueOf(sourceMap.get("pageSize"));
			Long rowsOffset = Long.valueOf(sourceMap.get("rowsOffset"));
			
			TrxRequestData requestData = new TrxRequestData(reqChannel, userId,trxStatus, pageSize, rowsOffset);
			TrxResponseData responseData = trxHessianService.qryTrxorderGoodsByUserId(requestData);
			
			
			rspMap.put("userId", responseData.getUserId());
			rspMap.put("trxorderGoodsId", responseData.getTrxorderGoodsId());
			rspMap.put("goodsId", responseData.getGoodsId());
			rspMap.put("goodsName", responseData.getGoodsName());
			rspMap.put("goodsTitle", responseData.getGoodsTitle());
			rspMap.put("goodsPayPrice", responseData.getGoodsPayPrice());
			rspMap.put("goodsDTPicUrl", responseData.getGoodsDTPicUrl());
			rspMap.put("goodsTrxStatus", responseData.getGoodsTrxStatus());
			rspMap.put("createDate", responseData.getCreateDate());
			rspMap.put("loseDate", responseData.getLoseDate());
			rspMap.put("trxorderGoodsSn", responseData.getTrxorderGoodsSn());
			rspMap.put("usedDate", responseData.getUsedDate());
			rspMap.put("trxOrderId", responseData.getTrxOrderId());
			rspMap.put("voucherType", responseData.getVoucherType());
			rspMap.put("merchantName", responseData.getMerchantName());
			rspMap.put("lastUpdateDate", responseData.getLastUpdateDate() + "");
			rspMap.put("totalRows", responseData.getTotalRows() + "");
			rspMap.put("pageSize", responseData.getPageSize() + "");
			rspMap.put("rowsOffset", responseData.getRowsOffset() + "");
			rspMap.put("voucherCode", responseData.getVoucherCode());
			
			rspMap.put(DESCRIPTION_KEY, "");
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_SUCCESS);
		} catch (BaseException e) {
			rspMap.put(RSPCODE_KEY, String.valueOf(e.getCode()));
			logger.error(e);
			e.printStackTrace();
		} catch (Exception e) {
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_EXCEPTION);
			logger.error(e);
			e.printStackTrace();
		}
		return rspMap;
	}

	/**
	 * 查看凭证密码
	 * 
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> qryVoucherByTgId(Map<String, String> sourceMap) {
		printHessianEntryLog("qryVoucherByTgId", sourceMap);
		Map<String, String> rspMap = new HashMap<String, String>();
		try {
			ReqChannel reqChannel = ReqChannel.valueOf(sourceMap.get("reqChannel"));
			Long trxorderGoodsId = Long.valueOf(sourceMap.get("trxorderGoodsId"));
			Long userId = Long.valueOf(sourceMap.get("userId"));
			TrxRequestData requestData = new TrxRequestData(reqChannel,trxorderGoodsId, userId);
			TrxResponseData responseData = trxHessianService.qryVoucherByTgId(requestData);
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_SUCCESS);
			rspMap.put("userId", responseData.getUserId());
			rspMap.put("trxorderGoodsId", responseData.getTrxorderGoodsId());
			rspMap.put("trxorderGoodsSn", responseData.getTrxorderGoodsSn());
			rspMap.put("voucherCode", responseData.getVoucherCode());
			rspMap.put("voucherType", responseData.getVoucherType());
			rspMap.put(DESCRIPTION_KEY, responseData.getDescription());
		} catch (BaseException e) {
			rspMap.put(RSPCODE_KEY, String.valueOf(e.getCode()));
			logger.error(e);
			e.printStackTrace();
		} catch (Exception e) {
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_EXCEPTION);
			logger.error(e);
			e.printStackTrace();
		}
		return rspMap;
	}
	
	/**
	 * 我的钱包查询
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> queryPurse(Map<String, String> sourceMap) {
		printHessianEntryLog("queryPurse", sourceMap);
		Map<String, String> rspMap = new HashMap<String, String>();
		try {
			Long userId = Long.valueOf(sourceMap.get("userId"));
			String pageOffset = sourceMap.get("pageOffset");// 偏移量
			String isCachePage = sourceMap.get("isCachePage");// 是否缓存
			String uuid = sourceMap.get("uuid");// UUID值
			String pageSize = sourceMap.get("pageSize");// 页面大小
			ReqChannel reqChannel = ReqChannel.valueOf(sourceMap.get("reqChannel"));// 交易对外接口请求类型

			TrxRequestData requestData = new TrxRequestData(reqChannel, userId,Long.valueOf(pageSize),Long.valueOf(pageOffset),uuid,isCachePage);
			
			TrxResponseData responseData = trxHessianService.queryPurse(requestData);
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_SUCCESS);
			rspMap.put("createDate", responseData.getCreateDate());
			rspMap.put("actHistoryType",responseData.getStrActHistoryType());
			rspMap.put("trxAmount", responseData.getTrxAmount());
			rspMap.put("totalRows",responseData.getTotalRows().toString());
			rspMap.put("uuid", responseData.getUuid());
			rspMap.put("userId", responseData.getUserId().toString());
			rspMap.put("pageSize",responseData.getPageSize().toString());
			rspMap.put("pageOffset",responseData.getRowsOffset().toString());
			rspMap.put(DESCRIPTION_KEY, responseData.getDescription());
		} catch (BaseException e) {
			rspMap.put(RSPCODE_KEY, String.valueOf(e.getCode()));
			logger.error(e);
			e.printStackTrace();
		} catch (Exception e) {
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_EXCEPTION);
			logger.error(e);
			e.printStackTrace();
		}
		return rspMap;
	}
	
	

	@Override
	public Map<String, String> qryAllowBuyCountByUIdAndGId (Map<String, String> sourceMap)
	{
		
		Map<String, String> rspMap = new HashMap<String, String>();
		try {
			printHessianEntryLog("qryAllowBuyCountByUIdAndGId", sourceMap);
			ReqChannel.valueOf(sourceMap.get("reqChannel"));// 交易对外接口请求类型
			String userId = sourceMap.get("userId");      //用户id
			String goodsIdStr = sourceMap.get("goodsId");      //用户id
			Long goodsId =Long.parseLong(goodsIdStr) ;
  
			Set<Long> goodsIdset  = new  HashSet<Long>();
			goodsIdset.add(goodsId);
			Map<Long, Integer> singlecountMap =  payLimitService.findSingleCount(goodsIdset);
			int  singleCount =   singlecountMap.get(goodsId) ;
			
			Map<String, Object> maxcountMap =	trxSoaService.getMaxCountAndIsAvbByIdInMem(goodsId);
			Long maxCount = Long.parseLong(maxcountMap.get("maxcount").toString())   ;
			String isavaliable = maxcountMap.get("isavaliable").toString()   ;
			
			Map<String, String> result = shopCartService.checkAllowBuyCount(isavaliable, Long.parseLong(String.valueOf(singleCount)), maxCount, Long.parseLong(userId), goodsId);
		   Long allowBuyCount =	 Long.valueOf(result.get("allowBuyCount"));
		
			rspMap.put("userId",  userId);
			rspMap.put("allowBuyCount", String.valueOf(allowBuyCount));
			rspMap.put("goodsId", goodsIdStr);
			rspMap.put("allowBuyType", result.get("allowBuyType"));
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_SUCCESS);

		} catch (Exception e) {
			rspMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_EXCEPTION);
			logger.error(e);
			e.printStackTrace();
		}

		return rspMap;

	}
	

	/**
	 * 入口日志答应。后续会放到拦截器中
	 * 
	 * @param methord
	 */
	public static void printHessianEntryLog(String methordName,
			Map<String, String> sourceMap) {

		if (sourceMap != null) {

			for (Map.Entry<String, String> item : sourceMap.entrySet()) {
				logger.info("++++methordName:" + methordName + "->"
						+ item.getKey() + "++++" + item.getValue() + "++++++");
			}
		} else {

			logger.info("++++" + methordName + "->sourceMap is null+++++++");

		}

	}
	
	/**
	 * 入口日志答应。后续会放到拦截器中
	 * 
	 * @param methord
	 */
	public static void printHessianLog(String methordName,
			Map<String, Object> sourceMap) {

		if (sourceMap != null) {

			for (Map.Entry<String, Object> item : sourceMap.entrySet()) {
				logger.info("++++methordName:" + methordName + "->"
						+ item.getKey() + "++++" + item.getValue() + "++++++");
			}
		} else {

			logger.info("++++" + methordName + "->sourceMap is null+++++++");

		}

	}
	

	public TrxHessianService getTrxHessianService() {
		return trxHessianService;
	}

	public void setTrxHessianService(TrxHessianService trxHessianService) {
		this.trxHessianService = trxHessianService;
	}

	public TrxSoaService getTrxSoaService() {
		return trxSoaService;
	}

	public void setTrxSoaService(TrxSoaService trxSoaService) {
		this.trxSoaService = trxSoaService;
	}
	

	public PayLimitService getPayLimitService() {
		return payLimitService;
	}

	public void setPayLimitService(PayLimitService payLimitService) {
		this.payLimitService = payLimitService;
	}

	
	public ShopCartService getShopCartService() {
		return shopCartService;
	}

	public void setShopCartService(ShopCartService shopCartService) {
		this.shopCartService = shopCartService;
	}

	/**
	 * 账户支付成功或者网银支付成功后删除购购物车内部方法
	 * @param responseData
	 * @throws Exception 
	 * @throws ShoppingCartException
	 */
	public void delShopCartPaySuc(OrderInfo orderInfo,TrxResponseData responseData,AccountType payType) throws Exception
		 {
		try {
			
			String extendInfo = "";
			if (!(AccountType.VC.equals(payType) || AccountType.CASH.equals(payType))) {// 如果是非法调用

				return;
			}

			if (AccountType.VC.equals(payType)) {// 如果是账户支付
				String payResult = orderInfo.getPayResult();// 账户支付结果
				extendInfo = orderInfo.getExtendInfo();// 支付扩展信息

				if (!"SUCCESS".equals(payResult)) {// 如果账户支付不成功
					return;
				}

			} else {
				// 网银支付
				extendInfo = responseData.getExtendInfo();// 支付扩展信息

			}
			if(extendInfo!=null&&!"".equals(extendInfo)){
			String[] shopCartAry = extendInfo.split("-");
			String goodsId = shopCartAry[2];// 商品ID
			String userId = shopCartAry[3];
			goodsId = goodsId.replace(".", ",");
			shopCartService.delShoppingCartByIdPaySuc(userId, goodsId);
			}
		}catch (Exception e ){
			e.printStackTrace();
			logger.debug(e);
		}
	}
	
	/**
	 * 删除购物车（生吞异常）
	 * @param orderInfo
	 * @param responseData
	 * @param payType
	 */
	public void delShopCartPaySucNew(OrderInfo orderInfo,TrxResponseData responseData,AccountType payType){
		try {
			String extendInfo = "";
			List<TrxorderGoods> tgList = new ArrayList<TrxorderGoods>();
			if (!(AccountType.VC.equals(payType) || AccountType.CASH.equals(payType))) {// 如果是非法调用

				return;
			}

			if (AccountType.VC.equals(payType)) {// 如果是账户支付
				String payResult = orderInfo.getPayResult();// 账户支付结果
				extendInfo = orderInfo.getExtendInfo();// 支付扩展信息
				tgList = orderInfo.getTgList();
				if (!"SUCCESS".equals(payResult)) {// 如果账户支付不成功
					return;
				}

			} else {
				// 网银支付
				tgList = responseData.getTgList();
				extendInfo = responseData.getExtendInfo();// 支付扩展信息

			}

			String goodsIdStr = "";
			String miaoshaIdStr = "";
			if (tgList != null && tgList.size() > 0) {
				for (TrxorderGoods tg : tgList) {
					Long goodsId = tg.getGoodsId();
					Long trxRuleId = tg.getTrxRuleId();
					Long miaoshaId = 0L;
					if (3==trxRuleId.intValue()) {
						if (!"".equals(tg.getExtend_info())) {
							miaoshaId = Long.valueOf(tg.getExtend_info());
						}
					}
					if (miaoshaId == 0) {
						goodsIdStr = goodsIdStr + goodsId + ",";
					} else {
						miaoshaIdStr = miaoshaIdStr + miaoshaId + ",";
					}
				}

				if (goodsIdStr.contains(",")) {
					goodsIdStr = goodsIdStr.substring(0,goodsIdStr.length() - 1);
				}

				if (miaoshaIdStr.contains(",")) {
					miaoshaIdStr = miaoshaIdStr.substring(0, miaoshaIdStr.length() - 1);
				}

				String userId = "";// 用户UESRID
				if (extendInfo != null && !"".equals(extendInfo)) {
					String[] shopCartAry = extendInfo.split("-");
					userId = shopCartAry[3];
				}
				// 正常商品删除购物车
				if (!"".equals(goodsIdStr)) {
					shopCartService.delShoppingCartByIdPaySuc(userId,goodsIdStr);
				}
				// 秒杀商品删除购物车
				if (!"".equals(miaoshaIdStr)) {
					shopCartService.delShoppingCartByIdPaySucMiaosha(userId,miaoshaIdStr);
				}
			}
		}catch(Exception e ){
			logger.debug(e);
			e.printStackTrace();
			
		}
	}
	public VipHessianService getVipHessianService() {
		return vipHessianService;
}

	public void setVipHessianService(VipHessianService vipHessianService) {
		this.vipHessianService = vipHessianService;
	}

	@Override
	public void countMemberDaily(Date date) {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("date", date);
		printHessianLog("countMemberDaily", paramsMap);
		//查询服务器时间前一天的商家会员
		List<Map<String,Object>> lstTrxOrderInfo = vipHessianService.queryTrxOrderInfo(date);
		if(lstTrxOrderInfo!=null && lstTrxOrderInfo.size()>0){
			for(Map<String,Object> trxOrderInfo : lstTrxOrderInfo){
				try{
					//添加新会员
					vipHessianService.addNewVipStatistics(trxOrderInfo);
				}catch(Exception e){
					logger.info("fail to add vip:"+trxOrderInfo.get("user_id")+",guestId:"+trxOrderInfo.get("guest_id"));
				}
			}
			//按月更新各个商家的会员数
			vipHessianService.updateVipInfoForMonth(date);
		}
	}
	/**
     * 商品订单查询,根据ID。不查询行数
     * @param  sourceMap
     * @return Map<String, Object>
     */
    @Override
    public Map<String, Object> queryTrxGoodsByIds(Map<String, String> sourceMap) {
        try {
            printHessianEntryLog("queryTrxGoodsByIds", sourceMap);
            Map<String, Object> result=new HashMap<String, Object>();
            
            Map<String, String> condition = trxHessianService.getQueryTrxGoodsByIdsCondition(sourceMap);
            
            List<Map<String,Object>>  dataList = trxHessianService.getTrxGoodsByIds(condition);
            if(dataList==null){
                result.put("data", dataList);//具体数据
                result.put(RSPCODE_KEY, 1);//返回结果
                return result;
            }
            for(Map<String,Object> mapTmpset:dataList){
                mapTmpset.put("confirmDate", DateUtils.formatDate((Date)mapTmpset.get("confirmDate"), "yyyy-MM-dd HH:mm:ss"));
                mapTmpset.put("createDate", DateUtils.formatDate((Date)mapTmpset.get("createDate"), "yyyy-MM-dd HH:mm:ss"));
                
                mapTmpset.put("trxGoodsId", mapTmpset.get("trxGoodsId").toString());
                mapTmpset.put("trxGoodsSn", mapTmpset.get("trxGoodsSn").toString());
                mapTmpset.put("goodsName", mapTmpset.get("goodsName").toString());
                mapTmpset.put("voucherId", mapTmpset.get("voucherId").toString());
                mapTmpset.put("payPrice", mapTmpset.get("payPrice").toString());
                mapTmpset.put("trxStatus", mapTmpset.get("trxStatus").toString());
                mapTmpset.put("goodsId", mapTmpset.get("goodsId").toString());
                mapTmpset.put("trxOrderId", mapTmpset.get("trxOrderId").toString());
                mapTmpset.put("guestId", mapTmpset.get("guestId").toString());
                mapTmpset.put("isSendMerVou", mapTmpset.get("isSendMerVou").toString());
                mapTmpset.put("isFreeze", mapTmpset.get("isFreeze").toString());
                mapTmpset.put("userId", mapTmpset.get("userId").toString());
                mapTmpset.put("email", mapTmpset.get("email").toString());
                mapTmpset.put("mobile", mapTmpset.get("mobile").toString());
                mapTmpset.put("outRequestId", mapTmpset.get("outRequestId").toString());
                //mapTmpset.put("createDate", mapTmpset.get("createDate").toString());
                mapTmpset.put("trxOrderMobile", mapTmpset.get("trxOrderMobile").toString());
                //mapTmpset.put("confirmDate", mapTmpset.get("confirmDate").toString());
                mapTmpset.put("dividePrice", mapTmpset.get("dividePrice").toString());
                mapTmpset.put("merSettleStatus", mapTmpset.get("merSettleStatus").toString());
                mapTmpset.put("voucherCode", mapTmpset.get("voucherCode").toString());

            }
            
            
            /*List<Map<String,Object>>  dataList = new ArrayList<Map<String,Object>>();
            Map<String,Object> map3= new HashMap<String, Object>();
            map3.put("aa", "ssd");
            map3.put("ua278", new Date());
            map3.put("ta255", new Long(111));
           map3.put("ka244", new Double(33));
           //map3.put("ha23", new BigDecimal("55"));
          // map3.put("da23", new Timestamp(1000));
            dataList.add(map3);*/
            result.put("data", dataList);//具体数据
            
            result.put(RSPCODE_KEY, 1);//返回结果
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> result=new HashMap<String, Object>();
            result.put(RSPCODE_KEY, -1);
            return result;
        }
        
    }
	@Override
	public List<Map<String, Object>> queryIncomeStatistics(
			Map<String, Object> paramsMap) {
		printHessianLog("queryIncomeStatistics", paramsMap);
		return vipHessianService.queryIncomeStatistics(paramsMap);
	}

	@Override
	public List<Map<String, Object>> queryMemberCountMonth(Long guestId,String startDate,String endDate) {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("guestId", guestId);
		paramsMap.put("startDate", startDate);
		paramsMap.put("endDate", endDate);
		printHessianLog("queryMemberCountMonth", paramsMap);
		return vipHessianService.queryAllVipNumByDate(guestId, startDate, endDate);
	}

	@Override
	public List<Map<String, Object>> queryMemberEvaluation(
			Map<String, Object> paramsMap) {
		printHessianLog("queryMemberEvaluation", paramsMap);
		return vipHessianService.queryEvaluation(paramsMap, (Integer)paramsMap.get("curPage"), (Integer)paramsMap.get("pageSize"));
	}

	@Override
	public int queryMemberEvaluationCount(Map<String, Object> paramsMap) {
		printHessianLog("queryMemberEvaluationCount", paramsMap);
		return vipHessianService.queryEvaluateCount(paramsMap);
	}

	@Override
	public List<Map<String, Object>> queryOnlineMember(
			Map<String, Object> paramsMap) {
		printHessianLog("queryOnlineMember", paramsMap);
		return vipHessianService.queryVip(paramsMap);
	}

	@Override
	public int queryOnlineMemberCount(Map<String, Object> paramsMap) {
		printHessianLog("queryOnlineMemberCount", paramsMap);
		return vipHessianService.queryVipCount(paramsMap);
	}

	@Override
	public Map<String, Object> queryOnlineMemberDetail(
			Map<String, Object> paramsMap) {
		printHessianLog("queryOnlineMemberDetail", paramsMap);
		return vipHessianService.queryVipById(Long.parseLong(paramsMap.get("user_id").toString()),Long.parseLong(paramsMap.get("guest_id").toString()));
	}

	@Override
	public List<Map<String, Object>> queryVipProduct(
			Map<String, Object> paramsMap) {
		printHessianLog("queryVipProduct", paramsMap);
		return vipHessianService.queryVipProduct(paramsMap);
	}

	@Override
	public int queryVipProductCount(Map<String, Object> paramsMap) {
		printHessianLog("queryVipProductCount", paramsMap);
		return vipHessianService.queryVipProductCount(paramsMap);
	}

    @Override
	public Map<String, Object> queryMenuByOrderId(Long trxorderId,Long guestId) {
		// TODO Auto-generated method stub
    	Map<String, Object> paramsMap = new HashMap<String, Object>();
    	paramsMap.put("trxorderId", trxorderId);
    	paramsMap.put("guestId", guestId);
    	printHessianLog("queryMenuByOrderId", paramsMap);
		return vipHessianService.queryMenuByOrderId(trxorderId,guestId);
	}

	@Override
	public Map<String, Object> queryVipStatistics(Long guestId) {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("guestId", guestId);
		printHessianLog("queryVipStatistics", paramsMap);
		return vipHessianService.queryVipStatistics(guestId);
	}
	
	@Override
	public Map<String, Object> queryOnlineOrder(Map<String, Object> params) {
		printHessianLog("queryOnlineOrder", params);
		return vipHessianService.queryOnlineOrder(params);
	}
	
	//查询商家订单团购、网上分店的购买数量、消费数量
    @Override
    public Map<String, String> queryTrxGoodsCountForGuest(Map<String, String> sourceMap) {
        printHessianEntryLog("queryTrxGoodsCountForGuest", sourceMap);
        Map<String, String> resMap = new HashMap<String, String>();
        try {
            //查询商家购买数量
            Map<String, Object> buyCountMap = trxHessianService.queryTrxGoodsBuyCountForGuest(sourceMap);
            //查询商家消费数量
            Map<String, Object> usedCountMap = trxHessianService.queryTrxGoodsUsedCountForGuest(sourceMap);
            
            resMap.put("tuanBuyCount", buyCountMap.get("tuanBuyCount").toString());
            resMap.put("shopBuyCount", buyCountMap.get("shopBuyCount").toString());
            
            resMap.put("tuanUsedCount", usedCountMap.get("tuanUsedCount").toString());
            resMap.put("shopUsedCount", usedCountMap.get("shopUsedCount").toString());
            resMap.put(DESCRIPTION_KEY, sourceMap.get(DESCRIPTION_KEY));
            resMap.put(RSPCODE_KEY, STATUS_VALUE_IN_SUCCESS);
            
        } catch (Exception e) {
            resMap.put(RSPCODE_KEY, RESPCODE_VALUE_IN_EXCEPTION);
            e.printStackTrace();
        }
        
        
        return resMap;
        
    }
    
}
