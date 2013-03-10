package com.beike.dao.cps.tuan800;

import java.util.List;
import java.util.Map;

/**
 * CPS tuan800
 * @author janwen
 * Apr 17, 2012
 */
public interface CPSTuan800Dao {

	
	
	/**
	 * 下单未付款,保存订单信息
	 * @param params{trxorder_id,seal:qpcps}
	 */
	public void saveOrderNoPay(Map<String, Object> params,String[] cps_cookie,Map order_info_map);
	
	/**
	 * 下单付款成功,更新订单状态
	 * @param params{trxorder_id,seal:qpcps}
	 */
	public int saveOrderPay(Map params,Map order_info);
	
	/**
	 * 退款,更新订单状态
	 * @param params{trxorder_id,trx_goods_sn[1,2,3,4](List数组),seal:qpcps}
	 */
	public int cancelOrder(Map<String,Object> params);
	
	/**
	 * 查询订单信息
	 * @param trxorder_id
	 */
	public List<Map> getOrderInfo(String trxorder_id);
	
	/**
	 * 根据商品ID集合查询分类
	 * @param goodsIds  商品ID集合
	 * @return
	 */
	public List<Map<String, Object>> getClassificationIds(String goodsIds);
	
	/**
	 * 查询cps表获取订单相关信息,减轻订单库压力
	 * @param trxorder_id
	 * @return
	 */
	public List<Map> getOrderInfoCps(String trxorder_id,String trx_goods_sn);
	
	/**
	 * 
	 * @param beingIndex
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<Map> getOrder4Tuan800(Long beginIndex,String startdate,String enddate,String cid);
	
	/**
	 * 
	 * @param startdate
	 * @param enddate
	 * @return 记录总数
	 */
	public Long getTotalResults(String startdate,String enddate,String cid);
	/**
	 * 
	 * @param goodsidList
	 * @return 当前页商品title
	 */
	public List<Map> getGoodsTitle(List<String> goodsidList);
}
