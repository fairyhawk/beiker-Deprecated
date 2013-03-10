package com.beike.core.service.trx.partner.impl;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.beike.common.bean.trx.VoucherInfo;
import com.beike.common.bean.trx.partner.PartnerInfo;
import com.beike.core.service.trx.partner.PartnerVoucherService;

/**
 * @Title: PartnerVoucherForNomalServiceImpl.java
 * @Package  com.beike.core.service.trx.parter
 * @Description:  合作分销商API 凭证远程查询及推送 service  for 标准商户
 * @date 5 30, 2012 2:16:47 PM
 * @author wh.cheng
 * @version v1.0
 */
@Service("partnerVoucherForNomalService")
public class PartnerVoucherForNomalServiceImpl implements PartnerVoucherService{

	@Override
	public String pushVoucherInfo(VoucherInfo voucherInfo,
			PartnerInfo partnerInfo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> qryVoucherInfoToPar(VoucherInfo voucherInfo,
			PartnerInfo partnerInfo) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
}
