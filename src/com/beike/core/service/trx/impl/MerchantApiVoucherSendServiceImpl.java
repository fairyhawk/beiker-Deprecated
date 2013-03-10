package com.beike.core.service.trx.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.bean.trx.VoucherParam;
import com.beike.common.entity.trx.SendType;
import com.beike.common.entity.trx.TrxLog;
import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.common.enums.trx.TrxLogType;
import com.beike.common.exception.BaseException;
import com.beike.core.service.trx.VoucherSendService;
import com.beike.dao.CodeOperator.CodeOperatorConfigureDao;
import com.beike.dao.trx.TrxLogDao;
import com.beike.entity.common.Sms;
import com.beike.form.SmsInfo;
import com.beike.service.common.EmailService;
import com.beike.service.common.SmsService;
import com.beike.util.Constant;
import com.beike.util.DateUtils;
import com.beike.util.DesString;
import com.beike.util.PropertyUtil;
import com.beike.util.StringUtils;
import com.beike.util.TrxConstant;
import com.bjlyw.ts.service.ITicketService;
import com.bjlyw.ts.util.GetTicketService;

/**
 * @Title: MerchantApiVoucherSendServiceImpl.java
 * @Package com.beike.core.service.trx.impl
 * @Description: 通过商家Api发送商家凭证码服务类
 * @date 3 8, 2012 11:24:04 AM
 * @author wh.cheng
 * @version v1.0
 */
@Service("merchantApiVoucherSendService")
public class MerchantApiVoucherSendServiceImpl implements VoucherSendService {

	@Resource(name = "smsService")
	private SmsService smsService;

	@Resource(name = "trxLogDao")
	private TrxLogDao trxLogDao;

	private final Log logger = LogFactory.getLog(TrxOrderServiceImpl.class);
	PropertyUtil propertyUtil = PropertyUtil.getInstance("project");
	// 机构号
	public String organization = propertyUtil
			.getProperty("merchant_api_voucher_organization");
	// 接口密钥
	public String merchantDes = propertyUtil
			.getProperty("merchant_api_voucher_des");
	// 接口密码
	public String password = propertyUtil
			.getProperty("merchant_api_voucher_password");

	// 异常报警邮件
	public String merchant_api_voucher_email = propertyUtil
			.getProperty("merchant_api_voucher_email");

	// 扣款报警邮件模板
	public static final String MERCHANT_TRX_ERROR = "MERCHANT_TRX_ERROR";

	@Autowired
	private EmailService emailService;
	@Autowired
	private CodeOperatorConfigureDao codeOperatorConfigureDao;

	/**
	 * 
	 * 发送凭证码
	 * 
	 * @param tg
	 * @param voucher
	 * @param goodsTitle
	 * @param mobile
	 * @param smsTemlate
	 */
	@Override
	public VoucherParam sendVoucher(VoucherParam voucherParam) {
		TrxorderGoods tg = voucherParam.getTrxorderGoods();
		if (!checkSendType(voucherParam)) {// 校验是否通过。应该在此处设异常，后期改造

			return voucherParam;

		}

		// 发送平台短信部分start
		String goodsName = StringUtils.cutffStr(voucherParam.getGoodsTitle(),
				TrxConstant.smsVouGoodsNameCount, "");// 商品简称
		String ordLoseDate = DateUtils.toString(tg.getOrderLoseDate(),
				"yyyy-MM-dd");// 过期时间
		String trxGoodsSn = tg.getTrxGoodsSn();// 商品订单号

		String[] mobileAry = voucherParam.getMobile();

		@SuppressWarnings("unused")
		String smsTemplate = voucherParam.getSmsTemplate();

		SendType sendType = voucherParam.getSendType();

		String email = voucherParam.getEamil();

		String mobile = mobileAry[0];// 发送的手机号

		StringBuilder voucherSendLogSb = new StringBuilder();// 凭证发送日志内容
		TrxLog trxLog = new TrxLog(trxGoodsSn, new Date(),
				TrxLogType.TRXORDERGOODS, "服务密码发送成功");
		// 发送商家短信部分start
		try {
			if (SendType.SMS.equals(sendType) || SendType.BOTH.equals(sendType)) {
				ITicketService service = GetTicketService.getService();
				String trxGoodsId = getReqSeq(voucherParam.getTrxorderGoods()
						.getId());
				String req_seq = organization + DateUtils.getStringTodayto()
						+ trxGoodsId;// 流水号
				//String productStr = getProductStr(tg.getGoodsId());// 产品编号
				String productStr = "";
				Map<String, String>  cpMap = codeOperatorConfigureDao.findProductNumByGoodsId(tg.getGoodsId(), "SUNSHINE");// 是否商品有商家API产品映射编码
				if(cpMap!=null){
					productStr = cpMap.get("product_num");
				}else{
					logger.info("++++++++++++sendVoucher()+++++++++productStr=null ++++++++error+trxGoodsSn="+trxGoodsSn);
					//报警
					alertMerchantVoucher(voucherParam.getTrxorderGoods().getGuestId(),tg.getGoodsId(),"产品编号与平台goodsId没有配置!");	
					return null;
				}
				StringBuilder paramString = new StringBuilder();
				paramString
						.append("<?xml version=\"1.0\" encoding=\"utf-8\"?><business_trans version=\"1.0\">");
				paramString.append("<request_type>add_order</request_type>");
				paramString.append("<organization>");
				paramString.append(organization);
				paramString.append("</organization>");
				paramString.append("<password>");
				paramString.append(password);
				paramString.append("</password>");
				paramString.append("<req_seq>");
				paramString.append(req_seq);
				paramString.append("</req_seq>");
				paramString.append("<order>");
				paramString.append("<product_num>");
				paramString.append(productStr);
				paramString.append("</product_num>");
				paramString.append("<num>1</num>");
				paramString.append("<mobile>");
				paramString.append(voucherParam.getMobile()[0]);
				paramString.append("</mobile>");
				paramString.append("<use_date></use_date>");
				paramString.append("<real_name_type>");
				paramString.append("0");
				paramString.append("</real_name_type>");
				paramString.append("<real_name>");
				paramString.append("");
				paramString.append("</real_name>");
				paramString.append("</order></business_trans>");

				logger.info("++++++++++" + paramString.toString()
						+ "++++++++++++");
				String strdes = new DesString().encrypt(URLEncoder.encode(
						paramString.toString(), "UTF-8"), merchantDes);

				String strReturn = service
						.getEleInterface(organization, strdes);
				strReturn = new DesString().decrypt(strReturn, merchantDes);
				strReturn = URLDecoder.decode(strReturn, "UTF-8");
				logger.info("++++++++++" + strReturn + "++++++++++++");

				String returnStr = strReturn.substring(strReturn
						.lastIndexOf("<id>") + 4, strReturn
						.lastIndexOf("</id>"));
				if ("0000".equals(returnStr)) {

					String returnVoucherCode = strReturn.substring(strReturn
							.lastIndexOf("<order_num>") + 11, strReturn
							.lastIndexOf("</order_num>"));// 商家返回流水号存入凭证码
					voucherParam.setVoucherCode(returnVoucherCode);

					
					voucherSendLogSb.append("至手机号:" + mobile);

					// 发送平台短信部分end

				} else if ("1101".equals(returnStr)) {// 订单信息错误
					alertMerchantVoucher(voucherParam.getTrxorderGoods()
							.getGuestId(), voucherParam.getTrxorderGoods()
							.getTrxGoodsSn(), "调用商家凭证API订单信息错误");
				} else if ("1200".equals(returnStr)) {// 产品编号错误
					alertMerchantVoucher(voucherParam.getTrxorderGoods()
							.getGuestId(), voucherParam.getTrxorderGoods()
							.getTrxGoodsSn(), "调用商家凭证API产品编号错误");
				} else if ("1201".equals(returnStr)) {// 电子票发送失败
					alertMerchantVoucher(voucherParam.getTrxorderGoods()
							.getGuestId(), voucherParam.getTrxorderGoods()
							.getTrxGoodsSn(), "调用商家凭证API电子票发送失败");
				} else if ("1202".equals(returnStr)) {
					
					reTryForExp(service, returnStr, voucherParam, returnStr, returnStr, returnStr, returnStr, returnStr, paramString);
				
					
				} else if ("1000".equals(returnStr)) {// 重复的流水号
					alertMerchantVoucher(voucherParam.getTrxorderGoods()
							.getGuestId(), voucherParam.getTrxorderGoods()
							.getTrxGoodsSn(), "调用商家凭证API重复的流水号");
				}

			} else if (SendType.EMAIL.equals(sendType)
					|| SendType.BOTH.equals(sendType)) {
				if (SendType.BOTH.equals(sendType)) {
					voucherSendLogSb.append(";");
				}
				voucherSendLogSb.append("至邮箱:" + email);
				// 发邮件
				String emailParams[] = { goodsName, trxGoodsSn,
						voucherParam.getVoucherCode(), ordLoseDate,
						DateUtils.toString(new Date(), "yyyy-MM-dd") };

				emailService.send(null, null, null, null, null, null,
						new String[] { email }, null, null, new Date(),
						emailParams, Constant.EMAIL_VOUCHER_DISPATCH);

			}
			trxLog.setLogContent(voucherSendLogSb.toString());
			trxLogDao.addTrxLog(trxLog);
		} catch (Exception e) {
			alertMerchantVoucher(voucherParam.getTrxorderGoods().getGuestId(),
					voucherParam.getTrxorderGoods().getTrxGoodsSn(), "调用商家凭证API");
			e.printStackTrace();
		}
		return voucherParam;
	}

	/**
	 * 重发
	 * 
	 * @param tg
	 * @param voucher
	 * @param goodsTitle
	 * @param mobile
	 * @param smsTemlate
	 */
	public void reSendVoucher(VoucherParam voucherParam) {
		try {
			TrxorderGoods tg = voucherParam.getTrxorderGoods();
			if (!checkSendType(voucherParam)) {// 校验是否通过。应该在此处设异常，后期改造

				return;

			}

			String goodsName = StringUtils.cutffStr(voucherParam
					.getGoodsTitle(), TrxConstant.smsVouGoodsNameCount, "");// 商品简称
			String ordLoseDate = DateUtils.toString(tg.getOrderLoseDate(),
					"yyyy-MM-dd");// 过期时间
			SendType sendType = voucherParam.getSendType();
			ITicketService service = GetTicketService.getService();
			String trxGoodsId = getReqSeq(voucherParam.getTrxorderGoods()
					.getId());
			String req_seq = organization + DateUtils.getStringTodayto()
					+ trxGoodsId;// 流水号
			StringBuilder paramString = new StringBuilder();

			if (SendType.SMS.equals(sendType) || SendType.BOTH.equals(sendType)) {
				paramString
						.append("<?xml version=\"1.0\" encoding=\"utf-8\"?><business_trans version=\"1.0\">");
				paramString.append("<request_type>repeat_order</request_type>");
				paramString.append("<organization>");
				paramString.append(organization);
				paramString.append("</organization>");
				paramString.append("<password>");
				paramString.append(password);
				paramString.append("</password>");
				paramString.append("<req_seq>");
				paramString.append(req_seq);
				paramString.append("</req_seq>");
				paramString.append("<order>");
				paramString.append("<order_num>");
				paramString.append(voucherParam.getVoucherCode());// 阳光绿洲订单号
				paramString.append("</order_num>");
				paramString.append("</order></business_trans>");

				logger.info("++++++++++" + paramString.toString()
						+ "++++++++++++");
				String strdes = new DesString().encrypt(URLEncoder.encode(
						paramString.toString(), "UTF-8"), merchantDes);
				String strReturn = service
						.getEleInterface(organization, strdes);
				strReturn = new DesString().decrypt(strReturn, merchantDes);
				strReturn = URLDecoder.decode(strReturn, "UTF-8");
				logger.info("++++++++++" + strReturn + "++++++++++++");

				String returnStr = strReturn.substring(strReturn
						.lastIndexOf("<id>") + 4, strReturn
						.lastIndexOf("</id>"));
				if ("0000".equals(returnStr)) {

					String returnVoucherCode = strReturn.substring(strReturn
							.lastIndexOf("<order_num>") + 11, strReturn
							.lastIndexOf("</order_num>"));// 商家返回流水号存入凭证码
					voucherParam.setVoucherCode(returnVoucherCode);

				} else if ("3201".equals(returnStr)) {// 无此订单
					alertMerchantVoucher(voucherParam.getTrxorderGoods()
							.getGuestId(), voucherParam.getTrxorderGoods()
							.getTrxGoodsSn(), "调用商家凭证API无此订单");

				} else if ("1000".equals(returnStr)) {// 重复的流水号

					alertMerchantVoucher(voucherParam.getTrxorderGoods()
							.getGuestId(), voucherParam.getTrxorderGoods()
							.getTrxGoodsSn(), "调用商家凭证API重复的流水号");
				}
			} 
			if (SendType.EMAIL.equals(sendType)
					|| SendType.BOTH.equals(sendType)) {

				// 发送邮件部分
				String emailStr = voucherParam.getEamil();
				String nowDate = DateUtils.toString(new Date(), "yyyy-MM-dd");
				String emailParams[] = { goodsName, tg.getTrxGoodsSn(),
						voucherParam.getVoucherCode(), ordLoseDate,
						 nowDate };

				emailService.send(null, null, null, null, null, null,
						new String[] { emailStr },
						null,// 邮件地址----------------------------------------------------
						null, new Date(), emailParams,
						Constant.MERCHANT_API_VOUCHER_EMAIL);

				// 发送邮件部分end

			}
		} catch (Exception e) {
			alertMerchantVoucher(voucherParam.getTrxorderGoods().getGuestId(),
					voucherParam.getTrxorderGoods().getTrxGoodsSn(), "调用商家凭证API");
			e.printStackTrace();
		}
	}

	/**
	 * 转发
	 * 
	 * @param tg
	 * @param voucher
	 * @param goodsTitle
	 * @param mobileAry
	 * @param smsTemlate
	 */
	public void transSendVoucher(VoucherParam voucherParam) {
		try {
			TrxorderGoods tg = voucherParam.getTrxorderGoods();
			if (!checkSendType(voucherParam)) {// 校验是否通过。应该在此处设异常，后期改造

				return;

			}

			String goodsName = StringUtils.cutffStr(voucherParam
					.getGoodsTitle(), TrxConstant.smsVouGoodsNameCount, "");// 商品简称
			String ordLoseDate = DateUtils.toString(tg.getOrderLoseDate(),
					"yyyy-MM-dd");// 过期时间
			SendType sendType = voucherParam.getSendType();
			ITicketService service = GetTicketService.getService();
			String trxGoodsId = getReqSeq(tg.getId());
			String req_seq = organization + DateUtils.getStringTodayto()
					+ trxGoodsId;// 流水号
			StringBuilder paramString = new StringBuilder();

			if (SendType.SMS.equals(sendType) || SendType.BOTH.equals(sendType)) {
				paramString
						.append("<?xml version=\"1.0\" encoding=\"utf-8\"?><business_trans version=\"1.0\">");
				paramString.append("<request_type>sendto_order</request_type>");
				paramString.append("<organization>");
				paramString.append(organization);
				paramString.append("</organization>");
				paramString.append("<password>");
				paramString.append(password);
				paramString.append("</password>");
				paramString.append("<req_seq>");
				paramString.append(req_seq);
				paramString.append("</req_seq>");
				paramString.append("<order>");
				paramString.append("<order_num>");
				paramString.append(voucherParam.getVoucherCode());// 阳光绿洲订单号
				paramString.append("</order_num>");
				paramString.append("<old_mobile>");
				paramString.append(voucherParam.getMobile()[0]);// 原手机号
				paramString.append("</old_mobile>");
				paramString.append("<new_mobile>");
				paramString.append(voucherParam.getMobile()[1]);// 新手机号
				paramString.append("</new_mobile>");
				paramString.append("</order></business_trans>");
				logger.info("++++++++++" + paramString.toString()
						+ "++++++++++++");
				String strdes = new DesString().encrypt(URLEncoder.encode(
						paramString.toString(), "UTF-8"), merchantDes);
				String strReturn = service
						.getEleInterface(organization, strdes);
				strReturn = new DesString().decrypt(strReturn, merchantDes);
				strReturn = URLDecoder.decode(strReturn, "UTF-8");
				logger.info("++++++++++" + strReturn + "++++++++++++");

				String returnStr = strReturn.substring(strReturn
						.lastIndexOf("<id>") + 4, strReturn
						.lastIndexOf("</id>"));
				if ("0000".equals(returnStr)) {

					String returnVoucherCode = strReturn.substring(strReturn
							.lastIndexOf("<order_num>") + 11, strReturn
							.lastIndexOf("</order_num>"));// 商家返回流水号存入凭证码
					voucherParam.setVoucherCode(returnVoucherCode);

				} else if ("4201".equals(returnStr)) {// 无此订单

					alertMerchantVoucher(voucherParam.getTrxorderGoods()
							.getGuestId(), voucherParam.getTrxorderGoods()
							.getTrxGoodsSn(), "调用商家凭证API无此订单");
				} else if ("1000".equals(returnStr)) {// 重复的流水号
					alertMerchantVoucher(voucherParam.getTrxorderGoods()
							.getGuestId(), voucherParam.getTrxorderGoods()
							.getTrxGoodsSn(), "调用商家凭证API重复的流水号");

				}

			} 
			if (SendType.EMAIL.equals(sendType)
					|| SendType.BOTH.equals(sendType)) {

				// 发送邮件部分
				String emailStr = voucherParam.getEamil();
				String nowDate = DateUtils.toString(new Date(), "yyyy-MM-dd");
				String emailParams[] = { goodsName, tg.getTrxGoodsSn(),
						voucherParam.getVoucherCode(), ordLoseDate,
						voucherParam.getVoucherCode(), nowDate };

				emailService.send(null, null, null, null, null, null,
						new String[] { emailStr },
						null,// 邮件地址
						null, new Date(), emailParams,
						Constant.MERCHANT_API_VOUCHER_EMAIL);
				// 发送邮件部分end

			}
		} catch (Exception e) {
			alertMerchantVoucher(voucherParam.getTrxorderGoods().getGuestId(),
					voucherParam.getTrxorderGoods().getTrxGoodsSn(), "调用商家凭证API");
			e.printStackTrace();
		}
	}

	/**
	 * 生成流水号
	 * 
	 * @param id
	 * @return
	 */
	public String getReqSeq(Long id) {
		String trxGoodsId = id.toString();
		if (trxGoodsId.length() < 6) {
			trxGoodsId = (trxGoodsId + "000000").substring(0, 6);
		}
		trxGoodsId = trxGoodsId.substring(trxGoodsId.length() - 5)
				+ (int) (Math.random() * 10);
		return trxGoodsId;
	}
	/**
	 * 邮件报警方法
	 * 
	 * @param guestId
	 * @param branchId
	 */
	public void alertMerchantVoucher(Long guestId, String trxGoodsSn, String brrer) {

		// 发送内部报警邮件
		String alertEmailParams[] = { String.valueOf(guestId),
				trxGoodsSn, brrer };
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
							MERCHANT_TRX_ERROR);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return;

	}
	


	/**
	 * 校验发送类型
	 * 
	 * @param voucherParam
	 */
	public boolean checkSendType(VoucherParam voucherParam) {
		String mobile = voucherParam.getMobile()[0];
		String email = voucherParam.getEamil();
		SendType sendType = voucherParam.getSendType();
		boolean result = false;

		logger.info("++++mobile:" + mobile + "+++++++++email:" + email
				+ "+++++++sendType:" + sendType);
		if ((mobile == null || mobile.length() == 0)
				&& (email == null || email.length() == 0)) {

			return result;

		}

		if (SendType.SMS.equals(sendType)
				&& (mobile == null || mobile.length() == 0)) {
			return result;

		}

		if (SendType.EMAIL.equals(sendType)
				&& (email == null || email.length() == 0)) {
			return result;

		}

		if (SendType.BOTH.equals(sendType)
				&& (mobile == null || mobile.length() == 0 || email == null || email
						.length() == 0)) {

			return result;

		}

		return true;

	}
	/**
	 * 当对方
	 * @param service
	 * @param strdes
	 * @param voucherParam
	 * @param goodsName
	 * @param trxGoodsSn
	 * @param ordLoseDate
	 * @param smsTemplate
	 * @param mobile
	 * @param voucherSendLogSb
	 * @throws BaseException
	 * @throws UnsupportedEncodingException
	 */
	public void reTryForExp(ITicketService service,String strdes,VoucherParam  voucherParam,String goodsName,
			String trxGoodsSn,String ordLoseDate,String smsTemplate,String mobile,StringBuilder voucherSendLogSb ) throws BaseException, UnsupportedEncodingException{
		
		String strReturn1 = service.getEleInterface(organization, strdes);
		strReturn1 = new DesString().decrypt(strReturn1, merchantDes);
		strReturn1 = URLDecoder.decode(strReturn1, "UTF-8");
		logger.info("++++++++++" + strReturn1 + "++++++++++++");
		String returnStr1 = strReturn1.substring(strReturn1
				.lastIndexOf("<id>") + 4, strReturn1
				.lastIndexOf("</id>"));
		if ("0000".equals(returnStr1)) {

			String returnVoucherCode = strReturn1.substring(strReturn1
					.lastIndexOf("<order_num>") + 11, strReturn1
					.lastIndexOf("</order_num>"));// 商家返回流水号存入凭证码
			voucherParam.setVoucherCode(returnVoucherCode);

			// 短信参数
			Object[] smsParam = new Object[] { goodsName, trxGoodsSn,
					ordLoseDate };

			Sms sms = smsService.getSmsByTitle(smsTemplate);// 获取短信实体
			String template = sms.getSmscontent(); // 获取短信模板

			String contentResult = MessageFormat.format(template,
					smsParam);
			SmsInfo sourceBean = new SmsInfo(mobile, contentResult,
					"15", "1");

			logger.info("+++++++++++smsVoucher:mobile:" + mobile
					+ "+++trxgoodsSn:" + trxGoodsSn
					+ "->voucherCode:*****+++++++");

			smsService.sendSms(sourceBean);
			voucherSendLogSb.append("至手机号:" + mobile);

			// 发送平台短信部分end
		}else{
			alertMerchantVoucher(voucherParam.getTrxorderGoods()
					.getGuestId(), voucherParam.getTrxorderGoods()
					.getTrxGoodsSn(), "调用商家凭证API产品存在异常");
		}
		
		
		
	}
	
	/**
	 * 邮件报警方法
	 * 
	 * @param guestId
	 * @param branchId
	 */
	public void alertMerchantVoucher(Long guestId, Long trxGoods, String brrer) {

		// 发送内部报警邮件
		String alertEmailParams[] = { String.valueOf(guestId),String.valueOf(trxGoods), brrer };
		if (merchant_api_voucher_email != null&& merchant_api_voucher_email.length() > 0) {
			String[] alertVcActDebitEmailAry = merchant_api_voucher_email.split(",");
			int alertEmailCount = alertVcActDebitEmailAry.length;

			try {
				for (int i = 0; i < alertEmailCount; i++) {
					emailService.send(null, null, null, null, null, null,new String[] { alertVcActDebitEmailAry[i] }, 
									  null,null, new Date(), alertEmailParams,MERCHANT_TRX_ERROR);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return;

	}
}
