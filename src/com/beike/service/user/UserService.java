package com.beike.service.user;


import java.util.List;
import com.beike.common.enums.user.ProfileType;
import com.beike.common.exception.AccountException;
import com.beike.common.exception.UserException;
import com.beike.entity.user.User;
import com.beike.entity.user.UserProfile;
import com.beike.form.UserForm;
import com.beike.service.GenericService;
import com.beike.userloginlog.model.UserLoginLog;

/**
 * <p>Title:用户相关</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date May 4, 2011
 * @author ye.tian
 * @version 1.0
 */

public interface UserService extends GenericService<User, Long>{
	
	public User findUserByMobile(String mobile)throws UserException;
	/**
	 * 用户正常注册
	 * @param email		邮件
	 * @param mobile	手机
	 * @param password	密码
	 * @throws UserException	异常
	 */
	public void addUser(String email,String mobile,String password)throws UserException;
	/**
	 * 用户email注册
	 * @param email		邮箱
	 * @param password	密码
	 * @throws UserException	用户异常
	 */
	public User addUserEmailRegist(String email,String password,String ip)throws UserException, AccountException;
	
	/**
	 * 用户mobile注册
	 * @param mobile	手机
	 * @param password	密码
	 * @throws UserException	用户异常
	 */
	public void addUserMobileRegist(String mobile,String password)throws UserException;
	
	/**
	 * 用户是否存在
	 * @param mobile	手机
	 * @param email		email
	 * @return			
	 * @throws UserException
	 */
	public boolean isUserExist(String mobile,String email)throws UserException;
	
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
	 * @throws UserException
	 */
	public UserProfile getProfile(Long userid,String profileName)throws UserException;
	
	
	/**
	 * 更新用户扩展信息
	 * @param userProfile	  
	 * @throws UserException
	 */
	public void updateProfile(UserProfile userProfile)throws UserException;
	
	/**
	 * 判断用户Email里的地址是否有效
	 * 1.先查该验证url 是否有效
	 * 2.判断是否超过了3天
	 * @param urlKey		url中的key
	 * @param userid		用户的userid
	 * @return				判断是否有效 true有效 flase无效
	 */
	public boolean isUrlUsable(String urlKey,Long userid,String emailType)throws UserException;
	
	/**
	 * 判断用户名密码是否匹配
	 * @param mobile		手机号
	 * @param password		密码
	 * @param email			email
	 * @return				true匹配 false不匹配
	 * @throws UserException
	 */
	public User isUserLogin(String mobile,String password,String email)throws UserException;
	
	/**
	 * 根据email查找用户
	 * @param email			email
	 * @return				返回User
	 * @throws UserException
	 */
	public User findUserByEmail(String email)throws UserException;
	
	
	/**
	 * 激活邮件状态
	 * @param user
	 * @throws UserException
	 */
	public void activationEmail(User user)throws UserException;
	
	/**
	 * 激活手机状态
	 * @param user
	 * @throws UserException
	 */
	public void activationMobile(User user,String validateMobile)throws UserException;
	
	/**
	 * 查询用户扩展信息
	 * @param userid		userid
	 * @param profileName	扩展名称
	 * @return
	 * @throws UserException
	 */
	public UserProfile getUserProfile(Long userid,String profileName)throws UserException;
	
	
	/**
	 * 更新用户信息
	 * @param user
	 * @throws UserException
	 */
	public void updateUserMessage(User user)throws UserException;
	
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
	
	/**
	 * 创建用户 手机+邮箱
	 * @param mobile
	 * @param email
	 * @param password
	 * @return
	 * @throws UserException
	 * @throws AccountException
	 */
	public User addUserEmailRegist(String mobile,String email, String password,String ip)
	throws UserException, AccountException ;
	
	/**
	 * 为用户添加优惠券
	 * @param userId 用户id
	 * @param vmAccountId 虚拟账号id
	 * @param amount 优惠券金额
	 * @param description 描述信息
	 * @return
	 */
	public boolean noTscAddCouponsForUser(Long userId,Long vmAccountId,Long amount,String description);
	
	public User addUserEmailRegist(UserForm userForm)throws UserException, AccountException;
	
	public boolean addLoginLog(UserLoginLog ulLog)throws Exception;
	
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
	 * @Title: addMobileRegister
	 * @Description: 手机端用户注册
	 * @param  mobile
	 * @param  email
	 * @param  password
	 * @param  ip
	 * @return User
	 * @author wenjie.mai
	 */
	public User addMobileRegister(String mobile, String email, String password,String ip)throws UserException, AccountException;
	
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
