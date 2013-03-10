package com.beike.service.cps.tuan800;

import java.util.Map;

public interface CPSTuan800Service {

	/**
	 * 下单未付款,保存订单信息
	 * @param params{trxorder_id,seal:qpcps}
	 */
	public void saveOrderNoPay(Map<String, Object> params);
	
	/**
	 * 下单付款成功,更新订单状态
	 * @param params{trxorder_id,seal:qpcps}
	 */
	public void saveOrderPay(Map<String,Object> params);
	
	/**
	 * 退款,更新订单状态
	 * @param params{trxorder_id,trx_goods_sn[1,2,3,4](List数组),seal:qpcps}
	 */
	public void cancelOrder(Map<String,Object> params);
	
	
	
	public int sendtoTuan800(String url);
	
	
	
	/**
	 * 分页返回数据,每次200条
	 * @param begindate
	 * @param enddate
	 * @param beginIndex 开始下标
	 * @return 拼接好的字符串
	 */
	public String getOrder4Tuan800(Long beginIndex,String begindate,String enddate,String cid);
	
	
	/**
	 * 
	 * @param startdate
	 * @param endate
	 * @return 记录总数
	 */
	public Long getTotalResults(String startdate,String enddate,String cid);
}
