package com.beike.core.service.trx;

import java.util.List;
import java.util.Map;

import com.beike.common.entity.trx.RebRecord;
import com.beike.common.enums.trx.OrderType;
import com.beike.common.enums.trx.TrxStatus;
import com.beike.common.exception.RebateException;

/**
 * @Title: RebRecordService.java
 * @Package com.beike.core.service.trx
 * @Description:返现记录处理类
 * @date May 9, 2011 6:40:17 PM
 * @author wh.cheng
 * @version v1.0
 */
public interface RebRecordService {

	public RebRecord create(RebRecord rebRecord) throws RebateException;

	public RebRecord update(RebRecord rebRecord) throws RebateException;

	public void updateStatus(Long id, TrxStatus TrxStatus)
			throws RebateException;

	// 完成返现
	public RebRecord complete(RebRecord rebRecord) throws RebateException;

	public Map<String, String> isRebateByTrxGoodsId(Long trxGoodsId)
			throws RebateException;

	public double findRebateAmountByUserId(Long userId);

	public List<RebRecord> listRebRecordByUserId(Long userId, int startRow,
			int pageSize);

	public int findRowCountByUserIdAndStatus(Long userId, String status);

	/**
	 * 用户名鉴权
	 * 
	 * @param userLoginName
	 * @return
	 * @throws RebateException
	 */
	public Map<String, String> validateAcccount(String userLoginName)
			throws RebateException;

	/**
	 * 根据订单类型查询订单号是否重复
	 * 
	 * @param requestId
	 * @param orderType
	 * @return
	 */
	public boolean isRequestIdRepeat(String requestId, OrderType orderType);
	

}
