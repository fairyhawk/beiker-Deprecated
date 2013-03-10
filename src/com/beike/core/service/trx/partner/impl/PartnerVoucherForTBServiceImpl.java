package com.beike.core.service.trx.partner.impl;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.bean.trx.VoucherInfo;
import com.beike.common.bean.trx.partner.PartnerInfo;
import com.beike.common.entity.trx.notice.Notice;
import com.beike.core.service.trx.notice.NoticeService;
import com.beike.core.service.trx.partner.PartnerVoucherService;
import com.beike.service.common.EmailService;
import com.beike.util.Configuration;
import com.beike.util.DateUtils;
import com.beike.util.HttpUtils;
import com.beike.util.PartnerUtil;
import com.beike.util.PropertyUtil;

/**
 * @Title: PartnerVoucherForTBServiceImpl.java
 * @Package com.beike.core.service.trx.parter
 * @Description: 合作分销商API 凭证远程查询及推送 service for TB
 * @date 5 30, 2012 2:16:47 PM
 * @author wh.cheng
 * @version v1.0
 */
@Service("partnerVoucherForTBService")
public class PartnerVoucherForTBServiceImpl implements PartnerVoucherService {

	@Autowired
	private NoticeService noticeService;
	
	@Autowired
	private EmailService emailService; 
	
	PropertyUtil propertyUtil = PropertyUtil.getInstance("project");
	// 异常报警邮件
	public String merchant_api_voucher_email = propertyUtil
			.getProperty("merchant_api_taobao_voucher_email");
	
	// 扣款报警邮件模板
	public static final String MERCHANT_TRX_VOUCHER_TAOBAO_ERROR = "MERCHANT_TRX_VOUCHER_TAOBAO_ERROR";

	private final Log logger = LogFactory.getLog(PartnerVoucherForTBServiceImpl.class);

	/**
	 * 远程查询分销商voucher info
	 * 
	 * @param voucherInfo
	 * @param userId
	 * @return
	 */
	public Map<String, String> qryVoucherInfoToPar(VoucherInfo voucherInfo, PartnerInfo partnerInfo) {
		
		
		Map<String, String> resultMap = new HashMap<String, String>();
		TreeMap<String, String> apiparamsMap = new TreeMap<String, String>();
		apiparamsMap.put("format", "json");
		apiparamsMap.put("method", "taobao.vmarket.eticket.consume");
		apiparamsMap.put("sign_method", "md5");
		apiparamsMap.put("app_key", partnerInfo.getDescription());
		apiparamsMap.put("v", "2.0");
		apiparamsMap.put("session", partnerInfo.getSessianKey());// 他用型需要sessionkey
		String timestamp = DateUtils.getStringDate();
		apiparamsMap.put("timestamp", timestamp);
		apiparamsMap.put("order_id", voucherInfo.getTrxorder().getOutRequestId());// 平台订单号
		apiparamsMap.put("verify_code", voucherInfo.getVoucher().getVoucherCode());//
		apiparamsMap.put("consume_num", "1");//
		Notice notice = noticeService.findTokenByHostNo(partnerInfo.getPartnerNo(), "taobao.vmarket.eticket.send", voucherInfo.getTrxorder().getOutRequestId());
		String token = notice.getToken();
		apiparamsMap.put("token", token);// token
		String sign = PartnerUtil.md5Signature(apiparamsMap, partnerInfo.getNoticeKeyValue());
		apiparamsMap.put("sign", sign);// 平台商品订单号
		logger.info("+++++++++++++++++" + apiparamsMap + "++++++++");
		//noticeSendService.createNotice(partnerInfo.getPartnerNo(), partnerInfo.getApiType(), voucherInfo.getTrxorder().getOutRequestId(), apiparamsMap, "taobao.vmarket.eticket.consume");//
		String url = this.getUrlByHostNo(partnerInfo.getPartnerNo(), partnerInfo.getApiType());
		String content = getContent(apiparamsMap);
		if (url.indexOf("?") == -1) {
			content = url + "?method=" + "taobao.vmarket.eticket.consume" + "&" + content;
		} else {
			content = url + "&method=" + "taobao.vmarket.eticket.consume" + "&" + content;
		}
		/*
		 * 获得发送地址和内容
		 * 数据库content存放格式为url?param=xxx&content=xxx
		 * 以?分割，下标0为url；下标1为content内容
		 */
		logger.info("+++++++++++++++++content = " + content + "++++++++");
		String [] urlContent = content.split("\\?",2);
		String urlStr = urlContent[0];  
		String contentStr = urlContent[1];
		String errorStr = "";
		try {
			String rspMsg = HttpUtils.URLPost(urlStr,contentStr);
			logger.info("+++++++++++++++++rspMsg = "+rspMsg+"+++++++++");
			if(rspMsg.indexOf("\"ret_code\":1") != -1){
				resultMap.put("status", "ALLOW_VALIDATE");
				resultMap.put("token", token);
			}else{
				// 不可核销
				resultMap.put("status", "ANTI_VALIDATE");
				if(rspMsg.indexOf("isv.eticket-service-unavailable:order-is-processing") != -1){
					errorStr = "订单并发操作并发限制；稍后调用";
				}
				if(rspMsg.indexOf("isv.eticket-order-not-found:invalid-orderid") != -1){
					errorStr = "传递的订单ID参数找不到对应的电子凭证订单信息；传递正确的订单ID参数";
				}
				if(rspMsg.indexOf("isv.eticket-seller-error:invalid-eticket-seller") != -1){
					errorStr = "sessionkey对应的用户没有入驻电子凭证平台；检查sessionkey是否正确或过期";
				}
				if(rspMsg.indexOf("isv.eticket-privilege-error:order-not-belongto-seller") != -1){
					errorStr = "订单不属于用户授权登录的卖家，卖家没有权限操作这个订单；检查用户授权是否有效，并且授权的用户是否和订单所属的店铺的用户一致";
				}
				if(rspMsg.indexOf("isv.missing-parameter:token") != -1){
					errorStr = "缺少token参数；检查传递的token参数";
				}
				if(rspMsg.indexOf("isv.eticket-token-error:invalid-token") != -1){
					errorStr = "Token参数校验失败；检查传递的token参数";
				}
				if(rspMsg.indexOf("isv.eticket-invalid-posid:invalid-pos-for-codemerchant") != -1){
					errorStr = "Posid校验失败；检查传递的posid是否和码商做了绑定";
				}
				if(rspMsg.indexOf("isv.missing-parameter:code") != -1){
					errorStr = "核销码参数为空；传递code核销码参数";
				}
				if(rspMsg.indexOf("isv.eticket-consume-error:code-not-available") != -1){
					errorStr = "该码不能进行核销；检查code核销码参数";
				}
				if(rspMsg.indexOf("isv.eticket-order-status-error:invalid-order-status") != -1){
					errorStr = "此状态的订单不允许进行核销码操作；不允许进行验码";
				}
				if(rspMsg.indexOf("isv.eticket-code-error:code-not-in-valid-time") != -1){
					errorStr = "要验证的码不在有效期范围内；不允许进行验码";
				}
				if(rspMsg.indexOf("isv.eticket-order-consume-num:consume-num-invalid") != -1){
					errorStr = "核销次数错误；检查是否输入了错误的核销次数：如0，-1，-3等";
				}
				if(rspMsg.indexOf("isv.eticket-order-consume-num:consume-num-overflow") != -1){
					errorStr = "请求核销次数大于该码的实际剩余可核销次数；不允许进行验码";
				}
				if(rspMsg.indexOf("isv.eticket-invalid-parameter:invalid-consume-serial-num") != -1){
					errorStr = "自定义核销流水号格式错误；必须是a-zA-Z0-9_的字母组成";
				}
				if(rspMsg.indexOf("isv.eticket-invalid-parameter:duplicate-consume-serial-num") != -1){
					errorStr = "重复的自定义核销流水号；每次核销都必须使用唯一的核销流水号";
				}
				if(rspMsg.indexOf("isv.eticket-service-unavailable:op-failed") != -1){
					errorStr = "服务异常；重新调用";
				}
				alertMerchantVoucher(errorStr,voucherInfo.getVoucher().getVoucherCode()+":("+voucherInfo.getTrxorder().getOutRequestId()+")");
			}
		} catch (IOException e) {
			e.printStackTrace();
			errorStr = "服务器连接超时异常";
			alertMerchantVoucher(errorStr,voucherInfo.getVoucher().getVoucherCode()+":("+voucherInfo.getTrxorder().getOutRequestId()+")");
			logger.debug(e);
		}
		return resultMap;
		/*
		logger.info("++++++++++qryVoucherInfoToPar++start++++++++++++++++++++");
		// 核销标识
		Map<String, String> resultMap = new HashMap<String, String>();
		String outReqId = voucherInfo.getTrxorder().getOutRequestId();
		Notice notice = noticeService.findTokenByHostNo(partnerInfo.getPartnerNo(), "taobao.vmarket.eticket.send", outReqId);
		String token = notice.getToken();
		// 组装请求数据
		TreeMap<String, String> requestParamsMap = new TreeMap<String, String>();
		requestParamsMap.put("method", "taobao.vmarket.eticket.beforeconsume");
		requestParamsMap.put("session", partnerInfo.getSessianKey());
		requestParamsMap.put("timestamp", DateUtils.getStringDate());
		requestParamsMap.put("app_key", partnerInfo.getDescription());
		requestParamsMap.put("v", "2.0");
		requestParamsMap.put("format", "json");
		requestParamsMap.put("sign_method", "md5");
		requestParamsMap.put("order_id", voucherInfo.getTrxorder().getOutRequestId());
		requestParamsMap.put("verify_code", voucherInfo.getVoucher().getVoucherCode());
		requestParamsMap.put("token", token);
		logger.info("++++++++++qryVoucherInfoToPar++++++++++++++++++++++" + requestParamsMap);
		String sign = PartnerUtil.md5Signature(requestParamsMap, partnerInfo.getNoticeKeyValue());
		requestParamsMap.put("sign", sign);
		String retCode = ""; // 淘宝核销码状态, 0代表不能核销， 1代表可以核销
		String itemTitle = "";
		String leftnum = "";
		try {
			// 获取淘宝响应数据
			Map<String, Object> responseDataMap = ParTaobaoOrderGenerator.getResponseParamsForTaoBao(requestParamsMap, partnerInfo);

			if (null != responseDataMap) {
				retCode = responseDataMap.get("ret_code").toString();
				itemTitle = responseDataMap.get("item_title").toString();
				leftnum = responseDataMap.get("left_num").toString();

				logger.info("+++++qryVoucherInfoToPar:+++++++item_title:" + itemTitle + "+++ret_code:" + retCode + "++left_num:" + leftnum + "++token:" + token + "+++++voucherCode: " + voucherInfo.getVoucher().getVoucherCode() + "+++++");
			}

			if ("1".equals(retCode)) {
				// 可以核销
				resultMap.put("status", "ALLOW_VALIDATE");
				resultMap.put("token", token);

			} else {
				// 不可核销
				resultMap.put("status", "ANTI_VALIDATE");
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e);

		}

		return resultMap;

	*/}
	
	/**
	 * 根据宿主编号、通知类型获得通知url地址
	 * @param hostNo 经销商编号
	 * @param parentName 名称
	 * @return    
	 * @return String
	 * @throws
	 */
	private String getUrlByHostNo(String hostNo,String name){
		String url = Configuration.getInstance().getValue(hostNo+"_"+name);
		
		return url;
	}

	private String getContent(Map<String,String> sourceMap) {
		if (null==sourceMap || sourceMap.size() == 0) {
			return ("");
		}
		StringBuilder content = new StringBuilder("");
		for(Entry<String,String> entry : sourceMap.entrySet()){
			if(null==entry.getValue() || "".equals(entry.getValue().trim())){
				continue;
			}
			content.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
		}
		content.deleteCharAt(content.length()-1);
		return content.toString();
	}
	
	
	/**
	 * 邮件报警方法
	 * 
	 * @param guestId
	 * @param branchId
	 */
	public void alertMerchantVoucher(String errorStr,String trxGoodsSn) {

		// 发送内部报警邮件
		String alertEmailParams[] = {"来源淘宝分销商："+errorStr,
				trxGoodsSn };
		if (merchant_api_voucher_email != null
				&& merchant_api_voucher_email.length() > 0) {
			String[] alertVcActDebitEmailAry = merchant_api_voucher_email
					.split(",");
			int alertEmailCount = alertVcActDebitEmailAry.length;

			try {
				for (int i = 0; i < alertEmailCount; i++) {
					emailService.send(null, null, null, null, null, null,
							new String[] { alertVcActDebitEmailAry[i] }, null,
							null, new Date(), alertEmailParams,
							MERCHANT_TRX_VOUCHER_TAOBAO_ERROR);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return;

	}
	
	/**
	 * 推送分销商voucher info
	 * 
	 * @param voucherInfo
	 * @param partnerinfo
	 * @return
	 */
	public String pushVoucherInfo(VoucherInfo voucherInfo, PartnerInfo partnerInfo) {
		/*TreeMap<String, String> apiparamsMap = new TreeMap<String, String>();
		apiparamsMap.put("format", "json");
		apiparamsMap.put("method", "taobao.vmarket.eticket.consume");
		apiparamsMap.put("sign_method", "md5");
		apiparamsMap.put("app_key", partnerInfo.getDescription());
		apiparamsMap.put("v", "2.0");
		apiparamsMap.put("session", partnerInfo.getSessianKey());// 他用型需要sessionkey
		String timestamp = DateUtils.getStringDate();
		apiparamsMap.put("timestamp", timestamp);
		apiparamsMap.put("order_id", voucherInfo.getTrxorder().getOutRequestId());// 平台订单号
		apiparamsMap.put("verify_code", voucherInfo.getVoucher().getVoucherCode());//
		apiparamsMap.put("consume_num", "1");//
		Notice notice = noticeService.findTokenByHostNo(partnerInfo.getPartnerNo(), "taobao.vmarket.eticket.send", voucherInfo.getTrxorder().getOutRequestId());
		String token = notice.getToken();
		apiparamsMap.put("token", token);// token
		String sign = PartnerUtil.md5Signature(apiparamsMap, partnerInfo.getNoticeKeyValue());
		apiparamsMap.put("sign", sign);// 平台商品订单号
		logger.info("+++++++++++++++++" + apiparamsMap + "++++++++");
		noticeService.createNotice(partnerInfo.getPartnerNo(), partnerInfo.getApiType(), voucherInfo.getTrxorder().getOutRequestId(), apiparamsMap, "taobao.vmarket.eticket.consume");//
*/
		return null;
	}

}