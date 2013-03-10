package com.beike.service.miaosha.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.dao.GenericDao;
import com.beike.dao.miaosha.MiaoshaRemindDao;
import com.beike.entity.miaosha.MiaoshaRemind;
import com.beike.service.impl.GenericServiceImpl;
import com.beike.service.miaosha.MiaoshaRemindService;

/**      
 * project:beiker  
 * Title:秒杀通知Service实现
 * Description:
 * Copyright:Copyright (c) 2012
 * Company:Sinobo
 * @author qiaowb  
 * @date Jul 31, 2012 3:25:39 PM     
 * @version 1.0
 */
@Service("miaoshaRemindService")
public class MiaoshaRemindServiceImpl extends GenericServiceImpl<MiaoshaRemind, Long> implements MiaoshaRemindService {

	@Autowired
	MiaoshaRemindDao miaoshaRemindDao;
	
	@Override
	public GenericDao<MiaoshaRemind, Long> getDao() {
		return miaoshaRemindDao;
	}

	@Override
	public int addMiaoshaRemind(MiaoshaRemind msRemind) {
		if(msRemind!=null){
			if(miaoshaRemindDao.checkRepeatRemind(msRemind.getMiaoshaid(), msRemind.getPhone())==0){
				return miaoshaRemindDao.addMiaoshaRemind(msRemind);
			}else{
				return 1;
			}
		}else{
			return 0;
		}
	}

	@Override
	public MiaoshaRemind findById(Long id) {
		return null;
	}
	
	@Override
	public MiaoshaRemind getMiaoshaRemind(Long userId, Long miaoshaId) {
		return miaoshaRemindDao.getMiaoshaRemind(userId, miaoshaId);
	}

	@Override
	public List<String> getRemindPhoneByMiaoshId(Long miaoshaId) {
		return miaoshaRemindDao.getRemindPhoneByMiaoshId(miaoshaId);
	}

	@Override
	public int deleteMiaoshaRemindByMiaoshId(Long miaoshaId) {
		return miaoshaRemindDao.deleteMiaoshaRemindByMiaoshId(miaoshaId);
	}
}