package com.beike.core.service.trx;

/**
 * @Title: TrxorderGoodsSnService.java
 * @Package com.beike.core.service.trx
 * @Description:订单商品号获取服务类接口(新起事务，独立出来使AOP生效)
 * @date 4 1, 2012 6:31:57 PM
 * @author wh.cheng
 * @version v1.0
 */
public interface TrxorderGoodsSnService {

	/**
	 * 商品订单号生成（包括循环避重。变更为预取）。新起事务
	 * 
	 * @param trxorderGoods
	 * @return
	 */
	public String createTrxGoodsSn();
	
	public String createTrxGoodsSnKing();

}
