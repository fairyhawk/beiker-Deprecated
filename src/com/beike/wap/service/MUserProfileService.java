package com.beike.wap.service;

import com.beike.service.GenericService;
import com.beike.wap.entity.MUserTemp;

/**
 * 手机wap用户注册临时数据处理
 * @author kun.wang
 */
public interface MUserProfileService extends GenericService<MUserTemp, Long>{
	/**
	 * 数据插入到beiker_user_temp，若原来存在数据，则更新
	 * @param mobile 手机号
	 * @param email 邮箱
	 * @param password 密码
	 * @return 返回当前插入的对象，附带数据库自动生成的id主键
	 */
	public MUserTemp addUserTemp(String mobile, String email ,String password);
	
	/**
	 * 验证激活码以及id是否存在记录
	 * @param id beiker_user_temp主键id
	 * @param code 激活码
	 */
	public boolean userIsExist(long id, String code);
	
	/**
	 * 根据手机号码获取用户临时信息
	 * @param 手机号码
	 */
	public MUserTemp getUserTempByMobile(String mobile);
	
	/**
	 * 检查当前手机号是否存在于临时表中
	 * @param mobile
	 */
	public boolean isMobileExist(String mobile);
	
	public MUserTemp findById(long id);
	
	/**
	 * 根据删除临时用户数据
	 * @param id
	 */
	public void deleteById(Long id);
//	/**
//	 * 根据手机号码删除信息
//	 * @param mobile 手机号码
//	 */
//	public void deleteUserTempByMobile(String mobile);
}
