package com.beike.core.service.trx.impl;

import java.text.MessageFormat;
import java.util.Date;

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
import com.beike.core.service.trx.VoucherSendService;
import com.beike.dao.trx.TrxLogDao;
import com.beike.entity.common.Sms;
import com.beike.form.SmsInfo;
import com.beike.service.common.EmailService;
import com.beike.service.common.SmsService;
import com.beike.util.Constant;
import com.beike.util.DateUtils;
import com.beike.util.StringUtils;
import com.beike.util.TrxConstant;

/**
 * @Title: PlatformVoucherSendServiceImpl.java
 * @Package com.beike.core.service.trx.impl
 * @Description: 发送千品凭证（含商家上传到千品平台的凭证码）
 * @date 3 8, 2012 11:24:04 AM
 * @author wh.cheng
 * @version v1.0
 */
@Service("platformVoucherSendService")
public class PlatformVoucherSendServiceImpl implements VoucherSendService {

	@Resource(name = "smsService")
	private SmsService smsService;
	@Autowired
	private EmailService emailService;

	@Resource(name = "trxLogDao")
	private TrxLogDao trxLogDao;
	private final Log logger = LogFactory
			.getLog(PlatformVoucherSendServiceImpl.class);

	/**
	 * 
	 * 发送凭证码
	 * 
	 * @param voucherParam
	 */
	public VoucherParam sendVoucher(VoucherParam voucherParam) {

		if (!checkSendType(voucherParam)) {// 校验是否通过。应该在此处设异常，后期改造

			return voucherParam;

		}
		TrxorderGoods tg = voucherParam.getTrxorderGoods();

		String goodsName = StringUtils.cutffStr(voucherParam.getGoodsTitle(),TrxConstant.smsVouGoodsNameCount, "");// 商品简称
		String ordLoseDate = DateUtils.toString(tg.getOrderLoseDate(),"yyyy-MM-dd");// 过期时间
		String trxGoodsSn = tg.getTrxGoodsSn();// 商品订单号
		double payPrice = tg.getPayPrice();//支付价格

		String voucherCode = voucherParam.getVoucherCode();// 凭证码

		String[] mobileAry = voucherParam.getMobile();

		String smsTemplate = voucherParam.getSmsTemplate();
		
		SendType sendType = voucherParam.getSendType();// 发送类型

		String email = voucherParam.getEamil();// 邮箱
		
		String outSmsTemplate=voucherParam.getOutSmsTemplate();
		
		StringBuilder voucherSendLogSb = new StringBuilder();// 凭证发送日志内容

		if (mobileAry == null || mobileAry.length == 0) {
			logger.debug("+++++++++trxGoodsSn:" + trxGoodsSn	+ "++++mobible is null");
			return voucherParam;

		}

		String mobile = mobileAry[1];// 发送的手机号
		// 短信参数
		Object[] platSmsParam = new Object[] { goodsName, trxGoodsSn,voucherCode, ordLoseDate };
		Object[] merSmsParam = new Object[] { goodsName, voucherCode,ordLoseDate };
		Object[] outSmsParam = new Object[] { trxGoodsSn,voucherCode };// 外部模板参数集合（淘宝定制）
		Object[] menuSmsParam = new Object[] { goodsName+":"+voucherParam.getSubGuestName(), payPrice,voucherCode, ordLoseDate };
		Object[] smsParam=merSmsParam;//默认为商家码模板
		if(outSmsTemplate!=null&& outSmsTemplate.length()>0){//如果外部模板不为空，则以外部模板为准（淘宝定制）
			smsParam=outSmsParam;
			
		}else if( Constant.SMS_VOUCHER_DISPATCH.equals(smsTemplate) ||  Constant.SMS_VOUVHER_DISPATCH_FOR_TB.equals(smsTemplate) || Constant.SMS_VOUVHER_DISPATCH_FOR_58TC.equals(smsTemplate)
				|| Constant.PAR1MALL_VOUCHERDISPATCH.equals(smsTemplate) || Constant.VOUCHERDISPATCHMIN.equals(smsTemplate)){
			
			smsParam=platSmsParam;
			
		}else if(Constant.SMS_MENU_VOUCHER_DISPATCH.equals(smsTemplate)){
			smsParam = menuSmsParam;
		}else if(Constant.VOUCHERDISPATCHOTHER.equals(smsTemplate)){
			//add by ljp 20130106 修改短信合并功能
			StringBuffer snAndCode = new StringBuffer();
			int i = 1;
			for(TrxorderGoods temp : voucherParam.getTrxorderGoodsList()){
				snAndCode.append(i+"订单号"+temp.getTrxGoodsSn()+","+i+"密码"+temp.getVoucherCode()+";");
				i++;
			}
			String snAndCodeStr = snAndCode.toString();
			snAndCodeStr = snAndCodeStr.substring(0, snAndCodeStr.lastIndexOf(";"));
			Object[] otherCountParam = new Object[]{goodsName, voucherParam.getTrxorderGoodsList().size(),snAndCodeStr,ordLoseDate};//当商品数量为1与5之间时
			smsParam = otherCountParam;
		}else if(Constant.VOUCHERDISPATCHMAX.equals(smsTemplate)){
			//add by ljp 20130106 修改短信合并功能
			Object[] maxCountParam = new Object[]{goodsName, voucherParam.getTrxorderGoodsList().size() };//当商品数量为大于5时
			smsParam = maxCountParam;
		}
		TrxLog trxLog = new TrxLog(trxGoodsSn, new Date(),TrxLogType.VOUCHER_SEND, "服务密码发送成功");
		try {
			if (SendType.SMS.equals(sendType) || SendType.BOTH.equals(sendType)) {
				
				String template="";
				if(outSmsTemplate!=null&& outSmsTemplate.length()>0){
					outSmsTemplate="商品订单号为:{0}"+outSmsTemplate+"【千品网】";//加上平台名字，不能浪费每次营销机会。不要便宜淘宝，打倒淘宝!!!
					template=outSmsTemplate.replace("$code", "{1}");//淘宝定制

				}else{
					Sms sms = smsService.getSmsByTitle(smsTemplate);// 获取短信实体
					template = sms.getSmscontent(); // 获取短信模板
				}


				String contentResult = MessageFormat.format(template, smsParam);
				SmsInfo sourceBean = new SmsInfo(mobile, contentResult, "15",	"1");

				logger.info("+++++++++++smsVoucher:mobile:" + mobile+ "+++trxgoodsSn:" + trxGoodsSn+ "->voucherCode:*****+++++++");

				smsService.sendSms(sourceBean);
				voucherSendLogSb.append("至手机号:" + mobile);
			}  if (SendType.EMAIL.equals(sendType)|| SendType.BOTH.equals(sendType)) {
					if (SendType.BOTH.equals(sendType)) {
						voucherSendLogSb.append(";");
				}
				voucherSendLogSb.append("至邮箱:" + email);
				// 发邮件
				String emailParams[] = { goodsName, trxGoodsSn, voucherCode,ordLoseDate,DateUtils.toString(new Date(), "yyyy-MM-dd") };

				emailService.send(null, null, null, null, null, null,new String[] { email }, null, null, new Date(),emailParams, Constant.EMAIL_VOUCHER_DISPATCH);
			}
			
			trxLog.setLogContent(voucherSendLogSb.toString());
			trxLogDao.addTrxLog(trxLog);
		} catch (Exception e) {
			e.printStackTrace();

		}

		return voucherParam;

	}

	/**
	 * 重发
	 * 
	 * @param voucherParam
	 */
	public void reSendVoucher(VoucherParam voucherParam) {

	}

	/**
	 * 转发
	 * 
	 * @param voucherParam
	 */
	public void transSendVoucher(VoucherParam voucherParam) {

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
}
