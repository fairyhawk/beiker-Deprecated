package com.beike.dao.trx;

import java.util.Date;
import java.util.List;

import com.beike.common.entity.trx.RebRecord;
import com.beike.common.enums.trx.TrxStatus;
import com.beike.dao.GenericDao;

/**
 * @Title: RebRecordDao.java
 * @Package com.beike.dao.trx
 * @Description: 虚拟币返现DAO
 * @date May 9, 2011 2:16:29 PM
 * @author wh.cheng
 * @version v1.0
 */
public interface RebRecordDao extends GenericDao<RebRecord, Long> {

	public void addRebRecord(RebRecord rebRecord);

	public RebRecord findById(Long id);

	public void update(RebRecord rebRecord);

	public RebRecord findByExternalId(String externalId);

	public void updateStatusByExId(String externalId, TrxStatus trxStatus,
			Date closeDate);

	// 下发成功虚拟币总数
	public double findSucSumByUserId(Long userId);

	// 返现List

	public List<RebRecord> findListByList(Long userId, int startRow,
			int pageSize);

	public int findRowCountByUserIdAndStatus(Long userId, String status);

	public RebRecord findRebByreqIdAndType(String requestId, String orderType);
}
