package com.beike.wap.action.brand;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.beike.util.BeanUtils;
import com.beike.util.Constant;
import com.beike.util.MCookieKey;
import com.beike.util.RandomNumberUtils;
import com.beike.util.StringUtils;
import com.beike.util.WebUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.wap.action.user.MBaseUserAction;
import com.beike.wap.entity.MMerchant;
import com.beike.wap.entity.MerchantCatlog;
import com.beike.wap.service.MCatalogService;
import com.beike.wap.service.MMerchantService;
import com.beike.wap.service.MRegionService;
import com.beike.wap.service.MTagService;
/**
 * 品牌分类查询action
 * @author k.w
 */
@Controller
@RequestMapping("/wap/brand/brandQueryController.do")
public class MBrandQueryAction extends MBaseUserAction{
	
	@RequestMapping(params = "method=showMoreBrand")
	public Object showMoreBrand(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap)
	{
		super.setCookieUrl(request, response);
		try {
			String region = request.getParameter("region");
			String region_ext = request.getParameter("regionext");
			String catlog = request.getParameter("catlog");
			String catlog_ext = request.getParameter("catlogext");

			String currentPage = request.getParameter("currentPage");
			
			int startPage = 0;
			
			if(!StringUtils.validNull(currentPage)){
				currentPage = "0";
				startPage = 0;
			}
			else
			{
				startPage = Integer.parseInt(currentPage)*8;
			}

			String cityName = WebUtils.getCookieValue(
					MCookieKey.CITY_COOKIENAME, request);

			Map<String,Long> mapCity=(Map<String, Long>) memCacheService.get(MCookieKey.CITY_CATLOG);
			if(mapCity==null){
				mapCity=BeanUtils.getCity(request, "regionCatlogDao");
				memCacheService.set(MCookieKey.CITY_CATLOG, mapCity);
			}

			if(cityName==null||"".equals(cityName)){
				cityName="beijing";
			}
			Long cityid=null;
			if(mapCity!=null){
				cityid=mapCity.get(cityName);
			}
			
			Map<String,String> mTagMap = tagService.queryTagByParentIdInfo(0);
			Map<String,String> mRegionMap = regionService.queryRegionInfo(String.valueOf(0), String.valueOf(cityid));
			
			MerchantCatlog brandCatLog = new MerchantCatlog();
			//设置城市id
			brandCatLog.setCityid(cityid);
			if (StringUtils.validNull(region)) {
				brandCatLog.setRegionid(Long.parseLong(region));
				Map<String,String> regionMap = regionService.queryRegionInfo(region, String.valueOf(cityid));
				modelMap.addAttribute("regionMap", regionMap);
				modelMap.addAttribute("region", region);//用来传递查询条件的一级菜单,并且查询二级菜单
			}
			if (StringUtils.validNull(region_ext)) {
				if(!StringUtils.validNull(region)&&StringUtils.validNull(region_ext)){
					int regionInt = regionService.queryRegionParentId(region_ext);
					if(0==regionInt){
						brandCatLog.setRegionid(Long.parseLong(region_ext));
						regionInt = Integer.parseInt(region_ext);
					}
					brandCatLog.setRegionid(Long.valueOf(regionInt));
					Map<String,String> regionMap = regionService.queryRegionInfo(String.valueOf(regionInt), String.valueOf(cityid));
					modelMap.addAttribute("regionMap", regionMap);
					modelMap.addAttribute("region",String.valueOf(regionInt));
				}
				if(brandCatLog.getRegionid()!=Long.parseLong(region_ext)){
					brandCatLog.setRegionextid(Long.parseLong(region_ext));
				}
				modelMap.addAttribute("region_ext", region_ext);
			}
			if (StringUtils.validNull(catlog)) {
				brandCatLog.setTagid(Long.parseLong(catlog));
				Map<String,String> tagMap = tagService.queryTagByParentIdInfo(Integer.parseInt(catlog));
				modelMap.addAttribute("tagMap", tagMap);
				modelMap.addAttribute("catlog", catlog);
			}
			if (StringUtils.validNull(catlog_ext)) {
				brandCatLog.setTagextid(Long.parseLong(catlog_ext));
				modelMap.addAttribute("catlog_ext", catlog_ext);
			}
			int sum = mCatalogService.getBrandCatlogSum(brandCatLog);
			int pageSize = RandomNumberUtils.calculatePage(sum, 8);
			
			List<Long> brandIdList = mCatalogService.getAllMerchantId(startPage,brandCatLog);
			StringBuilder idSb = new StringBuilder("");
			if(brandIdList != null)
			{
				for (Long brandId : brandIdList) {
					idSb.append(brandId).append(",");
				}
			}
			if(idSb.length() != 0)
			{
				idSb = idSb.delete(idSb.length() - 1, idSb.length());
			}
			
			List<MMerchant> brandList = mMerchantService.getBrandByIds(idSb.toString());
			if(brandList == null)
			{
				sum = 0;
				pageSize = 0;
			}
			modelMap.addAttribute("sum", sum);
			modelMap.addAttribute("brandList", brandList);
			modelMap.addAttribute("pageSize", pageSize);
			modelMap.addAttribute("currentPage", currentPage);
			modelMap.addAttribute("mRegionMap", mRegionMap);
			modelMap.addAttribute("mTagMap", mTagMap);
			modelMap.addAttribute("UPLOAD_IMAGES_URL", Constant.UPLOAD_IMAGES_URL);
		} catch (Exception e) {
			e.printStackTrace();
			return new ModelAndView("redirect:/jsp/wap/500.jsp");
		}
		return "wap/query/queryBrand";
	}
	
	private final MemCacheService memCacheService = MemCacheServiceImpl
	.getInstance();
	
	@Autowired
	private MCatalogService mCatalogService;
	
	@Autowired
	private MMerchantService mMerchantService;
	
	
	@Resource(name = "wapTagService")
	private MTagService tagService;
	
	@Resource(name = "wapRegionService")
	private MRegionService regionService;
}
