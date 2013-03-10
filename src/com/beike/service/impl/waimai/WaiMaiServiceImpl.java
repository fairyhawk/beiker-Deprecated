package com.beike.service.impl.waimai;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.dao.waimai.WaiMaiDao;
import com.beike.entity.takeaway.TakeAway;
import com.beike.entity.takeaway.TakeAwayMenu;
import com.beike.form.TakeAwayDetailForm;
import com.beike.service.waimai.WaiMaiService;
import com.beike.util.BeanUtil;

/**
 * com.beike.service.impl.waimai.WaiMaiServiceImpl.java
 * @description:外卖Service实现
 * @Author:xuxiaoxian Copyright:Copyright (c) 2012
 * @Compony:Sinobo
 * @date: 2012-11-7
 */
@Service("waiMaiService")
public class WaiMaiServiceImpl implements WaiMaiService {
	@Autowired
	private WaiMaiDao waiMaiDao;

	/**
	 * @Title: getTakeOutByMerId
	 * @Description: 获取品牌支持外卖的分店
	 * @param @param merId:品牌标识
	 * @return 支持外卖分店集合
	 * @throws ：sql异常
	 */
	public List<Object> getTakeOutByMerId(Long merId) throws Exception {
		//获取品牌下外卖点菜
		List<Map<String, Object>> tkouts = waiMaiDao.getAllTakeOutByMerId(merId);

		return BeanUtil.convertResultToObjectList(tkouts, TakeAway.class);

	}

	//################getter and setter############
	public void setWaiMaiDao(WaiMaiDao waiMaiDao) {
		this.waiMaiDao = waiMaiDao;
	}

	@Override
	public TakeAwayDetailForm getTakeAwayDetailByMerchantId(Long merchantId) {
		TakeAwayDetailForm takeAwayDetailForm = new TakeAwayDetailForm();
		
		//查询外卖基本信息
		TakeAway takeAway = waiMaiDao.getTakeAwayByMerchantId(merchantId);
		if (takeAway==null) {
			return null;
		}
		takeAwayDetailForm.setTakeAwayInfo(takeAway);
		
		//如果是文字版菜单，查询菜品信息
		if (TakeAway.MENU_TYPE_W.equals(takeAway.getMenuType())) {
			List<TakeAwayMenu> menu = waiMaiDao.queryMenusByTakeAwayId(takeAway.getTakeawayId());
			takeAwayDetailForm.setMenu(menu);
		}
		return takeAwayDetailForm;
	}

	/** 
	 * @description：根据条件分页查询分店信息（地图显示）
	 * @param Map<String,String> map
	 * @param startRow
	 * @param pageSize
	 * @return List<Map<String,Object>>
	 * @throws 
	 */
	public List<Map<String,Object>> getMerDetailByConditions(Map<String,String> map,int startRow,int pageSize){
		return this.waiMaiDao.getMerDetailByCondition(map, startRow, pageSize);
	}
	
	/** 
	 * @description:查询当前可视区域内的分店/品牌 数量
	 * @param map
	 * @return int
	 * @throws 
	 */
	public int getMerchantCount(Map<String,String> map,boolean isBrand){
		return this.waiMaiDao.getMerchantCount(map,isBrand);
	}	
	
	/** 
	 * @description：通过分店Id分页查询美食地图分店信息
	 * @param ids
	 * @param conditionMap
	 * @param startRow
	 * @param pageSize
	 * @return List<Map<String,Object>>
	 * @throws 
	 */
	public List<Map<String,Object>> getSearchMerDetailByIds(
				List<Long> ids,Map<String,String> conditionMap,int startRow,int pageSize){
		if(null != ids && ids.size() > 0){
			return this.waiMaiDao.getSearchMerDetailByIds(ids, conditionMap, startRow, pageSize);
		}else{
			return null;
		}
	}
	
	/** 
	 * @description:根据分店搜索Id查询当前区域内分店数量
	 * @param ids
	 * @param conditionMap
	 * @return int
	 * @throws 
	 */
	public int getSearchMerCountByIds(List<Long> ids,Map<String,String> conditionMap){
		if(null != ids && ids.size() > 0 ){
			return this.waiMaiDao.getSearchMerCountByIds(ids, conditionMap);
		}
		return 0;
	}
	/** 
	 * @description:  一个品牌下取一个分店
	 * @param conditionMap
	 * @param startRow
	 * @param pageSize
	 * @return List<Map<String,Object>>
	 * @throws 
	 */
	public List<Map<String,Object>> getDistinctMerDetail(Map<String,String> conditionMap,int startRow,int pageSize){
		List<Long> idList = waiMaiDao.getBrandIdByCondition(conditionMap, startRow, pageSize);
		if(null != idList && idList.size() > 0){
			return waiMaiDao.getMerDetailByBrandId(idList, conditionMap);
		}
		return null;
	}
	/** 
	 * @description：分店所属品牌是否含有在售商品
	 * @param brandIdList
	 * @return Set<String>
	 * @throws 
	 */
	public Set<String> isBrandContainOnLineGoods(List<Long> brandIdList){
		Set<String> merSet = new HashSet<String>();
		if(null != brandIdList && brandIdList.size() > 0){
			List<Map<String,Object>> resList = this.waiMaiDao.isBrandContainOnLineGoods(brandIdList);
			if(null != resList && resList.size() > 0){
				for(Map<String,Object> map : resList){
					merSet.add(map.get("merchantid").toString());
				}
			}
		}
		return merSet;
	}
	/**
	 * 
	* @Title: getBranchsTakeAway
	* @Description: 取多个分店，并且取各分店指定数量的菜
	* @param @param branchids
	* @param @param menuCount    设定文件
	* @return void    返回类型
	* @throws
	 */
	@Override
	public Map<TakeAway,List<TakeAwayMenu>> getBranchsTakeAway(String branchids, Integer menuCount) throws Exception{
		// TODO Auto-generated method stub
		List<Map<String, Object>> takeawayMap = waiMaiDao.getBranchsTakeAway(branchids,menuCount);
		Map<TakeAway,List<TakeAwayMenu>> takeawayObjectMap = new HashMap<TakeAway,List<TakeAwayMenu>>();
		List<TakeAwayMenu> menus = null;
		for (Map<String,Object> ta:takeawayMap) { 
			TakeAway takeaway = new TakeAway();
			takeaway.setTakeawayId(Long.valueOf(String.valueOf(ta.get("takeaway_id"))));
			
			TakeAwayMenu menu = new TakeAwayMenu();
			menu.setMenuName(String.valueOf(ta.get("menu_name")));
			menu.setMenuPrice(String.valueOf(ta.get("menu_price")));
			menu.setMenuUnit(String.valueOf(ta.get("menu_unit")));
			
			
			menus = takeawayObjectMap.get(takeaway);
			if(null == menus){
				menus = new ArrayList<TakeAwayMenu>();
				menus.add(menu);
				
				takeaway.setBranchId((Long.valueOf(String.valueOf(ta.get("branch_id")))));
				takeaway.setDeliveryArea(String.valueOf(ta.get("delivery_area")));
				takeaway.setStartAmount(new BigDecimal(String.valueOf(ta.get("start_amount"))));
				takeaway.setTakeawayPhone(String.valueOf(ta.get("takeaway_phone")));
				takeaway.setTakeawayTime(String.valueOf(ta.get("takeaway_time")));
				
				takeawayObjectMap.put(takeaway, menus);
			}else{
				if(menus.size()>=menuCount)
					continue;
				menus.add(menu);
			}
		}
		
		return takeawayObjectMap;
	}
}
