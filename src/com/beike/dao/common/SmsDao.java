package com.beike.dao.common;

import java.util.List;

import com.beike.dao.GenericDao;
import com.beike.entity.common.Sms;
import com.beike.entity.common.SmsQuene;
import com.beike.form.SmsInfo;

/**
 * <p>
 * Title: 短信模板
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
 * @date May 6, 2011
 * @author ye.tian
 * @version 1.0
 */

public interface SmsDao extends GenericDao<Sms, Integer> {

	public Sms getSmsByTitle(String title);

	/**
	 * 保存短信置数据
	 * 
	 * @param sourceBean
	 * @return
	 */
	public String saveSmsInfo(SmsInfo sourceBean);

	/**
	 * 发送短信
	 * 
	 * @param operId
	 * @param operPass
	 * @param sendUrl
	 * @param sendCount
	 *            每次发送数量
	 * @return
	 */
	public String updateSmsInfo(String sendResult, String operId,
			String operPass, String sendUrl, SmsQuene smsQuene);

	/**
	 * 查询短信队列
	 * 
	 * @param sendCount
	 * @return
	 */
	public List<SmsQuene> getSmsInfoList(String sendCount);

}
