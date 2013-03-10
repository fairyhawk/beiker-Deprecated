package com.beike.core.service.trx.partner.impl;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.beike.common.bean.trx.partner.PartnerInfo;
import com.beike.core.service.trx.partner.PartnerService;

/**
 * @Title: PartnerForNomalServiceImpl.java
 * @Package com.beike.core.service.trx.parter.impl
 * @Description: 合作分销商(标准商户)API交易相关ServiceImpl
 * @date 5 30, 2012 2:16:47 PM
 * @author wh.cheng
 * @version v1.0
 */
@Service("partnerForNomalService")
public class PartnerForNomalServiceImpl implements PartnerService {

	@Override
	public String checkHmacData(String desStr, PartnerInfo partnerInfo, String partnerIP) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findVouInfoByActiveDate(String partnerNo, Date startTime, Date endTime, String trxStatusStr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findVouInfoByLastUpdateDate(String partnerNo, Date startTime, Date endTime, String trxStatus) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findVouInfoByVouId(String partnerNo, String voucherId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String generateRspHmac(Object source, String keyValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String processTrxOrder(Object ptop) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String noTscResendVoucher(Object ptop) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String synchroTrxOrder(Object ptopSource, String partnerNo) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object transReqInfo(String paramInfo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String checkHmacData(String desStr, String publicKey, String sign, PartnerInfo partnerInfo, String partnerIP) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.beike.core.service.trx.partner.PartnerService#findTrxorder(java.lang.Object, java.lang.String)
	 */
	@Override
	public String findTrxorder(Object ptopSource, String partnerNo) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.beike.core.service.trx.partner.PartnerService#checkHmacData(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public String checkHmacData(String params, String publicKey, String privateKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findVoucher(Object ptop, String partnerNo) throws Exception{
		return null;
	}

}