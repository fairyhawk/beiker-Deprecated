package com.beike.core.service.trx.partner;

import java.util.List;

import com.beike.common.bean.trx.partner.PartnerInfo;

/**
 * @Title: PartnerCommonService.java
 * @Package  com.beike.core.service.trx.parter
 * @Description: 合作分销商API交易相关基础Service
 * @date 5 30, 2012 2:16:47 PM
 * @author wh.cheng
 * @version v1.0
 */
public interface PartnerCommonService {
	
	/**
	 * 根据商家编号从mem中读取生效分销商信息
	 * @param partnerNo 
	 * @return
	 */
	public PartnerInfo qryAvaParterByNoInMem(String partnerNo);
	


	/**
	 * 跟据分销商编号查询其下所有分销商信息
	 * @param partnerNo
	 * @return
	 */
	public List<PartnerInfo> qryAllPartnerByNoInMem(String partnerNo);
	
	/**
	 * 跟据分销商编号查询其下所有分销商User_id信息
	 * @param partnerNo
	 * @return
	 */
	public List<Long> qryAllUserIdByNoInMem(String partnerNo);
	
	/**
	 * 根据user_id 检查是否归属该分销商
	 * @param partnerNo
	 * @param sourceUserId
	 * @return
	 */
	public   boolean  checkIsUIdBelongPNo(String partnerNo, Long sourceUserId);
	
	
	
	/**
	 * 根据user_id  查询有效的分销商信息
	 * @param userId
	 * @return
	 */
	public PartnerInfo qryAvaParterByUserIdInMem(Long userId);
	
	
	/**
	 * 根据user_id 查询相应分销商信息。理论上一个user_id下有且只有一条分销商信息。
	 * @param userId
	 * @return
	 */
	public PartnerInfo qryParterByUserIdInMem(Long userId);
	
}
