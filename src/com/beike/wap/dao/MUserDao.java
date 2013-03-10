package com.beike.wap.dao;

import com.beike.dao.GenericDao;
import com.beike.entity.user.User;
import com.beike.entity.user.UserProfile;
import com.beike.form.ProfileForm;
import com.beike.form.UserForm;

/**
 * 用户数据处理查询工具接口
 * @author kun.wang
 */
public interface MUserDao extends GenericDao<User,Long>{
	
	/**
	 * 通过Email查询User对象
	 * @param email 邮件地址
	 * @author kun.wang
	 * @return User对象
	 */
	public User findUserByEmail(String email);
	
	/**
	 * 通过手机号查询User对象
	 * @param mobile 手机号码
	 * @author kun.wang
	 * @return User对象
	 */
	public User findUserByMobile(String mobile);
	
	/**
	 *  增加用户
	 * @param userForm
	 */
	public void addUser(UserForm userForm);
	
	/**
	 * find user by user id
	 * @param 
	 */
	public User findById(long userId);
	
	/**
	 * 增加userprofile
	 * @param profile
	 * @return
	 */
	public void addProfile(ProfileForm profile);
	
	/**
	 * 更新扩展信息
	 * @param userProfile	
	 */
	public void updateUserProfile(UserProfile userProfile);
	
	/**
	 * 查出该用户某个profile属性值
	 * @param userid	用户id
	 * @param value		profile属性值
	 * @param name		profile属性名称
	 * @return			返回用户扩展属性
	 */
	public UserProfile getUserProfile(Long userid,String name);
	
}
