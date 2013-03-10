package com.beike.dao.businessbackground;

import java.util.List;
import java.util.Map;

/**
 * @Title：OrderDao.java
 * @Package com.beike.dao.businessbackground
 * @Description：
 * @date：2013-1-29 - 上午10:56:01
 * @author：zhaojinglong@qianpin.com
 * @version
 */
public interface OrderDao {
	/**
	 * 查询商品订单记录
	 *@param queryMap 查询参数
	 *@return
	 */
	public List<Map<String, Object>> getTrxOrderGoods(Map<String, Object> queryMap);
	/**
	 * 查询订单总的原价以及总的支付价
	 *@param trxorderId
	 *@return
	 */
	public Map<String, Object> queryOrderPrice(Long trxorderId);
}
