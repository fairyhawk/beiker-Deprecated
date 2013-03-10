/**  
* @Title: MergeWithBrandsAction.java
* @Package com.beike.action.goods
* @Description: 品牌专区聚合
* @author Qingcun Guo guoqingcun@qianpin.com  
* @date Aug 29, 2012 10:40:34 AM
* @version V1.0  
*/
package com.beike.action.brand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.beike.action.user.BaseUserAction;
import com.beike.entity.brand.MergeWithBrands;
import com.beike.entity.catlog.RegionCatlog;
import com.beike.entity.goods.Goods;
import com.beike.form.GoodsForm;
import com.beike.form.MerchantForm;
import com.beike.service.brand.MergeWithBrandsService;
import com.beike.service.goods.GoodsService;
import com.beike.service.merchant.ShopsBaoService;
import com.beike.util.BeanUtils;
import com.beike.util.Constant;
import com.beike.util.PinyinUtil;
import com.beike.util.PropertyUtil;
import com.beike.util.WebUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.ipparser.CityUtils;

/**
 * @ClassName: MergeWithBrandsAction
 * @Description: 品牌专区聚合
 * @author Qingcun Guo guoqingcun@qianpin.com
 * @date Aug 29, 2012 10:40:34 AM
 *
 */
@Controller
public class MergeWithBrandsAction extends BaseUserAction {

	private static String CITY_CATLOG = "CITY_CATLOG";
	private static Log log = LogFactory.getLog(MergeWithBrandsAction.class);
	
	private final PropertyUtil propertyUtil = PropertyUtil.getInstance("project");
	@Autowired
	private GoodsService goodsService;//商品
	@Autowired
	private MergeWithBrandsService mbService;//品牌聚合
	@Autowired
	private ShopsBaoService shopsBaoService;//商铺宝
	
	private String MB_KEY = "mb_";//品牌聚合，内存缓存key
	
	public void setGoodsService(GoodsService goodsService) {
		this.goodsService = goodsService;
	}
	public void setShopsBaoService(ShopsBaoService shopsBaoService) {
		this.shopsBaoService = shopsBaoService;
	}

	public void setMbService(MergeWithBrandsService mbService) {
		this.mbService = mbService;
	}

	/**
	 * 
	* @Title: getMergeWithBrands
	* @Description: 获取品牌聚合的所有数据
	* @param @param modelMap
	* @param @param request
	* @param @param response
	* @param @return    设定文件
	* @return Object    返回类型
	* @throws
	 */
	@RequestMapping("/brand/getMergeWithBrands.do")
	public Object getMergeWithBrands(ModelMap modelMap,
			HttpServletRequest request, HttpServletResponse response) {
		//缓存实例
		MemCacheService memCacheService = MemCacheServiceImpl.getInstance();
		
		//获取查询参数
		String goodsid = request.getParameter("goodsId");//商品标识
		String serialnum = request.getParameter("serialnum");//序号
		String tpbbk = request.getParameter("tpbbk");//同品比比看
		String zbqgg = request.getParameter("zbqgg");//周边去逛逛
		String review = request.getParameter("review");//预览模式
		
		//是否为后台预览模式
		boolean isView = false;
		//如果不满足以下条件 返回错误页面
		if(StringUtils.isBlank(goodsid)){
			request.setAttribute("ERRMSG", "没有找到相关商品!");
			return new ModelAndView("redirect:../404.html");
		}
		if(StringUtils.isNotBlank(review)&&"true".equalsIgnoreCase(review)){
			if(StringUtils.isBlank(serialnum)||StringUtils.isBlank(tpbbk)||StringUtils.isBlank(zbqgg)){
				request.setAttribute("ERRMSG", "没有找到相关商品!");
				return new ModelAndView("redirect:../404.html");
			}
			isView = true;
		}
		
		//主商品
		Long gsid = Long.parseLong(goodsid);
		Goods goods = null;
		try{
			goods = goodsService.findById(gsid);//获取商品数据
		}catch(Exception e){
			log.error("获取品牌聚合主商品时出现异常:原因", e);
			request.setAttribute("ERRMSG", "没有找到相关商品!");
			return new ModelAndView("redirect:../404.html");
		}
		if(null == goods){
			request.setAttribute("ERRMSG", "没有找到相关商品!");
			return new ModelAndView("redirect:../404.html");
		}
		
		
		// 汉语转换成拼音
		String city = PinyinUtil.hanziToPinyin(goods.getCity(), "");
		
		// 获取当前城市ID
		Map<String, Long> mapCity = (Map<String, Long>) memCacheService.get(CITY_CATLOG);
		
		if (mapCity == null) {
			mapCity = BeanUtils.getCity(request, "regionCatlogDao");
			memCacheService.set(CITY_CATLOG, mapCity);
		}

		request.setAttribute("NOWCITY", city);
		response.addCookie(WebUtils.cookie(CityUtils.CITY_COOKIENAME, city,CityUtils.validy));
		
		String xcity = "http://" + city;
		String refer = request.getRequestURL().toString();
		String staticurl = propertyUtil.getProperty("STATIC_URL");
		if (!refer.startsWith(xcity)) {
			if ("true".equals(staticurl)) {
				StringBuilder sb = new StringBuilder();
				sb.append("redirect:http://" + city + ".qianpin.com/unionpage/");
				sb.append(gsid);
				sb.append(".html");
				String params = WebUtils.parseQueryString(request);
				if (!org.apache.commons.lang.StringUtils.isBlank(params)) {
					sb.append("?");
					sb.append(WebUtils.replaceParams(params, "goodsId"));
				}

				return new ModelAndView(sb.toString());
			} else {
				StringBuilder sb = new StringBuilder();
				sb.append("redirect:http://" + city + ".qianpin.com/brand/getMergeWithBrands.do");
				String params = WebUtils.parseQueryString(request);
				if (!org.apache.commons.lang.StringUtils.isBlank(params)) {
					sb.append("?");
					sb.append(params);
				}
				return new ModelAndView(sb.toString());
			}

		}
		
		//从缓存里取
		boolean isCache = false;
		boolean isReCache = false;//是否重新缓存
		String MB_KEY = "MB_KEY_" + goodsid;//品牌聚合缓存key 同品
		String MB_KEY_TPBBK = MB_KEY + "_TPBBK";//品牌聚合缓存key 同品
		String MB_KEY_ZBQGG = MB_KEY + "_ZBQGG";//品牌聚合缓存key 周边
        
		MergeWithBrands mb = (MergeWithBrands)memCacheService.get(MB_KEY);
		if(null != mb){
			isCache = true;
		}
		//同品比比看
		List<GoodsForm> tpbbkList = (List<GoodsForm>)memCacheService.get(MB_KEY_TPBBK);
		if(null == tpbbkList){
			try{
				if(isView){
					//预览模式
					tpbbkList = goodsService.getGoodsFormByChildId(idToLong(tpbbk.split(",")));
				}else{
//					if(null == mb){
						mb = mbService.getMergeWithBrands(gsid);//获取品牌聚合数据
						isReCache = true;
//					}
					if(null != mb){
						tpbbkList = goodsService.getGoodsFormByChildId(idToLong(mb.getTpbbk().split(",")));
						memCacheService.set(MB_KEY_TPBBK,tpbbkList,60*60);
					}
				}
			}catch(Exception e){
				request.setAttribute("ERRMSG", "没有找到相关商品!");
				return new ModelAndView("redirect:../404.html");
			}
		}
		//周边去逛逛
		List<GoodsForm> zbqggList = (List<GoodsForm>)memCacheService.get(MB_KEY_ZBQGG);
		if(null == zbqggList){
			try{
				if(isView){
					//预览模式
					zbqggList = goodsService.getGoodsFormByChildId(idToLong(zbqgg.split(",")));
				}else{
//					if(null == mb){
						mb = mbService.getMergeWithBrands(gsid);//获取品牌聚合数据
						isReCache = true;
//					}
					if(null != mb){
						zbqggList = goodsService.getGoodsFormByChildId(idToLong(mb.getZbqgg().split(",")));
						memCacheService.set(MB_KEY_ZBQGG,zbqggList,60*60);
					}
				}
            }catch(Exception e){
            	request.setAttribute("ERRMSG", "没有找到相关商品!");
    			return new ModelAndView("redirect:../404.html");
			} 
		}
		
		if(null == mb){
			mb = new MergeWithBrands();
			isReCache = true;
		}
		if(null == mb.getSerialnum()){
			mb.setSerialnum(serialnum);
			isReCache = true;
		}
		//记录商品销售数量
		if(null == mb.getViewSalesCount()){
			//商品的实际销售量
			String salescount = goodsService.salesCount(gsid);
			if (StringUtils.isBlank(salescount)) {
				salescount = "0";
			}
			//商品销售数量
			int viewSalesCount = goods.getVirtualCount();
			if (null != salescount && salescount.trim().length() > 0) {
				viewSalesCount = Integer.parseInt(salescount) + goods.getVirtualCount();
			}
			mb.setViewSalesCount(viewSalesCount);
			isReCache = true;
		}
		//记录商品品牌数据
		if(null == mb.getMerchantForm()){
			MerchantForm merchantForm = shopsBaoService.getMerchantDetailByGoodsId(gsid);//获取商品的品牌数据
			mb.setMerchantForm(merchantForm);
			isReCache = true;
		}
		//记录商圈
		if(null == mb.getRegionName()){
			//商品所在区域
			Map<String, Map<Long, List<RegionCatlog>>> regionMap = 
				       (Map<String, Map<Long, List<RegionCatlog>>>) memCacheService.get("BASE_REGION_CATLOG");//商圈、属性分类
			// 假如memcache里没有就从数据库里查
			if (null == regionMap) {
				regionMap = BeanUtils.getCityCatlog(request, "regionCatlogDao");
				memCacheService.set("BASE_REGION_CATLOG", regionMap);
			}
			RegionCatlog region = getRegionCatlog(goods,regionMap);
			mb.setRegionName(null == region ? "" :region.getCatlogName());
			isReCache = true;
		}
		//缓存或重新缓存
		if(!isCache||isReCache){
			memCacheService.set(MB_KEY, mb, 60*60);
		}
	
		
		//存入request
		request.setAttribute("goods", goods);
		request.setAttribute("SALES_COUNT", mb.getViewSalesCount());
		request.setAttribute("mb", mb);
		request.setAttribute("tpbbkList", tpbbkList);
		request.setAttribute("zbqggList", zbqggList);
		request.setAttribute("merchantForm", mb.getMerchantForm());
		request.setAttribute("firstRegionName", mb.getRegionName());
		request.setAttribute("UPLOAD_IMAGES_URL",Constant.UPLOAD_IMAGES_URL);   
		
		return "brand/mergewithbrands";
	}


	/**
	 * 
	* @Title: getRegionCatlog
	* @Description: 获取商品所在一级商圈
	* @param @param goods
	* @param @param regionMap
	* @param @return    设定文件
	* @return RegionCatlog    返回类型
	* @throws
	 */
	public RegionCatlog getRegionCatlog(Goods goods,Map<String, Map<Long, List<RegionCatlog>>> regionMap){
		// 汉语转换成拼音
		String city = PinyinUtil.hanziToPinyin(goods.getCity(), "");
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
		//商品所在一级商圈
		RegionCatlog region = null;
		List<Map<String, Object>> lstFirstRegion = goodsService.getGoodsFirstRegionById(goods.getGoodsId());
		if (lstFirstRegion != null && lstFirstRegion.size() == 1) {
			region = regionKeyMap.get(lstFirstRegion.get(0).get("regionid"));
		}
		
		return region;
	}
	/**
	 * 
	* @Title: idToLong
	* @Description: 把String translate Long
	* @param @param ids
	* @param @return
	* @param @throws Exception    设定文件
	* @return List<Long>    返回类型
	* @throws
	 */
	public List<Long> idToLong(String[] ids) throws Exception{
		List<Long> idsList = new ArrayList<Long>();
		for(String id:ids){
			idsList.add(Long.valueOf(id));
		}
		return idsList;
	}
}
