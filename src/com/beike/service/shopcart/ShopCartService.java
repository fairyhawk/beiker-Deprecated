package com.beike.service.shopcart;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.beike.common.bean.trx.TrxResponseData;
import com.beike.common.exception.ShoppingCartException;
import com.beike.entity.shopcart.ShopCart;
import com.beike.entity.shopcart.ShopItem;
import com.beike.entity.shopcart.ShopcartSummary;

public interface ShopCartService {
	
	/**
	 * 查询可用数量
	 * @param isAvaliable
	 * @param singleCount
	 * @param maxCount
	 * @param userId
	 * @param goodsId
	 * @return
	 */
	public Map<String, String> checkAllowBuyCount(String isAvaliable, Long singleCount, Long maxCount, Long userId, Long goodsId) ;
	
	/**
	 *  查询购物车内商品数量
	 * @param userId
	 * @param goodsId
	 * @return
	 */
	public int  getShopItemCount (Long userId, Long goodsId);

	/**
	 * 手机接口：查询 购物车
	 * 
	 * @param userID
	 * @return
	 */
	public TrxResponseData qryShoppingCart(Long userID, Long pageSize,
			Long rowsoffset);

	/**
	 * 手机接口: 添加购物车
	 * 
	 * @param sourceMap
	 * @return
	 * @throws Exception
	 * @throws ShoppingCartException
	 */
	public TrxResponseData addShoppingCart(String userId, String goodsId, String goodsCount) throws ShoppingCartException;

	/**
	 * 手机接口:删除购物车
	 * 
	 * @param sourceMap
	 * @return
	 */
	public TrxResponseData delShoppingCartById(String userId,String shopCartId) throws ShoppingCartException ;
	
	
	/**
	 * 支付成功:删除购物车
	 * 
	 * @param sourceMap
	 * @return
	 */
	public void  delShoppingCartByIdPaySuc(String userId,String shopCartId);

	// 登陆用户购物车查询
	public List<ShopCart> queryShopCartByUserID(String userID);

	// 批量删除商品
	public void removeBatchGoods(String goodsid, String userid,String miaoshaId);

	public ShopcartSummary getShopCartSummary(List<ShopCart> shopcartList);

	// 添加新商品到购物车。用户登陆
	public boolean addShopItem(String goodsid, String merchantId, String userid,String miaoshaId);

	public ShopcartSummary getShopSummary(HttpServletRequest request);

	/*
	 * 用户购物车中,更新数据库购物车记录 FIXME jianwen
	 */
	public boolean updateShopItem(String goodsid, String count, String userid,String miaoshaId);

	// 用户在购物车中执行删除商品操作
	public boolean removeShopItem(String goodsID, String userid,String miaoshaId);

	/**
	 * 通过ShopItem的集合 来获取 ShopCart的集合 Add by zx.liuzx
	 */
	public List<ShopCart> getTempShopCart(List<ShopItem> listShopItem);

	/**
	 * 通过json 串来获取List<ShopItem> 集合 Add by zx.liuzx
	 */
	public List<ShopItem> getListShopItem(String json);

	/**
	 * 添加ShopItem到json 串 Add by zx.liuzx
	 */
	public String addTempShopItem(String goodsId, String merchantId, String json,String miaoshaId);

	/**
	 * 根据goodsId修改ShopItem 的数量 Add by zx.liuzx
	 */
	public String modifyTempShopItem(String goodsId, String buyCount,
			String json,String miaoshaId);

	/**
	 * 根据goodsId移除json 串中的ShopItem Add by zx.liuzx
	 */
	public String removeTempShopItem(String goodsId, String json,String miaoshaId);

	/**
	 * 根据goodsIds[]批量移除json 串中的多个ShopItem Add by zx.liuzx
	 */
	public String removeBatchShopItem(String batchGoodsId, String json,String miaoshaId);

	/**
	 * 依据merchantId 分类 List<ShopCart>
	 * 
	 * @param listShopCart
	 * @return
	 * @author zx.liu
	 */
	public List<ShopCart> classifyShopCartByMerid(List<ShopCart> listShopCart);

	/**
	 * 购买成功后根据表主键IN删除购物车
	 * 
	 * @param goodsIdStr
	 *            add by wenhua.cheng
	 */
	public void removeShopCartBySuc(String shopCartIdStr);

	public boolean appendShopItem(ShopItem shopItem);

	/**
	 * 加入购物车，增加购买数量参数
	 * 
	 * @param goodsId
	 * @param merchantId
	 * @param buyCount
	 * @param json
	 * @return
	 * @author qiaowb
	 */
	public String addTempShopItem(String goodsId, String merchantId,
			Long buyCount, String json,String miaoshaId);

	/**
	 * 加入购物车，增加购买数量参数
	 * 
	 * @param goodsid
	 * @param merchantId
	 * @param buyCount
	 * @param userid
	 * @return qiaowb
	 */
	public boolean addShopItem(String goodsid, String merchantId,
			Long buyCount, String userid,String miaoshaId);
	
	  /**
	 * 支付成功:秒杀商品删除购物车
	 * 
	 * @param sourceMap
	 * @return
	 */
	public void delShoppingCartByIdPaySucMiaosha(String userId, String miaoshaId);
	
	/**
	 * 通过json 串来获取ShopItem 对象
	 * @param json
	 * @param goodsId
	 * @param miaoshaId
	 * @return
	 */
	public ShopItem getListShopItem(String json,String goodsId,String miaoshaId);
	
	/**
	 * @param userID
	 * @param goodsid
	 * @param merchantId
	 * @param miaoshaId
	 * @return
	 */
	public ShopItem preQryInWtDBShopCartByUserID(String userID,String goodsid,String merchantId,String miaoshaId);
	
	/**
	 * 添加购物车值，判断了总量限购
	 * @param goodsId
	 * @param merchantId
	 * @param buyCount
	 * @param json
	 * @param miaoshaId
	 * @return
	 */
	public String addTempShopItemNew(String goodsId, String merchantId,
			Long buyCount, String json,String miaoshaId,String limitCount) ;
	
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
	public String addShopItemNew(String goodsid, String merchantId,
			Long buyCount, String userid,String miaoshaId,String limitCount);
}
