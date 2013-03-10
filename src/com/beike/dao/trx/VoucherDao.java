package com.beike.dao.trx;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.beike.common.entity.trx.Voucher;
import com.beike.common.enums.trx.VoucherStatus;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.dao.GenericDao;

/**
 * @Title: VoucherDao.java
 * @Package com.beike.dao.trx
 * @Description: 凭证Dao接口
 * @date May 26, 2011 6:32:31 PM
 * @author wh.cheng
 * @version v1.0
 */
public interface VoucherDao extends GenericDao<Voucher, Long> {

	public Voucher findById(Long id);

	public List<Voucher> findByGuestId(Long guestId);

	public Voucher findInit(VoucherStatus voucherStatus);

	/**
	 * 预取凭证
	 * 
	 * @param voucherStatus
	 * @param prefetchFlag
	 * @param prefetchCount
	 * @return
	 */
	public List<Voucher> findBatchVoucherForPre(VoucherStatus voucherStatus,int proPrefetchFlag, int prefetchCount);

	/**
	 * 批量更新凭证
	 * 
	 * @param voucherStatus
	 * @param prefetchFlag
	 * @param prefetchCount
	 * @return
	 */
	public int updateBatchVoucherForPre(List<Long> vouIdList,VoucherStatus proVoucherStatus, int postPrefetchFlag,int proPrefetchFlag);

	public Long addVoucher(Voucher voucher);

	public int findByDateAndStatus(Date curDate, VoucherStatus voucherStatus);

	public Voucher findByGuestIdAndCode(Long guestId, String voucherCode);

	public void update(Voucher voucher) throws StaleObjectStateException;

	public void updateStatusByIdAndDate(Long id, VoucherStatus voucherStatus,Date confirmDate, Long version) throws StaleObjectStateException;

	public void updateByGuestIdAndStatus(Long id, VoucherStatus voucherStatus,Long guestId, Long version) throws StaleObjectStateException;

	public Voucher findByVoucherCode(String voucherCode);
	
	public List<Voucher> findByVoucherCodes(String voucherCodes);
	
	/**
	 *根据激活时间和状态获取凭证及订单信息
	 * @param activeDate
	 * @return
	 */
	public List<Map<String,Object>> findByActiveDateAndStatus(Date startTime, Date endTime, String userIdStr,String trxStatusStr);

}
