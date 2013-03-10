package com.beike.core.service.trx.discountcoupon.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.bean.trx.TrxResponseData;
import com.beike.common.bean.trx.VmAccountParamInfo;
import com.beike.common.entity.discountcoupon.DiscountCoupon;
import com.beike.common.entity.trx.AccountHistory;
import com.beike.common.enums.trx.AccountStatus;
import com.beike.common.enums.trx.ActHistoryType;
import com.beike.common.enums.trx.BizType;
import com.beike.common.enums.trx.DiscountCouponStatus;
import com.beike.common.enums.trx.ReqChannel;
import com.beike.common.exception.AccountException;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.DiscountCouponException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.common.exception.VmAccountException;
import com.beike.common.guid.GuidGenerator;
import com.beike.core.service.trx.AccountHistoryService;
import com.beike.core.service.trx.discountcoupon.DiscountCouponService;
import com.beike.core.service.trx.vm.VmAccountService;
import com.beike.dao.discountcoupon.DiscountCouponDao;
import com.beike.dao.trx.AccountDao;
import com.beike.util.DateUtils;
import com.beike.util.Des3Encryption;
import com.beike.util.EnumUtil;
import com.beike.util.FileUtils;
import com.beike.util.PropertyUtil;
import com.beike.util.StringUtils;

/**   
 * @title: DiscountCouponServiceImpl.java
 * @package com.beike.core.service.trx.discountcoupon.impl
 * @description: 线下优惠券Service实现类
 * @author wangweijie  
 * @date 2012-7-11 下午08:25:02
 * @version v1.0   
 */

@Service("discountCouponService")
public class DiscountCouponServiceImpl implements DiscountCouponService {
	private static final Log log = LogFactory.getLog(DiscountCouponServiceImpl.class);
	@Autowired
	private DiscountCouponDao discountCouponDao;
	@Autowired
	private AccountHistoryService accountHistoryService;
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private VmAccountService vmAccountService;
	
	@Resource(name = "guidGeneratorService")
	private GuidGenerator guidGenerator;
	
	private static PropertyUtil propertyUtil = PropertyUtil.getInstance("project");
	private static String couponDesKeyFilePath = propertyUtil.getProperty("coupon_triple_des_key_file"); 
	private static String couponDesKey = "";
	/**
	 * 根据密码查询优惠券
	 * @param couponPwd
	 * @return
	 */
	@Override
	public DiscountCoupon findByCouponPwd(String couponPwd) {
		couponPwd = getEncryptPwd(couponPwd);
		return discountCouponDao.findByCouponPwd(couponPwd);
	}
	
	
	/**
	 * 根据用户主键查询优惠券记录
	 * @param userId
	 * @return
	 */
	@Override
	public List<DiscountCoupon> findByUserId(Long userId) {
		return discountCouponDao.findByUserId(userId);
	}

	@Override
	public TrxResponseData topupByCouponAndUserId(String couponPwd, Long userId, ReqChannel reqChannel)
			throws StaleObjectStateException, AccountException,DiscountCouponException,
			VmAccountException {
		
		log.info("++++++++couponPwd="+couponPwd+"+++++++++userId="+userId+"++++++++reqChannel=" + reqChannel);
		
		//查询优惠券信息
		couponPwd = getEncryptPwd(couponPwd);
		DiscountCoupon coupon = discountCouponDao.findByCouponPwd(couponPwd);
		log.info("+++++userId="+userId+"++couponPwd=" + couponPwd);
		
		/*
		 * 检查用户账号信息
		 * 检查用户是否是首次使用优惠券 
		 */
		checkUser(userId);
		/*
		 * 检查优惠券信息
		 */
		checkCoupon(coupon);
		
	
		/*
		 * 下发操作
		 */
		String requestId = guidGenerator.gainCode("DIS");
		VmAccountParamInfo vmAccountParamInfo = new VmAccountParamInfo();
		vmAccountParamInfo.setRequestId(requestId);		//请求号
		vmAccountParamInfo.setUserId(String.valueOf(userId));	//用户ID
		vmAccountParamInfo.setVmAccountId(String.valueOf(coupon.getVmAccountId()));	//虚拟账户ID
		vmAccountParamInfo.setAmount(String.valueOf(coupon.getCouponValue()));		//充值金额
		vmAccountParamInfo.setOperatorId("0");	// 操作员Id
		vmAccountParamInfo.setActHistoryType(ActHistoryType.VMDIS);	//
		vmAccountParamInfo.setBizType(BizType.COUPON);		//优惠券充值
		vmAccountParamInfo.setDescription("优惠券有效期"+DateUtils.formatDate(coupon.getLoseDate(), "yyyy-MM-dd")+"；逾期作废；不可提现");
		
		Long vmActHisId= vmAccountService.dispatchVm(vmAccountParamInfo);
		log.info("+++++++++++userId="+userId+"++coupon_pwd=" + coupon.getCouponPwd() + ";coupon_value=" + coupon.getCouponValue() + ";card_status="
				+ EnumUtil.transEnumToString(coupon.getCouponStatus())+ "vm_account_id:" + coupon.getVmAccountId() + "+++++++");
		
	
		
		/*
		 * 更新优惠券为已使用
		 */
		coupon.setUserId(userId);
		coupon.setBizId(vmActHisId);
		coupon.setCouponStatus(DiscountCouponStatus.USED);
		discountCouponDao.updateCouponStatusAndUserId(coupon.getCouponStatus(),coupon.getUserId(),coupon.getId(), coupon.getVersion());
		
	
		
		// 查询出当前账户余额
		double actAmount = accountDao.findBalanceByStatus(userId, AccountStatus.ACTIVE);
		TrxResponseData trxResponseData = new TrxResponseData();
		trxResponseData.setBalance(actAmount);
		trxResponseData.setCouponValue(String.valueOf(coupon.getCouponValue()));
		trxResponseData.setLoseDate(DateUtils.formatDate(coupon.getLoseDate(), "yyyy-MM-dd HH:mm:ss"));
		
		
		return trxResponseData;
	}
	
	/**
	 * 检查用户账户
	 * @param userId
	 */
	private void checkUser(Long userId) throws AccountException,DiscountCouponException{
		
		
		//判断用户是否是首次使用优惠券充值
		List<AccountHistory> accountHistoryList = accountHistoryService.getHistoryInfoByUserId(userId);
		if(null != accountHistoryList){
			int count = 0;
			for(AccountHistory accountHistory : accountHistoryList){
				if(BizType.COUPON.name().equals(StringUtils.toTrim(accountHistory.getBizType())) 
						&& ActHistoryType.VMDIS.equals(accountHistory.getActHistoryType())){
					/*
					 *要给部分网易注册返10元用户补充充值
					 *截止至10月3日23：59，此时间后再关闭
					 *UNIX时间戳1351180799999L=2012-10-25 23:59:59.999
					 */
					if(System.currentTimeMillis() < 1351180799999L && 0==count){
						 count = count + 1;
						 log.info("+++++++++++++163 user="+userId+",recharge no forbidden+++++");
					}else{
						throw new DiscountCouponException(BaseException.DISCOUNTCOUPON_NOT_FIRST);
					}
				}
			}
		}
	}
	
	/**
	 * 检查优惠券
	 * @param coupon
	 * @throws DiscountCouponException
	 * @throws VmAccountException
	 */
	private void checkCoupon(DiscountCoupon coupon) throws DiscountCouponException,VmAccountException{
		//优惠券密码无效
		if(null == coupon){
			throw new DiscountCouponException(BaseException.DISCOUNTCOUPON_PWD_INVALID);
		}
		
		DiscountCouponStatus couponStatus = coupon.getCouponStatus();
		if(!DiscountCouponStatus.ACTIVE.equals(couponStatus)){
			//优惠券过期
			if(DiscountCouponStatus.TIMEOUT.equals(couponStatus)){
				throw new DiscountCouponException(BaseException.DISCOUNTCOUPON_EXPIRED);
			}
			//优惠券已使用
			else if(DiscountCouponStatus.USED.equals(couponStatus)){
				throw new DiscountCouponException(BaseException.DISCOUNTCOUPON_USED);
			}
			//优惠券无效
			else{
				throw new DiscountCouponException(BaseException.DISCOUNTCOUPON_STATUS_INVALID);
			}
		}
	}
	
	
	
	/**
	 * 获得加密密码
	 * @param password
	 * @return
	 */
	private String getEncryptPwd(String password){
		return Des3Encryption.encode(getCouponKey(), password);// 加密
	}
	
	
	
	private String getCouponKey(){
		if (null == couponDesKey || couponDesKey.length()==0) {
			String couponDesKeySeed = StringUtils.toTrim(FileUtils.getFileContent(couponDesKeyFilePath));
			int length = couponDesKeySeed.length();
			if (length < 24) { // 不足24位前面补0
				couponDesKey = couponDesKeySeed;
				for(int i=0;i<24-length;i++){
					couponDesKey += "0";
				}
				log.debug("++++++++++++++couponDesKeySeed.length < 24,append '0' ++++++++++couponDesKey:" + couponDesKey + "++++++++");

			} else {// 超出或者等于24位，截取前24位

				couponDesKey = couponDesKeySeed.substring(0, 24);
				log.debug("++++++++++++++couponDesKeySeed.length > 24,cut [0-24]:++++++++++couponDesKey:" + couponDesKey + "++++++++");
			}
		}
		return couponDesKey;
	}


	@Override
	public List<DiscountCoupon> findExpireCouponInActiveStatus() {
		return discountCouponDao.findExpireCouponInActiveStatus();
	}


	@Override
	public void updateCouponStatus(DiscountCouponStatus status,String description, Long id,Long version)throws StaleObjectStateException {
		discountCouponDao.updateCouponStatus(status,description, id, version);
	}
	
}
