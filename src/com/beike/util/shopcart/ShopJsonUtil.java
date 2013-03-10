package com.beike.util.shopcart;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

import com.beike.entity.shopcart.ShopCart;
import com.beike.entity.shopcart.ShopItem;

/**
 * 基于Cookie的临时购物车 此工具仅仅负责转换为json 字符串或者把 json 串转换为List<ShopItem>
 * 
 * @author zx.liu
 */

public class ShopJsonUtil {

	public static final int SECONDS_HALF_YEAR = 60 * 60 * 24 * 180;
	public static final int SECONDS_ONE_YEAR = 60 * 60 * 24 * 365;
	public static final String ShopCookieKey = "SHOPCART_COOKIE_KEY";

	/**
	 * @param args
	 */
	private static final String Merchant_ID = "merchantid";
	private static final String Goods_ID = "goodsid";
	private static final String Buy_Count = "buy_count";
	private static final String Add_Time = "addtime";
	private static final String MIAOSHA_ID = "miaoshaid";

	/**
	 * 添加ShopItem 到json, 然后转换为json字符串
	 * 
	 * @param shopItem
	 * @param json
	 * @return
	 */
	public static String addItem(ShopItem shopItem, String json) {

		List<ShopItem> itemList = null;
		if (isNull(json)) {
			itemList = new ArrayList<ShopItem>();
			itemList.add(shopItem);
		} else {
			int itemIndex = queryIndex(shopItem.getGoodsid(), json,shopItem.getMiaoshaid());
			if (itemIndex == -2) { // -2表示查找不到
				itemList = query(json);
				if (itemList == null) {
					return json; // 异常发生
				}
				itemList.add(shopItem);
			} else {
				itemList = query(json);
				if (itemList == null) {
					return json; // 异常发生
				}
				ShopItem tempItem = itemList.get(itemIndex); //根据索引来获取临时的ShopItem
				//modify by qiaowb 2011-11-14 购买数量取shopItem值
				if(tempItem.getBuy_count().longValue() + shopItem.getBuy_count() > 999 ){
					itemList.get(itemIndex).setBuy_count(999L); //商品ID重复则加一,倘若数量超过999L则设为999L个 	
				} else {
					itemList.get(itemIndex).setBuy_count(tempItem.getBuy_count() + shopItem.getBuy_count()); // 商品ID重复则数量加1
				}
				itemList.get(itemIndex).setMiaoshaid(shopItem.getMiaoshaid());
				itemList.get(itemIndex).setAddtime(shopItem.getAddtime()); // 此处需要更改日期为当前值
			}
		}
		return writeJson(itemList);
	}
	
	
	/**
	 * 添加ShopItem 到json, 然后转换为json字符串
	 * 
	 * @param shopItem
	 * @param json
	 * @return
	 */
	public static String addItemNew(ShopItem shopItem, String json) {

		
		List<ShopItem> itemList = null;
		String payLimitCount = "SUCCESS";
		if (isNull(json)) {
			itemList = new ArrayList<ShopItem>();
			itemList.add(shopItem);
		} else {
			int itemIndex = queryIndex(shopItem.getGoodsid(), json,shopItem.getMiaoshaid());
			if (itemIndex == -2) { // -2表示查找不到
				itemList = query(json);
				if (itemList == null) {
					return json; // 异常发生
				}
				itemList.add(shopItem);
			} else {
				itemList = query(json);
				if (itemList == null) {
					return json; // 异常发生
				}
				ShopItem tempItem = itemList.get(itemIndex); //根据索引来获取临时的ShopItem
				//modify by qiaowb 2011-11-14 购买数量取shopItem值
				if(tempItem.getBuy_count().longValue() + shopItem.getBuy_count()<shopItem.getLimitCount()){
				if(tempItem.getBuy_count().longValue() + shopItem.getBuy_count() > 999 ){
					itemList.get(itemIndex).setBuy_count(999L); //商品ID重复则加一,倘若数量超过999L则设为999L个 	
				} else {
					itemList.get(itemIndex).setBuy_count(tempItem.getBuy_count() + shopItem.getBuy_count()); // 商品ID重复则数量加1
				}
				}else{
					if(shopItem.getLimitCount() > 999 ){
						itemList.get(itemIndex).setBuy_count(999L); //商品ID重复则加一,倘若数量超过999L则设为999L个 	
					}else{
						itemList.get(itemIndex).setBuy_count(shopItem.getLimitCount()); // 
					}
					payLimitCount = "limitError";
				}
				itemList.get(itemIndex).setMiaoshaid(shopItem.getMiaoshaid());
				itemList.get(itemIndex).setAddtime(shopItem.getAddtime()); // 此处需要更改日期为当前值
			}
		}
		
		
		return writeJson(itemList)+"|"+payLimitCount;
	}

	/**
	 * 修改购物车条目信息（商品数量）,需要实时地响应
	 * 
	 * @param goodsId
	 * @param buyCount
	 * @param json
	 * @return
	 */
	public static String modify(ShopItem shopItem, String json) {

		int itemIndex = queryIndex(shopItem.getGoodsid(), json,shopItem.getMiaoshaid());
		List<ShopItem> itemList = query(json);
		if (itemList == null) {
			return json;
		}
		if (itemIndex != -1 && itemIndex != -2) {			
			if(shopItem.getBuy_count().longValue() > 999){
				itemList.get(itemIndex).setBuy_count(999L);	// 如果商品数量大于999,则设置商品数量为999L			
			} else {
				itemList.get(itemIndex).setBuy_count(shopItem.getBuy_count()); //修改商品数量为当前最新值
			}
			// itemList.get(itemIndex).setAddtime(shopItem.getAddtime()); //这里没有必要修改更新日期
		}
		return writeJson(itemList);
	}

	/**
	 * 修改商品数量,及时的响应
	 * 
	 * @param goodsId
	 * @param buyCount
	 * @param json
	 * @return
	 */
	public static String modify(Long goodsId, Long buyCount, String json,Long miaoshaId) {

		int itemIndex = queryIndex(goodsId, json,miaoshaId);
		List<ShopItem> itemList = query(json);
		if (itemList == null) {
			return json;
		}
		if (itemIndex != -1 && itemIndex != -2) {
			itemList.get(itemIndex).setBuy_count(buyCount);
		}
		return writeJson(itemList);
	}

	/**
	 * 减少单个商品的数量
	 * 
	 * @param goodsId
	 * @param json
	 * @return
	 */
	public static String reduce(Long goodsId, String json,Long miaoshaId) {

		int itemIndex = queryIndex(goodsId, json,miaoshaId);
		List<ShopItem> itemList = query(json);
		if (itemList == null) {
			return json;
		}
		if (itemIndex != -1 && itemIndex != -2) {
			Long buyCount = itemList.get(itemIndex).getBuy_count();
			if (buyCount <= 1) {
				remove(goodsId, json,miaoshaId);
			} else {
				itemList.get(itemIndex).setBuy_count(buyCount - 1);
			}
		}
		return writeJson(itemList);
	}

	/**
	 * 根据goodsId 来从json中移除一个ShopItem
	 * 
	 * @param goodsId
	 * @param json
	 * @return
	 */
	public static String remove(Long goodsId, String json,Long miaoshaId) {

		int itemIndex = queryIndex(goodsId, json,miaoshaId);
		List<ShopItem> itemList = query(json);
		if (itemList == null) {
			return json;
		}
		if (itemIndex != -1 && itemIndex != -2) {
			itemList.remove(itemIndex);
		}

		return writeJson(itemList);
	}

	/**
	 * 删除全部的ShopItem
	 * 
	 * @param json
	 * @return
	 */
	public static String removeAll(String json) {
		return writeJson(null);
	}

	/**
	 * 添加数据到json,留作备用
	 * 
	 * @param goodsId
	 * @param buyCount
	 * @param json
	 * @return
	 */
	public static String addToJson(Long goodsId, Long buyCount, String json,Long miaoshaId) {

		ShopItem item = new ShopItem();
		item.setGoodsid(goodsId);
		item.setBuy_count(buyCount);
		List<ShopItem> itemList = null;
		if (isNull(json)) {
			itemList = new ArrayList<ShopItem>();
		} else {
			int itemIndex = queryIndex(goodsId, json,miaoshaId);
			if (itemIndex == -2) {
				itemList = query(json);
				if (itemList == null) {
					return json;
				}
			} else {
				return json;
			}
		}
		itemList.add(item);
		return writeJson(itemList);
	}

	/**
	 * 转换list<ShopItem> 为JSONArray 的形式,然后再转为字符串
	 * 
	 * @param itemList
	 * @return
	 */
	public static String writeJson(List<ShopItem> itemList) {

		if (null == itemList || itemList.size() == 0) {
			return null;
		}
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.registerJsonValueProcessor(java.util.Date.class,
				new JsonDateValueProcessor());
		JSONArray array = JSONArray.fromObject(itemList, jsonConfig);
		return array.toString();
	}
	
	/**
	 * 转换list<ShopCart> 为JSONArray 的形式,然后再转为字符串
	 * 
	 * @param itemList
	 * @return
	 */
	public static String writeJsonShopCart(List<ShopCart> itemList) {

		if (null == itemList || itemList.size() == 0) {
			return null;
		}
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.registerJsonValueProcessor(java.util.Date.class,
				new JsonDateValueProcessor());
		JSONArray array = JSONArray.fromObject(itemList, jsonConfig);
		return array.toString();
	}

	/**
	 * 从json 串中获取相应ShopItem 的索引值
	 * 
	 * @param goodsId
	 * @param json
	 * @return
	 */
	public static int queryIndex(Long goodsId, String json,Long miaoshaId) {

		if (null == json || goodsId == null) {
			return -1; // 数据 异常
		}
		JSONArray array = JSONArray.fromObject(json);
		for (int i = 0; i < array.size(); i++) {
			JSONObject object = JSONObject.fromObject(array.get(i));
			long msId = 0;
			if(object.containsKey(MIAOSHA_ID)){
				msId = object.getLong(MIAOSHA_ID);
			}
			if ((object.getLong(Goods_ID) == goodsId)&&(msId == miaoshaId))
				return i; // 正确的索引值
		}
		return -2; // 查找不到
	}

	/**
	 * 转换json 为List<ShopItem> 的集合
	 * 
	 * @param json
	 * @return
	 */
	public static List<ShopItem> query(String json) {

		if (isNull(json)) {
			return null;
		}

		List<ShopItem> itemList = new ArrayList<ShopItem>();
		JSONArray array = JSONArray.fromObject(json);
		for (int i = 0; i < array.size(); i++) {
			JSONObject object = JSONObject.fromObject(array.get(i));
			ShopItem item = new ShopItem();
			item.setMerchantid(object.getLong(Merchant_ID));
			item.setGoodsid(object.getLong(Goods_ID));
			item.setBuy_count(object.getLong(Buy_Count));
			if(object.containsKey(MIAOSHA_ID)){
				item.setMiaoshaid(object.getLong(MIAOSHA_ID));
			}else{
				item.setMiaoshaid(0L);
			}
			try {
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				item.setAddtime(format.parse(object.getString(Add_Time)));
			} catch (Exception e) {
				e.printStackTrace();
			}
			itemList.add(item);
		}
		if ((itemList != null && itemList.size() == 0) || itemList == null) {
			return null;
		}
		return itemList;
	}

	
	/**
	 * 转换json 为包含goodsId,miaoshaId的ShopItem 对象
	 * 
	 * @param json
	 * @return
	 */
	public static ShopItem query(String json,String goodsId,String miaoshaId) {
		if (isNull(json)) {
			return null;
		}
		JSONArray array = JSONArray.fromObject(json);
		ShopItem item = new ShopItem();
		for (int i = 0; i < array.size(); i++) {
			JSONObject object = JSONObject.fromObject(array.get(i));
			item.setMerchantid(object.getLong(Merchant_ID));
			item.setGoodsid(object.getLong(Goods_ID));
			item.setBuy_count(object.getLong(Buy_Count));
			if(object.containsKey(MIAOSHA_ID)){
				item.setMiaoshaid(object.getLong(MIAOSHA_ID));
			}else{
				item.setMiaoshaid(0L);
			}
			if(item.getGoodsid().longValue()==Long.valueOf(goodsId).longValue()&&item.getMiaoshaid().longValue()==Long.valueOf(miaoshaId)){
				return item;
			}
		}
		return item;
	}
	/**
	 * 转义Json, 去掉Json中多余的转义符
	 * 
	 * @param json
	 * @return
	 */
	public static String escape(String json) {

		if (null == json || "".equals(json) || json.trim() == null
				|| json.trim() == "[]") {
			return null;
		}
		return json.replace("\\", "");
	}

	/**
	 * 主要是用来判断Json 字符串是否为空值
	 * 
	 * @param string
	 * @return
	 */
	private static boolean isNull(String string) {

		if (null == string || "".equals(string) || string.trim() == null
				|| string.trim() == "[]") {
			return true;
		}
		return false;
	}

	/**
	 * 格式化Json 中日期值的处理器
	 */
	public static class JsonDateValueProcessor implements JsonValueProcessor {

		private String format = "yyyy-MM-dd HH:mm:ss";

		public JsonDateValueProcessor() {
		}

		public JsonDateValueProcessor(String format) {
			this.format = format;
		}

		public Object processArrayValue(Object value, JsonConfig jsonConfig) {
			return process(value, jsonConfig);
		}

		public Object processObjectValue(String key, Object value,
				JsonConfig jsonConfig) {
			return process(value, jsonConfig);
		}

		private Object process(Object value, JsonConfig jsonConfig) {
			if (value instanceof Date) {
				String str = new SimpleDateFormat(format).format((Date) value);
				return str;
			}
			return value == null ? null : value.toString();
		}

		public String getFormat() {
			return format;
		}

		public void setFormat(String format) {
			this.format = format;
		}
	}

}
