package com.beike.core.service.trx.notify.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.entity.trx.TrxOrder;
import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.common.enums.trx.NotifyType;
import com.beike.common.enums.trx.TrxBizType;
import com.beike.common.exception.BaseException;
import com.beike.core.service.trx.impl.TrxOrderServiceImpl;
import com.beike.core.service.trx.notify.NotifyRecordBizService;
import com.beike.core.service.trx.soa.proxy.TrxSoaService;
import com.beike.dao.notify.TrxorderNotifyRecordDao;
import com.beike.dao.trx.TrxOrderDao;
import com.beike.dao.trx.soa.proxy.UserSoaDao;
import com.beike.entity.common.Sms;
import com.beike.entity.notify.TrxorderNotifyRecord;
import com.beike.form.SmsInfo;
import com.beike.service.common.SmsService;
import com.beike.util.DateUtils;
import com.beike.util.StringUtils;
import com.beike.util.TrxConstant;

/**
 * 订单到期短信业务实现
 * 
 * @author yurenli
 * 
 */
@Service("notifyRecordBizService")
public class NotifyRecordBizServiceImpl implements NotifyRecordBizService {
	@Autowired
	private UserSoaDao userSoaDao;
	@Resource(name = "smsService")
	private SmsService smsService;
	@Autowired
	private TrxorderNotifyRecordDao trxorderNotifyRecordDao;

	@Autowired
	private TrxOrderDao trxOrderDao;

	@Autowired
	private TrxSoaService trxSoaService;
	private final Log logger = LogFactory.getLog(TrxOrderServiceImpl.class);

	// 验证短信提醒模板
	public static final String SMS_TRXORDER_NOTIFY_INSPECT = "SMS_TRXORDER_NOTIFY_INSPECT";
	// 退款到账户提醒模板
	public static final String SMS_TRXORDER_NOTIFY_RETURN_ACT = "SMS_TRXORDER_NOTIFY_RETURN_ACT";
	// 退款到银行卡提醒模板
	public static final String SMS_TRXORDER_NOTIFY_RETURN_BANK = "SMS_TRXORDER_NOTIFY_RETURN_BANK";
	// 订单过期提醒模板
	public static final String SMS_TRXORDER_NOTIFY_OVERDUE = "SMS_TRXORDER_NOTIFY_OVERDUE";
	
	//短信合并修改模板
	private static final String SMS_TRXORDER_NOTIFY_END_OVERDUE = "SMS_TRXORDER_NOTIFY_END_OVERDUE";

	/*
	 * 订单被使用短信通知业务实现类 (non-Javadoc)
	 * 
	 * @see
	 * com.beike.core.service.trx.notify.NotifyRecordBizService#processNotifyInspect
	 * (com.beike.common.entity.trx.TrxorderGoods)
	 */
	@Override
	public void processNotifyByBizType(TrxorderGoods tog, TrxBizType bizType,boolean isSendSmsNotify) {
		
		if (!isSendSmsNotify) {
			return;
		}
		try {

			Long goodsId = tog.getGoodsId();
			List<Long> goodsIdList = new ArrayList<Long>();
			goodsIdList.add(goodsId);
			String hour = DateUtils.getHour();
			int hourInt = Integer.valueOf(hour);// 发送短信时间为9:00--21:00之间
			String trxGoodsSn = tog.getTrxGoodsSn();
			String trxSnStr = "";
			if (trxGoodsSn != null && trxGoodsSn.length() > 3) {
				trxSnStr = trxGoodsSn.substring(trxGoodsSn.length() - 3);
			}
			Map<Long, String> goodsTitleMap = trxSoaService.findGoodsTitle(goodsIdList);
			String goodsTitle = goodsTitleMap.get(goodsId);
			goodsTitle = StringUtils.cutffStr(goodsTitle,TrxConstant.smsVouGoodsNameCount, "");// 商品简称
			String payPrice = String.valueOf(tog.getPayPrice());
			TrxOrder trxOrder = trxOrderDao.findById(tog.getTrxorderId());

			// 获取用户ID
			Long userId = 0L;
			if (trxOrder != null) {
				userId = trxOrder.getUserId();
			}
			Map<String, Object> userMap = userSoaDao.findMobileById(userId);
			String mobile = "";// 手机号
			if (userMap != null) {
				mobile = (String) userMap.get("mobile");
			}
			logger.info("+++++++++++smsVoucher:mobile:" + mobile+ "+++trxgoodsSn:" + trxGoodsSn + "+++++++");
			if (tog.getPayPrice() > 1 || tog.getPayPrice() == 1) {
				if (8 < hourInt && hourInt < 21) {// 直接发送短信

					if (TrxBizType.INSPECT.equals(bizType)) {// 验证短信提醒

						Object[] smsParam = new Object[] { goodsTitle, trxSnStr };
						this.smsNotify(mobile, SMS_TRXORDER_NOTIFY_INSPECT,
								smsParam);

					} else if (TrxBizType.RETURNACT.equals(bizType)) {// 退款到账户提醒

						Object[] smsParam = new Object[] { goodsTitle,
								trxSnStr, payPrice };
						this.smsNotify(mobile, SMS_TRXORDER_NOTIFY_RETURN_ACT,
								smsParam);

					} else if (TrxBizType.RETURNBANK.equals(bizType)) {// 退款到银行卡提醒

						Object[] smsParam = new Object[] { goodsTitle,
								trxSnStr, payPrice };
						this.smsNotify(mobile, SMS_TRXORDER_NOTIFY_RETURN_BANK,
								smsParam);
					}
					logger.info("+++++++++++smsVoucher:mobile:" + mobile+ "+++trxgoodsSn:" + trxGoodsSn + "+++++++bizType="+ bizType.toString() + "+++++++++");
				} else {// 放入短信提醒队列
					TrxorderNotifyRecord tnr = new TrxorderNotifyRecord();
					tnr.setCreateDate(new Date());
					tnr.setNotifyType(NotifyType.REALTIME);
					tnr.setUserId(userId);
					if (TrxBizType.INSPECT.equals(bizType)) {// 验证短信提醒
						tnr.setBizType(TrxBizType.INSPECT);
						tnr.setExpress(goodsTitle + ":" + trxSnStr);
					} else if (TrxBizType.RETURNACT.equals(bizType)) {// 退款到账户提醒
						tnr.setBizType(TrxBizType.RETURNACT);
						tnr.setExpress(goodsTitle + ":" + trxSnStr + ":"
								+ payPrice);
					} else if (TrxBizType.RETURNBANK.equals(bizType)) {// 退款到银行卡提醒
						tnr.setBizType(TrxBizType.RETURNBANK);
						tnr.setExpress(goodsTitle + ":" + trxSnStr + ":"
								+ payPrice);
					}
					trxorderNotifyRecordDao.addTrxorderNotifyRecord(tnr);
					logger.info("+++++++++++smsVoucher:userId=" + userId
							+ "+++++++++date = " + new Date().toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("+++++++++++++++++" + e.getMessage()
					+ "+++++++++++++++++");
		}
	}

	/*
	 * 将订单短信提醒记录表中数据发送至用户service (non-Javadoc)
	 * 
	 * @see
	 * com.beike.core.service.trx.notify.NotifyRecordBizService#processNotifySms
	 * (com.beike.entity.notify.TrxorderNotifyRecord)
	 */
	@Override
	public void processNotifySms(TrxorderNotifyRecord tnr) {
		logger
				.info("++++++++++++++++++++processNotifySms start++++++++++++++++++++++++++++");
		try {
			String hour = DateUtils.getHour();
			int hourInt = Integer.valueOf(hour);// 发送短信时间为9:00--21:00之间
			Long userId = tnr.getUserId();
			Map<String, Object> userMap = userSoaDao.findMobileById(userId);
			String mobile = "";// 手机号
			if (userMap != null) {
				mobile = (String) userMap.get("mobile");
			}
			TrxBizType bizType = tnr.getBizType();
			NotifyType notifyType = tnr.getNotifyType();
			String express = tnr.getExpress();
			String[] expressArray = express.split(":");

			if (8 < hourInt && hourInt < 21) {
				if (TrxBizType.INSPECT.equals(bizType)
						&& NotifyType.REALTIME.equals(notifyType)) {// 验证短信提醒
					this.smsNotify(mobile, SMS_TRXORDER_NOTIFY_INSPECT,
							expressArray);
				} else if (TrxBizType.RETURNACT.equals(bizType)
						&& NotifyType.REALTIME.equals(notifyType)) {// 退款到账户提醒
					this.smsNotify(mobile, SMS_TRXORDER_NOTIFY_RETURN_ACT,
							expressArray);
				} else if (TrxBizType.RETURNBANK.equals(bizType)
						&& NotifyType.REALTIME.equals(notifyType)) {// 退款到银行卡提醒
					this.smsNotify(mobile, SMS_TRXORDER_NOTIFY_RETURN_BANK,
							expressArray);
				} else if (TrxBizType.OVERDUE.equals(bizType)
						&& NotifyType.TENDAYS.equals(notifyType)) {// 订单过期提醒10天
					this.smsNotify(mobile, SMS_TRXORDER_NOTIFY_END_OVERDUE,
							expressArray);
				} else if (TrxBizType.OVERDUE.equals(bizType)
						&& NotifyType.THREE.equals(notifyType)) {// 订单过期提醒3天
					this.smsNotify(mobile, SMS_TRXORDER_NOTIFY_END_OVERDUE,
							expressArray);
				}
				logger.info("+++++++++++++mobile=" + mobile
						+ "+++++++++++notifyType=" + notifyType.toString()
						+ "++++++bizType=" + bizType.toString());
				// 更新订单提醒记录表中状态
				trxorderNotifyRecordDao.updateAccountNotifyById(tnr.getId(),true);
			} else {
				return;

			}
		} catch (Exception e) {
			trxorderNotifyRecordDao.updateAccountNotifyById(tnr.getId(),false);
			e.printStackTrace();
			logger.info("+++++++++++++++++" + e.getMessage()
					+ "+++++++++++++++++");
		}
	}

	/**
	 * 发送短信接口
	 * 
	 * @param tog
	 * @param smsTemplate
	 * @throws BaseException
	 */
	public void smsNotify(String mobile, String smsTemplate, Object[] smsParam)
			throws BaseException {
		Sms sms = smsService.getSmsByTitle(smsTemplate);// 获取短信实体
		String template = sms.getSmscontent(); // 获取短信模板

		String contentResult = MessageFormat.format(template, smsParam);
		SmsInfo sourceBean = new SmsInfo(mobile, contentResult, "15", "1");
		smsService.sendSms(sourceBean);
	}

}
