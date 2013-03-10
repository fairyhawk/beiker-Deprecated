package com.beike.biz.service.trx.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.beike.biz.service.trx.OrderFoodService;
import com.beike.common.entity.trx.MenuGoodsOrder;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.TrxOrderException;
import com.beike.dao.diancai.DianCaiDao;
import com.beike.dao.goods.GoodsDao;
import com.beike.dao.trx.MenuGoodsOrderDao;
import com.beike.dao.trx.soa.proxy.GoodsSoaDao;
import com.beike.entity.goods.Goods;
import com.beike.entity.onlineorder.DiscoutType;
import com.beike.entity.onlineorder.OrderMenu;
import com.beike.util.Amount;
import com.beike.util.DateUtils;
/**
 * @desc 点餐服务类
 * @author ljp
 * @date 20121128
 *
 */
@Repository("orderFoodService")
public class OrderFoodServiceImpl implements OrderFoodService{
	
	@Autowired
	private GoodsDao goodsDao;
	@Autowired
	private GoodsSoaDao goodsSoaDao;
	@Autowired
	private DianCaiDao dianCaiDao;
	@Autowired
	private MenuGoodsOrderDao menuGoodsOrderDao;
	@Override
	public Goods queryGoodsByBuyFoodInfo(List<Map<String, Integer>> buyFoodInfo, String orderId,String guestId) throws Exception{
		Goods goods = goodsDao.getGoodsDaoById(Long.valueOf(orderId));
		if(goods == null){
			throw new TrxOrderException(BaseException.MENU_ILLEALL_GOODID);
		}
		//获取活动信息
		Map<String, Object> onlineOrderMap = dianCaiDao.getGuestIdByOrderId(Long.parseLong(orderId));
		if(onlineOrderMap == null || onlineOrderMap.size() == 0){
			throw new TrxOrderException(BaseException.MENU_ILLEALL_GOODID);
		}
		//计算订单的原价
		double sourcePrice = calculateSourcePrice(buyFoodInfo, orderId);
		goods.setSourcePrice(sourcePrice);
		//计算订单的支付价...打折后的价格
		double payPrice = calculatePayPrice(orderId,sourcePrice,onlineOrderMap);
		goods.setPayPrice(payPrice);
		
		goods.setRebatePrice(0.00);
		//设置分成价格 不四舍五入
		goods.setDividePrice(Amount.divRondHalfDown(Amount.mul(sourcePrice, Double.parseDouble(onlineOrderMap.get("settle_discount")+"")),10,2));//Amount.div(payPrice, Double.parseDouble(onlineOrderMap.get("settle_discount")+""))
		String merchantname = queryMerchantNameByMerchantId(guestId);
		goods.setMerchantname(merchantname);
		goods.setMerchantid(guestId);
		return goods;
	}
	
	@Override
	public String findGuestIdByOrderId(String orderId) throws Exception {
		Map<String, Object> map = dianCaiDao.getOrderGuestMapByOrderId(Long.parseLong(orderId));
		if(map != null && map.size() > 0){
			return map.get("guest_id")+"";
		}
		return null;
	}
	
	
	@Override
	public String  queryMerchantNameByMerchantId(String merchantId)throws Exception{
		Map<String,Object> map1 = goodsSoaDao.getMerchantById(Long.parseLong(merchantId));
		String parentId =  map1.get("parentId")+"";
		if(!"0".equals(parentId)){
			map1 = goodsSoaDao.getMerchantById(Long.parseLong(parentId));
		}
		String merchantname = (String)map1.get("merchantName");
		return merchantname;
	}
	
	

	@SuppressWarnings("unchecked")
	@Override
	public Map queryOrderMenuByIds(List<Long> menuIds)throws Exception{
		List<Map<String, Object>> category = dianCaiDao.getCategoryByMenuIds(menuIds);
		List<String> categoryString = new ArrayList<String>();
		for(Map<String, Object> map : category){
			categoryString.add(map.get("category")+"");
		}
		List<OrderMenu> orderMenus =  dianCaiDao.getOrderMenusByMenuId(menuIds);
		Map map = new HashMap();
		map.put("category", categoryString);
		map.put("orderMenus", orderMenus);
		return map;
	}
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void createMenuGoodsOrder(Map<String, String> rspMap)
			throws Exception {
		
		String menuInfo = rspMap.get("menuInfo");
		String menuInfoArray[] = menuInfo.split("\\|");
		
		List<Map<String, Integer>> buyFoodInfo = (List<Map<String, Integer>>)new JSONParser().parse(menuInfoArray[2]);
		List<Long> menuIds = new ArrayList<Long>();
		Map<Long,Long> idCount = new HashMap<Long, Long>();
		for(Map<String, Integer> map : buyFoodInfo){
			Set<Entry<String, Integer>> enties = map.entrySet();
			for(Entry<String, Integer> e:enties){
				menuIds.add(Long.parseLong(e.getKey()+""));
				idCount.put(Long.parseLong(e.getKey()+""),Long.parseLong(e.getValue()+""));
			}
		}
		List<OrderMenu> orderMenus =dianCaiDao.getOrderMenusByMenuId(menuIds);
		for(OrderMenu om : orderMenus){
			MenuGoodsOrder menuGoodsOrder = new MenuGoodsOrder();
			menuGoodsOrder.setCreateDate(new Timestamp( new Date().getTime()));
			menuGoodsOrder.setTrxOrderGoodsId(Long.parseLong(menuInfoArray[1])	);
			menuGoodsOrder.setTrxOrderId(Long.parseLong(menuInfoArray[0]));
			menuGoodsOrder.setDescription("");
			menuGoodsOrder.setMenuCategory(om.getMenuCategory());
			menuGoodsOrder.setMenuCount(idCount.get(om.getMenuId()));
			menuGoodsOrder.setMenuExplain(om.getMenuExplain());
			menuGoodsOrder.setMenuId(om.getMenuId());
			menuGoodsOrder.setMenuLogo(om.getMenuLogo());
			menuGoodsOrder.setMenuName(om.getMenuName());
			menuGoodsOrder.setMenuPrice(new BigDecimal(om.getMenuPrice()));
			menuGoodsOrder.setMenuSort(om.getMenuSort());
			menuGoodsOrder.setMenuUnit(om.getMenuUnit());
			menuGoodsOrder.setOrderId(om.getOrderId());
			menuGoodsOrder.setVersion(1L);
			menuGoodsOrderDao.addMenuGoodsOrder(menuGoodsOrder);
		}
	}

	/**
	 * @desc 计算菜单打折后的价格
	 * @param orderId 活动Id
	 * @param payPrice 打折前的总价
	 * @param onlineOrderMap 活动id 对应的onlineOrder表
	 * @return
	 */
	private  double calculatePayPrice(String orderId, double sourcePrice , Map<String, Object> onlineOrderMap){
		Date beginDate = new Date( ((Timestamp)onlineOrderMap.get("order_start_time")).getTime());
		Date endDate = new Date( ((Timestamp)onlineOrderMap.get("order_end_time")).getTime());
		String auditStatus = onlineOrderMap.get("audit_status")+"";
		//如果当前时间在活动开始和结束时间或者 活动是下线状态   不打折
		if(!DateUtils.betweenBeginAndEnd(new Date(), beginDate, endDate) || "OFFLINE".equals(auditStatus)){
			return sourcePrice;
		}
		String engine = (String)onlineOrderMap.get("discount_engine");
		// 如果活动对应的打折引擎为空则不打折
		if(engine == null || "".equals(engine)){
			return sourcePrice;
		}
		Map<String, Object> engines = dianCaiDao.getDiscountEngine(Long.parseLong(orderId), engine).get(0);
		//如果在查询此活动对应的打折信息时没有查询到则不打折
		if(engines == null || engines.size() == 0){
			return sourcePrice;
		}
		double payPrice = 0.00;//打完折后的价格
		if(DiscoutType.FULLLESS.toString().equals(engine)){
			
			//打折前的价格-打折前的价格/fullAmount*lessAmount
			payPrice = Amount.sub(sourcePrice, Amount.mul(new BigDecimal(Amount.div(sourcePrice, Double.valueOf(engines.get("full_amount")+""))).setScale(0, BigDecimal.ROUND_DOWN).doubleValue(),Double.valueOf(engines.get("less_amount")+"")));
		}else if(DiscoutType.OVERALLFOLD.toString().equals(engine)){
			//打折前的价格*折扣
			payPrice = Amount.div(Amount.mul(sourcePrice, Double.valueOf(engines.get("discount")+"")), 10, 2);
			//payPrice = payPriceBD.multiply( new BigDecimal(Double.valueOf(engines.get("discount")+""))).divide(new ).doubleValue();
		}else if(DiscoutType.INTERVALLESS.toString().equals(engine)){
			List<Map<String, Object>> interval = dianCaiDao.getEngineIntervallessByPrice(sourcePrice,Long.parseLong(orderId));
			if(interval == null || interval.size() == 0){
				payPrice = sourcePrice;
			}else{
				//打折前的价格 - 减额
				payPrice = Amount.sub(sourcePrice, Double.parseDouble(interval.get(0).get("less_amount")+"")) ;
			}
		}
		//返回折后的价格
		return payPrice;
	}
	
	/**
	 *  计算菜单打折前总价
	 * @param buyFoodsInfo
	 * @param orderId
	 * @return
	 * @throws Exception
	 */
	private double calculateSourcePrice(List<Map<String, Integer>> buyFoodsInfo, String orderId)throws Exception{
		double sourcePrice = 0.00;
		Map<String, Integer> menuIdAndCount = new HashMap<String, Integer>();
		//客户购买的所有商品ID
		List<Long> menuIds = new ArrayList<Long>();
		
		for(Map<String,Integer> map : buyFoodsInfo){
			menuIds.add(Long.parseLong(map.get("menuId")+""));
			menuIdAndCount.put(map.get("menuId")+"", Integer.parseInt(map.get("menuCount")+""));
		}
		//查询商品对应的价格
		List<Map<String, Object>> idAndPrice = dianCaiDao.getOrderMenuByMenuId(menuIds) ;
		if(idAndPrice == null || idAndPrice.size() == 0){
			throw new TrxOrderException(BaseException.MENU_ILLEALL_DATA);
		}
		for (int i = 0; i < idAndPrice.size(); i++) {
			//增加校验页面传过来的活动编号和menu在数据库的活动编号是否一致
			if(!orderId.equals(idAndPrice.get(i).get("order_id").toString())){
				throw new TrxOrderException(BaseException.MENU_ILLEGALL_ORDERID);
			}
			Map<String, Object> itemMap = idAndPrice.get(i);
			Long menuid = new Long(itemMap.get("menu_id").toString());
			Double price = new Double(itemMap.get("menu_price").toString());
			double count = Double.parseDouble(menuIdAndCount.get(menuid+"")+"");
			//对数量加判断保证下单成功并不会出现负数...
			if( count < 1 ){
				count = 1;
			}
			if(count > 100){
				count = 100;
			}
			sourcePrice = Amount.add(sourcePrice, Amount.mul(price, count));
		}
		return sourcePrice;
	}

	@Override
	public boolean checkInputParameter(String orderId, String guestId)
			throws Exception {
		try {
			List<Map<String, Object>> list = menuGoodsOrderDao.queryOrderGuestMapByOrderIdAndGuestId(Long.parseLong(orderId), Long.parseLong(guestId));
			if( list == null || list.size() == 0 ){
				return false;
			}else{
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	
	
}
