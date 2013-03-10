package com.beike.dao.trx;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.beike.common.entity.trx.RefundRecord;
import com.beike.common.enums.trx.PaymentType;
import com.beike.common.enums.trx.ProviderType;
import com.beike.common.enums.trx.RefundStatus;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.dao.GenericDao;

/**
 * @Title: RefundRecordDao.java
 * @Package com.beike.dao.trx
 * @Description: 退款记录DAO
 * @date May 24, 2011 2:15:14 PM
 * @author wh.cheng
 * @version v1.0
 */
@Repository("refundRecordDao")
public interface RefundRecordDao extends GenericDao<RefundRecord, Long> {

	public Long addRefundRecord(RefundRecord refundRecord);

	public RefundRecord findById(Long id);

	public List<RefundRecord>  findByOrdId(Long ordId);

	/**
	 * 根据商品订单明细ID查出退款记录
	 * 
	 * @param id
	 * @return
	 */
	public RefundRecord findByTrxGoodsId(Long trxGoodsId);

	public List<RefundRecord> findByUserId(Long userId);

	public void update(RefundRecord refundRecord);

	/**
	 * 根据ID更新账户退款状态
	 * 
	 * @param id
	 * @param refundStatus
	 * @return
	 */
	public void updateByIdAndRefundStatus(Long id,
			RefundStatus refundStatus,Long version) throws StaleObjectStateException;
	/**
	 * 根据状态和时间 商品订单号查询退款申请
	 * @param refundStatus
	 * @param date
	 * @param trxgoods
	 * @return List<RefundRecord>
	 */
	public List<RefundRecord>  findByStatusAndDate(RefundStatus refundStatus,String date,String trxgoods);
	/**
	 * 查询制定payment的退款申请
	 * @param paymentType
	 * @param providerType
	 * @param refundStatus
	 * @param date
	 * @return
	 */
	public List<Map<String, Object>> findByPayTypeAndStatusAndDate(PaymentType paymentType,ProviderType providerType, RefundStatus refundStatus,String startDate,String endDate,String payDate) ;
}
