package com.beike.dao.trx.partner;

import java.util.List;
import java.util.Map;

import com.beike.entity.partner.PartnerBindVoucher;

/**   
 * @title: PartnerBindVoucherDao.java
 * @package com.beike.dao.trx.partner
 * @description: 
 * @author wangweijie  
 * @date 2012-9-6 下午07:43:25
 * @version v1.0   
 */
public interface PartnerBindVoucherDao {
	
	/**
	 * 添加分销商优惠券绑定信息
	 *
	 * @param partnerVoucher
	 */
	public void addPartnerVoucher(PartnerBindVoucher pbv);
	
	/**
	 * 查询双方voucher信息
	 * @return
	 */
	public Map<String,Object> queryVoucherBothSides(String partherNo,String outRequestId);
	
	/**
	 * 根据voucherId 查询优惠券
	 *
	 * @param voucherId
	 * @return
	 */
	public PartnerBindVoucher queryPartnerBindVoucher(String partnerNo,Long voucherId);
	
	
	public List<PartnerBindVoucher> queryPartnerBindVoucherList(String partherNo,String outRequestId);
	
}
