package com.beike.dao.adweb;

import java.util.List;

import com.beike.common.entity.adweb.AdWebTrxInfo;
import com.beike.dao.GenericDao;

/**
 * <p>Title:广告联盟交易记录</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date Feb 29, 2012
 * @author ye.tian
 * @version 1.0
 */

public interface AdWebTrxInfoDao extends GenericDao<AdWebTrxInfo,Long>{
	/**
	 * 广告联盟访问记录
	 * @param adwebid
	 * @param adcid
	 * @param adwi
	 */
	public Long addAdWebTrxInfo(AdWebTrxInfo adWebTrx);
	
	/**
	 * 修改并 更新 加入交易记录
	 * @param adwtrxid
	 * @param trxid
	 * @param buycount
	 * @param buymoney
	 */
	public void updateAdWebTrxInfo(Long adtrxid,String trxid,Integer buycount,Double buymoney);
	
	/**
	 * 查询广告联盟访问记录
	 * @param adwebid
	 * @param adcid
	 * @param adwi
	 * @return
	 */
	public AdWebTrxInfo getAdWebTrxInfo(Long adwebid,String adcid,String adwi);
	
	public AdWebTrxInfo getAdWebTrxInfoById(Long adwebTrxId);
	
	public List<AdWebTrxInfo> getAdWebTrxInfoList(String fromDate,String endDate,String srccode,String cid);
	
	public AdWebTrxInfo getAdWebTrxInfoByTrxId(String trxOrderId);
	
	public void updateWebTrxStatus(String trxOrderId);
	
	public List<AdWebTrxInfo> getAdWebTrxInfoList(String fromDate,String endDate,String trxOrderId);
}
