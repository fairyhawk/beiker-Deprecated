package com.beike.wap.action.brand;

import java.util.Date;
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
import com.beike.util.DateUtils;
import com.beike.util.MCookieKey;
import com.beike.util.StringUtils;
import com.beike.util.WebUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.wap.action.user.MBaseUserAction;
import com.beike.wap.entity.MGoods;
import com.beike.wap.entity.MMerchant;
import com.beike.wap.entity.MRegion;
import com.beike.wap.entity.MerchantCatlog;
import com.beike.wap.service.MBrandService;
import com.beike.wap.service.MCatalogService;
import com.beike.wap.service.MMerchantService;
import com.beike.wap.service.MRegionService;
import com.beike.wap.service.MTagService;

/**
 * Title : BrandIndexAction
 * <p/>
 * Description :品牌信息Action
 * <p/>
 * CopyRight : CopyRight (c) 2011
 * </P>
 * Company : SinoboGroup.com </P> JDK Version Used : JDK 5.0 +
 * <p/>
 * Modification History :
 * <p/>
 * 
 * <pre>
 * NO.    Date    Modified By    Why & What is modified
 * </pre>
 * 
 * <pre>1     2011-09-23    lvjx            Created
 * 
 * <pre>
 * <p/>
 *
 * @author  lvjx
 * @version 1.0.0.2011-09-23
 */
@Controller
@RequestMapping("/wap/brand/brandIndexController.do")
public class MBrandIndexAction extends MBaseUserAction {

	@RequestMapping(params = "method=queryIndexShowMes")
	public ModelAndView queryIndexShowMes(HttpServletRequest request,
			HttpServletResponse response,
			ModelMap modelMap) {
		super.setCookieUrl(request, response);
//		WebUtils.setMCookieByKey(response,  MCookieKey.CATALOG_CLICK_STATE, Constant.WAP_BRAND_PAGE, 60 * 10); 
		Constant.CLICK_STATE = Constant.WAP_BRAND_PAGE;
		//10100
		//10200
		//10300
		//10400
		try {
			String cityName = WebUtils.getCookieValue(
					MCookieKey.CITY_COOKIENAME, request);
			if(!StringUtils.validNull(cityName)){
				cityName = "beijing";
				int validy = 60 * 60 * 24 * 30 * 12 * 10;
				WebUtils.setMCookieByKey(response, MCookieKey.CITY_COOKIENAME, cityName, validy);
			}
			Map<String,Long> mapCity=(Map<String, Long>) memCacheService.get(MCookieKey.CITY_CATLOG);
			if(mapCity==null){
				mapCity=BeanUtils.getCity(request, "regionCatlogDao");
				memCacheService.set(MCookieKey.CITY_CATLOG, mapCity);
			}
			Date currentDate = new Date();
			Map<String,String> mTagMap = tagService.queryTagByParentIdInfo(0);
			List<MRegion> mRegionList = regionService.queryWapHotRegionData(
					currentDate, cityName);
			if(null==mRegionList){
				Date maxDate = regionService.queryMaxDate(cityName);
				mRegionList = regionService.queryWapHotRegionData(
						maxDate, cityName);
			}
			Long cityid=null;
			if(mapCity!=null){
				cityid=mapCity.get(cityName);
			}
			MerchantCatlog brandCatLog = new MerchantCatlog();
			brandCatLog.setCityid(cityid);
			List<MMerchant> foodsList = null;
			if(null==foodsList){
				brandCatLog.setTagid(10100L);
				List<Long> brandIdList = mCatalogService.getAllMerchantId(0,brandCatLog);//brandCatLog
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
				
				foodsList = mMerchantService.getBrandByIds(idSb.toString());
//				foodsList = brandService.queryIndexShowMes(1, 1, 1,
//						DateUtils.parseToDate(DateUtils.formatTime(-1, "yyyy-MM-dd"),"yyyy-MM-dd"), cityName);
			}
			List<MMerchant> leisureList = null;
			if(null==leisureList){
				brandCatLog.setTagid(10200L);
				List<Long> brandIdList = mCatalogService.getAllMerchantId(0,brandCatLog);//brandCatLog
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
				
				leisureList = mMerchantService.getBrandByIds(idSb.toString());
//				leisureList = brandService.queryIndexShowMes(1, 2, 1,
//						DateUtils.parseToDate(DateUtils.formatTime(-1, "yyyy-MM-dd"),"yyyy-MM-dd"), cityName);
			}
			List<MMerchant> beautyList = null;
			if(null==beautyList){
				brandCatLog.setTagid(10300L);
				List<Long> brandIdList = mCatalogService.getAllMerchantId(0,brandCatLog);//brandCatLog
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
				
				beautyList = mMerchantService.getBrandByIds(idSb.toString());
//				beautyList = brandService.queryIndexShowMes(1, 3, 1,
//						DateUtils.parseToDate(DateUtils.formatTime(-1, "yyyy-MM-dd"),"yyyy-MM-dd"), cityName);
			}
			List<MMerchant> serviceList = null;
			if(null==serviceList){
				brandCatLog.setTagid(10400L);
				List<Long> brandIdList = mCatalogService.getAllMerchantId(0,brandCatLog);//brandCatLog
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
				
				serviceList = mMerchantService.getBrandByIds(idSb.toString());
//				serviceList = brandService.queryIndexShowMes(1, 4, 1,
//						DateUtils.parseToDate(DateUtils.formatTime(-1, "yyyy-MM-dd"),"yyyy-MM-dd"), cityName);
			}
			List<MGoods> brandScrollList = brandService.queryIndexShowMes(4, 0,
					1, currentDate, cityName);
			if(null==brandScrollList){
				brandScrollList = brandService.queryIndexShowMes(4, 0,
						1, DateUtils.parseToDate(DateUtils.formatTime(-1, "yyyy-MM-dd"),"yyyy-MM-dd"), cityName);
			}
			
			modelMap.addAttribute("mRegionList", mRegionList);
			modelMap.addAttribute("foodsList", foodsList);
			modelMap.addAttribute("leisureList", leisureList);
			modelMap.addAttribute("beautyList", beautyList);
			modelMap.addAttribute("serviceList", serviceList);
			modelMap.addAttribute("brandScrollList", brandScrollList);
			modelMap.addAttribute("mTagMap", mTagMap);
			modelMap.addAttribute("UPLOAD_IMAGES_URL", Constant.UPLOAD_IMAGES_URL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ModelAndView modelAndView = new ModelAndView("wap/index/brandIndex");
		return modelAndView;
	}

	@Resource(name = "wapBrandService")
	private MBrandService brandService;

	@Resource(name = "wapRegionService")
	private MRegionService regionService;
	
	@Resource(name = "wapTagService")
	private MTagService tagService;
	
	@Autowired
	private MCatalogService mCatalogService;
	
	@Autowired
	private MMerchantService mMerchantService;

	private final MemCacheService memCacheService = MemCacheServiceImpl
	.getInstance();
}
