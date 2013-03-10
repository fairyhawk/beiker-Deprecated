package com.beike.dao.trx;

import java.util.Map;

import com.beike.dao.GenericDao;

/**
 * @Title: MerVoucherDao.java
 * @Package com.beike.dao.trx
 * @Description: 商家凭证Dao接口
 * @date May 26, 2011 6:32:31 PM
 * @author wh.cheng
 * @version v1.0
 */
public interface MerVoucherDao extends GenericDao<Object, Long> {
	/**
	 * 根据商家ID和商家ID获取商家自有的凭证
	 * 
	 * @param guest
	 * @param goods
	 * @return
	 */
	public Map<String, Object> findByGtIdAndGdId(Long guest, Long goods);

	/**
	 * 根据商家自有凭证Id更新商家凭证状态
	 * 
	 * @param id
	 * @param voucherId
	 */
	public void updateMerVoucher(Long id, Long voucherId);

}
