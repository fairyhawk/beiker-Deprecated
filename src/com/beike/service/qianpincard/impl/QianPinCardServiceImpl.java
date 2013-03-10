package com.beike.service.qianpincard.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.beike.dao.GenericDao;
import com.beike.dao.catlog.RegionCatlogDao;
import com.beike.dao.qianpincard.QianPinCardDao;
import com.beike.entity.catlog.QPCardRegionCatlog;
import com.beike.entity.catlog.RegionCatlog;
import com.beike.entity.goods.Goods;
import com.beike.form.GoodsForm;
import com.beike.service.impl.GenericServiceImpl;
import com.beike.service.qianpincard.QianPinCardService;
import com.beike.util.DateUtils;

/**
 * @Title: 千品卡Service实现
 * @Package com.beike.service.qianpincard.impl
 * @Description: TODO
 * @author wenjie.mai
 * @date Feb 29, 2012 10:32:04 AM
 * @version V1.0
 */
@Service("qianPinCardService")
public class QianPinCardServiceImpl extends GenericServiceImpl<Goods, Long>
		implements QianPinCardService {
	@Autowired
	private QianPinCardDao qianPinCardDao;
	@Autowired
	@Qualifier("regionCatlogDao")
	private RegionCatlogDao regionCatlogDao;
	@Autowired
	@Qualifier("propertyCatlogDao")
	private RegionCatlogDao propertyCatlogDao;

	@Override
	public Goods findById(Long id) {

		return null;
	}

	@Override
	public GenericDao<Goods, Long> getDao() {
		return null;
	}

	public QianPinCardDao getQianPinCardDao() {
		return qianPinCardDao;
	}

	public void setQianPinCardDao(QianPinCardDao qianPinCardDao) {
		this.qianPinCardDao = qianPinCardDao;
	}

	@Override
	public List<Long> getTopSaleCardGoods(String areaId, int count, int nDays) {
		List<Long> lstInGoods = qianPinCardDao.getCardGoodsIdsByCityId(areaId);
		return qianPinCardDao.getTopSaleGoods(DateUtils.getTimeBeforeORAfter(-1
				* nDays), DateUtils.getTimeBeforeORAfter(0), count, lstInGoods);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GoodsForm> getCouponCashFor24Hour(Long cityId) {

		List<GoodsForm> goodsForm = new ArrayList<GoodsForm>();
		StringBuilder   idBuilder = new StringBuilder();
		String nowTime   = DateUtils.formatDate(new Date(),"yyyy-MM-dd");
		String startTime = nowTime + " 00:00:00";
		String endTime   = nowTime + " 23:59:59";

		Map<Long,Long> checkMap = new LinkedHashMap<Long,Long>();
		Set<Long>     idsList  = new LinkedHashSet<Long>();
		
		List<Long> cashIdList  = qianPinCardDao.getCouponCashForCityId(cityId);
		
		if(cashIdList == null) return null;
		
		List<Long> saleIds = qianPinCardDao.getTopSaleGoods(startTime, endTime, 0, cashIdList);
		
		if(saleIds == null) return null;
		
		for(Long li : saleIds){
			idBuilder.append(li).append(",");
		}
		
		String ids = idBuilder.substring(0,idBuilder.lastIndexOf(","));
		
		List saleList = qianPinCardDao.getCheckGoodIdOneMerchant(ids);
		
		if(saleList == null) return null;
		
		for(Object ox : saleList){
			Map mx = (Map) ox;
			Long merchantid = (Long) mx.get("merchantid"); 
			Long goodsid    = (Long) mx.get("goodsid");
			Long value = checkMap.get(merchantid);
			if(value == null){
				checkMap.put(merchantid, goodsid);
				if(checkMap.size() <=6){
					idsList.add(goodsid);
				}else{
					break;
				}
			}
		}
		
		if(checkMap.size() <5 || idsList.size() <5) return null;
		
		List cashList = qianPinCardDao.getCouponCashById(idsList);
		
		if(cashList == null) return null;
		
		for(Object ox : cashList){
			GoodsForm form = new GoodsForm();
			Map mx = (Map) ox;
			Long goodsid = (Long) mx.get("goodsid");
			BigDecimal sourcePrice  = (BigDecimal) mx.get("sourcePrice");
			BigDecimal currentPrice = (BigDecimal) mx.get("currentPrice");
			String  listlogo =  (String) mx.get("listlogo");
			form.setGoodsId(goodsid);
			form.setSourcePrice(sourcePrice.doubleValue());
			form.setCurrentPrice(currentPrice.doubleValue());
			form.setListlogo(listlogo);
			goodsForm.add(form);
			
		}
		
		return goodsForm;
	}

	@Override
	public List<Long> getCardGoodsOrderOnTime(String areaId) {
		List<Long> lstInGoods = qianPinCardDao.getCardGoodsIdsByCityId(areaId);
		return qianPinCardDao.getGoodsIdsOrderOntime(lstInGoods);
	}

	@Override
	public Map<String, Long> getGoodsTotal(int cityid) {
		Map<Long, List<RegionCatlog>> allProperty = propertyCatlogDao
				.getAllCatlog();
		Map<String, Long> goodsCountMap = new HashMap<String, Long>();
		List<RegionCatlog> parentPropertyList = allProperty.get(0L);

		for (int i = 0; i < parentPropertyList.size(); i++) {
			goodsCountMap.put(String.valueOf(parentPropertyList.get(i).getCatlogid()),
					qianPinCardDao.getGoodsCountByCity(
							parentPropertyList.get(i).getCatlogid()
									.intValue(), cityid));
		}
		return goodsCountMap;
	}

	@Override
	public Map<Long, List<Long>> getHotGoods(int cityid) {
		Map<Long, List<Long>> hotGoodsMap = new HashMap<Long, List<Long>>();
		List goodsidMap = qianPinCardDao.getGoodsByCategoryID(cityid);
		Map<Long,List<Long>> tempMap = new HashMap<Long, List<Long>>();
		List<Long> goodsidList = null;
		for(int i=0;i<goodsidMap.size();i++){
			Map resultMap = (Map)goodsidMap.get(i);
			
			Long tagid = (Long)resultMap.get("tagid");
			if(tempMap.containsKey(tagid)){
				 goodsidList = tempMap.get(tagid);
				goodsidList.add((Long)resultMap.get("goodid"));
				tempMap.put(tagid, goodsidList);
			}else{
				goodsidList = new ArrayList<Long>();
				goodsidList.add((Long)resultMap.get("goodid"));
				tempMap.put(tagid, goodsidList);
			}
		}
		for(Map.Entry<Long, List<Long>> entry:tempMap.entrySet()) {
		
			List<Long> results = qianPinCardDao.getTopSaleGoods(
					DateUtils.getTimeBeforeORAfter(-1), DateUtils
							.getTimeBeforeORAfter(0), 4, entry.getValue());

			hotGoodsMap.put(
					+ entry.getKey(), results);

		}
		return hotGoodsMap;
	}

	@Override
	public Map<String, List<QPCardRegionCatlog>> getHotRegion(int cityid) {
		Map<Long, List<RegionCatlog>> allProperty = propertyCatlogDao.getAllCatlog();
		Map<String,List<QPCardRegionCatlog>> hotRegionMap = new HashMap<String, List<QPCardRegionCatlog>>();
		List<RegionCatlog> parentPropertyList = allProperty.get(0L);
		for (int i = 0; i < parentPropertyList.size(); i++) {
			List list = qianPinCardDao.getSecondCategoryRank(parentPropertyList.get(i).getCatlogid().intValue(),cityid);
			List<QPCardRegionCatlog> temp = new ArrayList<QPCardRegionCatlog>();
			for(int j=0;j<list.size();j++){
				QPCardRegionCatlog r = new QPCardRegionCatlog();
				Map map = (Map) list.get(j);
				r.setCatlogid((Long) map.get("regionextid"));
				r.setCatlogName((String) map.get("region_name"));
				r.setRegion_enname((String) map.get("region_enname"));
				r.setParent_en_name((String) map.get("parent_en_name"));
				temp.add(r);
			}
			hotRegionMap.put(String.valueOf(parentPropertyList.get(i).getCatlogid()), temp);
		}
		return hotRegionMap;
	}

	@Override
	public List<RegionCatlog> getTopCategory() {
		Map<Long, List<RegionCatlog>> allProperty = propertyCatlogDao
				.getAllCatlog();
		
		List<RegionCatlog> parentPropertyList = allProperty.get(0L);
		return parentPropertyList;
	}
}
