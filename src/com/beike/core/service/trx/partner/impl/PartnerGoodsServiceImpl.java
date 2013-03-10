package com.beike.core.service.trx.partner.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.bean.trx.partner.Par360buyOrderParam;
import com.beike.core.service.trx.partner.PartnerGoodsService;
import com.beike.entity.goods.Goods;
import com.beike.service.goods.GoodsService;

/**
 *
 * @author 赵静龙 创建时间：2012-10-31
 */
@Service("partnerGoodsService")
public class PartnerGoodsServiceImpl implements PartnerGoodsService {
	@SuppressWarnings("unused")
	private static final Log logger = LogFactory.getLog(PartnerGoodsServiceImpl.class);
	@Autowired
	private GoodsService goodsService;

	/* (non-Javadoc)
	 * @see com.beike.core.service.trx.partner.PartnerGoodsService#processGoodsSellCount(java.lang.Object)
	 */
	@Override
	public String processGoodsSellCount(Object ptop) throws Exception{
		Par360buyOrderParam par360param =(Par360buyOrderParam) ptop;
		String venderTeamId = par360param.getVenderTeamId();
		long salescount = querySellCount(venderTeamId);

		if(-1L == salescount){
			throw new Exception("+++++++++++++++++没有找到此商品++++++++++++");
		}
		StringBuffer responseData = new StringBuffer();
		//团购销量查询 data返回报文
		responseData.append("<Message xmlns=\"http://tuan.360buy.com/QueryTeamSellCountResponse\">");
		responseData.append("<VenderTeamId>" + venderTeamId + "</VenderTeamId>");		//团购ID
		responseData.append("<SellCount>"+ salescount + "</SellCount>");		//销量
		responseData.append("</Message>");
		return responseData.toString();
	}
	
	/* (non-Javadoc)
	 * @see com.beike.core.service.trx.partner.PartnerGoodsService#querySellCount(java.lang.String)
	 */
	public long querySellCount(String venderTeamId) {
		long count = 0;
		Goods good = goodsService.findById(Long.parseLong(venderTeamId));
		if(good == null){
			return -1L;
		}else{
			String salescount = goodsService.salesCount(Long.parseLong(venderTeamId));
			if(StringUtils.isEmpty(salescount)){
				salescount = "0";
			}
			count = good.getVirtualCount() + Long.parseLong(salescount);
		}
		if(count == 0){
			count = 1;
		}
		return count;
	}
}
