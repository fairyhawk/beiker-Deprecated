package com.beike.dao.vm;


import com.beike.common.entity.vm.SubAccount;
import com.beike.common.entity.vm.VmCancelRecord;
import com.beike.dao.GenericDao;

public interface VmCancelRecordDao extends GenericDao<VmCancelRecord, Long> {


	/**
	 * 根据主键id查询子账户历史表实体 renli.yu
	 * 
	 * @param id
	 * @return
	 */
	public SubAccount findById(Long id);

	/**
	 * 添加子账户历史表实体
	 * 
	 * @param vmCancelRecord
	 */
	public Long addVmCancelRecord(VmCancelRecord vmCancelRecord);
	
}
