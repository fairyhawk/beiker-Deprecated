package com.beike.service.impl.shopcart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.bean.trx.TrxResponseData;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.ShoppingCartException;
import com.beike.core.service.trx.limit.PayLimitService;
import com.beike.dao.goods.GoodsDao;
import com.beike.dao.merchant.MerchantDao;
import com.beike.dao.miaosha.MiaoShaDao;
import com.beike.dao.shopcart.ShopCartDao;
import com.beike.dao.trx.soa.proxy.GoodsSoaDao;
import com.beike.entity.goods.Goods;
import com.beike.entity.merchant.Merchant;
import com.beike.entity.miaosha.MiaoSha;
import com.beike.entity.shopcart.ShopCart;
import com.beike.entity.shopcart.ShopItem;
import com.beike.entity.shopcart.ShopcartSummary;
import com.beike.entity.user.User;
import com.beike.service.shopcart.ShopCartService;
import com.beike.util.DateUtils;
import com.beike.util.StringUtils;
import com.beike.util.TrxConstant;
import com.beike.util.WebUtils;
import com.beike.util.shopcart.ShopJsonUtil;
import com.beike.util.singletonlogin.SingletonLoginUtils;

/**
 * 购物车的业务逻辑实现
 * 
 * @author jianwen
 * 
 */

@Service("shopCartService")
public class ShopCartServiceImpl implements ShopCartService {

	private static final Log logger = LogFactory
			.getLog(ShopCartServiceImpl.class);
	@Autowired
	private ShopCartDao shopCartDao;

	@Autowired
	private GoodsDao goodsDao;

	@Autowired
	private MerchantDao merchantDao;

	@Autowired
	private GoodsSoaDao goodsSoaDao;

	@Autowired
	private PayLimitService payLimitService;
	@Autowired
	private MiaoShaDao miaoShaDao;
	

	@Override
	public boolean addShopItem(String goodsid, String merchantId, String userid,String miaoshaId) {
		return shopCartDao.addShopItem(goodsid, merchantId, userid,miaoshaId);
	}

	@Override
	public List<ShopCart> queryShopCartByUserID(String userID) {
		return shopCartDao.queryShopCartByUserID(userID);
	}
	@Override
	public ShopItem preQryInWtDBShopCartByUserID(String userID,String goodsid,String merchantId,String miaoshaId) {
		return shopCartDao.isshopItemExists(goodsid,merchantId,userID,miaoshaId);
	}
	@Override
	public boolean removeShopItem(String goodsID, String userid,String miaoshaId) {
		return shopCartDao.removeShopItem(goodsID, userid,miaoshaId);
	}

	@Override
	public boolean updateShopItem(String goodsid, String count, String userid,String miaoshaId) {
		return shopCartDao.updateShopItem(goodsid, count, userid,miaoshaId);
	}

	@Override
	public ShopcartSummary getShopCartSummary(List<ShopCart> shopcartList) {
		ShopCart children = null;
		int totalProduct = 0;

		double totalMoney = 0.0f;
		double totalReturn = 0.0f;
		if (shopcartList != null) {
			for (Iterator<ShopCart> iterator = shopcartList.iterator(); iterator
					.hasNext();) {
				children = iterator.next();
				if (1 == children.getIsavaliable()) {
					totalMoney = totalMoney
							+ children.getGoodsCurrentPrice()
							* children.getBuyCount().doubleValue();
					totalReturn = totalReturn
							+ children.getGoodsRebatePrice().doubleValue()
							* children.getBuyCount();
				}
				// totalProduct = totalProduct + 1;
			}
			totalProduct = shopcartList.size();
		}

		return new ShopcartSummary(totalProduct, totalMoney, totalProduct);
	}

	@Override
	public void removeBatchGoods(String goodsid, String userid,String miaoshaId) {

		String[] goodsidList = goodsid.split(",");
		String[] miaoshaIdList = miaoshaId.split(",");

		shopCartDao.removeBatchGoods(goodsidList, userid,miaoshaIdList);

	}

	/**
	 * 通过ShopItem的集合 来获取 ShopCart的集合 Add by zx.liuzx
	 */
	public List<ShopCart> getTempShopCart(List<ShopItem> listShopItem) {

		if (null == listShopItem) {
			return null;
		}
		List<ShopCart> listShopCart = new ArrayList<ShopCart>();
		for (ShopItem item : listShopItem) {
			ShopCart shopCart  = shopCartDao.findTempShopCart(item.getGoodsid());
			shopCart.setMiaoshaid(0L);
			 Long salesCount = 0L;
			 if(item.getMiaoshaid()!=0){
				 MiaoSha miaosha = miaoShaDao.getMiaoShaById(item.getMiaoshaid());
				 if(miaosha.getGoodsId().equals(item.getGoodsid())){
					 shopCart.setGoodsName(miaosha.getMsTitle());
					 shopCart.setEndTime(miaosha.getMsEndTime());
					 shopCart.setMaxCount(Long.valueOf(miaosha.getMsMaxCount()));
					 shopCart.setUserBuyCount(Long.valueOf(miaosha.getMsSingleCount()));
					 shopCart.setGoodsCurrentPrice(miaosha.getMsPayPrice());
					 logger.info("++++++++++++++++"+miaosha.getMsPayPrice()+"++++++++"+new BigDecimal(miaosha.getMsPayPrice()));
					 shopCart.setMiaoshaid(item.getMiaoshaid());
					 logger.info("++++++++++++++++"+shopCart.getGoodsCurrentPrice());
					 int msStatus = miaosha.getMsStatus();
					 Date startTime = new Date(miaosha.getMsStartTime().getTime());
						Date endTime =new Date(miaosha.getMsEndTime().getTime());
						boolean booDate = DateUtils.betweenBeginAndEnd(new Date(),startTime,endTime);
					 if(msStatus==1&&booDate){
					 shopCart.setIsavaliable(1);
					 }else {
						 shopCart.setIsavaliable(0);
					 }
				 }
				 salesCount = Long.valueOf(miaosha.getMsSaleCount());
			 }else{
				 salesCount = shopCartDao.findGoodSalesCount(item.getGoodsid());
			 }
			 
			 // 补充：防止脏数据的出现控制
			if (null == shopCart) {
				continue;
			}
			String merchantName = shopCartDao.findMerchantName(item
					.getMerchantid());
			
			shopCart.setMerchantId(item.getMerchantid());
			shopCart.setMerchantName(merchantName);
			shopCart.setBuyCount(item.getBuy_count());
			shopCart.setAddTime(item.getAddtime());
			shopCart.setSalesCount(salesCount);
			if (shopCart.getUserBuyCount().longValue() > 0) {
				shopCart
						.setCanCount(shopCart.getUserBuyCount() > (shopCart
								.getMaxCount() - salesCount) ? (shopCart
								.getMaxCount() - salesCount) : shopCart
								.getUserBuyCount());// //商品上限量与已购买量差
			} else {
				shopCart.setCanCount(shopCart.getMaxCount() - salesCount);
			}
			listShopCart.add(shopCart);
		}
		return listShopCart;

	}

	/**
	 * 通过json 串来获取List<ShopItem> 集合 Add by zx.liuzx
	 */
	public List<ShopItem> getListShopItem(String json) {

		if (null == json) {
			return null;
		}
		// 事先去除Json中的转义
		return ShopJsonUtil.query(ShopJsonUtil.escape(json));
	}
	
	/**
	 * 通过json 串来获取ShopItem 对象
	 */
	public ShopItem getListShopItem(String json,String goodsId,String miaoshaId) {

		if (null == json) {
			return null;
		}
		// 事先去除Json中的转义
		return ShopJsonUtil.query(ShopJsonUtil.escape(json),goodsId,miaoshaId);
	}

	/**
	 * 添加ShopItem 到json 串 Add by zx.liuzx
	 */
	public String addTempShopItem(String goodsId, String merchantId, String json,String miaoshaId) {
		return addTempShopItem(goodsId, merchantId, 1L, json,miaoshaId);
	}

	/**
	 * 修改jsong串ShopItem的数量,返回json串 Add by zx.liuzx
	 */
	public String modifyTempShopItem(String goodsId, String buyCount,
			String json,String miaoshaId) {

		if (null == json) {
			return null;
		}
		ShopItem shopItem = new ShopItem();
		shopItem.setGoodsid(Long.parseLong(goodsId));
		shopItem.setMiaoshaid(Long.parseLong(miaoshaId));
		shopItem.setBuy_count(Long.parseLong(buyCount)); // 新的商品数量
		// shopItem.setAddtime(new Date()); // 修改数量时不用更新日期
		// 事先去除Json中的转义
		return ShopJsonUtil.modify(shopItem, ShopJsonUtil.escape(json));
	}

	/**
	 * 根据goodsId移除json 串中的ShopItem Add by zx.liuzx
	 */
	public String removeTempShopItem(String goodsId, String json,String miaoshaId) {
		if (null == goodsId || "".equals(goodsId) || null == json
				|| "".equals(json)) {
			return null;
		}
		// 事先去除Json中的转义
		return ShopJsonUtil.remove(Long.parseLong(goodsId), ShopJsonUtil
				.escape(json),Long.parseLong(miaoshaId));
	}

	/**
	 * 根据goodsIds[]批量移除json 串中的多个ShopItem Add by zx.liuzx
	 */
	public String removeBatchShopItem(String batchGoodsId, String json,String miaoshaId) {
		if (null == batchGoodsId || "".equals(batchGoodsId) || null == json
				|| "".equals(json)) {
			return null;
		}
		// 获得商品ID的数组
		String[] goodsIds = batchGoodsId.split(",");
		String[] miaoshaIds = miaoshaId.split(",");
		// 事先去除Json中的转义
		String shopJson = ShopJsonUtil.escape(json);
		for (int i=0;i<goodsIds.length;i++ ) {
			shopJson = ShopJsonUtil.remove(Long.parseLong(goodsIds[i]), shopJson,Long.parseLong(miaoshaIds[i]));
		}
		return shopJson;
	}

	/**
	 * 依据merchantId 分类 List<ShopCart>
	 * 
	 * @param listShopCart
	 * @return
	 * @author zx.liu
	 */
	public List<ShopCart> classifyShopCartByMerid(List<ShopCart> listShopCart) {

		if (null == listShopCart) {
			return null;
		}

		List<ShopCart> merListShopCart = new ArrayList<ShopCart>();
		// shopCartList 是已经排好序的 List<ShopCart>
		List<ShopCart> shopCartList = this.sortShopCartByMerid(listShopCart);
		int flag = 0;
		List<ShopCart> tempShopCartList = null;

		/**
		 * 该循环有待优化
		 * 
		 * @author zx.liu
		 */
		for (int i = 0; i < shopCartList.size(); i++) {
			if (flag == i) {
				merListShopCart.add(shopCartList.get(flag));
				tempShopCartList = new ArrayList<ShopCart>();
			}
			if (shopCartList.get(flag).getMerchantId().compareTo(
					shopCartList.get(i).getMerchantId()) == 0) {
				tempShopCartList.add(shopCartList.get(i));
				// shopCartList.get(flag).setAddTime(compareDate(shopCartList.get(flag).getAddTime(),
				// shopCartList.get(i).getAddTime()));
			} else {
				shopCartList.get(flag).setShopcartList(tempShopCartList);
				flag = i--;
			}
			// 最后的补充
			if (i + 1 == shopCartList.size()) {
				shopCartList.get(flag).setShopcartList(tempShopCartList);
			}
		}

		// 最后再依据 addTime降序 对merListShopCart 进行排序
		return this.sortShopCartByTime(merListShopCart);
	}

	/**
	 * 依据addTime 对List<ShopCart> 排序
	 * 
	 * @param listShopCart
	 * @return
	 * @author zx.liu
	 */
	@SuppressWarnings("unchecked")
	private List<ShopCart> sortShopCartByTime(List<ShopCart> listShopCart) {

		/*
		 * // 测试 for (ShopCart shop : listShopCart) { System.out.println();
		 * System.out.println("AddTime01 " + shop.getMerchantId() + " : " +
		 * shop.getMerchantName() + " : " + shop.getAddTime()); }
		 */

		Comparator comparator = new MyComparator();
		Collections.sort(listShopCart, comparator);

		/*
		 * // 测试 for (ShopCart shop : listShopCart) { System.out.println();
		 * System.out.println("AddTime02 " + shop.getMerchantId() + " : " +
		 * shop.getMerchantName() + " : " + shop.getAddTime()); }
		 */

		return listShopCart;

	}

	/**
	 * 依据merchantId 对List<ShopCart> 排序
	 * 
	 * @param listShopCart
	 * @return
	 * @author zx.liu
	 */
	@SuppressWarnings("unchecked")
	private List<ShopCart> sortShopCartByMerid(List<ShopCart> listShopCart) {

		/*
		 * // 测试 for (ShopCart shop : listShopCart) { System.out.println();
		 * System.out.println("MerId01 " + shop.getMerchantId() + " : " +
		 * shop.getMerchantName() + " : " + shop.getAddTime()); }
		 */

		// Comparator comparator = new MyComparator();
		Comparator comparator2 = new MyComparator2();
		Collections.sort(listShopCart, comparator2);

		/*
		 * // 测试 for (ShopCart shop : listShopCart) { System.out.println();
		 * System.out.println("MerId02 " + shop.getMerchantId() + " : " +
		 * shop.getMerchantName() + " : " + shop.getAddTime()); }
		 */

		return listShopCart;

	}

	/**
	 * 比较两个日期, 返回大的日期值 // 备用
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	@SuppressWarnings("unused")
	private Date compareDate(Date date1, Date date2) {

		if (date1.after(date2)) {
			return date1;
		}
		return date2;
	}

	@SuppressWarnings("unchecked")
	private class MyComparator implements Comparator {
		public int compare(Object obj1, Object obj2) {
			ShopCart shopCart1 = (ShopCart) obj1;
			ShopCart shopCart2 = (ShopCart) obj2;
			// 按照 addTime 降序 来排序 List<ShopCart> 的集合
			if (shopCart1.getAddTime().before(shopCart2.getAddTime())) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	@SuppressWarnings("unchecked")
	private class MyComparator2 implements Comparator {
		public int compare(Object obj1, Object obj2) {
			ShopCart shopCart1 = (ShopCart) obj1;
			ShopCart shopCart2 = (ShopCart) obj2;
			// 按照 merchantId升序 和addTime降序 两个属性来排序 List<ShopCart> 的集合
			if (shopCart1.getMerchantId().compareTo(shopCart2.getMerchantId()) == 1) {
				return 1;
			} else if (shopCart1.getMerchantId().compareTo(
					shopCart2.getMerchantId()) == 0) {
				if (shopCart1.getAddTime().before(shopCart2.getAddTime())) {
					return 1;
				}
				return 0;
			} else {
				return 0;
			}
		}
	}

	@Override
	public ShopcartSummary getShopSummary(HttpServletRequest request) {
		List<ShopItem> listShopItem = null;
		List<ShopCart> listShopCart = null;

		// 判断用户是否登录
		User user = SingletonLoginUtils.getMemcacheUser(request);
		// 用户未登录时（user == null）,仅读取用户端的Cookie信息
		if (user == null) {

			// 仅仅从Cookie 获取购物车信息
			String shopJson = WebUtils.getCookieValue(
					ShopJsonUtil.ShopCookieKey, request);
			// 将json 解析为List<ShopItem> 的集合
			listShopItem = getListShopItem(shopJson);
			listShopCart = getTempShopCart(listShopItem); // Service

		} else {
			// 以下的else表示用户登录后的相关操作`
			listShopCart = queryShopCartByUserID(Long.toString(user.getId()));
		}

		// 根据merchantId 来分类List<ShopCart> 集合
		// List<ShopCart> shopCartList = classifyShopCartByMerid();
		return getShopCartSummary(listShopCart);

	}

	/**
	 * 购买成功后根据表主键IN删除购物车
	 * 
	 * @param goodsIdStr
	 *            add by wenhua.cheng
	 */
	public void removeShopCartBySuc(String shopCartIdStr) {

		try {

			shopCartDao.removeShopCartBySuc(shopCartIdStr);

		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
	
	/**
	 * 购买成功后秒杀商品根据表秒杀ID删除购物车
	 * 
	 * @param goodsIdStr
	 *            add by wenhua.cheng
	 */
	public void removeShopCartBySucMiaosha(String shopCartIdStr) {

		try {

			shopCartDao.removeShopCartBySucMiaosha(shopCartIdStr);

		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
	}

	@Override
	public  boolean appendShopItem(ShopItem shopItem) {
		try {
			return shopCartDao.appendShopItem(shopItem);
		} catch (Exception e) {
			logger.error("购物车重复索引异常,忽略");
			e.printStackTrace();
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.beike.service.shopcart.ShopCartService#addTempShopItem(java.lang.
	 * String, java.lang.String, java.lang.Long, java.lang.String)
	 */
	public String addTempShopItem(String goodsId, String merchantId,
			Long buyCount, String json,String miaoshaId) {
		ShopItem shopItem = new ShopItem();
		shopItem.setMerchantid(Long.parseLong(merchantId));
		shopItem.setGoodsid(Long.parseLong(goodsId));
		shopItem.setBuy_count(buyCount);
		shopItem.setMiaoshaid(Long.parseLong(miaoshaId));
		shopItem.setAddtime(new Date()); // 添加商品时默认为当前日期
		// 事先去除Json中的转义
		return ShopJsonUtil.addItem(shopItem, ShopJsonUtil.escape(json));
	}
	
	/**
	 * 添加购物车数据值，做了总量限购判断
	 * @param goodsId
	 * @param merchantId
	 * @param buyCount
	 * @param json
	 * @param miaoshaId
	 * @return
	 */
	public String addTempShopItemNew(String goodsId, String merchantId,
			Long buyCount, String json,String miaoshaId,String limitCount) {
		ShopItem shopItem = new ShopItem();
		shopItem.setMerchantid(Long.parseLong(merchantId));
		shopItem.setGoodsid(Long.parseLong(goodsId));
		shopItem.setBuy_count(buyCount);
		shopItem.setMiaoshaid(Long.parseLong(miaoshaId));
		shopItem.setAddtime(new Date()); // 添加商品时默认为当前日期
		shopItem.setLimitCount(Long.valueOf(limitCount));
		// 事先去除Json中的转义
		return ShopJsonUtil.addItemNew(shopItem, ShopJsonUtil.escape(json));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.beike.service.shopcart.ShopCartService#addShopItem(java.lang.String,
	 * java.lang.String, java.lang.Long, java.lang.String)
	 */
	public boolean addShopItem(String goodsid, String merchantId,
			Long buyCount, String userid,String miaoshaId) {
		return shopCartDao.addShopItem(goodsid, merchantId, buyCount, userid,miaoshaId);
	}
	
	public String addShopItemNew(String goodsid, String merchantId,
			Long buyCount, String userid,String miaoshaId,String limitCount) {
		return shopCartDao.addShopItemNew(goodsid, merchantId, buyCount, userid,miaoshaId,Long.valueOf(limitCount));
	}

	@Override
	public TrxResponseData qryShoppingCart(Long userID, Long pageSize,
			Long rowsOffset) {
		logger.info("++++++++++qryShoppingCart  ------ userID:" + userID
				+ "+++++++++      ");
		TrxResponseData rep =  new TrxResponseData();
		rep.setUserId(String.valueOf(userID));
		rep.setPageSize(pageSize);
		rep.setRowsOffset(rowsOffset);
		// 查询总记录数
		long totalRows = shopCartDao.queryShopCartCountByUserID(userID);
		rep.setTotalRows(totalRows);
		

		// 查询购物车
		List<ShopItem> ShopItemList = shopCartDao.queryShopCartInfoByUserID(
				userID, pageSize, rowsOffset);

		if (null != ShopItemList && ShopItemList.size() > 0) {
			StringBuilder shopCartIdReturnStr = new StringBuilder(); // 购物车id
			StringBuilder goodsIdReturnStr = new StringBuilder(); // 商品id
			StringBuilder goodsNameReturnStr = new StringBuilder(); // 商品名称
			StringBuilder goodsCountReturnStr = new StringBuilder(); // 商品数量
			StringBuilder goodsTitleReturnStr = new StringBuilder(); // 商品简称
			StringBuilder goodsDTPicUrlReturnStr = new StringBuilder(); // 商品详情图片地址
			StringBuilder goodsPayPriceReturnStr = new StringBuilder(); // 商品支付价格
			StringBuilder goodsIsAvailableReturnStr = new StringBuilder(); // 商品是否有效
			StringBuilder goodsLastUpdateReturnStr = new StringBuilder(); // 最后更新时间
			StringBuilder merchantNameReturnStr = new StringBuilder(); // 品牌名称
			StringBuilder allowBuyCountReturnStr = new StringBuilder(); // 可购买数量

			for (ShopItem shopItem : ShopItemList) {
				Long shopCartId = shopItem.getShopcartid();
				Long ShopGoodsId = shopItem.getGoodsid();
				Long merchantId = shopItem.getMerchantid();
				Long buyCount = shopItem.getBuy_count();
				String lastUpdateDateStr = DateUtils.toString(shopItem
						.getAddtime(), "yyyy-MM-dd HH:mm:ss");
				
				// 查询商品信息
				Goods goods = goodsDao.getGoodsDaoById(ShopGoodsId);
				if (null == goods) {
					logger
							.info("qryShoppingCart : goodsObj is not found +++++++ ShopGoodsId:"
									+ ShopGoodsId + "  ");
					continue;
				}
				Long goodsId = goods.getGoodsId();
				String goodsName = goods.getGoodsname();
				String goodsTitle = goods.getGoodsTitle();
				String goodsDTPicUrl = goods.getLogo3();
				double goodsPayPrice = goods.getPayPrice();
				int goodsIsAvailable = goods.getIsavaliable();
				long  goodsSingleCount =  goods.getGoodsSingleCount() ;
				long  goodsMaxCount =   goods.getMaxcount() ;
				//拼接图片地址路径
				goodsDTPicUrl = TrxConstant.UPLOAD_IMAGES_PATH + goodsDTPicUrl;
				
				// 查询品牌信息
				Merchant merchant = merchantDao.getMerchantNameById(merchantId);
				String mderchantName = merchant.getMerchantname();
				
				// 查询可购买数量
				Map<String, String> result = checkAllowBuyCount(String.valueOf(goodsIsAvailable), goodsSingleCount, goodsMaxCount, userID, goodsId);
				Long allowBuyCount = Long.valueOf(result.get("allowBuyCount"));
				
				shopCartIdReturnStr.append(shopCartId).append("|");
				goodsIdReturnStr.append(goodsId).append("|");
				goodsCountReturnStr.append(buyCount).append("|");
				goodsLastUpdateReturnStr.append(lastUpdateDateStr).append("|");
				goodsNameReturnStr.append(goodsName).append("|");
				goodsTitleReturnStr.append(goodsTitle).append("|");
				goodsDTPicUrlReturnStr.append(goodsDTPicUrl).append("|");
				goodsPayPriceReturnStr.append(goodsPayPrice).append("|");
				goodsIsAvailableReturnStr.append(goodsIsAvailable).append("|");
				merchantNameReturnStr.append(mderchantName).append("|");
				allowBuyCountReturnStr.append(allowBuyCount).append("|");
			}

			// 去掉最后一个|号
			shopCartIdReturnStr.deleteCharAt(shopCartIdReturnStr.length() - 1);
			goodsIdReturnStr.deleteCharAt(goodsIdReturnStr.length() - 1);
			goodsCountReturnStr.deleteCharAt(goodsCountReturnStr.length() - 1);
			goodsLastUpdateReturnStr.deleteCharAt(goodsLastUpdateReturnStr
					.length() - 1);
			goodsNameReturnStr.deleteCharAt(goodsNameReturnStr.length() - 1);
			goodsTitleReturnStr.deleteCharAt(goodsTitleReturnStr.length() - 1);
			goodsDTPicUrlReturnStr
					.deleteCharAt(goodsDTPicUrlReturnStr.length() - 1);
			goodsPayPriceReturnStr
					.deleteCharAt(goodsPayPriceReturnStr.length() - 1);
			goodsIsAvailableReturnStr.deleteCharAt(goodsIsAvailableReturnStr
					.length() - 1);
			merchantNameReturnStr
					.deleteCharAt(merchantNameReturnStr.length() - 1);
			allowBuyCountReturnStr.deleteCharAt(allowBuyCountReturnStr.length() - 1);
			

			// 构造返回值
			
			rep.setShopCartId(shopCartIdReturnStr.toString());
			rep.setGoodsId(goodsIdReturnStr.toString());
			rep.setGoodsCount(goodsCountReturnStr.toString());
			rep.setGoodsName(goodsNameReturnStr.toString());
			rep.setGoodsTitle(goodsTitleReturnStr.toString());
			rep.setGoodsDTPicUrl(goodsDTPicUrlReturnStr.toString());
			rep.setGoodsPayPrice(goodsPayPriceReturnStr.toString());
			rep.setGoodsIsAvailable(goodsIsAvailableReturnStr.toString());
			rep.setMerchantName(merchantNameReturnStr.toString());
			rep.setGoodsLastUpdate(goodsLastUpdateReturnStr.toString());
			rep.setAllowBuyCount(allowBuyCountReturnStr.toString());

		}
		return rep;
	}
	

	@Override
	public TrxResponseData addShoppingCart(String userIdReq, String goodsIdReq,
			String buyCountReq) throws ShoppingCartException {
		TrxResponseData rep = null;

		logger.info("++++++++addShoppingCart ----- userIdReq:" + userIdReq
				+ "++++ goodsIdReq:" + goodsIdReq + " +++  buyCountReq:"
				+ buyCountReq + "   ");

		if (StringUtils.validNull(userIdReq)
				&& StringUtils.validNull(goodsIdReq)
				&& StringUtils.validNull(buyCountReq)) {
			Map<String, String> goodsInfoMap = new HashMap<String, String>();

			StringBuilder shopCartIdReturnStr = new StringBuilder(); // 购物车id
			StringBuilder goodsIdReturnStr = new StringBuilder(); // 商品id
			StringBuilder goodsNameReturnStr = new StringBuilder(); // 商品名称
			StringBuilder goodsCountReturnStr = new StringBuilder(); // 商品数量
			StringBuilder goodsTitleReturnStr = new StringBuilder(); // 商品简称
			StringBuilder goodsDTPicUrlReturnStr = new StringBuilder(); // 商品详情图片地址
			StringBuilder goodsPayPriceReturnStr = new StringBuilder(); // 商品支付价格
			StringBuilder goodsLastUpdateReturnStr = new StringBuilder(); // 最后更新时间
			StringBuilder merchantNameReturnStr = new StringBuilder(); // 品牌名称

			String[] arrayGoodsId = goodsIdReq.split("\\|");
			String[] arrayBuyCount = buyCountReq.split("\\|");

			// 校验 goodsId 重复
			Map<String, String> tempMap = new HashMap<String, String>();
			int arrayGoodsCount = arrayGoodsId.length;
			for (int j = 0; j < arrayGoodsCount; j++) {
				String goodsId = arrayGoodsId[j];
				if (!tempMap.containsKey(goodsId)) {
					tempMap.put(goodsId, goodsId);
				}
			}

			if (arrayGoodsId.length != arrayBuyCount.length
					|| tempMap.size() != arrayGoodsId.length) {
				logger.debug(" +++++addShoppingCart:-------userIdReq:"
						+ userIdReq + "++++ ShoppingCartException:"
						+ BaseException.SHOPPINGCART_LIST_NOT_EQULAS
						+ "++++++++++ ");
				throw new ShoppingCartException(
						BaseException.SHOPPINGCART_LIST_NOT_EQULAS);
			}

			for (int j = 0; j < arrayGoodsId.length; j++) {
				String goodsId = arrayGoodsId[j];
				String buyCount = arrayBuyCount[j];
				goodsInfoMap.put(goodsId, buyCount);
			}

			// 拼装批量查询的sql
			StringBuilder goodsIdStr = new StringBuilder();
			for (String goodsId : arrayGoodsId) {
				goodsIdStr.append(goodsId);
				goodsIdStr.append(",");
			}
			goodsIdStr.deleteCharAt(goodsIdStr.length() - 1);

			// 查询 商品信息， 品牌信息
			List<Map<String, Object>> infoList = goodsDao
					.findGoodInfoByShopCartGoodsId(goodsIdStr.toString());

			// 循环添加购物车
			for (Map<String, Object> infoMap : infoList) {

				String merchantId = infoMap.get("merchantId").toString();
				String goodsId = infoMap.get("goodsId").toString();
				String goodsIdReturn = infoMap.get("goodsId").toString();
				String goodsNameReturn = infoMap.get("goodsName").toString();
				String goodsTitleReturn = infoMap.get("goodsTitle").toString();
				String goodsDTPicUrlReturn = infoMap.get("goodsDTPicUrl").toString();
				String goodsPayPriceReturn = infoMap.get("goodsPayPrice").toString();
				String merchantNameReturn = infoMap.get("merchantName").toString();
				
				//拼接图片地址路径
				goodsDTPicUrlReturn = TrxConstant.UPLOAD_IMAGES_PATH + goodsDTPicUrlReturn;

				// 添加购物车前， 计算是否超限
				String buyCount = goodsInfoMap.get(goodsId);// 本次欲购买商品数量
				// 添加购物车
				boolean addFlag = shopCartDao.addShopItem(goodsId, merchantId,
						Long.parseLong(buyCount), userIdReq,"0");//0为秒杀ID，手机接口不参与秒杀
				if (!addFlag) {
					logger.debug("+++++++++addShoppingCart EXCEPTION"
							+ BaseException.SHOPPINGCART_ADD_ITEM_FIELD
							+ "+++goodsIdStr:" + goodsId + "+++userIdReq:"
							+ userIdReq + "+++buyCountStr:" + buyCount
							+ "++++++++++");
					throw new ShoppingCartException(
							BaseException.SHOPPINGCART_ADD_ITEM_FIELD);
				}
				// 查询购物车
				ShopItem shopItem = shopCartDao.isshopItemExists(goodsId,
						merchantId, userIdReq,"0");//0为秒杀ID，手机接口不参与秒杀
				if (null == shopItem) {
					logger.debug("++++++addShoppingCart-----"
							+ BaseException.SHOPCART_ITEM_NOTFOUND
							+ "+++goodsIdStr:" + goodsId + "+++userIdReq:"
							+ userIdReq + "+++++");
					throw new ShoppingCartException(
							BaseException.SHOPCART_ITEM_NOTFOUND);
				}
				Long shopCartIdReturn = shopItem.getShopcartid();
				Long goodsCountReturn = shopItem.getBuy_count();// 添加成功后购物车单条商品数量
				String lastUpdateDateStr = DateUtils.toString(shopItem
						.getAddtime(), "yyyy-MM-dd HH:mm:ss");

				shopCartIdReturnStr.append(shopCartIdReturn).append("|");
				goodsIdReturnStr.append(goodsIdReturn).append("|");
				goodsNameReturnStr.append(goodsNameReturn).append("|");
				goodsCountReturnStr.append(goodsCountReturn).append("|");
				goodsTitleReturnStr.append(goodsTitleReturn).append("|");
				goodsDTPicUrlReturnStr.append(goodsDTPicUrlReturn).append("|");
				goodsPayPriceReturnStr.append(goodsPayPriceReturn).append("|");
				goodsLastUpdateReturnStr.append(lastUpdateDateStr).append("|");
				merchantNameReturnStr.append(merchantNameReturn).append("|");

			}

			// 去掉最后的|号
			shopCartIdReturnStr.deleteCharAt(shopCartIdReturnStr.length() - 1);
			goodsIdReturnStr.deleteCharAt(goodsIdReturnStr.length() - 1);
			goodsCountReturnStr.deleteCharAt(goodsCountReturnStr.length() - 1);
			goodsNameReturnStr.deleteCharAt(goodsNameReturnStr.length() - 1);
			goodsTitleReturnStr.deleteCharAt(goodsTitleReturnStr.length() - 1);

			goodsDTPicUrlReturnStr.deleteCharAt(goodsDTPicUrlReturnStr.length() - 1);
			goodsPayPriceReturnStr.deleteCharAt(goodsPayPriceReturnStr.length() - 1);
			goodsLastUpdateReturnStr.deleteCharAt(goodsLastUpdateReturnStr.length() - 1);
			merchantNameReturnStr.deleteCharAt(merchantNameReturnStr.length() - 1);

			// 构造返回值
			rep = new TrxResponseData();
			rep.setUserId(String.valueOf(userIdReq));
			rep.setShopCartId(shopCartIdReturnStr.toString());
			rep.setGoodsId(goodsIdReturnStr.toString());
			rep.setGoodsCount(goodsCountReturnStr.toString());
			rep.setGoodsName(goodsNameReturnStr.toString());
			rep.setGoodsTitle(goodsTitleReturnStr.toString());
			rep.setGoodsDTPicUrl(goodsDTPicUrlReturnStr.toString());
			rep.setGoodsPayPrice(goodsPayPriceReturnStr.toString());
			rep.setMerchantName(merchantNameReturnStr.toString());
			rep.setGoodsLastUpdate(goodsLastUpdateReturnStr.toString());
		}
		return rep;
	}

	@Override
	public TrxResponseData delShoppingCartById(String userId,
			String shoppingCartId) throws ShoppingCartException {
		TrxResponseData rsp = null;
		logger.info("delShoppingCartById---shoppingCartId:" + shoppingCartId
				+ "++++ userId:" + userId + "   ");
		if (StringUtils.validNull(shoppingCartId)
				&& StringUtils.validNull(userId)) {
			Map<String, String> tempMap = new HashMap<String, String>();
			String[] arrayShopCardId = shoppingCartId.split("\\|");

			// 校验 重复
			for (String shopCardIdStr : arrayShopCardId) {
				if (!tempMap.containsKey(shopCardIdStr)) {
					tempMap.put(shopCardIdStr, shopCardIdStr);
				}
			}

			if (tempMap.size() != arrayShopCardId.length) {
				logger.debug("++++++++delShoppingCartById-----"
						+ BaseException.SHOPPINGCART_LIST_NOT_EQULAS
						+ " +++++shoppingCartId:" + shoppingCartId
						+ " ++++ userId:" + userId + "+++++");
				throw new ShoppingCartException(
						BaseException.SHOPPINGCART_LIST_NOT_EQULAS);
			}

			// 批量删除购物车， 拼装shoppingCartIds串
			StringBuilder shoppingCartIds = new StringBuilder();
			for (String shopCardIdStr : arrayShopCardId) {
				shoppingCartIds.append(shopCardIdStr).append(",");
			}
			shoppingCartIds.deleteCharAt(shoppingCartIds.length() - 1);

			logger
					.info("++++++++delShoppingCartByIds---- +++++shoppingCartIds:"
							+ shoppingCartIds.toString()
							+ " ++++ userId:"
							+ userId + "+++++");

			shopCartDao.removeShopItemByids(shoppingCartIds.toString(), Long
					.parseLong(userId));

			rsp = new TrxResponseData(userId, shoppingCartId);
		}
		return rsp;
	}
	
	/**
	 * 支付成功:正常商品删除购物车
	 * 
	 * @param sourceMap
	 * @return
	 */
	public void delShoppingCartByIdPaySuc(String userId, String goodsId) {

		try {
			this.removeShopCartBySuc(goodsId + "|" + userId);
			//delShoppingCartById(userId, shopCartId);
		} catch (Exception e) {
			logger.debug(e + "+++++goodsId:" + goodsId + "++++++userId:"
					+ userId);
			e.printStackTrace();
		}

	}

	
	/**
	 * 支付成功:秒杀商品删除购物车
	 * 
	 * @param sourceMap
	 * @return
	 */
	public void delShoppingCartByIdPaySucMiaosha(String userId, String miaoshaId) {

		try {
			this.removeShopCartBySucMiaosha(miaoshaId + "|" + userId);
			//delShoppingCartById(userId, shopCartId);
		} catch (Exception e) {
			logger.debug(e + "+++++miaoshaId:" + miaoshaId + "++++++userId:"
					+ userId);
			e.printStackTrace();
		}

	}
	/**
	 * 购物车中检查限购数量 add by wenhua.cheng
	 * 
	 * @param isAvaliable
	 *            是否有效（是否上下架）
	 * @param singleCount
	 *            个人限购数量
	 * @param maxCount
	 *            总量限购数量
	 * @return
	 *          result  
	 *          		allowBuyCount:限购数量
	 *          		allowBuyType:限购数量的类型 0下架1个人限构2总量限购
	 */
	public Map<String, String> checkAllowBuyCount(String isAvaliable, Long singleCount, Long maxCount, Long userId, Long goodsId)
	{
		Map<String, String> result = new HashMap<String,String>();
		Long allowBuyCount = 0L;
		if ("0".equals(isAvaliable))
		{// 若商品已下架，则可购买数量直接置0
			result.put("allowBuyCount", allowBuyCount+"");
			result.put("allowBuyType", "0");
			return result;
		} else
		{// 上架中。总量限购的限购数量一般为10W，加上总量限购是多用户并发（时效性差），故先判断个人限购商品。如果个人限购已超限，直接返回；否则继续判断总量限购。
			Long singleAllowBuyCount = -1L;// 设个处置，总量限购计算时做差异化判断
			if (singleCount > 0)
			{// 如果是个人限购商品

				singleAllowBuyCount = payLimitService.allowPayCount(singleCount, userId, goodsId,0L);// 允许购买的数量（不含购物车里的数量）

				if (singleAllowBuyCount == 0L)
				{// 如果个人限购超限
					result.put("allowBuyCount", allowBuyCount+"");
					result.put("allowBuyType", "1");
					return result;
				}
			}
			// 非个人限购或者个人限购没有超限

			List<Map<String, Object>> salesCountMapList = goodsSoaDao.getGoodsProfileByGoodsid(String.valueOf(goodsId));// 商品已经购买量

			Long salesCount = Long.valueOf(salesCountMapList.get(0).get("salesCount").toString());
			allowBuyCount = maxCount - salesCount > 0 ?  maxCount - salesCount  : 0;// 总量限购如果为负，则归零
			String allowBuyType = "2";
			if (singleAllowBuyCount > 0)
			{// 个人限购没有超限
				//allowBuyCount = allowBuyCount > singleAllowBuyCount ? singleAllowBuyCount : allowBuyCount;// 总量限购和个人限购取最小者
				if(allowBuyCount > singleAllowBuyCount ){
					allowBuyCount = singleAllowBuyCount;
					allowBuyType = "1";
				}

			}
			result.put("allowBuyCount", allowBuyCount+"");
			result.put("allowBuyType", allowBuyType);
			return result;
		}

	}

	@Override
	public int getShopItemCount(Long userId, Long goodsId)
	{
		int shopItemCount = shopCartDao.getShopItemCount(userId, goodsId);
		return shopItemCount;
	}
}
