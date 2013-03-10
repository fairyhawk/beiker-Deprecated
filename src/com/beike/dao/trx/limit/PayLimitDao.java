package com.beike.dao.trx.limit;

import java.util.List;

import com.beike.common.entity.trx.limit.PayLimit;
import com.beike.dao.GenericDao;

public interface PayLimitDao extends GenericDao<PayLimit, Long> {

	/**
	 * 保存
	 * 
	 * @param payLimit
	 * @return
	 */
	public void savePayLimit(PayLimit payLimit);

	/**
	 * 更新
	 * 
	 * @param payLimit
	 */
	public void updatePayLimit(PayLimit payLimit);

	/**
	 * 根据主键id查询
	 * 
	 * @param id
	 * @return
	 */
	public PayLimit findById(Long id);

	/**
	 * 根据userid和goodsid查询.goodsid 以","分隔
	 * 
	 * @param uId
	 * @param gIdStr
	 * @return
	 */
	public List<PayLimit> findUseridAndGoodsIdStr(Long uId, String gIdStr);

	/**
	 * 根据userid和goodsid查询
	 * 
	 * @param payLimit
	 * @return
	 */
	public PayLimit findUseridAndGoodsid(Long UId, Long GId,Long miaoshaId);

}
