package com.beike.dao.trx;

import java.util.Date;
import java.util.List;

import com.beike.common.entity.trx.Payment;
import com.beike.common.enums.trx.PaymentType;
import com.beike.common.enums.trx.ProPayStatus;
import com.beike.common.enums.trx.TrxStatus;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.dao.GenericDao;

/**
 * @Title: PaymentDao.java
 * @Package com.beike.dao.trx
 * @Description:支付信息DAO
 * @date May 16, 2011 2:43:23 PM
 * @author wh.cheng
 * @version v1.0
 */

public interface PaymentDao extends GenericDao<Payment, Long> {

	public void addPayment(Payment payment);

	public void updatePayment(Payment payment) throws StaleObjectStateException;

	public Payment findById(Long id);

	public Payment findByPayReqId(String payRequestId);

	public Payment findByPayReqIdAndType(String payRequestId,
			ProPayStatus proPayStatus, PaymentType paymentType);

	public Payment findBySn(String sn);

	public Payment findByTrxIdAndType(Long trxId, PaymentType paymentType,
			TrxStatus trxStatus);

	public Payment findByTrxIdAndType(Long trxId, PaymentType paymentType);

	/**
	 * 根据交易订单Id、payemntType、trxStatus查询PaymentList
	 * 
	 * @param trxId
	 * @param trxStatus
	 * @param paymentType
	 * @return
	 */
	public List<Payment> findByTrxIdAndTypeAndStatus(Long trxId,
			TrxStatus trxStatus, String paymentType);

	public List<Payment> findByTrxId(Long trxId);

	public void updatePayStatusByReqId(String payReqId,
			ProPayStatus proPaySatus, Long version)
			throws StaleObjectStateException;

	public void updatePayStatusBySn(String sn, ProPayStatus proPayStatus,
			Date payConfirmDate, Long version) throws StaleObjectStateException;

	public void updatePayStatusByReqId(String payReqId,
			ProPayStatus proPayStatus, Date payConfirmDate, Long version)
			throws StaleObjectStateException;

	public void updateTrxSatusBySn(String sn, TrxStatus trxStatus, Long version)
			throws StaleObjectStateException;

}
