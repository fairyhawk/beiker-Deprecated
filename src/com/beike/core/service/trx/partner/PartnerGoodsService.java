package com.beike.core.service.trx.partner;
/**
 *
 * @author 赵静龙 创建时间：2012-10-31
 */
public interface PartnerGoodsService {
	
	/**
	 * 根据获取封装请求参数中的团购ID查询所上报的团购项目在合作伙伴方的总销量情况
	 * @param ptop
	 * @return
	 */
	public String processGoodsSellCount(Object ptop) throws Exception;
	
	/**
	 * 查询商品销量
	 * @param venderTeamId
	 * @return
	 */
	public long querySellCount(String venderTeamId);
}
