package com.beike.dao.shopcart;

import java.util.List;
import java.util.Map;

import com.beike.entity.shopcart.ShopCart;
import com.beike.entity.shopcart.ShopItem;

/**
 * 购物车DAO层
 */
public interface ShopCartDao {
	
	/**
	 *  查询购物车内商品数量
	 * @param userId
	 * @param goodsId
	 * @return
	 */
	public int  getShopItemCount (Long userId, Long goodsId);
	
	/**
	 * 手机接口:查询购物车
	 * @param userID
	 * @return
	 */
	public List<ShopItem> queryShopCartInfoByUserID(Long userID ,Long pageSize,Long pageoffset );
	
	/**
	 * 手机接口:查询购物车
	 * @param userID
	 * @return
	 */
	public long  queryShopCartCountByUserID(Long userID );
	
	/**
	 *  手机接口：查询购物车
	 * @param shopcartId
	 * @return
	 */
	public  Map<String, Object>  queryShopCartById(String shopcartId) ;
	
	/**
	 *  手机接口：批量删除购物车
	 * @param shoppingCartId
	 * @param userid
	 * @return
	 */
	public void removeShopItemByids (String shoppingCartIds, Long userid);
	

	public ShopItem isshopItemExists(String goodsid, String merchantID,
			String userid,String miaoshaId);

	// 登陆用户购物车查询
	public List<ShopCart> queryShopCartByUserID(String userID);

	// 临时购物车查询
	public List<ShopItem> queryTempShopCart(String goodsID);

	// 查询当前品牌下商品
	public List<ShopCart> queryShopCartByMerchantID(String merchantID);

	// 用户登陆批量添加
	public boolean addShopItem(ShopItem shopItem, String userid,String miaoshaId);

	// 添加新商品到购物车。用户登陆
	public boolean addShopItem(String goodsid, String merchantId, String userid,String miaoshaId);

	/*
	 * 用户支付后,更新数据库购物车记录 FIXME jianwen
	 */
	public boolean updateShopItem(String goodsid, String count, String userid,String miaoshaId);

	// 用户在购物车中执行删除商品操作
	public boolean removeShopItem(String goodsID, String userid,String miaoshaId);

	public void removeBatchGoods(String[] goodsid, String userid,String[] miaoshaId);

	
	/**
	 * 购买成功后根据表主键IN删除购物车
	 * 
	 * @param goodsIdStr
	 *            add by wenhua.cheng
	 */
	public void removeShopCartBySuc(String goodsIdStr);

	
	/**
	 * 根据 ShopItem中的merchantId 来获取 ShopCart 中的merchantname 信息
	 * 
	 *  Add by zx.liu
	 */
	public String findMerchantName(Long merchantId);

	
	/**
	 * 补充Dao的定义,用户登录时添加Cookie的购物信息到数据库
	 * 
	 * Add by zx.liu
	 * 
	 */
	public boolean appendShopItem(ShopItem shopItem);

		
	/**
	 * 根据 ShopItem中的goodsId 来获取 ShopCart 的信息 Add by zx.liu
	 * 
	 */
	public ShopCart findTempShopCart(Long goodsId);

	/**
	 * 根据 ShopItem中的goodsId 来获取 ShopCart 中的profilevalue 信息 Add by zx.liu
	 */
	public Long findGoodSalesCount(Long goodsId);
	
	/**
	 * 添加新商品到购物车
	 * @param goodsid
	 * @param merchantId
	 * @param buyCount
	 * @param userid
	 * @return
	 */
	public boolean addShopItem(String goodsid, String merchantId, Long buyCount, String userid,String miaoshaId);
	
	/**
	 *  购买成功后根据表秒杀ID删除购物车
	 * @param miaoshaIdStr
	 */
	public void removeShopCartBySucMiaosha(String miaoshaIdStr);
	
	/**
	 * 商品详情页购物车添加
	 * @param goodsid
	 * @param merchantId
	 * @param buyCount
	 * @param userid
	 * @param miaoshaId
	 * @param limitCount
	 * @return
	 */
	public  String addShopItemNew(String goodsid, String merchantId,
			Long buyCount, String userid,String miaoshaId,Long limitCount) ;
}
