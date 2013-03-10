package com.beike.core.service.trx.partner.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.biz.service.hessian.TrxHessianService;
import com.beike.common.bean.trx.OrderInfo;
import com.beike.common.bean.trx.TrxRequestData;
import com.beike.common.bean.trx.partner.Par1mallOrderGenerator;
import com.beike.common.bean.trx.partner.Par1mallOrderParam;
import com.beike.common.bean.trx.partner.PartnerInfo;
import com.beike.common.entity.trx.TrxOrder;
import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.common.entity.trx.Voucher;
import com.beike.common.enums.trx.NoticeStatus;
import com.beike.common.enums.trx.RefundHandleType;
import com.beike.common.enums.trx.RefundSourceType;
import com.beike.common.enums.trx.ReqChannel;
import com.beike.common.enums.trx.TrxStatus;
import com.beike.common.exception.BaseException;
import com.beike.core.service.trx.RefundService;
import com.beike.core.service.trx.TrxOrderService;
import com.beike.core.service.trx.TrxorderGoodsService;
import com.beike.core.service.trx.notice.NoticeService;
import com.beike.core.service.trx.partner.PartnerService;
import com.beike.core.service.trx.soa.proxy.TrxSoaService;
import com.beike.dao.trx.TrxOrderDao;
import com.beike.dao.trx.TrxorderGoodsDao;
import com.beike.dao.trx.VoucherDao;
import com.beike.util.Amount;
import com.beike.util.DateUtils;
import com.beike.util.StringUtils;
import com.beike.util.XmlUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;

/**   
 * @title: PartnerFor1mallService.java
 * @package com.beike.core.service.trx.partner.impl
 * @description: 一号店分销商
 * @author wangweijie  
 * @date 2012-11-14 下午05:53:02
 * @version v1.0   
 */
@Service("partnerFor1mallService")
public class PartnerFor1mallServiceImpl implements PartnerService {
	private static final Log logger = LogFactory.getLog(PartnerFor1mallServiceImpl.class);

	
	@Autowired
	private TrxOrderService trxOrderService;
	@Autowired
	private TrxHessianService trxHessianService;
	@Autowired
	private TrxorderGoodsService trxorderGoodsService;
	@Autowired
	private TrxSoaService trxSoaService;
	@Autowired
	private TrxorderGoodsDao trxorderGoodsDao;
	@Autowired
	private RefundService refundService;
	@Autowired
	private TrxOrderDao trxOrderDao;
	@Autowired
	private NoticeService noticeService;
	@Autowired
	private VoucherDao voucherDao;
	

	
	private final MemCacheService memCacheService = MemCacheServiceImpl.getInstance();

	@Override
	public String checkHmacData(String desStr, PartnerInfo partnerInfo,String reqIp) {
		String legalIP = StringUtils.toTrim(partnerInfo.getIp());
		boolean boo = checkIp(reqIp, legalIP);
		if (!boo) {
			logger.info("+++++++++++++1mall..IP++{ERROR}++++++++");
			return "10100";	//身份验证失败---IP受限制返回相关错误信息
		}
		if(!partnerInfo.getPartnerNo().equals(Par1mallOrderGenerator.PARTERNO_1MALL)){
			logger.info("+++++++++++++1mall..PARTERNO++{ERROR}++++++++");
			return "10100";	//身份验证失败---分销商编号错误
		}
		try {
			Map<String,String> paramMap = Par1mallOrderGenerator.getParamMap(desStr);
			String sign = StringUtils.toTrim(paramMap.get("sign"));
			if(!Par1mallOrderGenerator.checkSign(paramMap, partnerInfo.getSessianKey(), sign)){
				return "10100";	//签名验证失败
			}
		} catch (Exception e) {
			logger.error("handle 1mall error:",e);
			e.printStackTrace();
			return "10100";	//接口调用异常
		}
		return "";
		
	}

	@Override
	public String findVouInfoByActiveDate(String partnerNo, Date startTime,Date endTime, String trxStatusStr) {
		return null;
	}

	@Override
	public String findVouInfoByLastUpdateDate(String partnerNo, Date startTime,Date endTime, String trxStatus) {
		return null;
	}

	/**
	 * 查询消费券信息
	 */
	@Override
	public String findVouInfoByVouId(String format, String trxOrderIdStr) {
		return null;
	}

	@Override
	public String generateRspHmac(Object source, String keyValue) {
		return Par1mallOrderGenerator.packageResponseMsg((Par1mallOrderParam)source);
	}

	/**
	 * 消费券短信重新发送
	 */
	@Override
	public String noTscResendVoucher(Object ptop) throws Exception {
		Par1mallOrderParam par1mallparam = (Par1mallOrderParam)ptop;
		String outRequestId = par1mallparam.getOrderCode();
		Long trxOrderId = Long.parseLong(par1mallparam.getPartnerOrderCode());
		String voucherCode = StringUtils.toTrim(par1mallparam.getVoucherCode());
		List<Long> userIdList=par1mallparam.getUserIdList();
		
		String mobile=par1mallparam.getReceiveMobile();
		String requestTime=par1mallparam.getRequestTime();
		if (!StringUtils.isMobileNo(mobile)) {//手机号必须合法
			par1mallparam.setTotalCount(0);
			par1mallparam.setErrorCount(1);
			par1mallparam.setErrorCode("yhd.group.buy.order.inform.param_invalid");  //手机号非法
			par1mallparam.setErrorDes("手机号非法");
			par1mallparam.setPkInfo("receiveMobile");
			String resMessage = Par1mallOrderGenerator.packageResponseMsg(par1mallparam);
			logger.info("+++++++++1mall resend voucher {MOBILE INVALID}+++++mobile="+mobile+"++++++resMessage="+resMessage);
			return resMessage;		
		}
		if(!isValidDate(requestTime,"yyyy-MM-dd HH:mm:ss")){  //请求时间
			par1mallparam.setTotalCount(0);
			par1mallparam.setErrorCount(1);
			par1mallparam.setErrorCode("yhd.group.buy.order.inform.param_invalid");  //请求时间非法，请检查请求时间
			par1mallparam.setErrorDes("请求时间非法，请检查请求时间");
			par1mallparam.setPkInfo("requestTime");
			String resMessage = Par1mallOrderGenerator.packageResponseMsg(par1mallparam);
			logger.info("+++++++++1mall resend voucher {DATE INVALID}+++++requestTime="+requestTime+"++++++resMessage="+resMessage);
			return resMessage;	
		}
		
		TrxOrder trxOrder = trxOrderDao.findByUserIdOutRequestId(outRequestId,userIdList);
		if(null == trxOrder){
			logger.info("+++++++++userIdList="+userIdList+"++++outRequestId="+outRequestId+"++++++++");
			par1mallparam.setTotalCount(0);
			par1mallparam.setErrorCount(1);
			par1mallparam.setErrorCode("yhd.group.buy.vouchers.get_orderCode_invalid");
			par1mallparam.setPkInfo("orderCode");
			String resMessage = Par1mallOrderGenerator.packageResponseMsg(par1mallparam);
			logger.info("+++++++++1mall query voucher {ORDER NOT EXIST}+++++trxOrderId="+trxOrderId+"++++++resMessage="+resMessage);
			return resMessage;
		}else if(trxOrder.getId().intValue() != trxOrderId.intValue()){
			logger.info("+++++++++userIdList="+userIdList+"++++outRequestId="+outRequestId+"+++++qianpin.orderId="+trxOrder.getId()+"+++1mall.orderId="+trxOrderId+"+++");
			par1mallparam.setTotalCount(0);
			par1mallparam.setErrorCount(1);
			par1mallparam.setErrorCode("yhd.group.buy.vouchers.get_partnerOrderCode_invalid");  //订单号关联失败
			par1mallparam.setPkInfo("partnerOrderCode");
			String resMessage = Par1mallOrderGenerator.packageResponseMsg(par1mallparam);
			logger.info("+++++++++1mall query voucher {ORDER NOT EXIST}+++++trxOrderId="+trxOrderId+"++++++resMessage="+resMessage);
			return resMessage;
		}
		
		Voucher voucher = voucherDao.findByVoucherCode(voucherCode);
		if(null == voucher){
			par1mallparam.setTotalCount(0);
			par1mallparam.setErrorCount(1);
			par1mallparam.setErrorCode("yhd.group.buy.voucher.resend_voucherCode_invalid"); //	找不到相应的消费券，请检查voucherCode
			par1mallparam.setPkInfo("voucherCode");
			String resMessage = Par1mallOrderGenerator.packageResponseMsg(par1mallparam);
			logger.info("+++++++++1mall query voucher {VOUCHER NOT EXIST}+++++trxOrderId="+trxOrderId+"++++++resMessage="+resMessage);
			return resMessage;
		}

		TrxorderGoods tg  = trxorderGoodsDao.findByVoucherId(voucher.getId());
		if(null == tg || tg.getTrxorderId().intValue()!=trxOrder.getId()){
			
			par1mallparam.setTotalCount(0);
			par1mallparam.setErrorCount(1);
			par1mallparam.setErrorCode("yhd.group.buy.voucher.resend_partnerOrderCode_invalid"); //	订单关联错误，请检查 partnerOrderCode
			par1mallparam.setPkInfo("");
			String resMessage = Par1mallOrderGenerator.packageResponseMsg(par1mallparam);
			logger.info("+++++++++1mall resend voucher {ORDER NOT EXIST}+++++trxOrderId="+trxOrderId+"++++++resMessage="+resMessage);
			return resMessage;
		}
		
		if(tg.getTrxStatus() != null && tg.getTrxStatus()!=TrxStatus.SUCCESS ){
			logger.info("+++++++++++1mall resend voucher trx_goods_sn="+tg.getTrxGoodsSn()+" trx_status is no SUCCESS++++++");
			par1mallparam.setTotalCount(1);
			par1mallparam.setErrorCount(1);
			par1mallparam.setErrorCode("yhd.group.buy.voucher.resend_voucherCode_used"); ////消费券已使用不再发送短信
			String resMessage = Par1mallOrderGenerator.packageResponseMsg(par1mallparam);
			logger.info("+++++++++1mall resend voucher {VOUCHER IS USED}+++++trxOrderId="+trxOrderId+"++++++resMessage="+resMessage);
			return resMessage;
		}
		
		Integer times = (Integer)memCacheService.get("YHD_ORDER_RESENDTIMES_"+voucherCode);
		if(null == times || times<=0){
			memCacheService.set("YHD_ORDER_RESENDTIMES_"+voucherCode,1,Par1mallOrderGenerator.YHD_VOUCHERRESEND_TIMES_TIMEOUT);
		}else{
			if(times>=Par1mallOrderGenerator.YHD_VOUCHERRESEND_LIMIT){
				par1mallparam.setTotalCount(0);
				par1mallparam.setErrorCount(1);
				par1mallparam.setErrorCode("yhd.group.buy.voucher.resend_requestNumber_invalid");  //超过重发限制，请稍后再试!
				par1mallparam.setPkInfo("");
				String resMessage = Par1mallOrderGenerator.packageResponseMsg(par1mallparam);
				logger.info("+++++++++1mall resend voucher {RESEND LIMIT}+++++times="+times+"++++++resMessage="+resMessage);
				return resMessage;	
			}
			
			//设置发送次数限制
			memCacheService.set("YHD_ORDER_RESENDTIMES_"+voucherCode,times+1,Par1mallOrderGenerator.YHD_VOUCHERRESEND_TIMES_TIMEOUT);
		}
		TrxRequestData requestData = new TrxRequestData();
		requestData.setMobile(mobile);
		requestData.setTrxorderGoodsId(tg.getId());
		requestData.setVerifyForTg(false);//对于商品订单是否需要鉴权.上步已做鉴权操作。避免多次查询
		requestData.setReqChannel(ReqChannel.PARTNER);

		
		try {
			//本地测试，短信网关关闭，导致异常
			trxHessianService.reSendVoucher(requestData);
			par1mallparam.setTotalCount(1);
			par1mallparam.setErrorCount(0);
			
			//设置订单重发次数+1
		} catch (Exception e) {
			logger.error("+++++++1mall resend voucher+trxOrderId=trxOrder"+trxOrder.getId()+"++voucherCode="+voucher.getVoucherCode()+"+++",e);
			int code = -1;
			if(e instanceof BaseException){
				code = ((BaseException)e).getCode();
			}
			logger.info("+++++++++++1mall resend voucher exceptionCode="+code);
			
			if(BaseException.VOUCHER_SEND_TRXORDER_GOODS_STATUS_INVALID == code){
				
				par1mallparam.setErrorCode("yhd.group.buy.voucher.resend_voucherCode_used"); ////消费券已使用不再发送短信
			}else{
				par1mallparam.setErrorCode("resend.voucher.error"); //	//找不到相应的消费券，请检查voucherCode
				par1mallparam.setErrorDes("重发券失败");
			}
			par1mallparam.setTotalCount(1);
			par1mallparam.setErrorCount(1);
		}
		
		String resMsg = Par1mallOrderGenerator.packageResponseMsg(par1mallparam);
		logger.info("+++++++++1mall resend voucher response="+resMsg+"+++");
		return resMsg;
	}
	

	//退款接口
	@SuppressWarnings("unchecked")
	@Override
	public String processTrxOrder(Object ptop) throws Exception {

		Par1mallOrderParam par1mallparam =(Par1mallOrderParam) ptop;
		String yhdOrderId = par1mallparam.getOrderCode(); 		//一号店订单号
		String trxOrderId= par1mallparam.getPartnerOrderCode();	//千品订单号
		Double refundAmount = par1mallparam.getRefundAmount();	//退款金额
		String refundRequestTime = par1mallparam.getRefundRequestTime();	//退款时间
		logger.info("++++++1mall refund+++yhdOrderId="+yhdOrderId+"+++trxOrderId="+trxOrderId+"++refundAmount++"+refundAmount+"++refundRequestTime="+refundRequestTime+"++++");
		String reason = "一号店发起退款";
		//判断订单id是否是京东的订单
		TrxOrder trxorder = trxOrderService.findById(Long.parseLong(trxOrderId));
		List<Long> userIdList = par1mallparam.getUserIdList();
		if(null == trxorder || !userIdList.contains(trxorder.getUserId())){
			par1mallparam.setUpdateCount(0);
			par1mallparam.setErrorCount(1);
			par1mallparam.setErrorCode("yhd.group.buy.order.inform_orderCode_exist");
			par1mallparam.setPkInfo("");
			String resMessage = Par1mallOrderGenerator.packageResponseMsg(par1mallparam);
			logger.info("+++++++++1mall resend voucher {ORDERID NOT EXIST}+++++trxOrderId="+trxOrderId+"++++++resMessage="+resMessage);
			return resMessage;
		}
		//金额不匹配
		if(trxorder.getOrdAmount()!=refundAmount){
			par1mallparam.setUpdateCount(0);
			par1mallparam.setErrorCount(1);
			par1mallparam.setErrorCode("yhd.group.buy.order.inform_orderAmount_invalid");
			par1mallparam.setPkInfo("");
			String resMessage = Par1mallOrderGenerator.packageResponseMsg(par1mallparam);
			logger.info("+++++++++1mall resend voucher {ORDERID NOT EXIST}+++++trxOrderId="+trxOrderId+"++++++resMessage="+resMessage);
			return resMessage;
		}

		List<TrxorderGoods> trxOrderGoodsList = trxorderGoodsDao.findByTrxId(trxorder.getId());	
		
		int refundCount = 0;
		double refundAmountSuc = 0;
		for(TrxorderGoods trxOrderGoods : trxOrderGoodsList){
			if(trxOrderGoods.getTrxorderId().equals(Long.valueOf(trxOrderId))){
				//判断是否已经退款成功
				if(TrxStatus.REFUNDTOACT.equals(trxOrderGoods.getTrxStatus())){
					logger.info("++++++++++1mall refund+++++++trxGoodsSn="+trxOrderGoods.getTrxGoodsSn()+" has been refunded...");
					continue;
				}
				//对订单进行退款
				refundService.processApplyForRefundToAct(trxOrderGoods.getId(), "分销商",RefundSourceType.PARTNER, RefundHandleType.MANUAL,reason);
				refundService.processToAct(trxOrderGoods.getId(), "分销商",RefundSourceType.PARTNER, RefundHandleType.MANUAL,reason);
				logger.info("++++++++++1mall refund+++++++trxGoodsSn="+trxOrderGoods.getTrxGoodsSn()+" refunded success...");
				refundAmountSuc += trxOrderGoods.getPayPrice();
				refundCount++;
			}
		}
		
		//向一号店发送消费券退款确认
		try{
			TreeMap<String, String> paramMap = new TreeMap<String, String>();
			// 系统级参数设置（必须）
			paramMap.put("checkCode",par1mallparam.getCheckCode());
			paramMap.put("merchantId", Par1mallOrderGenerator.PARTERNO_1MALL);
			paramMap.put("erp", Par1mallOrderGenerator.ERP);
			paramMap.put("erpVer", Par1mallOrderGenerator.ERPVER);
			paramMap.put("format", Par1mallOrderGenerator.FORMAT_JSON);
			paramMap.put("method", "yhd.group.buy.refund.confirm");
			paramMap.put("ver", "1.0");
			
			paramMap.put("orderCode", yhdOrderId);		//1号商城订单号码
			paramMap.put("partnerOrderCode", trxOrderId);		//合作方订单号码
			paramMap.put("refundAmount", String.valueOf(refundAmountSuc));		//refundAmount
			paramMap.put("refundConfirmTime", DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));		//退款确认时间

			String requestMsg = Par1mallOrderGenerator.packageRequest(paramMap,par1mallparam.getSecretKey());
			logger.info("++++++++++1mall send request message++"+requestMsg);
			noticeService.createNotice(Par1mallOrderGenerator.PARTERNO_1MALL,"1MALL_NOTICE_URL",yhdOrderId,requestMsg,"yhd.group.buy.refund.confirm",NoticeStatus.INIT);
			
		}catch (Exception e) {
			logger.error("++++1mall {ERROR}+++method=yhd.group.buy.refund.confirm++send error+",e);
		}
		String resMsg ="";

		if(par1mallparam.getFormat().equals(Par1mallOrderGenerator.FORMAT_JSON)){
			JSONObject jsonMsg = new JSONObject();
			JSONObject responseObj = new JSONObject();
			responseObj.put("totalCount", refundCount);
			responseObj.put("errorCount", 0);
				
			JSONObject refundInfo = new JSONObject();
			refundInfo.put("refundCode", trxOrderId);
			refundInfo.put("refundAmount", String.valueOf(refundAmountSuc));
			responseObj.put("refundInfo", refundInfo);

			jsonMsg.put("response", responseObj);
			resMsg = jsonMsg.toJSONString();
		}else{
			Map<String, Object> responseMap = new LinkedHashMap<String, Object>();
			responseMap.put("totalCount", refundCount);
			responseMap.put("errorCount", 0);
			
			LinkedHashMap<String,String> refundInfo = new LinkedHashMap<String, String>(2);
			refundInfo.put("refundCode", trxOrderId);
			refundInfo.put("refundAmount", String.valueOf(refundAmountSuc));
			responseMap.put("refundInfo", refundInfo);
			
			Map<String,Object> resMap = new LinkedHashMap<String, Object>(1);
			resMap.put("response", responseMap);
		
			resMsg = XmlUtils.object2xml(Par1mallOrderGenerator.XML_HEAD,"response",resMap);
		}
		logger.info("++++++++++++1mall refund response="+resMsg);
		return resMsg;
	}

	/**
	 * 团购订单信息通知
	 */
	@Override
	public String synchroTrxOrder(Object ptopSource, String partnerNo)throws Exception {
		Par1mallOrderParam par1mallParam =(Par1mallOrderParam)ptopSource;
		//List<Long> userIdList=par1mallParam.getUserIdList();
		String orderCode=par1mallParam.getOrderCode();//获取1号店订单号
		Double orderAmount=par1mallParam.getOrderAmount();//1号店订单价格
		String clientIp=par1mallParam.getClientIp();
		int productNum=par1mallParam.getProductNum();//购买数量
		Long outerGroupId = par1mallParam.getOuterGroupId();  //千品商品ID
		Double productPrice = par1mallParam.getProductPrice();	//商品价格
		List<Long> userIdList = par1mallParam.getUserIdList();

		if(productNum <=0){
			logger.info("++++++++++++++productNum="+productNum+"+++++++++");
			par1mallParam.setUpdateCount(0);
			par1mallParam.setErrorCount(1);
			par1mallParam.setErrorCode("number.lessthan.1");
			par1mallParam.setErrorDes("购买数量不能少于1，请检查productNum");
			par1mallParam.setPkInfo("productNum");
			String resMessage = Par1mallOrderGenerator.packageResponseMsg(par1mallParam);
			logger.info("+++++++++1mall order sync {PRODUCTNUM<1}+++++productNum="+productNum+"++++++resMessage="+resMessage);
			return resMessage;
		}
		
		//检查商品是否存在
		List<Map<String,Object>> goodsList = trxSoaService.findGoodsList(String.valueOf(outerGroupId));
		if(null == goodsList || null == goodsList.get(0)){
			par1mallParam.setUpdateCount(0);
			par1mallParam.setErrorCount(1);
			par1mallParam.setErrorCode("yhd.group.buy.order.inform_outerGroupId_invalid"); //找不到商品，请检查outerGroupId
			par1mallParam.setPkInfo("outerGroupId");
			String resMessage = Par1mallOrderGenerator.packageResponseMsg(par1mallParam);
			logger.info("+++++++++1mall order sync {GOODSID NO EXIST}+++++outerGroupId="+outerGroupId+"++++++resMessage="+resMessage);
			return resMessage;
		}
		
		//检查商品金额是否一致
		BigDecimal payPrice = (BigDecimal)goodsList.get(0).get("payPrice");
		Double goodsPayPrice = new Double(0);
		if(null != payPrice){
			goodsPayPrice = payPrice.doubleValue();
		}
		
		if(null == goodsPayPrice || goodsPayPrice.compareTo(productPrice) != 0){
			logger.info("+++++++++++++goodsPayPrice="+goodsPayPrice +"+++productPrice="+productPrice+"+++++++");
			par1mallParam.setUpdateCount(0);
			par1mallParam.setErrorCount(1);
			par1mallParam.setErrorCode("yhd.group.buy.order.inform_productPrice_invalid"); //商品价格不合法，请检查productPrice
			par1mallParam.setPkInfo("productPrize");
			String resMessage = Par1mallOrderGenerator.packageResponseMsg(par1mallParam);
			logger.info("+++++++++1mall order sync {PIRCE NOT MATCHING}+++++productPrice="+productPrice+"++goodsPayPrice="+goodsPayPrice+"++++resMessage="+resMessage);
			return resMessage;
		}
		
		//订单金额检查
		Double totalAmount = Amount.mul(goodsPayPrice, productNum);
		if(orderAmount.compareTo(totalAmount) != 0){
			logger.info("+++++++++++++totalAmount="+totalAmount +"+++orderAmount="+orderAmount+"+++++++");
			par1mallParam.setUpdateCount(0);
			par1mallParam.setErrorCount(1);
			par1mallParam.setErrorCode("yhd.group.buy.order.inform_orderAmount_invalid"); //订单金额不一致，请检查orderAmount
			par1mallParam.setPkInfo("orderAmount");
			String resMessage = Par1mallOrderGenerator.packageResponseMsg(par1mallParam);
			logger.info("+++++++++1mall order sync {ORDERAMOUNT NOT MATCHING}+++++orderAmount="+orderAmount+"++totalAmount="+totalAmount+"++++resMessage="+resMessage);
			return resMessage;
		}
		
		List<Map<String, Object>> mapList=null;
		TrxOrder trxOrder = null;
		//先到分销商订单表查询
		trxOrder = trxOrderDao.findByUserIdOutRequestId(orderCode,userIdList);
		if(null != trxOrder){
			logger.info("++++++++++1mll repeat order orderCode="+orderCode+"++++++++++" );
			Par1mallOrderParam param = new Par1mallOrderParam();
			param.setErrorCount(1);
			param.setUpdateCount(0);
			param.setFormat(par1mallParam.getFormat());
			param.setErrorCode("yhd.group.buy.order.inform_orderCode_exist"); //订单已存在，重复的订单请求
			param.setPkInfo("");
			String resMessage=Par1mallOrderGenerator.packageResponseMsg(param);
			mapList = trxorderGoodsService.preQryInWtDBByByTrxId(trxOrder.getId());
			logger.info("++++++++++++1mall sync order response{REPEAT}="+resMessage);
		}else{
			TrxRequestData trxRequestData = new TrxRequestData();
			trxRequestData.setReqChannel(ReqChannel.PARTNER);
			trxRequestData.setMobile(par1mallParam.getUserPhone());//手机号
			trxRequestData.setUserId(par1mallParam.getUserId());
			trxRequestData.setGoodsId(String.valueOf(par1mallParam.getOuterGroupId()));//我侧商品ID
			trxRequestData.setGoodsCount(String.valueOf(productNum));//购买数量
			trxRequestData.setUseEndDateComLose(false);//是否使用下线时间作为下单时间  一号店过期时间按照系统来处理
			trxRequestData.setUseOutPayPrice(true);//是否使用外部支付价格
			trxRequestData.setPayPrice(String.valueOf(payPrice));
			OrderInfo orderInfo = trxSoaService.tansTrxReqData(trxRequestData);
			orderInfo.setNeedActHis(false);//不需要走账
			orderInfo.setNeedLock(false);//不需要锁机制
			orderInfo.setOutRequestId(orderCode);
			orderInfo.setPartnerNo(partnerNo);
			orderInfo.setOutGoodsId(String.valueOf(par1mallParam.getProductId()));//1号店商品ID
			orderInfo.setClientIp(clientIp);
			logger.info("+++++++++create new partner order:[ReqChannel="+trxRequestData.getReqChannel()+",Mobile="+trxRequestData.getMobile()+",UserId="+trxRequestData.getUserId()+",clientIp="+clientIp+
					",GoodsId="+trxRequestData.getGoodsId()+",GoodsCount="+trxRequestData.getGoodsCount()+",PayPrice="+trxRequestData.getPayPrice()+",OutRequestId="+orderInfo.getOutRequestId()+",OutGoodsId="+orderInfo.getOutGoodsId()+"]");
			orderInfo=trxHessianService.noTscCreateTrxOrder(orderInfo);
			
			trxOrder=orderInfo.getTrxOrder();
			mapList = trxorderGoodsService.preQryInWtDBByByTrxId(trxOrder.getId());
			logger.info("+++++++1mall sync order success++++++++mapList="+mapList+"+++++++++++++");
		}
		
		TreeMap<String, String> apiparamsMap = new TreeMap<String, String>();
		apiparamsMap.put("checkCode", par1mallParam.getCheckCode());
		apiparamsMap.put("merchantId", Par1mallOrderGenerator.PARTERNO_1MALL);
		apiparamsMap.put("erp", Par1mallOrderGenerator.ERP);
		apiparamsMap.put("erpVer", Par1mallOrderGenerator.ERPVER);
		apiparamsMap.put("format", Par1mallOrderGenerator.FORMAT_JSON);
		apiparamsMap.put("method", "yhd.group.buy.order.verify");
		apiparamsMap.put("ver", "1.0");
		apiparamsMap.put("orderCode", trxOrder.getOutRequestId());
		apiparamsMap.put("partnerOrderCode", String.valueOf(trxOrder.getId()));
		apiparamsMap.put("orderAmount", String.valueOf(trxOrder.getOrdAmount()));
		apiparamsMap.put("orderCreateTime", DateUtils.formatDate(trxOrder.getCreateDate(), "yyyy-MM-dd HH:mm:ss"));
		
		String sign = Par1mallOrderGenerator.sign(apiparamsMap, par1mallParam.getSecretKey());
		apiparamsMap.put("sign", sign);
		try{
			noticeService.createNotice(Par1mallOrderGenerator.PARTERNO_1MALL,"1MALL_NOTICE_URL",orderCode, apiparamsMap, "yhd.group.buy.order.verify");
		}catch (Exception e) {
			logger.error("++++++1mall sync order notice {ERROR}+++++",e);
		}
		
		if(null != mapList && mapList.size()>0){
			par1mallParam.setUpdateCount(mapList.size());
			par1mallParam.setErrorCount(productNum-mapList.size());
		}else{
			par1mallParam.setUpdateCount(0);
			par1mallParam.setErrorCount(1);
			
			par1mallParam.setErrorCode("yhd.invoices.order.asyn.error");  //订单同步失败
			par1mallParam.setErrorDes("订单同步失败");
			par1mallParam.setPkInfo("");
		}
		
		String resMsg = Par1mallOrderGenerator.packageResponseMsg(par1mallParam);
		logger.info("++++++++++++1mall sync order response="+resMsg);
		return resMsg;
	}

	@Override
	public Object transReqInfo(String paramInfo) {
		return Par1mallOrderGenerator.transReqInfo(paramInfo);
	}
	
	/**
	 * ip 白名单检测（如果平台没有配置，则直接放行）
	 * 
	 * @param reqIp
	 * @param legalIP
	 * @return
	 */
	public boolean checkIp(String reqIp, String legalIP) {
		boolean boo = StringUtils.checkIp(reqIp, legalIP);
		return boo;
	}

	public boolean isValidDate(String dateStr,String patten){
		SimpleDateFormat sdf = new SimpleDateFormat(patten);
		try {
			sdf.parse(dateStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}


	/**
	 * 查询券
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String findVoucher(Object ptop, String partnerNo) throws Exception{
		Par1mallOrderParam par1mallParam =(Par1mallOrderParam)ptop;
		Long trxOrderId=Long.parseLong(par1mallParam.getPartnerOrderCode());
		String outRequestId = par1mallParam.getOrderCode();
		List<Long> userIdList = par1mallParam.getUserIdList();
		TrxOrder trxOrder = trxOrderDao.findByUserIdOutRequestId(outRequestId,userIdList);
		if(null == trxOrder){
			logger.info("+++++++++userIdList="+userIdList+"++++outRequestId="+outRequestId+"++++++++");
			par1mallParam.setUpdateCount(0);
			par1mallParam.setErrorCount(1);
			par1mallParam.setErrorCode("yhd.group.buy.vouchers.get_orderCode_invalid");
			par1mallParam.setPkInfo("orderCode");
			String resMessage = Par1mallOrderGenerator.packageResponseMsg(par1mallParam);
			logger.info("+++++++++1mall query voucher {ORDER NOT EXIST}+++++trxOrderId="+trxOrderId+"++++++resMessage="+resMessage);
			return resMessage;
		}else if(trxOrder.getId().intValue() != trxOrderId.intValue()){
			logger.info("+++++++++userIdList="+userIdList+"++++outRequestId="+outRequestId+"+++++qianpin.orderId="+trxOrder.getId()+"+++1mall.orderId="+trxOrderId+"+++");
			par1mallParam.setUpdateCount(0);
			par1mallParam.setErrorCount(1);
			par1mallParam.setErrorCode("yhd.group.buy.vouchers.get_partnerOrderCode_invalid");  //订单号关联失败
			par1mallParam.setPkInfo("partnerOrderCode");
			String resMessage = Par1mallOrderGenerator.packageResponseMsg(par1mallParam);
			logger.info("+++++++++1mall query voucher {ORDER NOT EXIST}+++++trxOrderId="+trxOrderId+"++++++resMessage="+resMessage);
			return resMessage;
		}
		List<Map<String,Object>> mapList= trxorderGoodsService.findVoucherInfoList(trxOrderId);
		if(null == mapList || mapList.size()<=0){
			par1mallParam.setUpdateCount(0);
			par1mallParam.setErrorCount(1);
			par1mallParam.setErrorCode("yhd.group.buy.vouchers.get_partnerOrderCode_invalid");  //消费券不存在
			par1mallParam.setErrorDes("消费券不存在");
			par1mallParam.setPkInfo("");
			String resMessage = Par1mallOrderGenerator.packageResponseMsg(par1mallParam);
			logger.info("+++++++++1mall query voucher {VOUCHER NOT EXIST}+++++trxOrderId="+trxOrderId+"++++++resMessage="+resMessage);
			return resMessage;
		}
		logger.info("++++++++++++++++mapList="+mapList);
		String resMsg ="";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		if(par1mallParam.getFormat().equals(Par1mallOrderGenerator.FORMAT_JSON)){
			JSONObject jsonMsg = new JSONObject();
			JSONObject responseObj = new JSONObject();
			responseObj.put("totalCount", mapList.size());
			responseObj.put("errorCount", 0);
			
			JSONObject voucherInfoList = new JSONObject();
			JSONArray voucherInfoArray = new JSONArray();
			for(Map<String,Object> voucherMap : mapList){
				JSONObject voucherInfo = new JSONObject();
				voucherInfo.put("voucherCode", null==voucherMap.get("voucherCode")?"":(String)voucherMap.get("voucherCode"));
				voucherInfo.put("issueTime", null==voucherMap.get("authDate")?"":sdf.format((Date)voucherMap.get("authDate")));
				if(TrxStatus.USED.name().equals((String)voucherMap.get("trxStatus")) || TrxStatus.COMMENTED.name().equals((String)voucherMap.get("trxStatus"))){
					voucherInfo.put("consumptionTime", null==voucherMap.get("lastUpdateDate")?"":sdf.format((Date)voucherMap.get("lastUpdateDate")));
				}else{
					voucherInfo.put("consumptionTime", "");
				}
				voucherInfo.put("voucherStartTime", null==voucherMap.get("createDate")?"":sdf.format((Date)voucherMap.get("createDate")));
				voucherInfo.put("voucherEndTime", null==voucherMap.get("orderLoseDate")?"":sdf.format((Date)voucherMap.get("orderLoseDate")));
				voucherInfo.put("voucherCount", TrxStatus.SUCCESS.name().equals((String)voucherMap.get("trxStatus"))?new Integer(1):new Integer(0));
				voucherInfoArray.add(voucherInfo);
			}
			voucherInfoList.put("voucherInfo",voucherInfoArray);
			responseObj.put("voucherInfoList", voucherInfoList);
			
			jsonMsg.put("response", responseObj);
			resMsg = jsonMsg.toJSONString();
		}else{
			Map<String,Object> responseMap = new LinkedHashMap<String, Object>();
			responseMap.put("totalCount", mapList.size());
			responseMap.put("errorCount", 0);
			Map<String,Object> voucherInfoList = new HashMap<String, Object>(1);
			List<Map<String,Object>> voucherInfoArray = new ArrayList<Map<String,Object>>();
			for(Map<String,Object> voucherMap : mapList){
				Map<String,Object> voucherInfo = new LinkedHashMap<String, Object>(6);
				voucherInfo.put("voucherCode", null==voucherMap.get("voucherCode")?"":(String)voucherMap.get("voucherCode"));
				voucherInfo.put("issueTime", null==voucherMap.get("authDate")?"":sdf.format((Date)voucherMap.get("authDate")));
				if(TrxStatus.USED.name().equals((String)voucherMap.get("trxStatus")) || TrxStatus.COMMENTED.name().equals((String)voucherMap.get("trxStatus"))){
					voucherInfo.put("consumptionTime", null==voucherMap.get("lastUpdateDate")?"":sdf.format((Date)voucherMap.get("lastUpdateDate")));
				}else{
					voucherInfo.put("consumptionTime", "");
				}
				voucherInfo.put("voucherStartTime", null==voucherMap.get("createDate")?"":sdf.format((Date)voucherMap.get("createDate")));
				voucherInfo.put("voucherEndTime", null==voucherMap.get("orderLoseDate")?"":sdf.format((Date)voucherMap.get("orderLoseDate")));
				voucherInfo.put("voucherCount", TrxStatus.SUCCESS.name().equals((String)voucherMap.get("trxStatus"))?new Integer(1):new Integer(0));
				voucherInfoArray.add(voucherInfo);
			}
			voucherInfoList.put("voucherInfo", voucherInfoArray);
			responseMap.put("voucherInfoList", voucherInfoList);
				
			Map<String,Object> resMap = new LinkedHashMap<String, Object>(1);
			resMap.put("response", responseMap);
		
			resMsg = XmlUtils.object2xml(Par1mallOrderGenerator.XML_HEAD,"response",resMap);
		}
		logger.info("++++++++++++1mall query voucher response="+resMsg);
		return resMsg;	
	}

	@Override
	public String checkHmacData(String desStr, String publicKey, String sign,PartnerInfo partnerInfo, String partnerIP) {
		return null;
	}

	@Override
	public String checkHmacData(String params, String publicKey,String privateKey) {
		return null;
	}

	@Override
	public String findTrxorder(Object ptopSource, String partnerNo) {
		return null;
	}


}
