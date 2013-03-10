package com.beike.dao.user;


import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.beike.dao.GenericDao;
import com.beike.entity.user.User;
import com.beike.entity.user.UserProfile;
import com.beike.form.ProfileForm;
import com.beike.form.UserForm;

/**
 * <p>Title:用户相关接口</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date May 5, 2011
 * @author ye.tian
 * @version 1.0
 */

public interface UserDao extends GenericDao<User, Long>{
	/**
	 * 根据id查找User
	 * @param id	用户主键
	 * @return		返回User对象
	 */
	public User findById(Long id);
	
	/**
	 * 根据手机号查找用户
	 * @param mobile		手机号
	 * @return				用户对象
	 */
	public User findUserByMobile(String mobile);
	
	
	/**
	 * 根据email查找用户
	 * @param email			邮箱
	 * @return				用户对象
	 */
	public User findUserByEmail(String email);
	
	/**
	 * 根据用户名（mobile或者email）和密码查找用户
	 * @param mobile		手机号
	 * @param email			邮箱
	 * @param password		MD5加密后密码
	 * @return				用户对象
	 */
	public User findUserByPassword(String mobile,String email,String password);
	
	/**
	 *  增加用户
	 * @param userForm
	 */
	public void addUser(UserForm userForm);
	/**
	 *  增加用户__针对添加user ip字段
	 * @param userForm
	 */
	public void addUser_new(UserForm userForm);
	/**
	 * 用户增加profile
	 * @param proFileForm
	 */
	public void addProfile(ProfileForm proFileForm);
	
	/**
	 * 查出该用户某个profile属性值
	 * @param userid	用户id
	 * @param value		profile属性值
	 * @param name		profile属性名称
	 * @return			返回用户扩展属性
	 */
	public UserProfile getUserProfile(Long userid,String value,String name);
	
	
	/**
	 * 更新扩展信息
	 * @param userProfile	
	 */
	public void updateUserProfile(UserProfile userProfile);
	
	/**
	 * 更新用户信息
	 * @param userForm
	 */
	public void updateUser(UserForm userForm);
	
	public User getUserByUserKey(String userKey);
	
	////////////////////首页统计需要/////////////////////////
	
	/**
	 * 根据用户id 查询未评价数量
	 */
	public Long getUnusedTrxorder(Long userId);
	
	/**
	 * 查询该用户的 时间列表
	 * @param userId
	 * @return
	 */
	public List<Map<String,Object>> readyLoseTrxorder(Long userId);
	
	/**
	 * 获得未评价个数
	 */
	public Long unComment(Long userId);
	
	/**
	 * 
	 * @Title: updateUserPassWord
	 * @Description: 修改某个用户的密码
	 * @param  userId
	 * @param  password
	 * @return void
	 * @author wenjie.mai
	 */
	public void updateUserPassWord(Long userId,String password);
	
	/**
	 * 
	 * @Title: getUserInfoByUserIds
	 * @Description: 批量查询用户信息
	 * @param 
	 * @return List
	 * @author wenjie.mai
	 */
	public List getUserInfoByUserIds(List<Long> idlist);
}
