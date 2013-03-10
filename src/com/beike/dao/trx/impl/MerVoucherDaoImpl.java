package com.beike.dao.trx.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.trx.MerVoucherDao;

/**
 * @Title: MerVoucherDao.java
 * @Package com.beike.dao.trx
 * @Description: 商家凭证Dao实现
 * @date May 26, 2011 6:32:31 PM
 * @author wh.cheng
 * @version v1.0
 */
@Repository("merVoucherDao")
public class MerVoucherDaoImpl extends GenericDaoImpl<Object, Long> implements
		MerVoucherDao {

	/**
	 * 根据商家ID和商家ID获取商家自有的凭证
	 * 
	 * @param guest
	 * @param goods
	 * @return
	 */
	@Override
	public Map<String, Object> findByGtIdAndGdId(Long guestId, Long goodsId) {

		String sql = "select id as id,mer_voucher_code as  merVoucherCode from beiker_mer_voucher where   is_gain=0  and guest_id=? and goods_id=? limit 1 for update";
		List<Map<String, Object>> rspList = this.getSimpleJdbcTemplate()
				.queryForList(sql, guestId, goodsId);

		if (rspList == null || rspList.size() == 0) {
			return null;
		}
		return rspList.get(0);
	}

	/**
	 * 根据商家自有凭证Id更新商家凭证状态
	 * 
	 * @param id
	 * @param voucherId
	 */

	@Override
	public void updateMerVoucher(Long id, Long voucherId) {

		String sql = "update beiker_mer_voucher set is_gain=1,voucher_id=? where id=?";
		this.getSimpleJdbcTemplate().update(sql, voucherId, id);
	}

}
