package com.beike.dao.merchant;

import java.util.List;

import com.beike.dao.GenericDao;
import com.beike.entity.merchant.BranchProfile;

/**
 * <p>
 * Title:店铺属性数据库操作
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
 * @author qiaowb
 * @version 1.0
 */

public interface BranchProfileDao extends GenericDao<BranchProfile, Long> {
	/**
	 * 通过店铺ID获取店铺属性信息
	 * @param ids
	 * @return
	 */
	List<BranchProfile> getBranchProfileById(String ids);
	
	
	public BranchProfile getBranchProfileById(Long branchid);
}
