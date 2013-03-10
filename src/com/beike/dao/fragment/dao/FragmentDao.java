package com.beike.dao.fragment.dao;

import java.util.List;

import com.beike.entity.common.Fragment;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date Jul 4, 2011
 * @author ye.tian
 * @version 1.0
 */

public interface FragmentDao {
	
	/**
	 * 查询Fragment
	 * @param city
	 * @param page
	 * @param name
	 * @return
	 */
	public Fragment getFragment(String city,String page,String name);
	
	/**
	 * 更新
	 * @param fragment
	 */
	public void updateFragment(Fragment fragment);
	
	
	/**
	 * 新增
	 * @param fragment
	 */
	public void insertFragment(Fragment fragment);
	
	
	public List<Fragment> getFragment(String city,String page);
	
	
	public void insertTestData(String city);
}
