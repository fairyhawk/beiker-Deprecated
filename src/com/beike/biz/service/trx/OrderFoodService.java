package com.beike.biz.service.trx;

import java.util.List;
import java.util.Map;

import com.beike.entity.goods.Goods;
/**
 * @desc 点餐服务类
 * @author ljp
 * @date 20121128
 *
 */
public interface OrderFoodService {
	/**
	 * @desc   根据购买信息生成Goods
	 * @param buyFoodInfo 格式为menuid - count
	 * @param orderId   活动商品Id
	 * @return Goods
	 * @author ljp
	 * @date 20121115
	 */
	public Goods queryGoodsByBuyFoodInfo(List<Map<String, Integer>> buyFoodInfo, String orderId,String guestId)throws Exception;
	
	/**
	 * @desc 根据活动id orderid 查询分店id
	 * @param orderId
	 * @throws Exception
	 * @author ljp
	 */
	public String findGuestIdByOrderId(String orderId)throws Exception;

	/**
	 * @desc 查询 商家名称MerchantName 根据MerchantId
	 * @param merchantId
	 * @return
	 * @throws Exception
	 * @author ljp
	 */
	public String queryMerchantNameByMerchantId(String merchantId) throws Exception;

	/**
	 * @desc 查询OrderMenu 和 category 根据 menuId
	 * @param menuIds
	 * @return map orderMenu对象和category list<String>
	 * @throws Exception
	 * @author ljp
	 */
	@SuppressWarnings("unchecked")
	public Map queryOrderMenuByIds(List<Long> menuIds) throws Exception;
	
	/**
	 * @desc 创建menuGoodsOrder表
	 * @param rspMap 下单成功后返回的值
	 * @throws Exception
	 * @author ljp
	 */
	public void createMenuGoodsOrder(Map<String, String> rspMap)throws Exception;
	
	/**
	 * @desc 检查活动id和分店id是否合法
	 * @param orderId 活动id
	 * @param guestId 分店id
	 * @return  true 合法   false 不合法
	 * @throws Exception
	 */
	public boolean checkInputParameter(String orderId, String guestId)throws Exception;
}
