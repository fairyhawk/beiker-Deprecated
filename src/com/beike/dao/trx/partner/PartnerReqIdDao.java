package com.beike.dao.trx.partner;

import com.beike.entity.partner.PartnerReqId;

/** 
* @ClassName: PartnerReqIdDao 
* @Description: TODO
* @author yurenli
* @date 2012-7-12 下午06:41:00 
* @version V1.0 
*/ 
public interface PartnerReqIdDao {

	
	/**
	 * @param partnerNo
	 * @param requestId
	 * @return
	 */
	public PartnerReqId findByPNoAndReqId(String partnerNo,String requestId);
	
	/**
	 * @param partnerReqId
	 */
	public void addPartnerReqId(PartnerReqId partnerReqId) ;
}
