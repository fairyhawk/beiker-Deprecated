package com.beike.core.service.trx.partner;

import java.util.List;
import java.util.Map;

import com.beike.entity.partner.PartnerBindVoucher;

/**   
 * @title: PartnerBindVoucherService.java
 * @package com.beike.core.service.trx.partner
 * @description: 
 * @author wangweijie  
 * @date 2012-9-6 下午08:19:16
 * @version v1.0   
 */
public interface PartnerBindVoucherService {
	
	public void savePartnerVouchers(List<PartnerBindVoucher> pbvList);
	
	public Map<String,Object> queryVoucherMap(String partnerNo,String outRequestId);
	
	public List<PartnerBindVoucher> preQryInWtDBByPartnerBindVoucherList( String partherNo, String outRequestId);
	/**
	 * 根据voucherId 查询优惠券
	 *
	 * @param voucherId
	 * @return
	 */
	public PartnerBindVoucher queryPartnerBindVoucher(String partnerNo,Long voucherId);
}
