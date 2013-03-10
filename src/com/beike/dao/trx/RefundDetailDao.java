package com.beike.dao.trx;

import java.util.Date;
import java.util.List;

import com.beike.common.entity.trx.RefundDetail;
import com.beike.common.enums.trx.PaymentType;
import com.beike.common.enums.trx.RefundStatus;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.dao.GenericDao;

/**
 * @Title: RefundDetailDao.java
 * @Package com.beike.dao.trx
 * @Description: 退款明细DAO
 * @date May 24, 2011 3:44:34 PM
 * @author wh.cheng
 * @version v1.0
 */
public interface RefundDetailDao extends GenericDao<RefundDetail, Long> {

	public Long addRefundDetail(RefundDetail refundDetail);

	public RefundDetail findById(Long id);

	// 根据trxId和pamentType\actRefundStatus查出所有已退款成功的金额
	public double findSucRudAmtByTrxId(Long trxId, PaymentType pamentType,
			RefundStatus actRefundStatus);

	// 根据recordId和pamentType\actRefundStatus查出所有已退款成功的金额
	public double findSucRudAmtByRecId(Long recId, PaymentType pamentType,
			RefundStatus actRefundStatus);

	public List<RefundDetail> findByRefundRecId(Long refundRecId);

	/**
	 * 根据支付机构流水号查询
	 * 
	 * @param id
	 * @return
	 */
	public List<RefundDetail> findByProExternalId(String proExternalId);

	public List<RefundDetail> findByPaymentId(Long paymentId);

	/**
	 * 根据支付机构退款 请求号查询
	 * 
	 * @param refundRequestId
	 * @return
	 */
	public List<RefundDetail> findByRefundRequestId(Long refundRequestId);

	public void update(RefundDetail refundDetail);

	/**
	 * 根据ID更新账户退款状态
	 * 
	 * @param id
	 * @param refundStatus
	 * @return
	 */
	public void updateByIdAndActStatus(Long id, RefundStatus actRefundStatus,Long version) throws StaleObjectStateException;

	/**
	 * 根据ID更新支付机构退款状态及操作时间和退款请求号
	 * 
	 * @param id
	 * @param refundStatus
	 * @return
	 */
	public void updateByIdAndProStatus(Long id, RefundStatus proRefundStatus,Date handleDate, String proRefundRedId,Long version,RefundStatus preProRefundStatus) throws StaleObjectStateException;

}
