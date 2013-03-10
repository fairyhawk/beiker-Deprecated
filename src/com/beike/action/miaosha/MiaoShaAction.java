package com.beike.action.miaosha;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.beike.action.user.BaseUserAction;
import com.beike.core.service.trx.limit.PayLimitService;
import com.beike.entity.catlog.RegionCatlog;
import com.beike.entity.goods.GoodKindly;
import com.beike.entity.goods.Goods;
import com.beike.entity.miaosha.MiaoSha;
import com.beike.entity.user.User;
import com.beike.form.GoodsForm;
import com.beike.form.MerchantForm;
import com.beike.service.goods.GoodsService;
import com.beike.service.goods.ad.ADGoodsService;
import com.beike.service.lucene.recommend.LuceneRecommendService;
import com.beike.service.merchant.ShopsBaoService;
import com.beike.service.miaosha.MiaoShaService;
import com.beike.service.unionpage.UnionPageService;
import com.beike.util.BeanUtils;
import com.beike.util.Constant;
import com.beike.util.DateUtils;
import com.beike.util.PinyinUtil;
import com.beike.util.PropertyUtil;
import com.beike.util.StringUtils;
import com.beike.util.WebUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.ipparser.CityUtils;
import com.beike.util.json.JSONException;
import com.beike.util.json.JsonUtil;
import com.beike.util.singletonlogin.SingletonLoginUtils;

/**
 * <p>
 * Title:秒杀action
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
 */
@Controller
public class MiaoShaAction extends BaseUserAction {

	private static Log log = LogFactory.getLog(MiaoShaAction.class);
	@Autowired
	private GoodsService goodsService;

	@Autowired
	private PayLimitService payLimitService;

	@Autowired
	private ADGoodsService adGoodsService;
	
	@Autowired
	private LuceneRecommendService luceneRecommendService;

	@Autowired
	private UnionPageService unionPageService;
	
	@Autowired
	private ShopsBaoService shopsBaoService;
	
	@Autowired
	private MiaoShaService miaoShaService;
	// 获取当前城市ID
	private final MemCacheService memCacheService = MemCacheServiceImpl.getInstance();
	
	private final PropertyUtil propertyUtil = PropertyUtil
			.getInstance("project");

	private static String REGION_CATLOG = "BASE_REGION_CATLOG";

	private static String PROPERTY_CATLOG = "PROPERTY_CATLOG_NEW";

	public GoodsService getGoodsService() {
		return goodsService;
	}

	public void setGoodsService(GoodsService goodsService) {
		this.goodsService = goodsService;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping("/miaosha/showMiaoShaDetail.do")
	public Object showMiaoSha(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			// 设置urlcookie
			super.setCookieUrl(request, response);
			String miaoshaId = request.getParameter("miaoshaId");
			if (miaoshaId == null || "".equals(miaoshaId)) {
				log.info("goodId 没找到对应的秒杀!~");
				request.setAttribute("ERRMSG", "没有找到相关秒杀!");
				return new ModelAndView("redirect:../404.html");
			}
			MiaoSha miaosha = miaoShaService.getMiaoShaById(Long.parseLong(miaoshaId));
			String goodId = null;
			if(miaosha!=null){
				goodId = String.valueOf(miaosha.getGoodsId());
			}
			if (goodId == null || "".equals(goodId)) {
				log.info("goodId 没找到对应的商品!~");
				request.setAttribute("ERRMSG", "没有找到相关商品!");
				return new ModelAndView("redirect:../404.html");
			}

			//GoodsCatlog goodsCatlog = null;
			Goods goods = null;
			List<GoodKindly> kindlist = null;
			try {
				goods = goodsService.findById(Long.parseLong(goodId));
				kindlist = goodsService.getGoodKindlyById(Long.parseLong(goodId));
			} catch (Exception e) {
				log.info(e);
				e.printStackTrace();
				request.setAttribute("ERRMSG", "没有找到相关商品!");
				return new ModelAndView("redirect:../404.html");
			}
			if (goods == null) {
				request.setAttribute("ERRMSG", "没有找到相关商品!");
				return new ModelAndView("redirect:../404.html");
			}
			if(miaosha.getMsStatus()==1){
				miaosha.setMsShortTitle(miaosha.getMsShortTitle().replace("下个秒杀：", "进行中的秒杀:"));
			}
			goods.setGoodsTitle(miaosha.getMsShortTitle());
			goods.setCurrentPrice(miaosha.getMsPayPrice());
			goods.setGoodsname(miaosha.getMsTitle());
			goods.setGoodsSingleCount(miaosha.getMsSingleCount());
			goods.setMaxcount(miaosha.getMsMaxCount());
			goods.setStartTime(miaosha.getMsStartTime());
			goods.setEndTime(miaosha.getMsEndTime());
			goods.setVirtualCount(miaosha.getMsVirtualCount());
			goods.setDividePrice(miaosha.getMsSettlePrice());

			MerchantForm merchantForm = shopsBaoService.getMerchantDetailByGoodsId(Long.parseLong(goodId));
			request.setAttribute("merchantForm", merchantForm);
	
			String staticurl = propertyUtil.getProperty("STATIC_URL");
			// 汉语转换成拼音
			String city = PinyinUtil.hanziToPinyin(goods.getCity(), "");
			Map<String, Long> mapCity = (Map<String, Long>) memCacheService.get("CITY_CATLOG");
			Long cityid = null;
			if (mapCity == null) {
				mapCity = BeanUtils.getCity(request, "regionCatlogDao");
				memCacheService.set("CITY_CATLOG", mapCity);
			}
			if (mapCity != null) {
				cityid = mapCity.get(city.trim());
			}
			
			request.setAttribute("NOWCITY", city);
			response.addCookie(WebUtils.cookie(CityUtils.CITY_COOKIENAME, city,
					CityUtils.validy));
			String refer = request.getRequestURL().toString();
			// 商品所在城市访问路径前缀
			String xcity = "http://" + city;
			if (!refer.startsWith(xcity)) {
				if ("true".equals(staticurl)) {
					StringBuilder sb = new StringBuilder();
					sb.append("redirect:http://" + city + ".qianpin.com/miaosha/"
							+ miaosha.getMsId() + ".html");
					String params = WebUtils.parseQueryString(request);
					if (!org.apache.commons.lang.StringUtils.isBlank(params)) {
						sb.append("?");
						String str = WebUtils.replaceParams(params, "miaoshaId");
						if (str != null) {
							sb.append(str);
						}
					}

					return new ModelAndView(sb.toString());
				} else {
					StringBuilder sb = new StringBuilder();
					sb.append("redirect:http://" + city
							+ ".qianpin.com/miaosha/showMiaoShaDetail.do");
					String params = WebUtils.parseQueryString(request);
					if (!org.apache.commons.lang.StringUtils.isBlank(params)) {
						sb.append("?");
						String str = WebUtils.replaceParams(params, "miaoshaId");
						if (str != null) {
							sb.append(str);
						}
					}
					return new ModelAndView(sb.toString());
				}
			}
			
			/**
			 * ********将一些商品信息放入memcache，并和uuid配对，在购买页面中取 start add by
			 * wenhua.cheng*******
			 */
/*			StringBuffer goodsDetailInfo = new StringBuffer();
			// 从memCahe里取商品信息
			try {
				goodsDetailInfo.append(URLEncoder.encode(goods.getGoodsname(),
						"utf-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}// 商品名字--1
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getGoodsId());// 商品ID---2
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getSourcePrice());// 商品原价格--3
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getPayPrice());// 商品购买价格--4
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getRebatePrice());// 商品返现价格--5
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getDividePrice());// 商品分成价格--6

			// TODO加入过期时间等
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getGuestId()); // guestID--7
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getOrderLoseAbsDate());// 订单过期时间段--8
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getOrderLoseDate());// 订单过期时间点--9

			String goodsDetailKey = StringUtils.createUUID();
			memCacheService.set(goodsDetailKey, goodsDetailInfo.toString(),
					18000);
			request.setAttribute("goodsDetailKey", goodsDetailKey);*/

			/** ********将一些商品信息放入memcache，并和uuid配对，在购买页面中取 end******* */
			//goodsCatlog = goodsService.searchGoodsRegionById(goods.getGoodsId());
			request.setAttribute("goodDetail", goods);
			//request.setAttribute("goodsCatlog", goodsCatlog);
			request.setAttribute("goodKindly", kindlist);
			
			// 包含页面的url
			String detailUrl = goodsService.getGoodDetailIncliudeUrl(Long
					.parseLong(goodId));

			// 获得秒杀的实际销售量
			int salescount = miaosha.getMsSaleCount();
			/**
			 * 此处是用于显示的商品数量
			 * 
			 * Add by zx.liu
			 */
			int viewSalesCount = goods.getVirtualCount();
			viewSalesCount = salescount + goods.getVirtualCount();
			// 商品的实际销售量
			// request.setAttribute("SALES_COUNT", salescount);
			// 用于页面显示的商品数量
			request.setAttribute("SALES_COUNT", viewSalesCount);
			request.setAttribute("REAL_SALES_COUNT", salescount);
			request.setAttribute("UPLOAD_IMAGES_URL",Constant.UPLOAD_IMAGES_URL);
			request.setAttribute("NEED_VIRTUAL", miaosha.getNeedVirtual());

			// 判断此文件是否存在 假如不存在页面不包含
			String detailFile = request.getRealPath("") + "/jsp/goods_detail/"
					+ detailUrl;
			File file = new File(detailFile);
			if (!file.exists()) {
				detailUrl = null;
			}
			request.setAttribute("detailUrl", detailUrl);
			BigDecimal big = new BigDecimal(goods.getSourcePrice()
					- goods.getCurrentPrice());
			big = big.setScale(1, BigDecimal.ROUND_HALF_UP);
			request.setAttribute("offerPrice", big.floatValue());

			// 判断用户是否登录
			User user = SingletonLoginUtils.getMemcacheUser(request);
			// 最大可购买数量
			Long canCount = (long) goods.getGoodsSingleCount();
			Long ssCount = goods.getMaxcount() - new Long(salescount);// 秒杀上限量与已购买量差
			if (canCount != 0) {
				if (user != null) {
					//TODO:秒杀数量检查
					canCount = payLimitService.allowPayCount(canCount, user
							.getId(), Long.parseLong(goodId), miaosha.getMsId());
				}
				if (canCount > ssCount) {
					canCount = ssCount;
				}
			} else {
				canCount = ssCount;
			}
			request.setAttribute("maxBuyCount", canCount);

			// 商圈、属性分类
			Map<String, Map<Long, List<RegionCatlog>>> regionMap = (Map<String, Map<Long, List<RegionCatlog>>>) memCacheService
					.get(REGION_CATLOG);

			Map<Long, Map<Long, List<RegionCatlog>>> propertCatlogMap = (Map<Long, Map<Long, List<RegionCatlog>>>) memCacheService.get(PROPERTY_CATLOG);

			Map<Long, List<RegionCatlog>> property_catlog = null;

			// 假如memcache里没有就从数据库里查
			if (regionMap == null) {
				regionMap = BeanUtils.getCityCatlog(request, "regionCatlogDao");
				memCacheService.set(REGION_CATLOG, regionMap);
			}
			
			if(propertCatlogMap == null){
				propertCatlogMap = BeanUtils.getCatlog(request,"propertyCatlogDao");
				property_catlog  = propertCatlogMap.get(cityid);
				memCacheService.set(PROPERTY_CATLOG, propertCatlogMap,60*60*24*360);
			}else{
				property_catlog  = propertCatlogMap.get(cityid);
			}	
			// 当前城市商圈
			Map<Long, List<RegionCatlog>> curRegionMap = regionMap.get(city);
			Map<Long, RegionCatlog> regionKeyMap = new HashMap<Long, RegionCatlog>();
			if (curRegionMap != null && !curRegionMap.isEmpty()) {
				for (Long regionKey : curRegionMap.keySet()) {
					List<RegionCatlog> lstRegion = curRegionMap.get(regionKey);
					if (lstRegion != null && lstRegion.size() > 0) {
						for (RegionCatlog region : lstRegion) {
							regionKeyMap.put(region.getCatlogid(), region);
						}
					}
				}
			}
			// 商品分类
			Map<Long, RegionCatlog> tagKeyMap = new HashMap<Long, RegionCatlog>();
			if (property_catlog != null && !property_catlog.isEmpty()) {
				for (Long tagKey : property_catlog.keySet()) {
					List<RegionCatlog> lstTag = property_catlog.get(tagKey);
					if (lstTag != null && lstTag.size() > 0) {
						for (RegionCatlog region : lstTag) {
							tagKeyMap.put(region.getCatlogid(), region);
						}
					}
				}
			}

			// 一级商圈
			String regionName = "";
			List<Map<String, Object>> lstFirstRegion = goodsService
					.getGoodsFirstRegionById(Long.parseLong(goodId));
			if (lstFirstRegion != null && lstFirstRegion.size() == 1) {
				RegionCatlog tmpregion = regionKeyMap.get(lstFirstRegion.get(0)
						.get("regionid"));
				if (tmpregion != null) {
					regionName = tmpregion.getCatlogName();
				}
			}
			request.setAttribute("firstRegionName", regionName);

			// 0220推荐
			int category_id = 0;
			Long goodsRegionid = 0L;
			Long goodsTagid = 0L;
			// 推荐商品
			List<Map<String, Object>> lstRegionIds = goodsService
					.getGoodsRegionIds(Long.parseLong(goodId));
			// 相关分类
			List<Object[]> lstGoodsTag = new LinkedList<Object[]>();
			// 相关商圈
			List<Object[]> lstGoodsRegion = new LinkedList<Object[]>();
			// 用于处理二级地域重复
			int iNextRegionCount = 0;
			String tmpGoodsRegionId = "";
			
			if (lstRegionIds != null && lstRegionIds.size() > 0) {
				Map<String, Object> mapRegionIds = lstRegionIds.get(0);

				Set<String> noRepeatSet = new HashSet<String>();
				for (int i = 0; i < lstRegionIds.size(); i++) {
					Map<String, Object> catlog = lstRegionIds.get(i);
					Long tagId = (Long) catlog.get("tagid");
					Long tagNextId = (Long) catlog.get("tagextid");
					Long regionId = (Long) catlog.get("regionid");
					Long regionextId = (Long) catlog.get("regionextid");
					if (i == 0) {
						category_id = tagId.intValue();
					}
					// 分类
					RegionCatlog tagA = tagKeyMap.get(tagId);
					if (!noRepeatSet.contains(String.valueOf(tagId))) {
						noRepeatSet.add(String.valueOf(tagId));
						if (tagA != null) {
							lstGoodsTag.add(new String[] {
									tagA.getRegion_enname(), "",
									tagA.getCatlogName() });
						}
					}
					if (!noRepeatSet.contains(String.valueOf(String
							.valueOf(tagId)
							+ "|" + tagNextId))) {
						noRepeatSet.add(String.valueOf(String.valueOf(tagId)
								+ "|" + tagNextId));

						RegionCatlog tagB = tagKeyMap.get(tagNextId);

						if (tagA != null && tagB != null) {
							lstGoodsTag.add(new String[] {
									tagA.getRegion_enname(),
									tagB.getRegion_enname(),
									tagB.getCatlogName() });
						}
					}

					// 商圈
					RegionCatlog regionA = regionKeyMap.get(regionId);
					if (!noRepeatSet.contains(String.valueOf(regionId))) {
						noRepeatSet.add(String.valueOf(regionId));
						if (regionA != null) {
							lstGoodsRegion.add(new String[] {
									regionA.getRegion_enname(), "",
									regionA.getCatlogName() });
						}
					}
					if (!noRepeatSet.contains(String.valueOf(String
							.valueOf(regionId)
							+ "|" + regionextId))) {
						noRepeatSet.add(String.valueOf(String.valueOf(regionId)
								+ "|" + regionextId));

						RegionCatlog regionB = regionKeyMap.get(regionextId);
						if (regionA != null && regionB != null) {
							lstGoodsRegion.add(new String[] {
									regionA.getRegion_enname(),
									regionB.getRegion_enname(),
									regionB.getCatlogName() });
						}
					}

					// 二级商圈数超过两个，不使用二级商圈做推荐算法
					if (iNextRegionCount <= 1
							&& tmpGoodsRegionId.indexOf(String
									.valueOf(regionextId)) < 0) {
						if (i == 0) {
							tmpGoodsRegionId = String.valueOf(regionextId);
						} else {
							tmpGoodsRegionId = tmpGoodsRegionId + ","
									+ regionextId;
						}

						iNextRegionCount++;
						if (iNextRegionCount > 2) {
							tmpGoodsRegionId = null;
						}
					}
				}

				request.setAttribute("lstGoodsTag", lstGoodsTag);
				request.setAttribute("lstGoodsRegion", lstGoodsRegion);

				goodsRegionid = (Long) mapRegionIds.get("regionid");
				goodsTagid = (Long) mapRegionIds.get("tagid");
			}

			List<Long> temp = new ArrayList<Long>();
			temp.add(goods.getGoodsId());

			List<Long> sameCategoryGoodsid = adGoodsService
					.getSameCategoryGoods(Integer.parseInt(cityid.toString()),
							category_id, "1", StringUtils.arrayToString(temp
									.toArray(), ","), 8);
			sameCategoryGoodsid = getRandomGoodsId(sameCategoryGoodsid, 4);
			temp.addAll(sameCategoryGoodsid);

			List<Long> foodCategoryGoodsid = adGoodsService
					.getSameCategoryGoods(Integer.parseInt(cityid.toString()),
							category_id, "2", StringUtils.arrayToString(temp
									.toArray(), ","), 8);
			foodCategoryGoodsid = getRandomGoodsId(foodCategoryGoodsid, 4);
			temp.addAll(foodCategoryGoodsid);
			// 低价商品算法修改(使用缓存)
			List<Long> lowpriceGoodsid = goodsService.getPartLowestGoods(
					cityid, 1L, StringUtils.arrayToString(temp.toArray(), ","));
			temp.addAll(lowpriceGoodsid);
			// 同类推荐
			request.setAttribute("sameCategoryGoods", goodsService
					.getGoodsFormByChildId(sameCategoryGoodsid));
			// 美食热销推荐
			request.setAttribute("foodCategoryGoods", goodsService
					.getGoodsFormByChildId(foodCategoryGoodsid));
			// 低价推荐
			request.setAttribute("lowpriceGoods", goodsService
					.getGoodsFormByChildId(lowpriceGoodsid));

			// 周边人气，暂时沿用原推荐商品算法
			List<Long> lstTuijian = goodsService.getSaleWithGoodsIds(
					goodsRegionid, tmpGoodsRegionId, goodsTagid, cityid, 20L,
					"");
			log.debug("lstTuijian===" + lstTuijian);

			// 剔除其他推荐商品ID，查询推荐商品
			if (lstTuijian != null) {
				if (temp != null && temp.size() > 0) {
					lstTuijian.removeAll(temp);
				}
				if (lstTuijian.size() > 4) {
					lstTuijian = lstTuijian.subList(0, 4);
				}
				List<GoodsForm> lstTuijianGoodsForm = goodsService
						.getGoodsFormByChildId(lstTuijian);
				request
						.setAttribute("lstTuijianGoodsForm",
								lstTuijianGoodsForm);
			}

			// 查询商品数量最多的一级商圈
			List<RegionCatlog> lstTopRegion = null;
			try{
				lstTopRegion = (List<RegionCatlog>)memCacheService.get("TopGoods_Region" + cityid);
				if(lstTopRegion==null){
					List<Long> lstTopRegionId = goodsService.getTopRegionCatlogId(
							cityid, 6L);
					 lstTopRegion = new ArrayList<RegionCatlog>();
					for (Long topRegion : lstTopRegionId) {
						lstTopRegion.add(regionKeyMap.get(topRegion));
					}
				}
				memCacheService.set("TopGoods_Region" + cityid, lstTopRegion, 60*60*2);
				request.setAttribute("lstTopRegion", lstTopRegion);
			}catch(Exception ex){
				List<Long> lstTopRegionId = goodsService.getTopRegionCatlogId(
						cityid, 6L);
				lstTopRegion = new ArrayList<RegionCatlog>();
				for (Long topRegion : lstTopRegionId) {
					lstTopRegion.add(regionKeyMap.get(topRegion));
				}
				memCacheService.set("TopGoods_Region" + cityid, lstTopRegion, 60*60*2);
				request.setAttribute("lstTopRegion", lstTopRegion);
			}

			try{
				//关键词搜索
				String[] aryRecommendKeywords = luceneRecommendService.getRecommend(goods.getGoodsname(), 20);
				List<Map<String,String>> listRecommendMsg = unionPageService.getListMsgByKeyWords(aryRecommendKeywords,20);
				request.setAttribute("recommandKeyWord", listRecommendMsg);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			//秒杀进行时
			List<MiaoSha> lstCurMiaoSha = null;
			lstCurMiaoSha = (List<MiaoSha>)memCacheService.get("Current_Miaosha_" + cityid);
			if(lstCurMiaoSha==null || lstCurMiaoSha.size()==0){
				lstCurMiaoSha = miaoShaService.getMiaoShaListByAreaId(cityid, 4);
				memCacheService.set("Current_Miaosha_" + cityid, lstCurMiaoSha, 60*10);
			}
			if(lstCurMiaoSha!=null && lstCurMiaoSha.size()>0){
				for(int i=0;i<lstCurMiaoSha.size();i++){
					if(lstCurMiaoSha.get(i).getMsId().compareTo(miaosha.getMsId())==0){
						lstCurMiaoSha.remove(i);
						break;
					}
				}
				if(lstCurMiaoSha.size()>3){
					lstCurMiaoSha.remove(3);
				}
			}
			request.setAttribute("lstCurMiaoSha", lstCurMiaoSha);
			
			if(miaosha.getMsStatus() == 1){
				//未开始
				if(miaosha.getMsStartTime().compareTo(new Timestamp(System.currentTimeMillis())) == 1){
					miaosha.setStartSeconds(DateUtils.countDifSeconds(new Timestamp(System.currentTimeMillis()),miaosha.getMsStartTime()));
					miaosha.setEndSeconds(DateUtils.countDifSeconds(miaosha.getMsEndTime(),new Timestamp(System.currentTimeMillis())));
					miaosha.setMsStatus(2);
				}else if(miaosha.getMsEndTime().compareTo(new Timestamp(System.currentTimeMillis())) == 1){
				//已开始，未结束
					miaosha.setStartSeconds(0l);
					miaosha.setEndSeconds(DateUtils.countDifSeconds(miaosha.getMsEndTime(),new Timestamp(System.currentTimeMillis())));
					miaosha.setMsStatus(1);
					
					if(miaosha.getMsSaleCount()>=miaosha.getMsMaxCount()){
						miaosha.setMsStatus(0);
					}else{
						miaosha.setMsStatus(1);
					}
				}else{
					miaosha.setStartSeconds(0l);
					miaosha.setEndSeconds(0l);
					miaosha.setMsStatus(0);
				}
			}else{
				miaosha.setStartSeconds(0l);
				miaosha.setEndSeconds(0l);
				miaosha.setMsStatus(0);
			}
			
			request.setAttribute("miaosha", miaosha);
			
			return "/miaosha/showMiaoShaDetail";
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

	@RequestMapping("/miaosha/getIndexMiaoSha.do")
	public void getIndexMiaoSha(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		String city = CityUtils.getCity(request, response);
		if (city == null || "".equals(city)) {
			city = "beijing";
		}
		// 获取当前城市ID
		Map<String, Long> mapCity = (Map<String, Long>) memCacheService.get("CITY_CATLOG");
		if (mapCity == null) {
			mapCity = BeanUtils.getCity(request, "regionCatlogDao");
			memCacheService.set("CITY_CATLOG", mapCity);
		}
		
		if (StringUtils.isEmpty(city)) {
			city = "beijing";
		}

		Long cityid = 0l;
		if (mapCity != null) {
			cityid = mapCity.get(city.trim());
		}
		
		Map<String,String> jsonMap = new HashMap<String,String>();
		jsonMap.put("showType", "0");
		
		List<MiaoSha> lstMiaoSha = null;
		List<Long> lstMiaoShaIds = miaoShaService.getIndexMiaoShaByCityId(cityid);
		if(lstMiaoShaIds!=null && lstMiaoShaIds.size()>0){
			lstMiaoSha = miaoShaService.getMiaoShaListByIds(lstMiaoShaIds);
			if(lstMiaoSha!=null && lstMiaoSha.size()>0){
				MiaoSha miaosha1 = lstMiaoSha.get(0);
				//开始显示
				if(miaosha1.getMsShowStartTime().compareTo(new Timestamp(System.currentTimeMillis())) <= 0){
					//未开始
					if(miaosha1.getMsStartTime().compareTo(new Timestamp(System.currentTimeMillis())) == 1){
						miaosha1.setStartSeconds(DateUtils.countDifSeconds(new Timestamp(System.currentTimeMillis()),miaosha1.getMsStartTime()));
						miaosha1.setEndSeconds(DateUtils.countDifSeconds(miaosha1.getMsEndTime(),new Timestamp(System.currentTimeMillis())));
						miaosha1.setMsStatus(2);
					}else if(miaosha1.getMsEndTime().compareTo(new Timestamp(System.currentTimeMillis())) == 1){
					//已开始，未结束
						miaosha1.setStartSeconds(0l);
						miaosha1.setEndSeconds(DateUtils.countDifSeconds(miaosha1.getMsEndTime(),new Timestamp(System.currentTimeMillis())));
						miaosha1.setMsStatus(1);
						if(miaosha1.getMsSaleCount()>=miaosha1.getMsMaxCount()){
							miaosha1.setMsStatus(0);
						}else{
							miaosha1.setMsStatus(1);
						}
					}else{
						miaosha1.setStartSeconds(0l);
						miaosha1.setEndSeconds(0l);
						miaosha1.setMsStatus(0);
					}
					MiaoSha miaosha2 = null;
					if(lstMiaoSha.size()>1){
						miaosha2 = lstMiaoSha.get(1);
					}
					jsonMap.put("showType", "1");
					jsonMap.put("miaoshaID", String.valueOf(miaosha1.getMsId()));
					jsonMap.put("miaoshaImg", miaosha1.getMsBanner());
					jsonMap.put("miaoshaStatus", String.valueOf(miaosha1.getMsStatus()));
					jsonMap.put("miaoshaTime", String.valueOf(miaosha1.getStartSeconds()));
					jsonMap.put("miaoshaTime2", String.valueOf(miaosha1.getEndSeconds()));
					if(miaosha2!=null){
						jsonMap.put("miaoshaNextTitle", String.valueOf(miaosha2.getMsShortTitle()));
						jsonMap.put("miaoshaNextID", String.valueOf(miaosha2.getMsId()));
					}else{
						jsonMap.put("miaoshaNextTitle", "");
						jsonMap.put("miaoshaNextID", "0");
					}
				}else{
					//未开始显示，显示首页对应位置碎片
					jsonMap.put("showType", "0");
				}
			}
		}
		response.setContentType("text/json; charset=UTF-8");
		response.setHeader("progma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		try {
			String text = JsonUtil.mapToJson(jsonMap);
			response.getWriter().print(text);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}
	/**
	 * 从商品ID集合中随机取固定数量商品ID
	 * 
	 * @param lstSource
	 * @param iCnt
	 * @return
	 */
	private List<Long> getRandomGoodsId(List<Long> lstSource, int iCnt) {
		if (lstSource != null && lstSource.size() > iCnt) {
			Random random = new Random(); // 随机数类
			StringBuffer bufIndex = new StringBuffer();
			int[] aryIndex = new int[iCnt];
			for (int i = 0; i < iCnt; i++) {
				// 取不重复的索引数
				int curI = random.nextInt(lstSource.size());
				while (bufIndex.indexOf(String.valueOf(curI)) >= 0) {
					curI = random.nextInt(lstSource.size());
				}
				bufIndex.append(curI);
				aryIndex[i] = curI;

			}
			// 排序
			Arrays.sort(aryIndex);
			List<Long> lstResult = new ArrayList<Long>();
			for (int i = 0; i < aryIndex.length; i++) {
				lstResult.add(lstSource.get(aryIndex[i]));
			}
			return lstResult;
		} else {
			return lstSource;
		}
	}
}
