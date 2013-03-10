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
import com.beike.common.enums.trx.ProCheckStatus;
import com.beike.common.enums.trx.ProPayStatus;
import com.beike.common.enums.trx.ProRefundStatus;
import com.beike.common.enums.trx.ProSettleStatus;
import com.beike.common.enums.trx.ProviderType;
import com.beike.common.enums.trx.TrxStatus;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.PaymentException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.common.guid.GuidGenerator;
import com.beike.core.service.trx.PaymentService;
import com.beike.dao.trx.PaymentDao;
import com.beike.util.Amount;
import com.beike.util.Constant;
import com.beike.util.EnumUtil;
import com.beike.util.GuidEncryption;
import com.beike.util.PropertyUtil;
import com.beike.util.StringUtils;

/**
 * @Title: PaymentServiceImpl.java
 * @Package com.beike.core.service.trx.impl
 * @Description:在线支付支付信息CORE Servie 实现类
 * @date May 17, 2011 3:33:47 PM
 * @author wh.cheng
 * @version v1.0
 */
@Service("cashPayPaymentService")
public class CashPayPaymentServiceImpl implements PaymentService {

	private final Log logger = LogFactory
			.getLog(CashPayPaymentServiceImpl.class);
	@Autowired
	private PaymentDao paymentDao;

	@Resource(name = "guidGeneratorService")
	private GuidGenerator guidGenerator;

	private final PropertyUtil propertyUtil = PropertyUtil
			.getInstance(Constant.PAY_INFO_PROPER_NAME);
	private final String payRequestFix = propertyUtil
			.getProperty(Constant.PAY_REQUEST_FIX);

	public Payment create(PaymentInfo paymentInfo) throws PaymentException {
		if (paymentInfo.getTrxAmount() < 0.01) {
			throw new PaymentException(
					BaseException.PAYMENT_TRXAMOUNT_TOO_SMALL);
		}

		// 生成序列号

		String payRequestId = guidGenerator.gainCode("Pay");
		logger.info("requestId:" + payRequestId + "trxId:"
				+ paymentInfo.getTrxorderId());

		String cashPaymentSn = GuidEncryption.encryptSimpler("C", payRequestId
				.substring(3, payRequestId.length() - 1)); //

		if (payRequestId == null) {
			payRequestId = payRequestFix + "Pay"
					+ StringUtils.getSysTimeRandom();

		}
		if (cashPaymentSn == null) {
			cashPaymentSn = StringUtils.getSysTimeRandom();

		}

		Payment payment = new Payment(new Date(), cashPaymentSn, paymentInfo
				.getTrxAmount(), TrxStatus.INIT);
		// 组装在线支付的Payment信息
		payment.setProviderType(EnumUtil.transStringToEnum(ProviderType.class,
				paymentInfo.getProviderType()));// 支付机构
		payment.setPayChannel(paymentInfo.getPayChannel());// 支付通道
		payment.setAccountId(paymentInfo.getAccountId());
		payment.setPayRequestId(payRequestFix + payRequestId);
		payment.setProCheckStatus(ProCheckStatus.UNCHECK);
		payment.setProPayStatus(ProPayStatus.INIT);
		payment.setProRefundStatus(ProRefundStatus.UNREFUND);
		payment.setProSettleStatus(ProSettleStatus.UNSETTLEED);
		payment.setTrxStatus(TrxStatus.INIT);
		payment.setTrxorderId(paymentInfo.getTrxorderId());
		payment.setPaymentType(PaymentType.PAYCASH);
		paymentDao.addPayment(payment);
		payment.setId(paymentDao.getLastInsertId());
		return payment;
	}

	public List<Payment> complete(PaymentInfo paymentInfo,List<Payment> paymentList,boolean isToLoad)
			throws PaymentException, StaleObjectStateException {
		
		List<Payment>   actPayPaymentList=new ArrayList<Payment>();//0条/1条/2条
 		Payment cashPayPayment=null;//现金支付payment
 		double  trxOrderAmount=paymentInfo.getTrxOrderAmount();//交易订单原金额
 		double sumPaymentAmout=0.0;
 		String payReqId="";
		for(Payment  payement:paymentList){
			if(!TrxStatus.INIT.equals(payement.getTrxStatus())){
				throw new PaymentException(BaseException.PAYMENT_TRXSTATUS_INVALID);
				
			}
			if(PaymentType.PAYCASH.equals(payement.getPaymentType())){
				cashPayPayment=payement;//只且只有一笔
				payReqId=payement.getPayRequestId();
			}else{
				actPayPaymentList.add(payement);//账户支付paymentList
			}
				sumPaymentAmout=Amount.add(sumPaymentAmout, payement.getTrxAmount());//payment总金额
		}
		
		logger.info("+++payReqId:"+payReqId+"++++cashPayPaymentAmount:"+cashPayPayment.getTrxAmount()+"+++++++++++callBackAmount"+ paymentInfo.getTrxAmount());
		//如果回调回来的金额和平台对应的payment不等
		if (cashPayPayment.getTrxAmount() != paymentInfo.getTrxAmount()) {
			throw new PaymentException(BaseException.PAYMENTAMOUNT_PROVIDERAMOUNT_NOT_EQUALS);
		}
		
		logger.info("+++payReqId:"+payReqId+"++++sumPaymentAmout:"+sumPaymentAmout+"+++++++++++trxOrderAmount"+trxOrderAmount);
		//如果非变更为充值，则核对内部金额
		if(!isToLoad){
			if(sumPaymentAmout!=trxOrderAmount){
			
				throw new PaymentException(BaseException.PAYMENTAMOUNT_TRXAMOUNT_NOT_EQUALS);
			
			}
		}
		
				
		cashPayPayment.setPayConfirmDate(paymentInfo.getPayConfirmDate());
		cashPayPayment.setProExternalId(paymentInfo.getProExternalId());
		cashPayPayment.setTrxStatus(TrxStatus.SUCCESS);
		cashPayPayment.setProPayStatus(ProPayStatus.SUCCESS);
		paymentDao.updatePayment(cashPayPayment);
		
		
		//如果非变更为充值，则更新账户支付相关Payment
		// 因为乐观锁问题，进行单条更新。最多两条。
		if(!isToLoad){
			if (actPayPaymentList.size() > 0) {

				for (Payment itemPayment : actPayPaymentList) {
					itemPayment.setTrxStatus(TrxStatus.SUCCESS);
					itemPayment.setPayConfirmDate(new Date());
					paymentDao.updatePayment(itemPayment);

				}
			}
		}
		
		
		paymentList.clear();
		paymentList.add(cashPayPayment);
		paymentList.addAll(actPayPaymentList);
		return paymentList;
	}
	

	/**
	 * 预查询查询相关支付记录
	 * @param trxId
	 * @return
	 */
	public  List<Payment> qryPaymentByPayReqId(String  payRequestId){
		Payment cashPayPayment = paymentDao.findByPayReqIdAndType(payRequestId, ProPayStatus.INIT, PaymentType.PAYCASH);
		if (cashPayPayment == null) { // 无此支付记录或者已被回调更新
			return null;
		}
		List<Payment> paymentList = new ArrayList<Payment>();
		paymentList.add(cashPayPayment);
		return paymentList;
		
	}

	/**
	 * 根据主键查询Payemnt
	 */
	public Payment findById(Long paymentId) {
		Payment cashPayment = paymentDao.findById(paymentId);
		return cashPayment;

	}
	
	
	/**
	 * 预查询查询相关支付记录(暂无相关服务调用此cashservice的实现方法。若使用，需测试)
	 * @param trxId
	 * @return
	 */
	public  List<Payment> preQryPayment(String trxIdStr){
		
		StringBuilder paymentTypeSb = new StringBuilder();
		paymentTypeSb.append("'");
		paymentTypeSb.append(EnumUtil.transEnumToString(PaymentType.ACTVC));
		paymentTypeSb.append("'");
		paymentTypeSb.append(",");
		paymentTypeSb.append("'");
		paymentTypeSb.append(EnumUtil.transEnumToString(PaymentType.ACTCASH));
		paymentTypeSb.append("'");
		paymentTypeSb.append(",");
		paymentTypeSb.append("'");
		paymentTypeSb.append(EnumUtil.transEnumToString(PaymentType.PAYCASH));
		paymentTypeSb.append("'");
		List<Payment> actPaymentList = paymentDao.findByTrxIdAndTypeAndStatus(
				Long.valueOf(trxIdStr), TrxStatus.INIT, paymentTypeSb.toString());

		return actPaymentList;
		
	
		
	}

}
