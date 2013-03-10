package com.beike.dao.trx;    

import com.beike.common.entity.trx.TrxLog;
import com.beike.dao.GenericDao;

/**   
 * @Title: TrxLogDao.java
 * @Package com.beike.dao.trx
 * @Description: TODO
 * @date Jun 30, 2011 5:11:11 PM
 * @author wh.cheng
 * @version v1.0   
 */
public interface TrxLogDao extends GenericDao<TrxLog,Long>{
	
	
	public Long addTrxLog(TrxLog  trxLog);
	
	
	public TrxLog findTtxLogById(Long id);
	
	public void updateTrxLog(Long id,String content);

}
 