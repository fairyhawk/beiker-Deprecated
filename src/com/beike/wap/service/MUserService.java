package com.beike.wap.service;

import com.beike.common.enums.user.ProfileType;
import com.beike.common.exception.AccountException;
import com.beike.common.exception.UserException;
import com.beike.entity.user.User;
import com.beike.entity.user.UserProfile;
import com.beike.service.GenericService;

/**
 * <p>Title:用户相关服务</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date  2011-9-22
 * @author kun.wang
 * @version 1.0
 */

public interface MUserService extends GenericService<User, Long>{
	/**
	 * 根据mobile查找用户
	 * @param mobile
	 * @return
	 */
	public User findUserByMobile(String mobile);
	
	/**
	 * 用户正常注册
	 * @param email		邮件
	 * @param mobile	手机
	 * @param password	密码
	 */
	public void addUser(String email,String mobile,String password);
	/**
	 * 用户email注册
	 * @param email		邮箱
	 * @param password	密码
	 */
	public User addUserEmailRegist(String email,String password);
	
	/**
	 * 用户mobile注册s
	 * @param mobile
	 * @param email
	 * @param password
	 * @param cusKey
	 * @return
	 * @throws AccountException
	 */

	public User addMobileRegist(String mobile, String email, String password, String cusKey) throws AccountException ;
	
	/**
	 * 用户是否存在
	 * @param mobile	手机
	 * @param email		email
	 * @return			
	 */
	public boolean isUserExist(String mobile,String email);
	
	/**
	 * 增加用户业务拓展信息
	 * @param profileName	 拓展属性名称
	 * @param profileValue	 拓展属性值
	 * @param userid		 用户id
	 * @param profileType    类型
	 */
	public void addProfile(String profileName,String profileValue,Long userid,ProfileType profileType)throws UserException;
	
	/**
	 * 根据用户id profilename 查询扩展属性
	 * @param userid		用户id
	 * @param profileName	拓展属性名称
	 * @return
	 */
	public UserProfile getProfile(Long userid,String profileName);
	
	
	/**
	 * 更新用户扩展信息
	 * @param userProfile	  
	 */
	public void updateProfile(UserProfile userProfile);
	
	/**
	 * 判断用户Email里的地址是否有效
	 * 1.先查该验证url 是否有效
	 * 2.判断是否超过了3天
	 * @param urlKey		url中的key
	 * @param userid		用户的userid
	 * @return				判断是否有效 true有效 flase无效
	 */
	public boolean isUrlUsable(String urlKey,Long userid,String emailType);
	
	/**
	 * 判断用户名密码是否匹配
	 * @param mobile		手机号
	 * @param password		密码
	 * @param email			email
	 * @return				true匹配 false不匹配
	 */
	public User isUserLogin(String mobile,String password,String email) throws UserException;
	
	/**
	 * 根据email查找用户
	 * @param email			email
	 * @return				返回User
	 */
	public User findUserByEmail(String email);
	
	
	/**
	 * 激活邮件状态
	 * @param user
	 */
	public void activationEmail(User user);
	
	/**
	 * 激活手机状态
	 * @param user
	 */
	public void activationMobile(User user,String validateMobile);
	
	/**
	 * 查询用户扩展信息
	 * @param userid		userid
	 * @param profileName	扩展名称
	 * @return
	 */
	public UserProfile getUserProfile(Long userid,String profileName);
	
	
	/**
	 * 更新用户信息
	 * @param user
	 */
	public void updateUserMessage(User user);
	
	public User getUserByUserKey(String userKey);
	
	/////////////////用户首页登录信息///////////////////
	
	/**
	 * 未评价订单个数
	 */
	public Long getUnusedTrxorder(Long userId);
	
	/**
	 * 用户快过期个数
	 * @param userId
	 * @return
	 */
	public Long readyLoseTrxorderCount(Long userId);
	
	/**
	 * 未评价个数
	 * @param userId
	 * @return
	 */
	public Long unComment(Long userId);
	
	/**
	 * 获得账户余额
	 */
	public double userbalance(Long userId);
}
