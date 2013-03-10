package com.beike.core.service.trx.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.bean.trx.PaymentInfo;
import com.beike.common.entity.trx.Payment;
import com.beike.common.enums.trx.PaymentType;
import com.beike.common.enums.trx.TrxStatus;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.PaymentException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.common.guid.GuidGenerator;
import com.beike.core.service.trx.PaymentService;
import com.beike.dao.trx.PaymentDao;
import com.beike.util.Amount;
import com.beike.util.EnumUtil;
import com.beike.util.GuidEncryption;
import com.beike.util.StringUtils;

/**
 * @Title: VcPaymentServiceImpl.java
 * @Package com.beike.core.service.trx.impl
 * @Description: 虚拟币支付信息CORE Servie 实现类
 * @date May 17, 2011 4:44:39 PM
 * @author wh.cheng
 * @version v1.0
 */
@Service("actPayPaymentService")
public class ActPayPaymentServiceImpl implements PaymentService {

	private final Log logger = LogFactory
			.getLog(ActPayPaymentServiceImpl.class);
	@Autowired
	private PaymentDao paymentDao;

	@Resource(name = "guidGeneratorService")
	private GuidGenerator guidGenerator;

	public Payment create(PaymentInfo paymentInfo) throws PaymentException {

		// 生成序列号
		String requestId = guidGenerator.gainCode("A");// 虚拟币payment不保存requestId，只保存计数
		logger.info("requestId:" + requestId + "trxId:"	+ paymentInfo.getTrxorderId());

		String vcPaymentSn = GuidEncryption.encryptSimpler("V", requestId.substring(1, requestId.length() - 1)); // 以requestId为基数生成序列号，保证唯一

		if (vcPaymentSn == null) {
			vcPaymentSn = StringUtils.getSysTimeRandom();
		}

		Payment payment = new Payment(new Date(), vcPaymentSn, paymentInfo.getTrxAmount(), TrxStatus.INIT);

		// 组装数据
		payment.setAccountId(paymentInfo.getAccountId());
		payment.setTrxorderId(paymentInfo.getTrxorderId());
		payment.setPaymentType(paymentInfo.getPaymentType());
		payment.setCouponId(paymentInfo.getCouponId());
		paymentDao.addPayment(payment);
		payment.setId(paymentDao.getLastInsertId());

		return payment;
	}

	public List<Payment> complete(PaymentInfo paymentInfo,List<Payment> paymentList,boolean isToLoad)
			throws PaymentException, StaleObjectStateException {
		
		List<Payment>   actPayPaymentList=new ArrayList<Payment>();//1条/2条
 		double  trxOrderAmount=paymentInfo.getTrxOrderAmount();//交易订单原金额
 		double sumPaymentAmout=0.0;
		for(Payment  payement:paymentList){
			if(!TrxStatus.INIT.equals(payement.getTrxStatus())){
				throw new PaymentException(BaseException.PAYMENT_TRXSTATUS_INVALID);
			}
			if(!PaymentType.PAYCASH.equals(payement.getPaymentType())){//多做一次校验(进来的应全是账户支付的payment)
				actPayPaymentList.add(payement);//账户支付paymentList
				sumPaymentAmout=Amount.add(sumPaymentAmout, payement.getTrxAmount());//payment总金额
				}
			}
		
		//内部金额核对不平
		if(sumPaymentAmout!=trxOrderAmount){
			
			throw new PaymentException(BaseException.PAYMENTAMOUNT_TRXAMOUNT_NOT_EQUALS);
			
		}
		
		// 因为乐观锁问题，进行单条更新。最多两条。
		for (Payment itemPayment : actPayPaymentList) {
			itemPayment.setTrxStatus(TrxStatus.SUCCESS);
			itemPayment.setPayConfirmDate(new Date());
			paymentDao.updatePayment(itemPayment);

		}
		
		paymentList.clear();
		paymentList.addAll(actPayPaymentList);
		
		
		return paymentList;
	}

	/**
	 * 预查询查询相关支付记录
	 * @param trxId
	 * @return
	 */
	public  List<Payment> preQryPayment(String trxIdStr){
		
		StringBuilder paymentTypeSb = new StringBuilder();
		paymentTypeSb.append("'");
		paymentTypeSb.append(EnumUtil.transEnumToString(PaymentType.ACTVC));
		paymentTypeSb.append("'");

		List<Payment> actPaymentList = paymentDao.findByTrxIdAndTypeAndStatus(Long.valueOf(trxIdStr), TrxStatus.INIT, paymentTypeSb.toString());

		return actPaymentList;
		
	}
	
	/**
	 * 根据主键查询Payemnt
	 */
	public Payment findById(Long paymentId) {

		Payment actPayment = paymentDao.findById(paymentId);
		return actPayment;

	}
	
	

}
