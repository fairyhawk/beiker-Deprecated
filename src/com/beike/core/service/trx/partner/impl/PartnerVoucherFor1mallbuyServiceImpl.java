package com.beike.core.service.trx.partner.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.beike.common.bean.trx.VoucherInfo;
import com.beike.common.bean.trx.partner.PartnerInfo;
import com.beike.core.service.trx.partner.PartnerVoucherService;

/**   
 * @title: PartnerVoucherFor1mallbuyServiceImpl.java
 * @package com.beike.core.service.trx.partner.impl
 * @description: 合作分销商API 凭证远程查询及推送 service for 一号店
 * @author wangweijie  
 * @date 2012-9-3 上午11:42:35
 * @version v1.0   
 */

@Service("partnerVoucherFor1mallbuyService")
public class PartnerVoucherFor1mallbuyServiceImpl implements PartnerVoucherService {
	
	/**
	 * 推送分销商voucher info
	 * @param voucherInfo
	 * @param partnerinfo
	 * @return
	 */
	@Override
	public String pushVoucherInfo(VoucherInfo voucherInfo, PartnerInfo partnerInfo) {
		return null;
	}

	/**
	 * 远程查询分销商voucher info
	 * @param voucherInfo
	 * @param userId
	 * @return
	 */
	@Override
	public Map<String, String> qryVoucherInfoToPar(VoucherInfo voucherInfo, PartnerInfo partnerInfo) {
		Map<String,String>  resultMap=new HashMap<String,String>();
		//可核销
		resultMap.put("status","ALLOW_VALIDATE");
		return resultMap;
	}

}
