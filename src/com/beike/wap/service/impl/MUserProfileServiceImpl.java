package com.beike.wap.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.util.MobilePurseSecurityUtils;
import com.beike.util.RandomNumberUtils;
import com.beike.util.StringUtils;
import com.beike.wap.dao.MUserProfileWapDao;
import com.beike.wap.entity.MUserTemp;
import com.beike.wap.form.UserForm;
import com.beike.wap.service.MUserProfileService;

@Service("mUserProfileService")
public class MUserProfileServiceImpl implements MUserProfileService {
	private static final int HMAC_KEY_LEN = 60;
	
	@Autowired
	private MUserProfileWapDao mUserProfileWapDao;
	
	@Override
	public MUserTemp findById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MUserTemp addUserTemp(String mobile, String email, String password) {
		MUserTemp ut = new MUserTemp();
		// TODO 生成随即验证码
		int count = mUserProfileWapDao.isMobileExist(mobile);
		String customerKey = StringUtils.getRandomString(HMAC_KEY_LEN);
		password = MobilePurseSecurityUtils.secrect(password, customerKey);
		ut.setPassword(password);
		ut.setCustomerkey(customerKey);
		ut.setMobile(mobile);
		ut.setEmail(email);
		if(count == 0)
		{
			String tmpCode = RandomNumberUtils.getRandomNumbers(4);
			ut.setvCode(Integer.parseInt(tmpCode));
			mUserProfileWapDao.addUserTemp(ut);
			ut = mUserProfileWapDao.findByMobile(mobile);
		}
		else
		{
			mUserProfileWapDao.updatePassword(ut);
			ut = mUserProfileWapDao.findByMobile(mobile);
		}
		return ut;
	}

	@Override
	public boolean userIsExist(long id, String code) {
		int count = mUserProfileWapDao.findByIdAndCode(id, code);
		return count == 0 ? false : true;
	}

	@Override
	public MUserTemp getUserTempByMobile(String mobile) {
		return null;
	}

	@Override
	public boolean isMobileExist(String mobile) {
		int count = mUserProfileWapDao.isMobileExist(mobile);
		return count == 0 ? false : true;
	}
	
	private void generatePassword(UserForm userForm, String password) {
		String customerKey = StringUtils.getRandomString(HMAC_KEY_LEN);
		password = MobilePurseSecurityUtils.secrect(password, customerKey);
		userForm.setPassword(password);
		userForm.setCustomerKey(customerKey);
	}

	@Override
	public MUserTemp findById(long id) {
		return mUserProfileWapDao.findById(id);
	}

	@Override
	public void deleteById(Long id) {
		mUserProfileWapDao.deleteById(id);
	}
}
