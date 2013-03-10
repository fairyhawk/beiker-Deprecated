package com.beike.dao.trx;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.beike.common.entity.trx.TrxOrder;
import com.beike.common.entity.trx.Voucher;
import com.beike.common.enums.trx.TrxStatus;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.dao.GenericDao;

/**
 * @Title: TrxOrderDao.java
 * @Package com.beike.dao.trx
 * @Description: 交易订单DAO
 * @date May 16, 2011 10:47:50 AM
 * @author wh.cheng
 * @version v1.0
 */
public interface TrxOrderDao extends GenericDao<TrxOrder, Long> {

	public void addTrxOrder(TrxOrder trxOrder);

	public void updateTrxOrder(TrxOrder trxOrder) throws StaleObjectStateException;

	public TrxOrder findByRequestId(String requestId);

	public TrxOrder findByExternalId(String externalId);

	public TrxOrder findById(Long id);
	
	/**
	 * @param userIdList
	 * @return
	 * @author wz.gu for tuan800 order status change notify 2012-11-09
	 */
	public TrxOrder findByUserId(List<Long> userIdList);

	public TrxOrder findByExIdAndStatus(String externalId, TrxStatus trxStatus);

	public void updateStatusById(Long id, TrxStatus trxStatus, Date closeDate,Long version) throws StaleObjectStateException;

	public void updateStatusByExId(String externalId, TrxStatus trxStatus,Date closeDate,Long version) throws StaleObjectStateException;
	
	/**
	 * 根据分销商订单号查询分销商订单信息
	 * @param userId
	 * @param outRequestId
	 * @return
	 */
	public TrxOrder findByUserIdOutRequestId(String outRequestId,List<Long>  userIdList);
	
	
	/**
	 * 分销商更新手机号
	 * @param trxorderId
	 * @param mobile
	 * @throws StaleObjectStateException
	 */
	public void updateMobileById(Long trxorderId,String mobile) throws StaleObjectStateException ;

	/**
     * 查询商品订单列表 分页 
     *@param condition
     *@param startRow 
     *@param pageSize
     *startRow和pageSize都为0时不分页 查询所有的
     *@return List<Map<String, Object>> 
     */
    public List<Map<String, Object>> queryTrxGoodsIds(Map<String, String> condition, Boolean isHistory);
    
    /**
     * 查询多个商品订单凭证
     * @param voucherIds
     * isHistory 为true查询历史记录
     * @return List<Voucher>
     */
    public List<Voucher> findConfrimTimeByTrxgoodsIds(String voucherIds,Boolean isHistory);
    
}
