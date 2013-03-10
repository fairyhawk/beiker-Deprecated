package com.beike.wap.dao;

import com.beike.dao.GenericDao;
import com.beike.wap.entity.MUserTemp;

/**
 * 用户注册临时表数据处理类
 */
public interface MUserProfileWapDao  extends GenericDao<MUserTemp,Long>{
	
	/**
	 * 根据电话号码删除信息
	 * @param mobile 手机号
	 */
	public void deleteByMobile(String mobile);
	
	/**
	 * 验证该号码是否存在于临时表中
	 * @param mobile 手机号
	 */
	public int isMobileExist(String mobile);
	
	/**
	 * 根据id，验证码查询数据确认激活
	 * @param id
	 * @param code
	 */
	public int findByIdAndCode(long id, String vCode);
	
	/**
	 * 插入数据
	 * @param userTemp 临时对象
	 */
	public void addUserTemp(MUserTemp userTemp);
	
	/**
	 * 更新数据
	 */
	public void updateByMobile(MUserTemp userTemp);
	
	/**
	 * 更新密码
	 */
	public void updatePassword(MUserTemp userTemp);
	
	/**
	 * 根据mobile查询对象
	 */
	public MUserTemp findByMobile(String mobile);
	
	public MUserTemp findById(long id);
	
	/**
	 * 根据id删除数据
	 * @param id
	 */
	public void deleteById(Long id);
}
