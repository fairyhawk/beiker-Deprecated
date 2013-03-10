package com.beike.service.impl.goods;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.beike.common.exception.BaseException;
import com.beike.dao.GenericDao;
import com.beike.dao.goods.GoodsDao;
import com.beike.dao.merchant.BranchProfileDao;
import com.beike.entity.catlog.GoodsCatlog;
import com.beike.entity.goods.GoodKindly;
import com.beike.entity.goods.Goods;
import com.beike.entity.goods.GoodsProfile;
import com.beike.entity.merchant.BranchProfile;
import com.beike.form.CommendGoodsForm;
import com.beike.form.GoodsForm;
import com.beike.form.MerchantForm;
import com.beike.page.Pager;
import com.beike.service.goods.GoodsService;
import com.beike.service.impl.GenericServiceImpl;
import com.beike.service.merchant.ShopsBaoService;
import com.beike.util.Constant;
import com.beike.util.DateUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;

/**
 * <p>
 * Title:商品service
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
 * @date May 16, 2011
 * @author ye.tian
 * @version 1.0
 */
@Service("goodsService")
public class GoodsServiceImpl extends GenericServiceImpl<Goods, Long> implements
		GoodsService {
	
	private static Log logger = LogFactory.getLog(GoodsServiceImpl.class);
	
	private MemCacheService memcacheService=MemCacheServiceImpl.getInstance();

	@Autowired
	private BranchProfileDao branchProfileDao;
	@Autowired
	private ShopsBaoService shopsBaoService;
	
	public List<GoodsForm> getTopGoodsForm() {
		List<Map<String, String>> listValues = goodsDao.getTopGoods(5);
		List<GoodsForm> list = new ArrayList<GoodsForm>();
		for (Map<String, String> mapValue : listValues) {
			String goodsId = mapValue.get("goodsid");
			String goodsname = mapValue.get("goodsname");
			String rebatePrice = mapValue.get("rebatePrice");
			String currentPrice = mapValue.get("currentPrice");
			String logo2 = mapValue.get("logo2");
			GoodsForm gf = new GoodsForm();
			gf.setGoodsId(Long.parseLong(goodsId));
			gf.setGoodsname(goodsname);
			gf.setRebatePrice(Double.parseDouble(rebatePrice));
			gf.setCurrentPrice(Double.parseDouble(currentPrice));
			gf.setLogo2(logo2);
			list.add(gf);
		}
		return list;

	}

	public String getGoodDetailIncliudeUrl(Long goodId) {
		GoodsProfile goodsProfile = goodsDao.getGoodsProfile(goodId,
				"detailpageurl");
		if (goodsProfile == null)
			return null;
		return goodsProfile.getProfileValue();

	}

	public List<MerchantForm> getGoodsMapMerchant(Long goodId, Pager pager) {
		//查询分店信息
		List<Map<String, String>> list = goodsDao.getAllGoodsMerchant(goodId,
				pager.getStartRow(), pager.getPageSize());
		
		List<MerchantForm> listForm = new ArrayList<MerchantForm>();
		
		//查询分店满意率
		if(list!=null && list.size()>0){
			Map<String,BranchProfile> progileMap = new HashMap<String,BranchProfile>();
			StringBuilder sb = new StringBuilder();
			for (Map<String, String> map : list) {
				sb.append(map.get("id"));
				sb.append(",");
			}
			String sbid = sb.substring(0, sb.lastIndexOf(","));
			List<BranchProfile> lstProfile = branchProfileDao.getBranchProfileById(sbid);
			
			if(lstProfile!=null && lstProfile.size()>0){
				for (BranchProfile profile : lstProfile) {
					profile.calculateScore();
					progileMap.put(String.valueOf(profile.getBranchId()), profile);
				}
			}
			
			for (Map<String, String> map : list) {
				MerchantForm merchantForm = new MerchantForm();
				String id = map.get("id");
				if (id != null && !"".equals(id)) {
					merchantForm.setId(id);
				}
				String merchantName = map.get("merchantname");
				if (merchantName != null && !"".equals(merchantName)) {
					merchantForm.setMerchantname(merchantName);
				}
				String latitude = map.get("latitude");
				if (latitude != null && !"".equals(latitude)) {
					merchantForm.setLatitude(latitude);
				}
				String tel = map.get("tel");
				if (tel != null && !"".equals(tel)) {
					merchantForm.setTel(tel);
				}
				String addr = map.get("addr");
				if (addr != null && !"".equals(addr)) {
					merchantForm.setAddr(addr);
				}
				String buinesstime = map.get("buinesstime");
				if (buinesstime != null && !"".equals(buinesstime)) {
					merchantForm.setBuinesstime(buinesstime);
				}
				String city = map.get("city");
				if (city != null && !"".equals(city)) {
					merchantForm.setCity(city);
				}
				
				merchantForm.setIs_Support_Takeaway(map.get("is_support_takeaway"));
				merchantForm.setIs_Support_Online_Meal(map.get("is_support_online_meal"));
				
				
				//author wenjie.mai 添加特色服务属性 2013.02.26
				String environment = map.get("environment");
				String capacity    = map.get("capacity");
				String otherservice= map.get("otherservice");
				
				if(StringUtils.isNotBlank(environment)){
					if(environment.indexOf(",") !=-1){
						environment = environment.trim().replaceAll(",","、");
					}
					if(StringUtils.isNotBlank(capacity) || StringUtils.isNotBlank(otherservice))
						environment += "、";
				}else{
					environment = "";
				}
				
				if(StringUtils.isNotBlank(capacity)){
					capacity = capacity.trim();
					if(StringUtils.isNotBlank(otherservice))
						capacity += "、";
				}else{
					capacity = "";
				}
				
				if(StringUtils.isNotBlank(otherservice)){
					if(otherservice.indexOf(",") !=-1){
						otherservice = otherservice.trim().replaceAll(",","、");
					}
				}else{
					otherservice = "";
				}
				
				merchantForm.setEnvironment(environment);
				merchantForm.setCapacity(capacity);
				merchantForm.setOtherservice(otherservice);
				//添加特色服务属性  The End
				
				
				BranchProfile profile = progileMap.get(merchantForm.getId());
				if(profile!=null){
					merchantForm.setMcScore(0L);
					merchantForm.setWellCount(profile.getWellCount());
					merchantForm.setSatisfyCount(profile.getSatisfyCount());
					merchantForm.setPoorCount(profile.getPoorCount());
					merchantForm.setSatisfyRate(profile.getSatisfyRate());
					merchantForm.setPartWellRate(profile.getPartWellRate());
					merchantForm.setPartSatisfyRate(profile.getPartSatisfyRate());
					merchantForm.setPartPoorRate(profile.getPartPoorRate());
				}else{
					merchantForm.setMcScore(0L);
					merchantForm.setWellCount(0L);
					merchantForm.setSatisfyCount(0L);
					merchantForm.setPoorCount(0L);
					merchantForm.setSatisfyRate(-1);
					merchantForm.setPartWellRate(-1);
					merchantForm.setPartSatisfyRate(-1);
					merchantForm.setPartPoorRate(-1);
				}
				
				listForm.add(merchantForm);
			}
		}
		return listForm;
	}

	public MerchantForm getMerchantById(Long goodId) {

		Map<String, String> merchantMap = goodsDao.getMerchantByGoodId(goodId);
		String merchantId = merchantMap.get("id");
		String merchantname = merchantMap.get("merchantname");
		String sevenrefound = merchantMap.get("sevenrefound");
		String quality = merchantMap.get("quality");
		String overrefound = merchantMap.get("overrefound");

		MerchantForm merchantForm = new MerchantForm();

		merchantForm.setId(merchantId);
		merchantForm.setMerchantname(merchantname);

		if (sevenrefound != null && !"".equals(sevenrefound)) {
			merchantForm.setSevenrefound(Long.parseLong(sevenrefound));
		}
		if (quality != null && !"".equals(quality)) {
			merchantForm.setQuality(Long.parseLong(quality));
		}
		if (overrefound != null && !"".equals(overrefound)) {
			merchantForm.setOverrefound(Long.parseLong(overrefound));
		}

		return merchantForm;

	}

	// @Transactional(propagation = Propagation.REQUIRED)
	public void addGood(GoodsForm form) throws BaseException {
		// 增加Goods
		Long goodId = goodsDao.addGood(form);
		// 增加 包含页面
		GoodsProfile goodsProfile = new GoodsProfile();
		goodsProfile.setGoodsId(goodId);
		goodsProfile.setProfileName("detailpageurl");
		goodsProfile.setProfileValue(form.getProfilevalue());
		goodsDao.addGoodsProfile(goodsProfile);

	}

	
	
	public Goods findById(Long id) {

		return goodsDao.getGoodsDaoById(id);

	}

	
	
	@Override
	public GenericDao<Goods, Long> getDao() {
		return goodsDao;

	}

	@Autowired
	private GoodsDao goodsDao;

	public GoodsDao getGoodsDao() {
		return goodsDao;
	}

	public void setGoodsDao(GoodsDao goodsDao) {
		this.goodsDao = goodsDao;
	}

	public String salesCount(Long goodId) {
		GoodsProfile gp = goodsDao.getGoodsProfile(goodId, Constant.SALES_COUNT);
		if (gp == null)
			return "";
		return gp.getProfileValue();
	}

	public Goods getGoodsByBrandId(Long merchantId) {
		Goods goods = goodsDao.getTopGoodsForShopBao(merchantId);
		if(goods==null){
			goods=goodsDao.getOneGoodsByForShopBao(merchantId);
		}
		String isTop = "";
	    Set<String> regionList = null;
		if(goods != null){
			isTop = goodsDao.getIsTopForShopBao(goods.getGoodsId());  
			if(!"".equals(isTop)){
				goods.setIsTop(isTop);
			}
			regionList = goodsDao.getGoodsRegion(goods.getGoodsId());
		}else{
			return null;
		}
		if(null != regionList && regionList.size()>0){
			if(regionList.size() ==1 ){
				String region = regionList.toString();
				region = region.substring(1,region.indexOf("]"));
				goods.setMainRegion(region);
			}
			goods.setMapRegion(regionList);
		} 
		
		return goods;
	}

	public int getGoodsCount(List<MerchantForm> listForm) {
		String ids = getListids(listForm);
		if ("".equals(ids))
			return 0;
		return goodsDao.getGoodsCount(ids);
	}

	private String getListids(List<MerchantForm> listForm) {
		StringBuilder sb = new StringBuilder();
		if (listForm == null || listForm.size() == 0)
			return "";
		for (MerchantForm merchantForm : listForm) {
			sb.append(merchantForm.getId());
			sb.append(",");
		}
		String sbid = sb.substring(0, sb.lastIndexOf(","));
		return sbid;
	}

	public List<GoodsForm> getGoodsFormByChildId(List<Long> idsList) {
		StringBuilder ids = new StringBuilder("");
		for (Long long1 : idsList) {
			ids.append(long1);
			ids.append(",");
		}
		if (ids.length() == 0) {
			return new ArrayList<GoodsForm>();
		}
		String id = ids.substring(0, ids.lastIndexOf(","));
		if ("".equals(ids.toString())) {
			return null;
		}
		return goodsDao.getGoodsByIds(id);
	}

	public List<Long> getGoodsCountIds(String idsCourse, Pager pager) {

		return goodsDao.getGoodsCountIds(idsCourse, pager.getStartRow(),
				pager.getPageSize());
	}

	public GoodsCatlog searchGoodsRegionById(Long goodsId) {
		return goodsDao.searchGoodsRegionById(goodsId);

	}

	public int getAllGoodsMerchantCount(Long goodId) {
		return goodsDao.getAllGoodsMerchantCount(goodId);

	}

	@Override
	public List<Goods> findByIdList(String goodId) {
		String gId = "";//为查询商品关联的品牌信息组装goodId
		String goodsId = "";//为查询正常商品组装的goodsId
		String gAndMsId = "";//为查询秒杀商品组装的goodsId
		Map<String,String> gAndMsMap = new HashMap<String,String>();//秒杀商品goodsId和秒杀Id对应关系
		String[] gMsArray = goodId.split(",");
		for(int j=0;j<gMsArray.length;j++){
			 gId = gId + gMsArray[j].split("-")[0]+",";
			String msId = gMsArray[j].split("-")[1];
			String ggoId = gMsArray[j].split("-")[0];
			if("0".equals(msId)){
				goodsId = goodsId + ggoId+",";
			}else{
				gAndMsId = gAndMsId + ggoId+",";
				gAndMsMap.put(ggoId, msId);
			}
		}
		
		if (gId.contains(",")) {
			gId = gId.substring(0, gId.lastIndexOf(","));
		}
		
		if (goodsId.contains(",")) {
			goodsId = goodsId.substring(0, goodsId.lastIndexOf(","));
		}
		
		if (gAndMsId.contains(",")) {
			gAndMsId = gAndMsId.substring(0, gAndMsId.lastIndexOf(","));
		}
		//根据goodId查询商家商品信息关联表
		Map<String,String> mergoodMap = new HashMap<String, String>();
		Map<String,String> merNameMap = new HashMap<String, String>();
		List<Map<String,String>> gmList = goodsDao.getGoodsMerchant(gId);
		for(int i=0;i<gmList.size();i++){
			Map<String,String>  map  =(Map<String,String>)gmList.get(i);
			String merchantidStr = String.valueOf(map.get("merchantid"));
			//---------
			List<Map<String,String>> merchantList = goodsDao.getMerchant(merchantidStr);
			for(int j=0;j<merchantList.size();j++){
				Map<String,String> map1 = merchantList.get(j);
				String merchantid = String.valueOf(map1.get("merchantid"));//获取商家id
				String merchantname = map1.get("merchantname");
				mergoodMap.put(String.valueOf(map.get("goodsid")),merchantname);
				merNameMap.put(String.valueOf(map.get("goodsid")), merchantid);
			}
			//----------------
			
		}
		
		List<Goods> goodsList = new ArrayList<Goods>();
		List<Goods> goodsMsList = new ArrayList<Goods>();
		
		//查询正常商品信息表
		if(!"".equals(goodsId)){
		goodsList  = goodsDao.getGoodsById(goodsId);
		for(int k=0;k<goodsList.size();k++){
			Goods good = goodsList.get(k);
			String merchantname = mergoodMap.get(good.getGoodsId().toString());
			good.setMerchantname(merchantname);//
			String merchantid = merNameMap.get(good.getGoodsId().toString());
			good.setMerchantid(merchantid);
			good.setMiaoshaid("0");
		}
		}
		
		//查询秒杀商品信息表
		if(!"".equals(gAndMsId)){
		 goodsMsList  = goodsDao.getGoodsById(gAndMsId);
		for(int k=0;k<goodsMsList.size();k++){
			Goods good = goodsMsList.get(k);
			String merchantname = mergoodMap.get(good.getGoodsId().toString());
			good.setMerchantname(merchantname);//
			String merchantid = merNameMap.get(good.getGoodsId().toString());
			good.setMerchantid(merchantid);
			good.setMiaoshaid(gAndMsMap.get(good.getGoodsId().toString()));
			
			
			}
		
		
		
		}
		ContentComparator cc = 	new ContentComparator();
		goodsList.addAll(goodsMsList);
		Collections.sort(goodsList, cc);

		//Goods 
		//List<Goods> list = goodsDao.getGoodsDaoByIdList(goodId);
		return goodsList;
	}


	

	/**
	 * 补充方法：
	 * 根据商品ID, 来获取该商品的虚拟购买数量
	 * 
	 * 
	 * Add by zx.liu
	 */
	public Long getGoodsVirtualCount (Long goodsId){
		
		Long goodsVirtualCount = goodsDao.getGoodsVirtualCountById(goodsId);
		if(null == goodsVirtualCount){
			return 0L;
		}		
		return goodsVirtualCount;
	}	

	
	public List<Map<String,Object>> getGoodsInfoByGoodsIds(Long... goodsid){
		List<Map<String,Object>> mapList = goodsDao.getGoodsInfoByGoodsIds(goodsid);
		for(Map<String,Object> map : mapList){
			Number merchantid = (Number)map.get("merchantid");
			if(merchantid!=null){
				MerchantForm merchantForm = shopsBaoService.getMerchantDetailById(merchantid.longValue());
				map.put("satisfyRate", merchantForm.getSatisfyRate());
			}
		}
		return mapList;
	}

	@Override
	public List<Goods> getGoodsDaoByIdList(String goodsid) {
		return goodsDao.getGoodsDaoByIdList(goodsid);
	}

	/* (non-Javadoc)
	 * @see com.beike.service.goods.GoodsService#getShopsBaoGoodsCount(java.util.List, java.lang.String)
	 */
	public int getShopsBaoGoodsCount(List<MerchantForm> listForm, String filterFlag) {
		String ids = getListids(listForm);
		if (StringUtils.isEmpty(ids)){
			return 0;
		}
		return goodsDao.getShopsBaoGoodsCount(ids, filterFlag);
	}

	/* (non-Javadoc)
	 * @see com.beike.service.goods.GoodsService#getShopsBaoGoodsCountIds(java.util.List, java.lang.String, int, int)
	 */
	public List<Long> getShopsBaoGoodsCountIds(List<MerchantForm> listForm,
			String filterFlag, Pager pager) {
		String ids = getListids(listForm);
		if (StringUtils.isEmpty(ids)){
			return new ArrayList<Long>();
		}
		return goodsDao.getShopsBaoGoodsCountIds(ids, filterFlag, pager.getStartRow(), pager.getPageSize());
	}

	/* (non-Javadoc)
	 * @see com.beike.service.goods.GoodsService#getSaleWithGoodsIds(java.lang.Long, java.lang.Long, java.lang.Long, java.lang.String)
	 */
	public List<Long> getSaleWithGoodsIds(Long regionid, String regionextids,
			Long tagid, Long cityid, Long count, String excludeIds) {
		List<Long> lstSecRegionIds = null;
		//当前商品包含两个以上二级区域（不包含两个）信息时，则推荐相同一级属性分类销售数量最大的其他商品
		if(StringUtils.isEmpty(regionextids)){
			//从缓存查询
			lstSecRegionIds = (List<Long>)memcacheService.get(tagid + "_" + cityid + "_tuijian1_" + count);
		}else if(!StringUtils.contains(regionextids, ",")){
		//只有一个二级区域从缓存查询
			//二级区域从缓存查询
			lstSecRegionIds = (List<Long>)memcacheService.get(regionextids + "_tuijian1_" + count);
			//二级区域+一级属性从缓存查询
			if(lstSecRegionIds == null){
				lstSecRegionIds = (List<Long>)memcacheService.get(regionextids + "_" + tagid + "_tuijian1_" + count);
			}
		}

		//缓存中没有，从数据库检索
		if(lstSecRegionIds == null){
			if(StringUtils.isEmpty(regionextids)){
				//一级属性推荐商品
				lstSecRegionIds = goodsDao.getRecommendGoods(null, null, tagid, cityid, count, excludeIds);
				//放入缓存
				memcacheService.set(tagid + "_" + cityid + "_tuijian1" + count, lstSecRegionIds);
			}else{
				//二级区域推荐商品
				lstSecRegionIds = goodsDao.getRecommendGoods(null, regionextids, null, cityid, count, excludeIds);
				//二级区域推荐商品数量不足，补充查询一级属性销量最大商品
				if(lstSecRegionIds.size()<count){
					StringBuilder sb = new StringBuilder();
					for (Long goodsId : lstSecRegionIds) {
						sb.append(goodsId);
						sb.append(",");
					}
					sb.append(excludeIds);
					excludeIds = sb.toString();
					if(excludeIds.endsWith(",")){
						excludeIds = excludeIds.substring(0,excludeIds.length()-1);
					}
					List<Long> lstFirRegionIds = goodsDao.getRecommendGoods(null, null, tagid, cityid, count - lstSecRegionIds.size(), excludeIds);
					
					lstSecRegionIds.addAll(lstFirRegionIds);
					
					//如果不是两个二级区域，将结果放入缓存
					if(!StringUtils.contains(regionextids, ",")){
						memcacheService.set(regionextids + "_" + tagid + "_tuijian1" + count, lstSecRegionIds);
					}
				}else if(!StringUtils.contains(regionextids, ",")){
				//二级区域推荐商品数量满足，如果不是两个二级区域，将结果放入缓存
					memcacheService.set(regionextids + "_tuijian1" + count, lstSecRegionIds);
				}
			}
		}
		return lstSecRegionIds;
	}

	/* (non-Javadoc)
	 * @see com.beike.service.goods.GoodsService#getRecommendGoodsIds(java.lang.Long, java.lang.Long, java.lang.Long, java.lang.String)
	 */
	public List<Long> getRecommendGoodsIds(Long regionid, String regionextid,
			Long tagid, Long cityid, Long count, String excludeIds) {
		return getSaleWithGoodsIds(regionid, regionextid, tagid, cityid, count, excludeIds);
	}

	/* (non-Javadoc)
	 * @see com.beike.service.goods.GoodsService#getGoodsRegionIds(java.lang.Long)
	 */
	public List<Map<String, Object>> getGoodsRegionIds(Long goodsId) {
		return goodsDao.getGoodsRegionIds(goodsId);
	}

	/* (non-Javadoc)
	 * @see com.beike.service.goods.GoodsService#getCouponRegionIds(java.lang.Long)
	 */
	public List<Map<String, Object>> getCouponRegionIds(Long couponId) {
		return goodsDao.getCouponRegionIds(couponId);
	}

	/* (non-Javadoc)
	 * @see com.beike.service.goods.GoodsService#getGoodsFirstRegionById(java.lang.Long)
	 */
	public List<Map<String, Object>> getGoodsFirstRegionById(Long goodsId) {
		return goodsDao.getGoodsFirstRegionById(goodsId);
	}
	
	@Override
	public List<Map<String,Object>> getMostExpGoodsId(String goodsIds) {
		return goodsDao.getMostExpGoodsId(goodsIds);
	}
	
	
	public class ContentComparator implements
		Comparator<Goods> {
	public int compare(Goods o1, Goods o2) {

			// 将 null 排到最后
		if (o1 == null) {
			return 1;
			}
		if (o2 == null || !(o2 instanceof Goods)) {
			return -1;
			}

		return o1.getMerchantid().compareTo(
				o2.getMerchantid());

		}
	}


	@Override
	public List<Long> getLowestGoods(Long cityId,String ids) {
		List<Long>  lowPriceList = new ArrayList<Long>();	
		lowPriceList = goodsDao.getLowestPriceByNow(cityId,ids);	
		if(lowPriceList !=null && lowPriceList.size() == 3){
			return lowPriceList;
		}else{
			int countNum = 0;
			String excepId = "";
			StringBuilder str = new StringBuilder();
			
			if(lowPriceList == null){
				countNum = 3;
				lowPriceList = new ArrayList<Long>();
				excepId = ids;
			}else{
				countNum = 3-lowPriceList.size();
				for(Long lo : lowPriceList){
					ids =  ids + "," + String.valueOf(lo);
				}
			}
			
			List<Long> idsList = goodsDao.queryLowestPrice(cityId,Long.parseLong(String.valueOf(countNum)),ids);
			if(idsList != null && idsList.size() >0){
				for(Long lo : idsList){
					lowPriceList.add(lo);
				}
			}
		}
		return lowPriceList;
	}
	
	@Override
	public List<Long> getTopGoodsByMerchantId(Long merchantId, int topCount,
			String excGoodsIds) {
		return goodsDao.getTopGoodsByMerchantId(merchantId, topCount, excGoodsIds);
	}

	@Override
	public List<Long> getPartLowestGoods(Long cityId, Long countNum, String ids) {
		List<Long> idList =  (List<Long>)memcacheService.get("LowestGoodsIds_" + cityId);
		//推荐商品分类 美食，休闲娱乐，生活服务
		Long[] arr = {10100L,10200L,10400L};
		//缓存未命中
		if(idList == null){
			idList = new ArrayList<Long>();
			for(int i=0;i<arr.length;i++){
				List<Long> list = null;
			    list = goodsDao.getPartLowestPrice(cityId, 1L, ids, arr[i]);
				if(list != null && list.size() > 0 ){
					idList.addAll(list);
				}
			}
			memcacheService.set("LowestGoodsIds_" + cityId, idList);
		}else{
			boolean needSearch = false;
			for(int i=0;i<idList.size();i++){
				Long tmpId = idList.get(i);
				if(ids.indexOf(String.valueOf(tmpId))>=0){
					idList.set(i, null);
					needSearch = true;
				}
			}
			if(needSearch){
				for(int i=0;i<idList.size();i++){
					Long tmpId = idList.get(i);
					if(tmpId == null){
						List<Long> list = goodsDao.getPartLowestPrice(cityId, 1L, ids, arr[i]);
						if(list != null && list.size() > 0 ){
							idList.set(i, list.get(0));
						}
					}
				}
				memcacheService.set("LowestGoodsIds_" + cityId, idList);
			}
		}
		return idList;
	}

	@Override
	public List<Long> getTopRegionCatlogId(Long cityId, Long countNum) {
		return goodsDao.getTopRegionCatlogId(cityId, countNum);
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public int getGoodCountByCity(String city, String type) {
		
		List li = goodsDao.getGoodCountByCity(city, type);
		
		if(li == null || li.size() == 0)
			return 0;
		
		Map map = (Map) li.get(0);
		
		Long  count = (Long) map.get("countid");
		return count.intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getLowestPriceGood(Long catlogid, Long cityid) {
		
		List lowlist = goodsDao.getLowestPriceGoodById(catlogid, cityid);
		
		if(lowlist == null)
			 return null;
		
		List<Long> idlist = new LinkedList<Long>();
		for(Object ox : lowlist){
			Map mx = (Map) ox;
			Long id = (Long) mx.get("goodsid");
			idlist.add(id);
		}
		
		return idlist;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GoodKindly> getGoodKindlyById(Long goodId) {
		
		List kindlist = goodsDao.getGoodKindlyByGoodId(goodId);
		
		if(kindlist == null || kindlist.size() == 0)
				return null;
		
		List<GoodKindly> goodKindlist = new ArrayList<GoodKindly>();
		
		for(Object obj : kindlist){
			Map mx = (Map) obj;
			GoodKindly goodKindly = new GoodKindly();
			
			Integer gid     = (Integer) mx.get("goods_id");
			String kindWarn = (String) mx.get("kindlywarnings");
			Integer light   = (Integer) mx.get("high_light");
			Timestamp createTime = (Timestamp) mx.get("create_time");
			
			goodKindly.setGoodId(gid);
			goodKindly.setKindlywarnings(kindWarn);
			goodKindly.setHighlight(light);
			goodKindly.setCreateTime(createTime);
			goodKindlist.add(goodKindly);
		}
		return goodKindlist;
	}
	/** 
	 * @description:获得推荐商品Id(同品类热销，周边人气，网站热销)
	 * @param params
	 * @return Map<String,List<Long>>
	 * @throws 
	 */
	@SuppressWarnings("unchecked")
	public Map<String,List<Long>> getCommendGoodsId(Map<String,String> params) throws Exception{
		Map<String,List<Long>> TJSPMap = new HashMap<String,List<Long>>();
		Long goodId = Long.parseLong(params.get("goodid"));
		double tagprice = Double.parseDouble(params.get("currentPrice"));
		String cityId = params.get("cityId");
		List<Map<String,Object>> goodsRegionIds = this.getGoodsRegionIds(goodId);
		Set<Long> tagIdSet = new HashSet<Long>();
		Set<Long> tagExtIdSet = new HashSet<Long>();
		Set<Long> regionIdSet = new HashSet<Long>();
		Set<Long> regionExtIdSet = new HashSet<Long>();
		if(null != goodsRegionIds && goodsRegionIds.size() > 0){
			for(Map<String,Object> catlog : goodsRegionIds){
				Long tagId = (Long) catlog.get("tagid");
				if(!tagIdSet.contains(tagId)){
					tagIdSet.add(tagId);
				}
				Long tagExtId = (Long) catlog.get("tagextid");
				if(!tagExtIdSet.contains(tagExtId)){
					tagExtIdSet.add(tagExtId);
				}
				Long regionId = (Long) catlog.get("regionid");
				if(!regionIdSet.contains(regionId)){
					regionIdSet.add(regionId);
				}
				Long regionextId = (Long) catlog.get("regionextid");
				if(!regionExtIdSet.contains(regionextId)){
					regionExtIdSet.add(regionextId);
				}
			}
		}
		//同品类热销，周边人气，网站热销
		List<Long> tplrxIdList = null;
		List<Long> zbrqIdList = null;
		List<Long> wzrxIdList = null;
		List<CommendGoodsForm> tplrxList = new ArrayList<CommendGoodsForm>();
		List<CommendGoodsForm> wzrxList = new ArrayList<CommendGoodsForm>();
		List<CommendGoodsForm> zbrqList = new ArrayList<CommendGoodsForm>();

		//和主商品同店铺的所有商品Id
		String sameMerchantGoodsIds = goodsDao.getMerchantGoodsByMainId(goodId);
		
		//网站热销
		wzrxIdList = (List<Long>)memcacheService.get("WANGZHAN_REXIAO_"+cityId);
		if(null == wzrxIdList){
			wzrxIdList=new ArrayList<Long>();
			List<Map<String,Object>> list3 = goodsDao.getRealSalesCount(sameMerchantGoodsIds,cityId);
			if(null != list3 && list3.size() > 0){
				Map<Long,CommendGoodsForm> wzrxMap = new HashMap<Long,CommendGoodsForm>();
				for(Map<String,Object> map : list3){
					Long resgoodsid = Long.parseLong(map.get("goodsid").toString());
					double salesCount = Double.parseDouble(map.get("sales_count").toString());
					String startTime =  map.get("startTime").toString();
					String nowTime = DateUtils.getNowTime();
					long onLineDays = DateUtils.getDistinceDay(startTime, nowTime);
					CommendGoodsForm form = new CommendGoodsForm();
					if(onLineDays > 0L){
						double sortWeights = salesCount/onLineDays;
						form.setGoodsId(resgoodsid);
						form.setSortWeight(sortWeights);
					}else{
						form.setSortWeight(salesCount);
						form.setGoodsId(resgoodsid);
					}
					
					wzrxMap.put(resgoodsid, form);
				}
				wzrxMap = this.removeSameBrandGoods(wzrxMap);
				wzrxList = this.getOrderCount(wzrxMap);

				//注：网站热销只取36，三个推荐位共36个商品，另外两个推荐位商品不足时，足够补齐推荐位
				wzrxIdList = this.convertListFormToListId(wzrxIdList, wzrxList, 36);
				memcacheService.set("WANGZHAN_REXIAO_"+cityId, wzrxIdList,60*60*24);
				logger.info("WANG ZHAN RE XIAO");
				for(int i = 0;i<wzrxIdList.size();i++){
					logger.info(wzrxList.get(i).toString());
				}
			}
		}
		
		//同品类热销（买了该商品的用户也买了）
		tplrxIdList = (List<Long>)memcacheService.get("TONG_PINLEI_REXIAO_"+cityId+"_"+goodId);
		if(null == tplrxIdList){
			tplrxIdList=new ArrayList<Long>();
			Map<String,String> tplrx = new HashMap<String,String>();
			tplrx.put("goodsIds", sameMerchantGoodsIds);
			tplrx.put("areaid", cityId);
			tplrx.put("tagid", com.beike.util.StringUtils.arrayToString(tagIdSet.toArray(), ","));
			List<Map<String,Object>> list1 = this.goodsDao.getListGoodsInfo(tplrx);
			if(null != list1 && list1.size() > 0){
				Map<Long,CommendGoodsForm> priceMap = new HashMap<Long,CommendGoodsForm>();
				Map<Long,CommendGoodsForm> regionMap = new HashMap<Long,CommendGoodsForm>();
				Map<Long,CommendGoodsForm> fenleiMap = new HashMap<Long,CommendGoodsForm>();
				List<Long> middleFenleiList = new ArrayList<Long>();
				for(Map<String,Object> map : list1){
					Long resgoodid = Long.parseLong(map.get("goodid").toString());
					double price = Double.valueOf(map.get("currentprice").toString()); 
					double interval = tagprice - price; //主商品价格与当前商品差价
					boolean flag = interval >= -10 && interval <= 10;//上下浮动10元范围内加10分
					if(flag && !priceMap.containsKey(resgoodid)){
						priceMap.put(resgoodid, new CommendGoodsForm(resgoodid,10));
					}
					Long regionExtId = Long.parseLong(map.get("regionextid").toString());
					Long regionId = Long.parseLong(map.get("regionid").toString());
					// 商圈未加过权重值
					if(!regionMap.containsKey(resgoodid)){
						 if(regionExtIdSet.contains(regionExtId)){
							 CommendGoodsForm form = new CommendGoodsForm(resgoodid,100);
							 form.getRegionExtId().add(regionExtId);
							 form.setAddTimes(1);
							 regionMap.put(resgoodid, form);
						 }else if(regionIdSet.contains(regionId)){
							 CommendGoodsForm form = new CommendGoodsForm(resgoodid,5);
							 form.getRegionId().add(regionId);
							 regionMap.put(resgoodid, form);
						 }
					}else{
						CommendGoodsForm form = regionMap.get(resgoodid);
						if(null != form){
							double sorts = form.getSortWeight();
							if(regionExtIdSet.contains(regionExtId) && !form.getRegionExtId().contains(regionExtId)){
								form.setAddTimes(form.getAddTimes()+1);
								form.getRegionExtId().add(regionExtId);
								form.setSortWeight(sorts+100);
							}else if(regionIdSet.contains(regionId) && !form.getRegionId().contains(regionId)){
								form.getRegionId().add(regionId);
								form.setSortWeight(sorts+5);
							}
						}
					}
					//分类权重
					Long tagExtId = Long.parseLong(map.get("tagextid").toString());
					if(!middleFenleiList.contains(resgoodid) && tagExtIdSet.contains(tagExtId)){
						middleFenleiList.add(resgoodid);
					}							
				}
				for(Long fenleiGoodId : middleFenleiList){
					CommendGoodsForm form = regionMap.get(fenleiGoodId);
					int addTimes = 0;
					if(null != form){
						addTimes = form.getAddTimes();
					}
					if(addTimes > 1){
						fenleiMap.put(fenleiGoodId, new CommendGoodsForm(fenleiGoodId,100*addTimes));
					}else{
						fenleiMap.put(fenleiGoodId, new CommendGoodsForm(fenleiGoodId,100));
					}
				}
				//将Map中的权重值合到一块
				regionMap = combineMap(priceMap, regionMap);
				regionMap = combineMap(fenleiMap, regionMap);
				regionMap = this.removeSameBrandGoods(regionMap);
				tplrxList = this.getOrderCount(regionMap);	
				
				tplrxIdList = this.convertListFormToListId(tplrxIdList, tplrxList, 12);
				memcacheService.set("TONG_PINLEI_REXIAO_"+cityId+"_"+goodId, tplrxIdList,60*60*24);
				logger.info("MGGSPDYHYML");
				for(int i = 0;i < tplrxIdList.size();i++){
					logger.info(tplrxList.get(i).toString());
				}
			}
		}
		
		//周边人气
		zbrqIdList = (List<Long>)memcacheService.get("ZHOUBIAN_RENQI_"+cityId+"_"+goodId);
		if(null == zbrqIdList){
			zbrqIdList=new ArrayList<Long>();
			Map<String,String> zbrq = new HashMap<String,String>();
			zbrq.put("goodsIds", sameMerchantGoodsIds);
			zbrq.put("areaid", cityId);
			zbrq.put("regionid", com.beike.util.StringUtils.arrayToString(regionIdSet.toArray(), ","));
			zbrq.put("tagextid", com.beike.util.StringUtils.arrayToString(tagExtIdSet.toArray(), ","));
			List<Map<String,Object>> list2 = this.goodsDao.getListGoodsInfo(zbrq);
			if(null != list2 && list2.size() > 0){
				Map<Long,CommendGoodsForm> priceMap = new HashMap<Long,CommendGoodsForm>();
				Map<Long,CommendGoodsForm> regionMap = new HashMap<Long,CommendGoodsForm>();
				Map<Long,CommendGoodsForm> fenleiMap = new HashMap<Long,CommendGoodsForm>();
				List<Long> middleFenleiList = new ArrayList<Long>();
				for(Map<String,Object> map : list2){
					Long resgoodid = Long.parseLong(map.get("goodid").toString());
					double price = Double.valueOf(map.get("currentprice").toString()); 
					double interval = tagprice - price; //主商品价格与当前商品差价
					boolean flag = interval >= -10 && interval <= 10;//上下浮动10元范围内加10分
					if(flag && !priceMap.containsKey(resgoodid)){
						priceMap.put(resgoodid, new CommendGoodsForm(resgoodid,8));
					}
					//同二级商圈加100分，累加
					Long regionExtId = Long.parseLong(map.get("regionextid").toString());
					if(!regionMap.containsKey(resgoodid) && regionExtIdSet.contains(regionExtId)){
						CommendGoodsForm form = new CommendGoodsForm(resgoodid,100);
						form.setAddTimes(1);
						form.getRegionExtId().add(regionExtId);
						regionMap.put(resgoodid, form);
					}else{
						CommendGoodsForm  form = regionMap.get(resgoodid);
						if(null != form){
							if(regionExtIdSet.contains(regionExtId) && !form.getRegionExtId().contains(regionExtId)){
								form.setAddTimes(form.getAddTimes()+1);
								form.setSortWeight(form.getSortWeight()+100);
								form.getRegionExtId().add(regionExtId);
							}
						}
					}
					//同一级类目加100乘商圈累加次数				
					Long tagId = Long.parseLong(map.get("tagid").toString());
					if(tagIdSet.contains(tagId) && !middleFenleiList.contains(resgoodid)){
						middleFenleiList.add(resgoodid);
					}					
				}
				for(Long fenleiGoodId : middleFenleiList){
					CommendGoodsForm form = regionMap.get(fenleiGoodId);
					int addTimes = 0;
					if(null != form){
						addTimes = form.getAddTimes();
					}
					if(addTimes > 1){
						fenleiMap.put(fenleiGoodId, new CommendGoodsForm(fenleiGoodId,100*addTimes));
					}else{
						fenleiMap.put(fenleiGoodId, new CommendGoodsForm(fenleiGoodId,100));
					}
				}
				
				//将Map中的权重值合到一块
				regionMap = combineMap(priceMap, regionMap);
				regionMap = combineMap(fenleiMap, regionMap);
				regionMap = this.removeSameBrandGoods(regionMap);
				zbrqList = this.getOrderCount(regionMap);	
				
				zbrqIdList = this.convertListFormToListId(zbrqIdList, zbrqList, 24);
				memcacheService.set("ZHOUBIAN_RENQI_"+cityId+"_"+goodId, zbrqIdList,60*60*24);
				
				logger.info("ZHOU BIAN REN QI");
				for(int i = 0;i < zbrqIdList.size(); i++){
					logger.info(zbrqList.get(i).toString());
				}
			}	
		}
		//同品类热销		
		wzrxIdList.removeAll(tplrxIdList);
		if(tplrxIdList.size() <12){
			for(int i = tplrxIdList.size();i<12;i++){
				if(wzrxIdList.size() >12){
					tplrxIdList.add(i,wzrxIdList.get(12));
					wzrxIdList.remove(12);
				}
			}
		}
		//周边人气
		zbrqIdList.removeAll(tplrxIdList);
		if(zbrqIdList.size() > 12){
			zbrqIdList = zbrqIdList.subList(0, 12);
		}
		wzrxIdList.removeAll(zbrqIdList);
		if(zbrqIdList.size() < 12){
			 for(int i= zbrqIdList.size();i<12;i++){
				 if(wzrxIdList.size() > 12){
					 zbrqIdList.add(i,wzrxIdList.get(12));
					 wzrxIdList.remove(12);
				 }
			 }
		}
		if(wzrxIdList.size() > 12){
			wzrxIdList = wzrxIdList.subList(0, 12);
		}
		TJSPMap.put("tplrx", tplrxIdList);
		TJSPMap.put("zbrq", zbrqIdList);
		TJSPMap.put("wzrx", wzrxIdList);
		logger.info("MGGSPDYHYML:"+tplrxIdList.toString());
		logger.info("ZHOU BIAN REN QI:"+zbrqIdList.toString());
		logger.info("WANG ZHAN RE XIAO:"+wzrxIdList.toString());
		return TJSPMap;
	}
	/** 
	 * @description:同一个品牌商品只取一件
	 * @param map
	 * @return Map<Long,CommendGoodsForm> 
	 * @throws 
	 */
	public Map<Long,CommendGoodsForm> removeSameBrandGoods(Map<Long,CommendGoodsForm> map){
		Set<Long> set = map.keySet();
		String goodsId = "";
		if(set.size() > 0){
			goodsId = com.beike.util.StringUtils.arrayToString(set.toArray(), ",");
		}
		Map<String,String> brandMap = new HashMap<String,String>();
		Map<Long,CommendGoodsForm> goodsMap = new HashMap<Long,CommendGoodsForm>();
		if(goodsId.length() > 0){
			List<Map<String,Object>> list = goodsDao.getGoodsBrandId(goodsId);
			if(null != list && list.size() > 0){
				for(Map<String,Object> resMap  : list){
					Long goodid = Long.parseLong(resMap.get("goodsid").toString());
					String merchantId = resMap.get("merchantid").toString();
					if(!brandMap.containsKey(merchantId)){
						brandMap.put(merchantId, merchantId);
						goodsMap.put(goodid, map.get(goodid));
					}
				}
			}
		}
		return goodsMap;
	}
	/** 
	 * @description：获取订单量并排序
	 * @param map     
	 * @return List<CommendGoodsForm>
	 * @throws 
	 */
	public List<CommendGoodsForm> getOrderCount(Map<Long,CommendGoodsForm> map){
		Set<Long> set = map.keySet();
		String theIds = "";
		if(set.size() > 0){
			theIds = com.beike.util.StringUtils.arrayToString(set.toArray(), ",");
		}
		
		List<Map<String,Object>> list = null;
		if(theIds.length() > 0){
			list = goodsDao.getOrderCount(theIds);
			if(null != list && list.size() > 0){
				for(Map<String,Object> res : list){
					if(null != res.get("goods_id") && null != res.get("cou")){
						Long goodid = Long.parseLong(res.get("goods_id").toString());
						CommendGoodsForm form = map.get(goodid);
						form.setOrderCount(Integer.parseInt(res.get("cou").toString()));
					}
				}
			}
		}
		List<CommendGoodsForm> resultList = new ArrayList<CommendGoodsForm>();
		for (Iterator<Long> it = set.iterator(); it.hasNext();) {
			Long key = it.next();
			resultList.add(map.get(key));
		}
		Collections.sort(resultList);
		return resultList;
	}
	
	/** 
	 * @param idList
	 * @param formList
	 * @param size 要截取的list长度
	 * @return List<Long>
	 * @throws 
	 */
	private List<Long> convertListFormToListId(List<Long> idList,List<CommendGoodsForm> formList,int size){
		idList = new ArrayList<Long>();
		if(formList.size() < size){
			size = formList.size();
		}
		for(int i = 0;i<size;i++){
			idList.add(i,formList.get(i).getGoodsId());
		}
		return idList;
	}
	
	//将两个Map合到一块，权重值相加
	private Map<Long,CommendGoodsForm> combineMap(
								Map<Long,CommendGoodsForm> middleMap, Map<Long,CommendGoodsForm> tagMap){
		Set<Long> set = middleMap.keySet();
		for (Iterator<Long> it = set.iterator(); it.hasNext();) {
			Long key = it.next();
			if(tagMap.containsKey(key)){
				CommendGoodsForm bf = tagMap.get(key);
				bf.setSortWeight(bf.getSortWeight()+middleMap.get(key).getSortWeight());					
			}else{
				tagMap.put(key, middleMap.get(key));
			}
		}
		return tagMap;
	}

	@SuppressWarnings("unchecked")
	public List<GoodsForm> getEmailRecommendGoodIdsByAreaId(Long areaId, int days){
		List<GoodsForm> listGoodsForm = (List<GoodsForm>) memcacheService.get("EMAIL_RECOMMEND_" + areaId);
		if (listGoodsForm != null) {
			return listGoodsForm;
		}
		String pointInTime = DateUtils.getTimeBeforeORAfter(-days, "yyyy-MM-dd 00:00:00");
		//一级品类---美食，商品所在地市，一周内销量最好的前三个商品
		List<Map<String, Object>> lstGoodsIdsOfMeishi = goodsDao.getGoodsIdsByFirstCatIds("10100", areaId);
		Set<Long> goodsIdsOfMeishiSet = new HashSet<Long>();
		for (Map<String, Object> goodsIdMap : lstGoodsIdsOfMeishi) {
			goodsIdsOfMeishiSet.add((Long) goodsIdMap.get("goodid"));
		}
		List<Map<String, Object>> lstGoodsIdsBestMs = goodsDao.getGoodIdsOfBestSellingWithinaPeriodOfTime(StringUtils
				.join(goodsIdsOfMeishiSet, ","), pointInTime, 3);
		
		//一级品类---休闲娱乐，商品所在地市，一周内销量最好的前两个商品
		List<Map<String, Object>> lstGoodsIdsOfEntertainment = goodsDao.getGoodsIdsByFirstCatIds("10200", areaId);
		Set<Long> goodsIdsOfEntertainmentSet = new HashSet<Long>();
		for (Map<String, Object> goodsIdMap : lstGoodsIdsOfEntertainment) {
			goodsIdsOfEntertainmentSet.add((Long) goodsIdMap.get("goodid"));
		}
		List<Map<String, Object>> lstGoodsIdsBestEnter = goodsDao.getGoodIdsOfBestSellingWithinaPeriodOfTime(StringUtils
				.join(goodsIdsOfEntertainmentSet, ","), pointInTime, 2);
		
		//一级品类---酒店品类，商品所在地市，一周内销量最好的前一个商品
		List<Map<String, Object>> lstGoodsIdsOfHotel = goodsDao.getGoodsIdsByFirstCatIds("10500", areaId);
		Set<Long> goodsIdsOfHotelSet = new HashSet<Long>();
		for (Map<String, Object> goodsIdMap : lstGoodsIdsOfHotel) {
			goodsIdsOfHotelSet.add((Long) goodsIdMap.get("goodid"));
		}
		List<Map<String, Object>> lstGoodsIdsBestHotel = goodsDao.getGoodIdsOfBestSellingWithinaPeriodOfTime(StringUtils
				.join(goodsIdsOfHotelSet, ","), pointInTime, 1);
		
		List<Long> lstGoodsId = new ArrayList<Long>();
		for(Map<String, Object> tmp : lstGoodsIdsBestMs){
			lstGoodsId.add((Long) tmp.get("goodsid"));
		}
		for(Map<String, Object> tmp : lstGoodsIdsBestEnter){
			lstGoodsId.add((Long) tmp.get("goodsid"));
		}
		for(Map<String, Object> tmp : lstGoodsIdsBestHotel){
			lstGoodsId.add((Long) tmp.get("goodsid"));
		}
		listGoodsForm = this.getGoodsFormByChildId(lstGoodsId);
		for (GoodsForm form : listGoodsForm) {
			Map<String,String> merchantMap = goodsDao.getMerchantByGoodId(form.getGoodsId());
			form.setMerchantId(Long.parseLong(merchantMap.get("id").toString()));
		}
		memcacheService.set("EMAIL_RECOMMEND_" + areaId, listGoodsForm, 60*60 * 4);
		return listGoodsForm;
	}
	@Override
	public List<Map<String, Object>> getMiaoShaInfoByGoodsIds(Long... miaoshaid) {
		return goodsDao.getMiaoshaInfoByGoodsIds(miaoshaid);
		
	}
	
	@Override
	public List<GoodsForm> queryGoodsByIds(Pager pager, List<Long> goodsIdList, String scope, String sort, boolean cashOnly) {
		return goodsDao.queryGoodsByIds(pager, goodsIdList, scope, sort, cashOnly);
	}

	@Override
	public List<Map<String, Object>> getHuodongGoodsId(Long goodsId) {
		return goodsDao.getHuodongGoodsId(goodsId);
	}

	public ShopsBaoService getShopsBaoService() {
		return shopsBaoService;
	}

	public void setShopsBaoService(ShopsBaoService shopsBaoService) {
		this.shopsBaoService = shopsBaoService;
	}
	
	@SuppressWarnings("unchecked")
	public List<Long> getBranchIdByGoodsId(Long goodId){
		List<Long> resList = (List<Long>)memcacheService.get("GOODS_BRANCH_IDLIST_"+goodId);
		if(null == resList || resList.size() == 0){
			List<Map<String,Object>> list = goodsDao.getBranchIdByGoodsId(goodId);
			resList = new ArrayList<Long>();
			if(null != list && list.size() > 0){
				for(Map<String,Object> map : list){
					Long branhId = Long.parseLong(map.get("merchantid").toString());
					resList.add(branhId);
				}
			}
			if(null != resList && resList.size() > 0){
				memcacheService.set("GOODS_BRANCH_IDLIST_"+goodId, resList, 60*60*24*7);
			}
		}
		return resList;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<GoodsForm> getTopGoodsWithFlagShip(List<Long> merchantIdList) {
		
		List topGoodlist = goodsDao.getTopGoodsWithFlagShip(merchantIdList);
		
		List<GoodsForm> rsForm = getGoodsFormList(topGoodlist);
		
		return rsForm;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public List<GoodsForm> getMaxSaleCountWithFlagShip(List<Long> merchantIdList) {
		
		List salelist = goodsDao.getMaxSaleCountWithFlagShip(merchantIdList);
		
		List<GoodsForm> salelistform = getGoodsFormList(salelist);
		
		return salelistform;
	}
	
	@SuppressWarnings("rawtypes")
	public List<GoodsForm> getGoodsFormList(List topGoodlist){
		
		if(topGoodlist == null || topGoodlist.size() == 0)
			return null;
		
		List<GoodsForm> listform = new LinkedList<GoodsForm>();
		
		for(Object obj:topGoodlist){
			Map map =  (Map) obj;
			
			Long brandId = (Long) map.get("merchantid");
			Long goodsId = (Long) map.get("goodsId");
			String goodsName = (String) map.get("goodsname");
			String city = (String)map.get("city");
			BigDecimal sourcePrice  = (BigDecimal) map.get("sourcePrice");
			BigDecimal currentPrice = (BigDecimal)map.get("currentPrice");
			BigDecimal payPrice    = (BigDecimal)map.get("payPrice");
			BigDecimal offerPrice  = (BigDecimal)map.get("offerPrice");
			BigDecimal rebatePrice = (BigDecimal)map.get("rebatePrice");
			BigDecimal dividePrice = (BigDecimal)map.get("dividePrice");
			Date endTime = (Date) map.get("endTime");
			Date startTime = (Date) map.get("startTime");
			int isavaliable = (Integer) map.get("isavaliable");
			String logo1 = (String) map.get("logo1");
			String logo2 = (String) map.get("logo2");
			String logo3 = (String) map.get("logo3");
			String logo4 = (String) map.get("logo4");
			String goodsTitle = (String) map.get("goods_title");
			Float discount = (Float) map.get("discount");
			
			GoodsForm goods = new GoodsForm();
			
			goods.setMerchantId(brandId);
			goods.setGoodsId(goodsId);
			goods.setGoodsname(goodsName);
			goods.setCity(city);
			goods.setSourcePrice(Double.valueOf(String.valueOf(sourcePrice)));
			goods.setCurrentPrice(Double.valueOf(String.valueOf(currentPrice)));
			goods.setPayPrice(Double.valueOf(String.valueOf(payPrice)));
			goods.setOfferPrice(Double.valueOf(String.valueOf(offerPrice)));
			goods.setRebatePrice(Double.valueOf(String.valueOf(rebatePrice)));
			goods.setDividePrice(Double.valueOf(String.valueOf(dividePrice)));
			goods.setEndTime(endTime);
			goods.setStartTime(startTime);
			goods.setIsavaliable(isavaliable);
			goods.setGoodsTitle(goodsTitle);
			goods.setLogo1(logo1);
			goods.setLogo2(logo2);
			goods.setLogo3(logo3);
			goods.setLogo4(logo4);
			goods.setDiscount(discount);

			listform.add(goods);
		}
		return listform;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Long getMerchantIdByGoodId(Long goodId) {
		
		List li = goodsDao.getMerchantIdByGoodId(goodId);
		
		if(li == null || li.size() == 0)
			return 0L;
		
		Map map = (Map) li.get(0);
		Long merid = (Long) map.get("merchantid");
		
		return merid==null?0:merid;
	}
}