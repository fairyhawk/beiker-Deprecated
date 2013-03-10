package com.beike.core.service.trx.partner;

import java.util.Map;

import com.beike.common.bean.trx.VoucherInfo;
import com.beike.common.bean.trx.partner.PartnerInfo;

/**
 * @Title: PartnerVoucherService.java
 * @Package  com.beike.core.service.trx.parter
 * @Description: 合作分销商API 凭证远程查询及推送 service(spring  bean相互依赖问题，故增加此service)
 * @date 5 30, 2012 2:16:47 PM
 * @author wh.cheng
 * @version v1.0
 */
public interface PartnerVoucherService {	
	/**
     * 远程查询分销商voucher info
     * @param voucherInfo
     * @param userId
     * @return
     */
	public Map<String,String>  qryVoucherInfoToPar(VoucherInfo voucherInfo,PartnerInfo partnerInfo);
	
	/**
	 * 推送分销商voucher info
	 * @param voucherInfo
	 * @param partnerinfo
	 * @return
	 */
	public String pushVoucherInfo(VoucherInfo voucherInfo,PartnerInfo partnerInfo);
	}
