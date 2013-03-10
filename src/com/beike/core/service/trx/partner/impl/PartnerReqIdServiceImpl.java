package com.beike.core.service.trx.partner.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.core.service.trx.partner.PartnerReqIdService;
import com.beike.dao.trx.partner.PartnerReqIdDao;
import com.beike.entity.partner.PartnerReqId;
import com.beike.util.StringUtils;

/**
 * @Title: PartnerReqIdServiceImpl.java
 * @Package  com.beike.core.service.trx.partner
 * @Description: 合作分销商API请求号相关Service实现
 * @date 5 30, 2012 2:16:47 PM
 * @author wh.cheng
 * @version v1.0
 */
@Service("partnerReqIdService")
public class PartnerReqIdServiceImpl  implements PartnerReqIdService {
	

	@Autowired 
	private PartnerReqIdDao   partnerReqIdDao; 
	/**
	 * 根据分销商编号和请求号查询是否存在（主库查询）
	 * @param PartnerNo
	 * @param reqId
	 * @return
	 */
	
	public boolean preQryInWtDBByPNoAndReqId(String partnerNo,String reqId){
		boolean result=false;
		
		PartnerReqId partnerReqId=partnerReqIdDao.findByPNoAndReqId(partnerNo, reqId);
		
		
		if(partnerReqId!=null){
			result=true;
		}
		
		return result;
	}
	
	
	/**
	 *  创建分销商ID
	 * @param parterReqId
	 */
	public void createReqId(String partnerNo,String reqId,Date createDate){
		
		if(StringUtils.isEmpty(partnerNo)  || StringUtils.isEmpty(reqId)){//如果分销商编号和外部请求号为空，则直接返回
			return;
		}
		PartnerReqId  parterReqId=new PartnerReqId(partnerNo,reqId,createDate);
		partnerReqIdDao.addPartnerReqId(parterReqId);
	}


	

}
