package com.beike.service.impl.diancai;

import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hamcrest.core.IsInstanceOf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.dao.diancai.DianCaiDao;
import com.beike.dao.merchant.BranchProfileDao;
import com.beike.entity.catlog.RegionCatlog;
import com.beike.entity.goods.Goods;
import com.beike.entity.merchant.BranchProfile;
import com.beike.entity.merchant.Merchant;
import com.beike.entity.onlineorder.AbstractEngine;
import com.beike.entity.onlineorder.BookingGoods;
import com.beike.entity.onlineorder.BranchInfo;
import com.beike.entity.onlineorder.DiscoutType;
import com.beike.entity.onlineorder.EngineFullLess;
import com.beike.entity.onlineorder.EngineIntervalLess;
import com.beike.entity.onlineorder.EngineOverAllFold;
import com.beike.entity.onlineorder.Interval;
import com.beike.entity.onlineorder.OrderMenu;
import com.beike.page.Pager;
import com.beike.service.diancai.DianCaiService;
import com.beike.service.goods.GoodsService;
import com.beike.util.BeanUtil;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.json.JSONArray;
import com.beike.util.json.JSONObject;

/**
 * com.beike.service.impl.diancai.DianCaiServiceImpl.java
 * 
 * @description:点菜Service实现
 * @Author:xuxiaoxian Copyright:Copyright (c) 2012
 * @Compony:Sinobo
 * @date: 2012-11-7
 */
@Service("dianCaiService")
public class DianCaiServiceImpl implements DianCaiService {

	@Autowired
	private BranchProfileDao branchProfileDao;

	@Autowired
	private GoodsService goodsService;

	private MemCacheService memCacheService = MemCacheServiceImpl.getInstance();

	@Override
	public BranchInfo getBranchInfo(Long branchid) {
		Map<String, Object> branchinfo_map = dianCaiDao.getBranchInfo(branchid)
				.get(0);
		BranchInfo branch = new BranchInfo();
		branch.setId(new Long(branchinfo_map.get("brandid").toString()));
		branch.setAddr(branchinfo_map.get("addr").toString());
		branch.setLatitude(branchinfo_map.get("latitude").toString());
		branch.setMerchantname(branchinfo_map.get("brandname").toString());
		branch.setBranchname(branchinfo_map.get("branchname").toString());
		branch.setLogo(branchinfo_map.get("logo").toString());
		branch.setBusinesstime(branchinfo_map.get("buinesstime").toString());
		branch.setTel(branchinfo_map.get("tel").toString());
		branch.setBranchid(new Long(branchinfo_map.get("branchid").toString()));
		List<BranchProfile> branchProfile = branchProfileDao
				.getBranchProfileById(branchid.toString());
		if (branchProfile != null && branchProfile.size() > 0) {
			branch.setReviewRate(branchProfile.get(0).calculateScore()
					.getPartWellRate());
		}
		return branch;
	}

	static final String TOP_GOODS_KEY = "diancan_top_goods_key_";

	@Override
	public BookingGoods getTopone(Long branchid) {
		BookingGoods bookingGoods = (BookingGoods) memCacheService
				.get(TOP_GOODS_KEY + branchid);
		if (bookingGoods == null) {
			List<Long> sell_goodsids = dianCaiDao.goodsOfBranch(branchid);
			if(sell_goodsids != null && sell_goodsids.size() > 0){
				List<Map<String, Object>> goodsid_list = dianCaiDao
						.getTopone(sell_goodsids);
				if (goodsid_list != null && goodsid_list.size() > 0) {
					Goods g = goodsService.findById(new Long(goodsid_list.get(0).get("goodsid").toString()));
					bookingGoods = new BookingGoods();
					bookingGoods.setCurrentPrice(g.getCurrentPrice());
					bookingGoods.setSourcePrice(g.getSourcePrice());
					bookingGoods.setLogourl(g.getLogo1());
					bookingGoods.setGoodsid(g.getGoodsId());
					Long sold = dianCaiDao.getGoodsSold(g.getGoodsId());
					bookingGoods.setSold(sold);
					List<String> regionexts = dianCaiDao.getGoodsRegionext(g
							.getGoodsId());
					bookingGoods.setRegionexts(regionexts);
					bookingGoods.setGoodsname(g.getGoodsname());
					bookingGoods.setTitle(g.getGoodsTitle());
					memCacheService.set(TOP_GOODS_KEY, bookingGoods);
				}
			}
		}
		return bookingGoods;
	}

	@Override
	public AbstractEngine getPromotionInfo(Long branchid) {
		try {
			List<Map<String, Object>> promotion_list = dianCaiDao
					.getPromotion(branchid);
			if(promotion_list != null && promotion_list.size() == 1){
				Map<String, Object> promotion_map = promotion_list.get(0);

				return abstractEngineFactory(promotion_map);
			}
			
			return null;
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("获取分店订餐优惠信息异常,非法id getPromotionInfo");
		}
	}


	@Autowired
	private DianCaiDao dianCaiDao;

	/**
	 * 
	 * @Title: getOrderByMerId
	 * @Description: 获取品牌支持点菜菜的分店
	 * @param @param merId:品牌标识
	 * @return 支持点菜分店集合
	 * @throws ：sql异常
	 */
	public List<Object> getOrderByMerId(Long merId) throws Exception {
		// 获取品牌下支持点菜
		List<Map<String, Object>> orders = dianCaiDao
				.getAllDianCaiByMerId(merId);

		return BeanUtil.convertResultToObjectList(orders, Merchant.class);

	}

	// #####################getter and setter##########

	public void setDianCaiDao(DianCaiDao dianCaiDao) {
		this.dianCaiDao = dianCaiDao;
	}

	@Override
	public List<String> getCategory(Long branchid) {
		List<Map<String,Object>> promotion = dianCaiDao.getPromotion(branchid);
		return dianCaiDao.getMenuCat(new Long(promotion.get(0).get("order_id").toString()));
	}

	private AbstractEngine abstractEngineFactory(
			Map<String, Object> promotion_map) {
		Long order_id = new Long(promotion_map.get("order_id").toString());
		String type = promotion_map.get("discount_engine").toString();
		String order_explain = promotion_map.get("order_explain").toString();
		Timestamp order_start_time = (Timestamp) promotion_map
				.get("order_start_time");
		Timestamp order_end_time = (Timestamp) promotion_map
				.get("order_end_time");
		
		String audit_status=(String) promotion_map.get("audit_status");
		boolean isOnline=false;
		if("ONLINE".equals(audit_status)){
			isOnline=true;
		}
		if (DiscoutType.FULLLESS.toString().equals(type)) {
			List<Map<String, Object>> promotion_details_list = dianCaiDao
					.getDiscountEngine(order_id, type);
			EngineFullLess discountEngine = new EngineFullLess();
			discountEngine.setOrderId(order_id);
			discountEngine.setEndtime(order_end_time);
			discountEngine.setStarttime(order_start_time);
			discountEngine.setTip(order_explain);
			Map<String, Object> promotion_details_map = promotion_details_list
					.get(0);
			Long engine_id = new Long(promotion_details_map.get("engine_id")
					.toString());
			discountEngine.setEngineId(engine_id);
			double full_amount = new Double(promotion_details_map.get(
					"full_amount").toString());
			double less_amount = new Double(promotion_details_map.get(
					"less_amount").toString());
			discountEngine.setFullAmount(full_amount);
			discountEngine.setLessAmount(less_amount);
			//补充 打折 信息跟活动走 活动下线 打折信息不展示 update by tianye
			discountEngine.setOnline(isOnline);
			
			return discountEngine;
		} else if (DiscoutType.OVERALLFOLD.toString().equals(type)) {
			List<Map<String, Object>> promotion_details_list = dianCaiDao
					.getDiscountEngine(order_id, type);
			EngineOverAllFold discountEngine = new EngineOverAllFold();
			discountEngine.setOrderId(order_id);
			discountEngine.setEndtime(order_end_time);
			discountEngine.setStarttime(order_start_time);
			discountEngine.setTip(order_explain);
			Map<String, Object> promotion_details_map = promotion_details_list
					.get(0);
			Long engine_id = new Long(promotion_details_map.get("engine_id")
					.toString());
			discountEngine.setEngineId(engine_id);
			double discount = new Double(promotion_details_map.get("discount")
					.toString());
			discountEngine.setDiscount(discount);
			
			discountEngine.setOnline(isOnline);
			return discountEngine;
		} else if (DiscoutType.INTERVALLESS.toString().equals(type)) {
			List<Map<String, Object>> promotion_details_list = dianCaiDao
					.getDiscountEngine(order_id, type);
			EngineIntervalLess discountEngine = new EngineIntervalLess();
			discountEngine.setOrderId(order_id);
			discountEngine.setEndtime(order_end_time);
			discountEngine.setStarttime(order_start_time);
			discountEngine.setTip(order_explain);
			discountEngine.setOrderId(order_id);
			List<Interval> intervals = new ArrayList<Interval>();
			for (int i = 0; i < promotion_details_list.size(); i++) {
				Map<String, Object> promotion_detail = promotion_details_list
						.get(i);
				Interval interval = new Interval();
				double interval_amount = new Double(promotion_detail.get(
						"interval_amount").toString());
				interval.setInterval_amount(interval_amount);
				double less_amount = new Double(promotion_detail.get(
						"less_amount").toString());
				interval.setLess_amount(less_amount);
				intervals.add(interval);
			}
			discountEngine.setOnline(isOnline);
			discountEngine.setIntervals(intervals);
			return discountEngine;
		}

		return null;
	}

	@Override
	public double caculateAmount(Long branchid, List<OrderMenu> items) {

		List<Long> menuids = new ArrayList<Long>();
		Map<Long, OrderMenu> items_cache = new HashMap<Long, OrderMenu>();
		for (int i = 0; i < items.size(); i++) {
			menuids.add(items.get(i).getMenuId());
			items_cache.put(items.get(i).getMenuId(), items.get(i));
		}

		List<Map<String, Object>> items_map_list = dianCaiDao
				.getOrderMenu(menuids,branchid);
		double amount = 0;
		for (int i = 0; i < items_map_list.size(); i++) {
			Map<String, Object> item_map = items_map_list.get(i);
			Long menuid = new Long(item_map.get("menu_id").toString());
			Double price = new Double(item_map.get("price").toString());
			OrderMenu om = items_cache.get(menuid);
			amount = amount + price * om.getCount();
		}

		AbstractEngine engine = getPromotionInfo(branchid);
        if(engine != null){
        	return engine.caculatePay(amount);
        }else{
        	return amount;
        }
		
	}



	@Override
	public Map<String,List<OrderMenu>> getPaidOrderMenuByTrxGoodsid(String trx_goods_id) {
		List<Map<String,Object>> paidOrder = dianCaiDao.getPaidOrderMenu(trx_goods_id);
		
		
		
		Map<String,List<OrderMenu>> results = new HashMap<String, List<OrderMenu>>();
		for(int i=0;i<paidOrder.size();i++){
			Map<String,Object> menu_map = paidOrder.get(i);
			OrderMenu om = new OrderMenu();
			om.setMenuCategory(menu_map.get("menu_category").toString());
			om.setMenuName(menu_map.get("memu_name").toString());
			om.setCount(new Integer(menu_map.get("menu_count").toString()));
			om.setMenuPrice(new Double(menu_map.get("menu_price").toString()));
			om.setMenuUnit(menu_map.get("menu_unit").toString());
			om.setMenuId(new Long(menu_map.get("menu_id").toString()));
			if (results.get(om.getMenuCategory()) != null) {
				results.get(om.getMenuCategory()).add(om);
			} else {
				List<OrderMenu> ordermenus = new ArrayList<OrderMenu>();
				ordermenus.add(om);
				results.put(om.getMenuCategory(), ordermenus);
			}
		}
		return results;
	}

	
	
	
	@Override
	public Map<String, List<OrderMenu>> gethistoryOrderMenus(JSONArray historyjson,Long selectBranchid) {
		Map<String,List<OrderMenu>> results = new HashMap<String, List<OrderMenu>>();
		try {
			List<Long> menu_ids = new ArrayList<Long>();
			Map<Long,OrderMenu> cache_orderMenu = new HashMap<Long, OrderMenu>();
			/**
			 *  {history:
			 *  [
			 *  {branchid:xx,
			 *  items:[{menuid:98,count:2,index:xxx,tag:xxx},
			 *  {menuid:93,count:2}]},
			 *  {branchid:xx,items:[{menuid:98,count:2},{menuid:93,count:2}]}
			 *  ]
			 *  };
			 */
			for(int i=0;i<historyjson.length();i++){
				JSONObject branches_jo =  historyjson.getJSONObject(i);
				JSONArray branches = branches_jo.getJSONArray("items");
		        long cookieid = branches_jo.getLong("branchid");
				for(int j=0;j<branches.length();j++){
					JSONObject branch = branches.getJSONObject(j);
					//当前分店已选未支付菜单
					if(selectBranchid == cookieid){
						menu_ids.add(branch.getLong("menuid"));
						OrderMenu orderMenu = new OrderMenu();
						orderMenu.setMenuId(branch.getLong("menuid"));
						orderMenu.setCount(branch.getInt("count"));
						orderMenu.setIndex(branch.getString("index"));
						cache_orderMenu.put(orderMenu.getMenuId(), orderMenu);
					}
				}
				
			}
			if(menu_ids != null && menu_ids.size() > 0){
				List<Map<String,Object>> menu_map_list = dianCaiDao.getOrderMenu(menu_ids,selectBranchid);
				for(int i=0;i<menu_map_list.size();i++){
					Map<String,Object> menu_map = menu_map_list.get(i);
					OrderMenu om = cache_orderMenu.get(new Long(menu_map.get("menu_id").toString()));
					om.setMenuCategory(menu_map.get("menu_category").toString());
					om.setMenuName(menu_map.get("menu_name").toString());
					
					
					
					Double d=new Double(menu_map.get("price")
							.toString());
					DecimalFormat formater = new DecimalFormat(); 
					if(d!=null&&d.doubleValue()>1000){
						formater.setMaximumFractionDigits(1); 
					}else{
						formater.setMaximumFractionDigits(2);
					}
					formater.setGroupingSize(0); 
					formater.setRoundingMode(RoundingMode.FLOOR);
					om.setMenuPrice(new Double(formater.format(d.doubleValue())));
					om.setMenuUnit(menu_map.get("menu_unit").toString());
					if (results.get(om.getMenuCategory()) != null) {
						results.get(om.getMenuCategory()).add(om);
					} else {
						List<OrderMenu> ordermenus = new ArrayList<OrderMenu>();
						ordermenus.add(om);
						results.put(om.getMenuCategory(), ordermenus);
					}
				}
			}
		
		} catch (Exception e) {
			//忽略非法菜单id(menuid)
			e.printStackTrace();
			logger.info("忽略该异常,非法的菜单id,无法查询菜单信息");
			return results;
		} 
		return results;
	}

	static final Log logger = LogFactory.getLog(DianCaiServiceImpl.class);
	@Override
	public List<List<OrderMenu>> getOrderMenuListByBranchid(Long branchid,
			List<String> tags) {
		List<Map<String, Object>> promotion_list = dianCaiDao
				.getPromotion(branchid);
		Map<String, Object> promotion_map = promotion_list.get(0);
		Long order_id = new Long(promotion_map.get("order_id").toString());
		return getOrderMenuListByID(order_id, tags);
	}

	@Override
	public List<List<OrderMenu>> getOrderMenuListByID(Long order_id,
			List<String> tags) {
		if (tags == null || tags.size() == 0) {
			tags = dianCaiDao.getMenuCat(order_id);
		}
		// 首页3个tag
		List<String> hometags = null;
		if(tags != null && tags.size() != 0 && tags.size() >= 3){
			 hometags = tags.subList(0, 3);
		}else if(tags != null && tags.size() > 0 && tags.size() < 3){
			hometags = tags.subList(0, tags.size());
		}else{
			throw new RuntimeException("分店就没有订餐信息,你进来干吗? com.beike.service.impl.diancai.DianCaiServiceImpl.getOrderMenuByID(Long, List<String>, int)");
		}
		List<List<OrderMenu>> results = new ArrayList<List<OrderMenu>>();
		for(int i=0;i<hometags.size();i++){
			List<String> current_tag = new ArrayList<String>();
			current_tag.add(hometags.get(i));
			List<Map<String, Object>> ordermenu_list = dianCaiDao.getOrderMenu(
					order_id, current_tag);
			List<OrderMenu> menus = new ArrayList<OrderMenu>();
			for (int j = 0; j < ordermenu_list.size(); j++) {
				Map<String, Object> ordermen_map = ordermenu_list.get(j);
				OrderMenu menu = new OrderMenu();
				menu.setMenuCategory(ordermen_map.get("menu_category").toString());
				menu.setMenuExplain(ordermen_map.get("menu_explain").toString());
				menu.setMenuId(new Long(ordermen_map.get("menu_id").toString()));
				menu.setMenuLogo(ordermen_map.get("menu_logo").toString());
				menu.setMenuName(ordermen_map.get("menu_name").toString() + " / " + ordermen_map.get("menu_unit").toString());
				
				Double d=new Double(ordermen_map.get("menu_price")
						.toString());
				DecimalFormat formater = new DecimalFormat(); 
				if(d!=null&&d.doubleValue()>1000){
					formater.setMaximumFractionDigits(1); 
				}else{
					formater.setMaximumFractionDigits(2);
				}
				formater.setGroupingSize(0); 
				formater.setRoundingMode(RoundingMode.FLOOR);
				menu.setMenuPrice(new Double(formater.format(d.doubleValue())));
				menu.setMenuSort(new Integer(ordermen_map.get("menu_sort")
						.toString()));
				menu.setMenuUnit(ordermen_map.get("menu_unit").toString());
				menu.setOrderId(new Long(ordermen_map.get("order_id").toString()));
				menus.add(menu);
			}
			results.add(menus);
		}
		
		return results;
	}

	
	@Override
	public List<BranchInfo> getHistroyBranchesInfo(List<Long> branchids) {
		List<Map<String,Object>> branches_list = dianCaiDao.getHistoryBranches(branchids);
		List<BranchInfo> results = new ArrayList<BranchInfo>();
		for(int i=0;i<branches_list.size();i++){
			Map<String,Object>	branch_map = branches_list.get(i);
			BranchInfo bi = new BranchInfo();
			bi.setBranchid(new Long(branch_map.get("branchid").toString()));
			bi.setBranchname(branch_map.get("branchname").toString());
			results.add(bi);
		}
		return results;
	}

	@Override
	public Map<Long, List<RegionCatlog>> getSupportOrderRegion(Long cityId,String nowStr) {
		
		return dianCaiDao.getSupportOrderRegion(cityId,nowStr);
	}
	@Override
	public Integer getCountListOfOrders(Map<String, String> paramMap) {
		// TODO Auto-generated method stub
		List<Map<String,Object>> merchanitIdList = dianCaiDao.getMerchanitIdListOfOrders(paramMap);
		if(merchanitIdList.size()>0){
			String merchantIdsStr = getColumnFromArray(merchanitIdList,"merchantid");
			Integer count = dianCaiDao.getCountOnlineOfOrder(merchantIdsStr,paramMap.get("nowStr"));
			return count;
		}else{
			return 0;
		}
		
//		return merchanitIdList.size();
	}
	/**
	 * 
	* @Title: listOfOrders
	* @Description: 可点餐的分店列表
	* @param @param paramMap    设定文件
	* @return void    返回类型
	* @throws
	 */
	@Override
	public List<Map<String, Object>> listOfOrders(Map<String, String> paramMap,Pager pager) {
		//可点餐的分店
		List<Map<String, Object>> merchantList = dianCaiDao.listOfMerchantOfOrders(paramMap);
		String merchantIdsStr = getColumnFromArray(merchantList,"merchantid");
		List<Map<String, Object>> merchantListOfOnline = dianCaiDao.getListOfMerchantOfOrdersOnline(merchantIdsStr,paramMap.get("nowStr"),pager);//有在线活动的可点餐分店
		//可点餐分店合并在线活动
		for(Map<String,Object> map:merchantListOfOnline){
			for(Map<String,Object> merchantMap:merchantList){
				if(merchantMap.get("merchantid").equals(map.get("branchid"))){
					map.putAll(merchantMap);//合并
				}
			}
		}
		//组织分店活动数据
		/*Map<String,OrderMenu> orderMenuCache = new HashMap<String, OrderMenu>();
		List<Map<String, Object>> removeList = new ArrayList<Map<String, Object>>();*/
		StringBuilder orderIds = new StringBuilder();
		for(Map<String,Object> map : merchantListOfOnline){
			//优惠信息
			AbstractEngine promotionInfo = abstractEngineFactory(map);
			if(promotionInfo != null){
			    String discountInfo = "";
			    String picpromotion = "";
			    if(promotionInfo instanceof EngineOverAllFold){
			    	//全场折扣
			    	EngineOverAllFold eoa = (EngineOverAllFold)promotionInfo;
			    	discountInfo = "全单"+displayDiscount(eoa.getDiscount())+"折";
			    	picpromotion = displayDiscount(eoa.getDiscount())+"折";
			    	
			    }else if(promotionInfo instanceof EngineIntervalLess){
			    	//区间减
			    	EngineIntervalLess ei = (EngineIntervalLess)promotionInfo;
			    	discountInfo = "区间减";
			    	picpromotion = "";
			    }else if(promotionInfo instanceof EngineFullLess){
			    	//满额减
			    	EngineFullLess ef = (EngineFullLess)promotionInfo;
			    	discountInfo = "满额减";
			    	picpromotion = "";
			    }
			    /*else{
			    	removeList.add(map);
			    	continue;
			    }*/
			    map.put("promotion",discountInfo);//在列表中显示的折扣样子
		    	map.put("picpromotion",StringUtils.isNotBlank(picpromotion) ? picpromotion : null);//在大图中显示的折扣样子
		    	map.put("discountjson", promotionInfo.formatJson());
			}
			//随机的某一分类的两道菜
			/*String orderId = map.get("order_id").toString();
			OrderMenu dish = orderMenuCache.get(orderId);
			if(null == dish){
				dish = dianCaiDao.getRandDishByOrderId(Long.valueOf(orderId));
				orderMenuCache.put(orderId, dish);
			}*/
			//存储orderid,以便后序批量取出数据
			orderIds.append(map.get("order_id")).append(",");
			//组织数据
			map.put("pi", promotionInfo);
		}
		//每个分店三道菜
		List<Map<String, Object>> orderMenuList = dianCaiDao.getRandDishByOrderId(orderIds.substring(0, orderIds.lastIndexOf(",")));
		for(Map<String,Object> merchantMap : merchantListOfOnline){
			for(Map<String,Object> orderMenuMap :orderMenuList){
				if(merchantMap.get("order_id").equals(orderMenuMap.get("order_id"))){
					merchantMap.putAll(orderMenuMap);
				}
			}
		}
		//删除不合适的活动记录
//		merchantListOfOnline.removeAll(removeList);
		return merchantListOfOnline;
	}
	/**
	 * 
	* @Title: getColumnFromArray
	* @Description: 取Map集合中的某一列数据
	* @param @param List
	* @param @param columnName
	* @param @return    设定文件
	* @return String    返回类型
	* @throws
	 */
	public String getColumnFromArray(List<Map<String, Object>> List,String columnName){
		StringBuilder sb = new StringBuilder();
		for (Map<String,Object> map : List) {
			sb.append(map.get(columnName)).append(",");
		}
		return sb.substring(0, sb.length()-1);
	}
	//整数折扣
	public static Map<Double,String> discountMap = new HashMap<Double, String>();
	static {
		discountMap.put(1.0, "1");
		discountMap.put(2.0, "2");
		discountMap.put(3.0, "3");
		discountMap.put(4.0, "4");
		discountMap.put(5.0, "5");
		discountMap.put(6.0, "6");
		discountMap.put(7.0, "7");
		discountMap.put(8.0, "8");
		discountMap.put(9.0, "9");
	}
	public String displayDiscount(double discount){
		if(discountMap.get(discount)==null){
			return discount+"";
		}else{
			return discountMap.get(discount);
		}
	}
	/**
	 * 从可点菜的分店中取菜
	 */
	@Override
	public List<OrderMenu> getBranchMenuList(String branchids, Integer limitNum) {
		// TODO Auto-generated method stub
		return dianCaiDao.getBranchMenuList(branchids,limitNum);
	}
}
