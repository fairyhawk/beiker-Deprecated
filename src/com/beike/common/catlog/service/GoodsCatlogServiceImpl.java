package com.beike.common.catlog.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.dao.catlog.CatlogDao;
import com.beike.dao.catlog.GoodsCatlogDao;
import com.beike.dao.catlog.RegionCatlogDao;
import com.beike.dao.goods.GoodsDao;
import com.beike.dao.impl.catlog.GoodsCatlogDaoImpl;
import com.beike.entity.catlog.AbstractCatlog;
import com.beike.entity.catlog.DefaultGoods;
import com.beike.entity.catlog.DefaultGoodsDateASCCompartor;
import com.beike.entity.catlog.DefaultGoodsDescCompartor;
import com.beike.form.GoodsForm;
import com.beike.page.Pager;
import com.beike.util.DateUtils;
import com.beike.util.StringUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.constants.GoodsRelatedConstants;

/**
 * <p>
 * Title:商品地域、标签属性服务
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: Sinobo
 * </p>
 * 
 * @date May 21, 2011
 * @author ye.tian
 * @version 1.0
 */
@Service("goodsCatlogService")
public class GoodsCatlogServiceImpl implements GoodsCatlogService {
	private static Log log = LogFactory.getLog(GoodsCatlogServiceImpl.class);
	@Resource(name = "goodsCatlogDao")
	private GoodsCatlogDao catLogDao;

	@Autowired
	private GoodsDao goodsDao;

	private final MemCacheService memcacheService = MemCacheServiceImpl
			.getInstance();

	@Resource(name = "regionCatlogDao")
	private RegionCatlogDao regionCatlogDao;

	public GoodsDao getGoodsDao() {
		return goodsDao;
	}

	public void setGoodsDao(GoodsDao goodsDao) {
		this.goodsDao = goodsDao;
	}

	@Override
	public List<Long> getCatlog(AbstractCatlog abstractCatlog, Pager pager) {
		int startRow = pager.getStartRow();
		// 默认排序通道
		if ((abstractCatlog == null || abstractCatlog.isOrderByNull())
				|| (abstractCatlog.getOrderbydefault() != null || "asc"
						.equals(abstractCatlog.getOrderbydefault()))) {
			// modify by qiaowb 2012-04-05 默认排序规则变化
			return catLogDao.getDefaultGoodsIdBySortWeight(abstractCatlog,
					pager);
			// return getDefaultCatlog(abstractCatlog, pager);
			// 按销售量排序通道
		} else if ((abstractCatlog != null || !abstractCatlog.isOrderByNull())
				&& (StringUtils.validNull(abstractCatlog.getOrderbysort()))) {
			return getSortCatlog(abstractCatlog, pager);
		}
		return catLogDao.searchCatlog(abstractCatlog, startRow,
				pager.getPageSize());
	}

	@Override
	public List<Long> getCatlog(List<Long> validGoodsIdList, AbstractCatlog abstractCatlog, Pager pager) {
		GoodsCatlogDaoImpl catlogDaoImpl = (GoodsCatlogDaoImpl) catLogDao;
		int startRow = pager.getStartRow();
		// 默认排序通道
		if ((abstractCatlog == null || abstractCatlog.isOrderByNull()) || (abstractCatlog.getOrderbydefault() != null || "asc".equals(abstractCatlog.getOrderbydefault()))) {
			// modify by qiaowb 2012-04-05 默认排序规则变化
			return catlogDaoImpl.getDefaultGoodsIdBySortWeight(validGoodsIdList, abstractCatlog, pager);
			// return getDefaultCatlog(abstractCatlog, pager);
			// 按销售量排序通道
		} else if ((abstractCatlog != null || !abstractCatlog.isOrderByNull()) && (StringUtils.validNull(abstractCatlog.getOrderbysort()))) {
			return getSortCatlog(validGoodsIdList,abstractCatlog, pager);
		}
		return catlogDaoImpl.searchCatlog(validGoodsIdList,abstractCatlog, startRow, pager.getPageSize());
	}

	public CatlogDao getCatLogDao() {
		return catLogDao;
	}

	public void setCatLogDao(GoodsCatlogDao catLogDao) {
		this.catLogDao = catLogDao;
	}

	@Override
	public List<GoodsForm> getGoodsFormFromId(List<Long> listids) {

		if (listids == null || listids.size() == 0)
			return null;
		StringBuilder sb = new StringBuilder();
		for (Long long1 : listids) {
			sb.append(long1);
			sb.append(",");
		}
		String course = sb.subSequence(0, sb.lastIndexOf(",")).toString();

		return goodsDao.getGoodsByIds(course);

	}

	// public List<GoodsForm> getGoodsFormByPage(List<Long> listids,Pager
	// pager){
	// if(listids==null||listids.size()==0)return null;
	// StringBuilder sb=new StringBuilder();
	// for (Long long1 : listids) {
	// sb.append(long1);
	// sb.append(",");
	// }
	// String course=sb.subSequence(0,sb.lastIndexOf(",")).toString();
	//
	// int startRow=pager.getStartRow();
	// // int endRow=pager.getStartRow()+pager.getPageSize();
	//
	// return goodsDao.getGoodsByIds(course,startRow,pager.getPageSize());
	// }

	@Override
	public int getCatlogCount(AbstractCatlog abstractCatlog) {
		return catLogDao.searchCatlogCount(abstractCatlog);
	}

	@Override
	public int getCatlogCount(List<Long> validGoodsIdList, AbstractCatlog abstractCatlog) {
		return catLogDao.searchCatlogCount(validGoodsIdList, abstractCatlog);
	}

	/*
	 * @Override public String getGoodscatByID(Long goodsid) { return
	 * catLogDao.getCatByID(goodsid); }
	 */

	@Override
	public List<Long> getCatlogRank(AbstractCatlog abstractCatlog, Pager pager) {
		return catLogDao.getCatlogRank(abstractCatlog, pager);
	}

	@Override
	public List<Long> getHotMerchantIDS(AbstractCatlog abstractCatlog) {

		return goodsDao.getHotMerchantIDS(abstractCatlog);
	}

	@Override
	public Map<String, Object> getCityIdByRegionId(String regionId) {

		Long rid = 0L;
		try {
			rid = Long.parseLong(regionId);
		} catch (Exception e) {
			e.printStackTrace();
			// 直接返回北京id
			return new HashMap<String, Object>();
		}
		return regionCatlogDao.getCityIdByRegion(rid);

	}

	@Override
	public List<Long> getDefaultCatlog(AbstractCatlog abstractCatlog,
			Pager pager) {
		Map<Long, DefaultGoods> map = new HashMap<Long, DefaultGoods>();
		List filteredGoodsid = catLogDao.searchDefaultGoodsId(abstractCatlog);

		if (filteredGoodsid == null || filteredGoodsid.size() == 0) {
			return null;
		} else {
			List topSaled = catLogDao.getTopSaled();
			List on_timeList = catLogDao.getGoodsOnTime(abstractCatlog);
			for (int i = 0; i < filteredGoodsid.size(); i++) {
				DefaultGoods dg = new DefaultGoods();
				dg.setGoodsid((Long) ((Map) filteredGoodsid.get(i))
						.get("goodid"));
				dg.setCurrentPrice(((BigDecimal) ((Map) filteredGoodsid.get(i))
						.get("currentPrice")).doubleValue());
				map.put(dg.getGoodsid(), dg);
			}
			for (int j = 0; j < on_timeList.size(); j++) {
				Map temp = (Map) on_timeList.get(j);
				DefaultGoods dg = map.get(new Long((Integer) temp
						.get("goods_id")));
				dg.setOn_time((Timestamp) temp.get("on_time"));
				map.put(dg.getGoodsid(), dg);
			}

			for (int h = 0; h < topSaled.size(); h++) {
				Map temp = (Map) topSaled.get(h);
				if (map.containsKey(temp.get("goods_id"))) {
					DefaultGoods dg = map.get(temp.get("goods_id"));
					dg.setSaled(Integer.parseInt(temp.get("top").toString()));

					map.put(dg.getGoodsid(), dg);
				}
			}
			List<DefaultGoods> goodsList = new ArrayList<DefaultGoods>();
			for (Map.Entry<Long, DefaultGoods> o : map.entrySet()) {
				DefaultGoods dg = o.getValue();
				if (dg.getCurrentPrice() < 3) {
					dg.setSaled(-5000 + dg.getSaled());
				}
				goodsList.add(dg);
			}
			Collections.sort(goodsList, new DefaultGoodsDescCompartor());
			List<Long> results = new ArrayList<Long>();
			int pagesize = pager.getPageSize();
			int currentpage = pager.getCurrentPage();

			// 可以用来检验数据是否一致
			// int count = pager.getTotalRows();
			if (pagesize * currentpage > goodsList.size()) {
				for (int i = pager.getStartRow(); i < goodsList.size(); i++) {
					results.add(goodsList.get(i).getGoodsid());
				}
			} else {
				for (int i = pager.getStartRow(); i < pagesize * currentpage; i++) {
					results.add(goodsList.get(i).getGoodsid());
				}
			}
			return results;
		}
	}

	@Override
	public List<Long> getSortCatlog(AbstractCatlog abstractCatlog, Pager pager) {
		Map<Long, DefaultGoods> map = new HashMap<Long, DefaultGoods>();
		List filteredGoodsid = catLogDao.searchDefaultGoodsId(abstractCatlog);

		if (filteredGoodsid == null || filteredGoodsid.size() == 0) {
			return null;
		} else {
			List saled = catLogDao.getGoodsSaled();
			List on_timeList = catLogDao.getGoodsOnTime(abstractCatlog);
			for (int i = 0; i < filteredGoodsid.size(); i++) {
				DefaultGoods dg = new DefaultGoods();
				dg.setGoodsid((Long) ((Map) filteredGoodsid.get(i))
						.get("goodid"));
				map.put(dg.getGoodsid(), dg);
			}
			for (int j = 0; j < on_timeList.size(); j++) {
				Map temp = (Map) on_timeList.get(j);
				DefaultGoods dg = map.get(new Long((Integer) temp
						.get("goods_id")));
				dg.setOn_time((Timestamp) temp.get("on_time"));
				map.put(dg.getGoodsid(), dg);
			}

			for (int h = 0; h < saled.size(); h++) {
				Map temp = (Map) saled.get(h);
				if (map.containsKey(temp.get("goodsid"))) {
					DefaultGoods dg = map.get(temp.get("goodsid"));
					dg.setSaled(Integer.parseInt(temp.get("saled").toString()));
					map.put(dg.getGoodsid(), dg);
				}
			}
			List<DefaultGoods> goodsList = new ArrayList<DefaultGoods>();
			for (Object o : map.values()) {

				goodsList.add((DefaultGoods) o);
			}
			if ("asc".equals(abstractCatlog.getOrderbysort())) {
				Collections.sort(goodsList, new DefaultGoodsDateASCCompartor());
			} else {
				Collections.sort(goodsList, new DefaultGoodsDescCompartor());
			}

			List<Long> results = new ArrayList<Long>();
			int pagesize = pager.getPageSize();
			int currentpage = pager.getCurrentPage();

			// 可以用来检验数据是否一致
			// int count = pager.getTotalRows();
			if (pagesize * currentpage > goodsList.size()) {
				for (int i = pager.getStartRow(); i < goodsList.size(); i++) {
					results.add(goodsList.get(i).getGoodsid());
				}
			} else {
				for (int i = pager.getStartRow(); i < pagesize * currentpage; i++) {
					results.add(goodsList.get(i).getGoodsid());
				}
			}
			return results;
		}
	}

	public List<Long> getSortCatlog(List<Long> validGoodsIdList, AbstractCatlog abstractCatlog, Pager pager) {
		Map<Long, DefaultGoods> map = new HashMap<Long, DefaultGoods>();
		GoodsCatlogDaoImpl goodsCatlogDaoImpl = (GoodsCatlogDaoImpl) catLogDao;
		List filteredGoodsid = goodsCatlogDaoImpl.searchDefaultGoodsId(validGoodsIdList, abstractCatlog);

		if (filteredGoodsid == null || filteredGoodsid.size() == 0) {
			return null;
		} else {
			List saled = catLogDao.getGoodsSaled();
			List on_timeList = catLogDao.getGoodsOnTime(validGoodsIdList,abstractCatlog);
			for (int i = 0; i < filteredGoodsid.size(); i++) {
				DefaultGoods dg = new DefaultGoods();
				dg.setGoodsid((Long) ((Map) filteredGoodsid.get(i)).get("goodid"));
				map.put(dg.getGoodsid(), dg);
			}
			for (int j = 0; j < on_timeList.size(); j++) {
				Map temp = (Map) on_timeList.get(j);
				DefaultGoods dg = map.get(new Long((Integer) temp.get("goods_id")));
				dg.setOn_time((Timestamp) temp.get("on_time"));
				map.put(dg.getGoodsid(), dg);
			}

			for (int h = 0; h < saled.size(); h++) {
				Map temp = (Map) saled.get(h);
				if (map.containsKey(temp.get("goodsid"))) {
					DefaultGoods dg = map.get(temp.get("goodsid"));
					dg.setSaled(Integer.parseInt(temp.get("saled").toString()));
					map.put(dg.getGoodsid(), dg);
				}
			}
			List<DefaultGoods> goodsList = new ArrayList<DefaultGoods>();
			for (Object o : map.values()) {

				goodsList.add((DefaultGoods) o);
			}
			if ("asc".equals(abstractCatlog.getOrderbysort())) {
				Collections.sort(goodsList, new DefaultGoodsDateASCCompartor());
			} else {
				Collections.sort(goodsList, new DefaultGoodsDescCompartor());
			}

			List<Long> results = new ArrayList<Long>();
			int pagesize = pager.getPageSize();
			int currentpage = pager.getCurrentPage();

			// 可以用来检验数据是否一致
			// int count = pager.getTotalRows();
			if (pagesize * currentpage > goodsList.size()) {
				for (int i = pager.getStartRow(); i < goodsList.size(); i++) {
					results.add(goodsList.get(i).getGoodsid());
				}
			} else {
				for (int i = pager.getStartRow(); i < pagesize * currentpage; i++) {
					results.add(goodsList.get(i).getGoodsid());
				}
			}
			return results;
		}
	}

	@Override
	public Map<String, Integer> getGoodsCatlogGroupCount(Long cityid) {
		// 分页查询各城市商品
		int curPage = 0;
		List<Map<String, Object>> lstCatlogGoods = catLogDao
				.getGoodsCatlogList(cityid, curPage);
		Map<String, String> repeatMap = new HashMap<String, String>();
		Long lastGoodsId = 0L;
		// 3天前日期
		Date tmpDate = DateUtils.toDate(
				DateUtils.getTimeBeforeORAfter(-3, "yyyy-MM-dd 00:00:00"),
				"yyyy-MM-dd HH:mm:ss");
		Map<String, Integer> groupCountMap = new HashMap<String, Integer>();

		while (lstCatlogGoods != null && lstCatlogGoods.size() > 0) {
			StringBuilder bufStr = new StringBuilder("");
			for (Map<String, Object> tmpData : lstCatlogGoods) {
				bufStr = bufStr.append(tmpData.get("goodid")).append(",");
			}
			Map<Long, List<String>> hmGoodsBiaoqian = new HashMap<Long, List<String>>();
			if (bufStr.length() > 0) {
				List<Map<String, Object>> lstGoodsBiaoqian = catLogDao
						.getGoodsBiaoqianList(bufStr.substring(0,
								bufStr.length() - 1));
				if (lstGoodsBiaoqian != null && lstGoodsBiaoqian.size() > 0) {
					for (Map<String, Object> bqMap : lstGoodsBiaoqian) {
						Long tmpGoodsId = (Long) bqMap.get("goodsid");
						Long tmpbiaoqianid = (Long) bqMap.get("biaoqianid");

						List<String> lstBiaoqian = hmGoodsBiaoqian
								.get(tmpGoodsId);
						if (lstBiaoqian == null) {
							lstBiaoqian = new ArrayList<String>();
						}
						lstBiaoqian.add("B" + String.valueOf(tmpbiaoqianid));
						hmGoodsBiaoqian.put(tmpGoodsId, lstBiaoqian);
					}
				}
			}

			// 计算各筛选条件商品数量
			for (Map<String, Object> tmpData : lstCatlogGoods) {
				Long curGoodsId = (Long) tmpData.get("goodid");
				if (lastGoodsId.compareTo(curGoodsId) != 0) {
					lastGoodsId = curGoodsId;
					repeatMap.clear();
				}

				String tagid = String.valueOf(tmpData.get("tagid"));
				String tagnextid = String.valueOf(tmpData.get("tagextid"));
				String[] aryTag = new String[] { "tagAll", tagid,
						tagid + "|" + tagnextid };

				String regionid = String.valueOf(tmpData.get("regionid"));
				String regionextid = String.valueOf(tmpData.get("regionextid"));
				String[] aryRegion = new String[] { "regionAll", regionid,
						regionid + "|" + regionextid };

				// 价格区间
				String[] aryPrice = new String[] { "" };
				BigDecimal bdPrice = (BigDecimal) tmpData.get("currentPrice");
				if (bdPrice == null) {
					bdPrice = new BigDecimal(0);
				}
				if (bdPrice.compareTo(new BigDecimal(50)) <= 0) {
					aryPrice[0] = "50";
				} else if (bdPrice.compareTo(new BigDecimal(100)) <= 0) {
					aryPrice[0] = "100";
				} else if (bdPrice.compareTo(new BigDecimal(300)) <= 0) {
					aryPrice[0] = "300";
				} else if (bdPrice.compareTo(new BigDecimal(500)) <= 0) {
					aryPrice[0] = "500";
				} else {
					aryPrice[0] = "1000";
				}

				// 3日内上线
				boolean isNew = false;
				Timestamp tsOnTime = (Timestamp) tmpData.get("on_time");
				if (tsOnTime.compareTo(tmpDate) >= 0) {
					isNew = true;
				}
				// 现金券
				boolean isCash = false;
				//商品代金券 add by xuxiaoxian 20130131
				boolean isToken = false;
				
				if ("1".equals(String.valueOf(tmpData.get("couponcash")))) {
					isCash = true;
				}else if("2".equals(String.valueOf(tmpData.get("couponcash")))){
					isToken = true;
				}
				
				List<String> lstBiaoqian = hmGoodsBiaoqian.get(curGoodsId);
				calculateGoodsCount(
						curGoodsId,
						repeatMap,
						groupCountMap,
						createKeyValue(aryTag, aryRegion, aryPrice, lstBiaoqian),
						isNew, isCash,isToken);
			}
			curPage++;
			lstCatlogGoods = catLogDao.getGoodsCatlogList(cityid, curPage);
		}
		groupCountMap.put(String.valueOf(cityid), 0);
		return groupCountMap;
	}

	/**
	 * 创建键值
	 * 
	 * @param aryTag
	 * @param aryRegion
	 * @param aryPrice
	 * @return
	 */
	private Set<String> createKeyValue(String[] aryTag, String[] aryRegion,
			String[] aryPrice, List<String> lstBiaoqian) {
		Set<String> keyList = new HashSet<String>();
		// 分类
		for (int i = 0; i < aryTag.length; i++) {
			// 分类
			keyList.add(aryTag[i]);

			// 分类|商圈
			for (int j = 0; j < aryRegion.length; j++) {
				keyList.add(aryTag[i] + "|" + aryRegion[j]);
				if (i == 0) {
					// 商圈
					keyList.add(aryRegion[j]);
				}
			}

			// 分类|价格
			for (int j = 0; j < aryPrice.length; j++) {
				keyList.add(aryTag[i] + "|" + aryPrice[j]);
				if (i == 0) {
					// 价格
					keyList.add(aryPrice[j]);
				}
			}
		}
		// 商圈|价格
		for (int i = 0; i < aryRegion.length; i++) {
			for (int j = 0; j < aryPrice.length; j++) {
				keyList.add(aryRegion[i] + "|" + aryPrice[j]);
			}
		}
		// 分类 + 商圈 + 价格
		for (int i = 0; i < aryTag.length; i++) {
			for (int j = 0; j < aryRegion.length; j++) {
				for (int k = 0; k < aryPrice.length; k++) {
					keyList.add(aryTag[i] + "|" + aryRegion[j] + "|"
							+ aryPrice[k]);
				}
			}
		}

		if (lstBiaoqian != null && lstBiaoqian.size() > 0) {
			List<String> lstTmpKey = new ArrayList<String>();
			lstTmpKey.addAll(keyList);
			for (String strBiaoqian : lstBiaoqian) {
				// 标签
				keyList.add(strBiaoqian);
				// 分类、商圈、价格与标签组合
				for (String tmpKey : lstTmpKey) {
					keyList.add(tmpKey + "|" + strBiaoqian);
				}
			}
		}
		return keyList;
	}

	/**
	 * 计算数量：同一商品相同key值只算一次数量，处理一个商品多个分类、多个商圈问题
	 * 
	 * @param goodsId
	 * @param repeatMap
	 * @param groupCountMap
	 * @param keyList
	 * @param isNew
	 * @param isCash
	 * @param isToken
	 */
	private void calculateGoodsCount(Long goodsId,
			Map<String, String> repeatMap, Map<String, Integer> groupCountMap,
			Set<String> keyList, boolean isNew, boolean isCash,boolean isToken) {
		if (keyList != null && keyList.size() > 0) {
			for (String key : keyList) {
				StringBuilder bufRepeatKey = new StringBuilder();
				bufRepeatKey.append(key).append("_").append(goodsId);
				if (!repeatMap.containsKey(bufRepeatKey.toString())) {
					repeatMap.put(bufRepeatKey.toString(), "1");

					doCalculate(groupCountMap, key);

					// 计算新品数量
					if (isNew) {
						doCalculate(groupCountMap, key + "|new1");
						// 计算新品现金券或商品代金券 add by xuxiaoxian 20130131
						if(isCash || isToken){
							doCalculate(groupCountMap, key + "|new1|both");
						}
						if(isCash){
							doCalculate(groupCountMap, key + "|new1|cash1");
						}else if(isToken){
							doCalculate(groupCountMap, key + "|new1|token");
						}
					}
					// 计算现金券或商品代金券
					if(isCash || isToken){
						doCalculate(groupCountMap, key + "|both");
					}
					if(isCash){
						doCalculate(groupCountMap, key + "|cash1");
					}else if(isToken){
						doCalculate(groupCountMap, key + "|token");
					}
				}
			}
		}
	}

	/**
	 * 计算数量：通过key从map中获取数量+1后重新放回map
	 * 
	 * @param groupCountMap
	 * @param key
	 */
	private void doCalculate(Map<String, Integer> groupCountMap, String key) {
		Integer valueCount = groupCountMap.get(key);
		if (valueCount == null) {
			valueCount = 0;
		}
		valueCount = valueCount + 1;
		groupCountMap.put(key, valueCount);
	}

	@Override
	public int[] getGoodsCountByCity(String cityen) {
		Map<String, Long> mapCity = (Map<String, Long>) memcacheService
				.get("CITY_CATLOG");
		if (mapCity == null) {
			mapCity = regionCatlogDao.getCityCatlog();
			memcacheService.set("CITY_CATLOG", mapCity);
		}
		if (StringUtils.isEmpty(cityen)) {
			cityen = "beijing";
		}
		Long cityid = null;
		if (mapCity != null) {
			cityid = mapCity.get(cityen.trim());
		}

		int[] aryGoodsCount = { 0, 0 };
		Map<String, Integer> mapCatlogCount = (Map<String, Integer>) memcacheService
				.get(GoodsRelatedConstants.MEMCAHCECACHE_KEY_CITY_GOODS_COUNT
						+ cityid);
		//主缓存值取不到，取备份值 add by qiaoweibo 2012-08-20
		if(mapCatlogCount == null){
			mapCatlogCount = (Map<String, Integer>) memcacheService
				.get(GoodsRelatedConstants.MEMCAHCECACHE_KEY_CITY_GOODS_COUNT + cityid + "_b");
		}
		if (mapCatlogCount == null
				|| mapCatlogCount.get(String.valueOf(cityid)) == null) {
			log.info("GoodsCatlogServiceImpl.getGoodsCountByCity="
					+ mapCatlogCount + ",cityId=" + cityid);
			mapCatlogCount = getGoodsCatlogGroupCount(cityid);
			// cache有效期1小时
			memcacheService.set(
					GoodsRelatedConstants.MEMCAHCECACHE_KEY_CITY_GOODS_COUNT
							+ cityid, mapCatlogCount, 60 * 60);
		}

		if (mapCatlogCount == null || mapCatlogCount.isEmpty()) {
			return aryGoodsCount;
		} else {
			Integer newCount = mapCatlogCount.get("tagAll|new1");
			if (newCount == null) {
				aryGoodsCount[0] = 0;
			} else {
				aryGoodsCount[0] = newCount;
			}
			Integer allCount = mapCatlogCount.get("tagAll");
			if (allCount == null) {
				aryGoodsCount[1] = 0;
			} else {
				aryGoodsCount[1] = allCount;
			}
		}
		return aryGoodsCount;
	}
}
