package com.beike.core.service.trx.partner.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.bean.trx.VoucherInfo;
import com.beike.common.bean.trx.partner.Par360buyOrderGenerator;
import com.beike.common.bean.trx.partner.PartnerInfo;
import com.beike.common.enums.trx.NoticeStatus;
import com.beike.core.service.trx.notice.NoticeService;
import com.beike.core.service.trx.partner.PartnerBindVoucherService;
import com.beike.core.service.trx.partner.PartnerVoucherService;
import com.beike.entity.partner.PartnerBindVoucher;
import com.beike.util.StringUtils;

/**   
 * @title: PartnerVoucherFor360buyServiceImpl.java
 * @package com.beike.core.service.trx.partner.impl
 * @description: 合作分销商API 凭证远程查询及推送 service for 京东
 * @author wangweijie  
 * @date 2012-9-3 上午11:42:35
 * @version v1.0   
 */

@Service("partnerVoucherFor360buyService")
public class PartnerVoucherFor360buyServiceImpl implements PartnerVoucherService {
	private final Log logger = LogFactory.getLog(PartnerVoucherFor360buyServiceImpl.class);
	
	@Autowired
	private NoticeService noticeService;
	
	@Autowired
	private PartnerBindVoucherService partnerBindVoucherService;
	/**
	 * 推送分销商voucher info
	 * @param voucherInfo
	 * @param partnerinfo
	 * @return
	 */
	@Override
	public String pushVoucherInfo(VoucherInfo voucherInfo, PartnerInfo partnerInfo) {
		logger.info("++++++++++++360buy+++++++pushVoucherInfo+++++++++++++");

		PartnerBindVoucher pbv = partnerBindVoucherService.queryPartnerBindVoucher(partnerInfo.getPartnerNo(), voucherInfo.getVoucher().getId());
		String jdOrderId = voucherInfo.getTrxorder().getOutRequestId();
//		String couponId = voucherInfo.getTrxorderGoods().getTrxGoodsSn();
//		String couponPwd = voucherInfo.getVoucher().getVoucherCode();
		StringBuffer messageData = new StringBuffer();
		messageData.append("<Message xmlns=\"http://tuan.360buy.com/VerifyCouponRequest\">");
			messageData.append("<VerifyType>0</VerifyType>");
			messageData.append("<JdOrderId>"+jdOrderId+"</JdOrderId>");//京东订单ID
			messageData.append("<CouponId>"+StringUtils.toTrim(pbv.getOutCouponId())+"</CouponId>");//券号(12位) 
			messageData.append("<CouponPwd>"+StringUtils.toTrim(pbv.getOutCouponPwd())+"</CouponPwd>");//密码(6位) 
		messageData.append("</Message>");
		
		//组装报文
		String content = Par360buyOrderGenerator.packageRequestMsg(messageData.toString(), partnerInfo.getPartnerNo(),partnerInfo.getSessianKey(),partnerInfo.getKeyValue());
		//轮循发送京东--报文
		/**
		 * 发送验券报文并获取响应报文
		 */
		noticeService.createNotice(partnerInfo.getPartnerNo(), "BUY360_VerifyVoucher", voucherInfo.getTrxorder().getOutRequestId(), content, "http://tuan.360buy.com/VerifyCouponRequest",NoticeStatus.INIT);

//		String resMessage = Par360buyOrderGenerator.sendRequestTo360buy(partnerInfo.getPartnerNo(), partnerInfo.getApiType(),Par360buyOrderGenerator.VERIFY_VOUCHER,message);
//		String returnStatus = "failed";
//		try {
//			Map<String,String> resultCodeInfoMap = Par360buyOrderGenerator.getResultCodeInfo(resMessage);
//			String resultCode = resultCodeInfoMap.get("ResultCode");
//			if(!resultCode.equals("200")){
//				logger.error("++++++++360 message validate error+++resultCode="+resultCodeInfoMap);
//				returnStatus = "failed";
//			}else{
//				String data = Par360buyOrderGenerator.get360buyDataMessage(resMessage, partnerInfo.getPartnerNo(), partnerInfo.getSessianKey(), partnerInfo.getKeyValue());
//				Map<String,String> resMap = Par360buyOrderGenerator.xml2Map(data);
//				String verifyResult = StringUtils.toTrim(resMap.get("VerifyResult"));
//				
//				logger.info("++++++++++++++360buy check +++verifyResult="+verifyResult);
//				//优惠券返回结果
//				if(jdOrderId.equals(resMap.get("JdOrderId"))){
//					//200-验证成功；301-优惠券不存在；302-优惠券已过期；303-优惠券已使用；304-优惠券等待退款；305-优惠券已退款
//					if("200".equals(verifyResult)){
//						returnStatus = "success";
//					}else{
//						//不可核销
//						returnStatus = "exception";
//					}
//				}else{
//					// 不可核销
//					returnStatus = "error";
//				}
//			}
//		} catch (Exception e) {
//			logger.error("++++++++360buy++++${ERROR}++parse meesage error",e);
//			e.printStackTrace();
//		}
//		NoticeStatus noticeStatus = NoticeStatus.FAIL;
//		if("success".equals(returnStatus)){
//			noticeStatus = NoticeStatus.SUCCESS;
//		}
		
		return null;
	}

	/**
	 * 远程查询分销商voucher info
	 * @param voucherInfo
	 * @param userId
	 * @return
	 */
	@Override
	public Map<String, String> qryVoucherInfoToPar(VoucherInfo voucherInfo, PartnerInfo partnerInfo) {
		Map<String,String>  resultMap=new HashMap<String,String>();
		
		String jdOrderId = voucherInfo.getTrxorder().getOutRequestId();
//		String trxGoodsSn = voucherInfo.getTrxorderGoods().getTrxGoodsSn();
//		String couponPwd = voucherInfo.getVoucher().getVoucherCode();
		
		PartnerBindVoucher pbv = partnerBindVoucherService.queryPartnerBindVoucher(partnerInfo.getPartnerNo(), voucherInfo.getVoucher().getId());
		if(null == pbv){
			logger.error("++++360buy ${ERROR} not found any data in beiker_bind_voucher.[partner_no="+partnerInfo.getPartnerNo()+";voucher_id="+voucherInfo.getVoucher().getId());
			resultMap.put("status", "ANTI_VALIDATE");
			return resultMap;
		}
		StringBuffer messageData = new StringBuffer();
		messageData.append("<Message xmlns=\"http://tuan.360buy.com/QueryCouponRequest\">");
			messageData.append("<JdOrderId>"+jdOrderId+"</JdOrderId>");//京东订单ID
			messageData.append("<CouponId>"+pbv.getOutCouponId()+"</CouponId>");//券号(12位) 
			messageData.append("<CouponPwd>"+pbv.getOutCouponPwd()+"</CouponPwd>");//密码(6位) 
		messageData.append("</Message>");
		
		//组装报文
		String message = Par360buyOrderGenerator.packageRequestMsg(messageData.toString(), partnerInfo.getPartnerNo(),partnerInfo.getSessianKey(),partnerInfo.getKeyValue());
		//发送查询券报文并获取响应报文
		String resMessage = Par360buyOrderGenerator.sendRequestTo360buy(partnerInfo.getPartnerNo(), partnerInfo.getApiType(),Par360buyOrderGenerator.QUERY_VOUCHER,message);
		try {
			Map<String,String> resultCodeInfoMap = Par360buyOrderGenerator.getResultCodeInfo(resMessage);
			String resultCode = resultCodeInfoMap.get("ResultCode");
			if(!resultCode.equals("200")){
				logger.error("++++++++360 message validate error+++resultCode="+resultCodeInfoMap);
				// 不可核销
				resultMap.put("status", "ANTI_VALIDATE");
			}else{
				String data = Par360buyOrderGenerator.get360buyDataMessage(resMessage, partnerInfo.getPartnerNo(), partnerInfo.getSessianKey(), partnerInfo.getKeyValue());
				Map<String,String> resMap = Par360buyOrderGenerator.xml2Map(data);
				String couponId = StringUtils.toTrim(resMap.get("CouponId"));
				String couponStatus = StringUtils.toTrim(resMap.get("CouponStatus"));
				
				logger.info("++++++++++++++360buy check couponId="+couponId+"++++couponStatus="+couponStatus);
				//优惠券返回结果
				if(pbv.getOutCouponId().equals(resMap.get("CouponId"))){
					//优惠券状态 -1:不存在 0:成功 10:已验证使用过 20:已过期 30:已退款 40:已经使用后的退款 50:优惠券未发放
					if("0".equals(couponStatus)){
						//可核销
						resultMap.put("status","ALLOW_VALIDATE");
					}else{
						//不可核销
						resultMap.put("status", "ANTI_VALIDATE");
					}
				}else{
					// 不可核销
					resultMap.put("status", "ANTI_VALIDATE");
				}
			}
		} catch (Exception e) {
			logger.error("++++++++360buy++++${ERROR}++parse meesage error",e);
			e.printStackTrace();
		}
		return resultMap;
	}

}
