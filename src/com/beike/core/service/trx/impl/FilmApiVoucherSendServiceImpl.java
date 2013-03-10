package com.beike.core.service.trx.impl;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.biz.service.trx.OrderFilmService;
import com.beike.common.bean.trx.FilmApiOrderParam;
import com.beike.common.bean.trx.VoucherParam;
import com.beike.common.entity.trx.FilmGoodsOrder;
import com.beike.common.entity.trx.SendType;
import com.beike.common.entity.trx.TrxLog;
import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.common.entity.trx.Voucher;
import com.beike.common.enums.trx.TrxLogType;
import com.beike.common.exception.BaseException;
import com.beike.core.service.trx.FilmApiGoodsOrderService;
import com.beike.core.service.trx.RefundService;
import com.beike.core.service.trx.VoucherSendService;
import com.beike.dao.trx.FilmGoodsOrderDao;
import com.beike.dao.trx.TrxLogDao;
import com.beike.dao.trx.VoucherDao;
import com.beike.entity.common.Sms;
import com.beike.form.SmsInfo;
import com.beike.service.common.EmailService;
import com.beike.service.common.SmsService;
import com.beike.util.DateUtils;
import com.beike.util.PropertyUtil;
import com.beike.util.StringUtils;
import com.beike.util.TrxConstant;

/**
 * @author yurenli
 * 网票网发送短信接口和重发接口
 */
@Service("filmApiVoucherSendService")
public class FilmApiVoucherSendServiceImpl implements VoucherSendService  {

	@Resource(name = "smsService")
	private SmsService smsService;

	@Resource(name = "trxLogDao")
	private TrxLogDao trxLogDao;
	
	@Autowired
	private OrderFilmService orderFilmService;
	@Autowired
	private VoucherDao voucherDao;
	
	PropertyUtil propertyUtil = PropertyUtil.getInstance("project");
	// 异常报警邮件
	public String merchant_api_voucher_email = propertyUtil
			.getProperty("merchant_api_voucher_email");
	@Autowired
	private FilmApiGoodsOrderService filmApiGoodsOrderService;
	@Autowired
	private FilmGoodsOrderDao filmGoodsOrderDao;
	
	// 扣款报警邮件模板
	public static final String MERCHANT_TRX_ERROR = "MERCHANT_TRX_ERROR";

	@Autowired
	private EmailService emailService;
	@Autowired
	private RefundService refundService;
	
	private final Log logger = LogFactory.getLog(FilmApiVoucherSendServiceImpl.class);
	
	@SuppressWarnings("unchecked")
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

		String smsTemplate = voucherParam.getSmsTemplate();


		String mobile = mobileAry[0];// 发送的手机号

		StringBuilder voucherSendLogSb = new StringBuilder();// 凭证发送日志内容
		TrxLog trxLog = new TrxLog(trxGoodsSn, new Date(),
				TrxLogType.TRXORDERGOODS, "服务密码发送成功");
		Map<String,String> condition = new HashMap<String,String>();
		condition.put("trxGoodsId",tg.getId().toString());
	
		FilmGoodsOrder fgo = new FilmGoodsOrder();
		try {
			List<Object> list = filmGoodsOrderDao.queryFilmGoodsOrderByCondition(condition);
			if(list!=null&&list.size()>0){
				fgo = (FilmGoodsOrder)list.get(0);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		
		try {
			 //调用网票网完成支付接口
			FilmApiOrderParam filmApiOrderParam = new FilmApiOrderParam();
			filmApiOrderParam.setFilmSid(fgo.getFilmTrxSn());
			filmApiOrderParam.setFilmPayNo(fgo.getFilmPayNo());
			filmApiOrderParam.setPlatformPayNo(tg.getVoucherId().toString());
			String rMsg = filmApiGoodsOrderService.updateFilmOrder(filmApiOrderParam);
			Map<String, String> tempMap = null;;
			try {
				tempMap = (Map<String, String>)new JSONParser().parse(rMsg);
				logger.info("-----------this wpw createorder result----"+"".equals(tempMap.get("RMsg").trim()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			logger.info("++++++++++++++++rMsg+++++++"+rMsg);
			if(tempMap != null &&"".equals(tempMap.get("RMsg").trim())){
				logger.info("-----------this wpw createorder result is ok----");
				// 短信参数
				Object[] smsParam = new Object[] { goodsName, trxGoodsSn,
						ordLoseDate };

				Sms sms = smsService.getSmsByTitle(smsTemplate);// 获取短信实体
				logger.info("-----------this wpw createorder sms----"+sms.getSmscontent());
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
				orderFilmService.updateStatusByTrxGoodsId(tg.getId());
				trxLog.setLogContent(voucherSendLogSb.toString());
				trxLogDao.addTrxLog(trxLog);
			}else{
				//将已使用订单做退款操作   
				Voucher voucher = voucherDao.findById(tg.getVoucherId());
				refundService.updateUsedByRefundtoact(voucher, tg);
				
				alertMerchantVoucher( voucherParam.getTrxorderGoods()
						.getTrxGoodsSn(), "调用完成支付API信息错误");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BaseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void reSendVoucher(VoucherParam voucherParam) {

		
		try {
			TrxorderGoods tg = voucherParam.getTrxorderGoods();
			if (!checkSendType(voucherParam)) {// 校验是否通过。应该在此处设异常，后期改造

				return;

			}

			FilmGoodsOrder fgo = new FilmGoodsOrder();
			try {
				Map<String,String> condition = new HashMap<String,String>();
				condition.put("trxGoodsId",tg.getId().toString());
				List<Object> list = filmGoodsOrderDao.queryFilmGoodsOrderByCondition(condition);
				if(list!=null&&list.size()>0){
					fgo = (FilmGoodsOrder)list.get(0);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
				FilmApiOrderParam filmApiOrderParam = new FilmApiOrderParam();
				filmApiOrderParam.setFilmSid(fgo.getFilmTrxSn());
				filmApiOrderParam.setFilmPayNo(fgo.getFilmPayNo());
				filmApiOrderParam.setPlatformPayNo(tg.getVoucherId().toString());
				String fiLmResendStr = filmApiGoodsOrderService.resendFilmCode(filmApiOrderParam);
				logger.info("++++++++++++++++rMsg+++++++"+fiLmResendStr);
				Map<String, String> tempMap = null;;
				try {
					tempMap = (Map<String, String>)new JSONParser().parse(fiLmResendStr);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if ("False".equalsIgnoreCase(tempMap.get("Flag"))) {
					alertMerchantVoucher(
							voucherParam.getTrxorderGoods().getTrxGoodsSn(), "调用重发API信息错误");
					

				} 
		} catch (Exception e) {
			alertMerchantVoucher(
					voucherParam.getTrxorderGoods().getTrxGoodsSn(), "调用重发API信息错误");
			e.printStackTrace();
		}
	}

	

	@Override
	public void transSendVoucher(VoucherParam voucherParam) {
		// TODO Auto-generated method stub
		
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
	 * 邮件报警方法
	 * 
	 * @param guestId
	 * @param branchId
	 */
	public void alertMerchantVoucher(String trxGoodsSn, String brrer) {

		// 发送内部报警邮件
		String alertEmailParams[] = {"网票网",
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
	
}
