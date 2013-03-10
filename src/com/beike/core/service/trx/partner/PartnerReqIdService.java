package com.beike.core.service.trx.partner;

import java.util.Date;

/**
 * @Title: PartnerReqIdService.java
 * @Package  com.beike.core.service.trx.partner
 * @Description: 合作分销商API请求号相关Service
 * @date 5 30, 2012 2:16:47 PM
 * @author wh.cheng
 * @version v1.0
 */
public interface PartnerReqIdService {
	
	
	
	
	/**
	 * 根据分销商编号和请求号查询是否存在（主库查询）
	 * @param PartnerNo
	 * @param reqId
	 * @return
	 */
	
	public boolean preQryInWtDBByPNoAndReqId(String partnerNo,String reqId);
	
	
	/**
	 *  创建分销商ID
	 * @param parterReqId
	 */
	public void createReqId(String partnerNo,String reqId,Date createDate);

}
