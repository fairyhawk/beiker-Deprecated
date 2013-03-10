package com.beike.service.adweb;

import com.beike.common.entity.adweb.AdWebTrxInfo;


/**
 * <p>Title: 广告管理交易记录日志</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date Feb 29, 2012
 * @author ye.tian
 * @version 1.0
 */

public interface AdWebTrxInfoService {
	/**
	 * 增加广告联盟日志
	 * @return
	 */
	public Long addAdWebTrxInfo(AdWebTrxInfo adWebTrx);
	
	/**
	 * 查询广告联盟日志
	 * @param adwebid
	 * @param adcid
	 * @param adwi
	 * @return
	 */
	public AdWebTrxInfo getAdWebTrxInfo(Long adwebid, String adcid, String adwi);
	
	
	/**
	 * 更新交易信息 到广告联盟日志表里
	 * @param adtrxid
	 * @param trxid
	 * @param buycount
	 * @param buymoney
	 */
	public void updateAdWebTrxInfo(Long adtrxid, String trxid,
			Integer buycount, Double buymoney);
	
	public AdWebTrxInfo getAdWebTrxInfoById(Long adtrxid);
	
	/**
	 * 调用查询接口 查询广告联盟某天的记录
	 * 并生成TXT格式
	 * @param srcCode
	 * @param cid
	 * @param date
	 * @return
	 */
	public String generateAdWebTrxInfoList(String srcCode,String cid,String date);
	
	/**
	 * 发送请求给广告联盟
	 * @param cid   渠道
	 * @param wi	下级网站信息
	 * @param on	订单号
	 * @param ta	订单数量
	 * @param pp	价格总数量
	 * @param date  订单时间
	 * @param url	请求地址
	 */
	public String httpRequestUrl(String url,String cid,String wi,String on,String ta,String pp,String date);
	
	public void updateAdWebTrxStatus(String trxOrderId);
	
	public String generateAdWebTrxInfoList(String date,String trxOrderid);
}
