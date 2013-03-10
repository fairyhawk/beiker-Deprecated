package com.beike.dao.vm;

import java.util.List;

import com.beike.common.entity.vm.VmTrxExtend;
import com.beike.common.enums.vm.RelevanceType;
import com.beike.dao.GenericDao;

/**
 * <p>
 * Title:交易关联扩展接口
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: Sinobo
 * </p>
 * 
 * @date 2011-11-16 14:14:07
 * @author renli.yu
 * @version 1.0
 */
public interface VmTrxExtendDao extends GenericDao<VmTrxExtend, Long> {

	/**
	 * 根据主键id查询子账户实体 renli.yu
	 * 
	 * @param id
	 * @return
	 */
	public VmTrxExtend findById(Long id);

	/**
	 * 根据个人总账户ID、交易ID和类型查询帐务扩展记录 （查询条件个人总账户ID是多余的，
	 * 但是这是退款啊，风险啊，多个条件更安全啊，强迫症啊有木有）* * (子账户ID逆序排列)
	 * 
	 * @param actId
	 * @param trxOrderId
	 * @param relevanceType
	 * @return
	 */
	public List<VmTrxExtend> findByTrxIdAndType(Long actId, Long trxOrderId,
			RelevanceType relevanceType);

	/**
	 * 根据个人总账户ID和交易ID查询帐务扩展记录 （查询条件个人总账户ID是多余的， 但是这是退款啊，风险啊，多个条件更安全啊，强迫症啊有木有）*
	 * 子账户ID逆序排列
	 * 
	 * @param actId
	 * @param trxOrderId
	 * @return
	 */
	public List<VmTrxExtend> findByTrxId(Long actId, Long trxOrderId);

	/**
	 * 更新子账户实体
	 * 
	 * @param vmAccount
	 */
	public void updateVmTrxExtend(VmTrxExtend vmTrxExtend);

	/**
	 * 新添加子账户实体
	 * 
	 * @param vmAccount
	 */
	public Long addVmTrxExtend(VmTrxExtend vmTrxExtend);

}
