package com.beike.wap.action.coupon;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.beike.util.BeanUtils;
import com.beike.util.Constant;
import com.beike.util.MCookieKey;
import com.beike.util.RandomNumberUtils;
import com.beike.util.StringUtils;
import com.beike.util.WebUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.wap.action.user.MBaseUserAction;
import com.beike.wap.entity.MCoupon;
import com.beike.wap.entity.MCouponCatlog;
import com.beike.wap.service.MCatalogService;
import com.beike.wap.service.MCouponDetailService;
import com.beike.wap.service.MRegionService;
import com.beike.wap.service.MTagService;
/**
 * 优惠卷分类查询action
 * @author k.w
 */
@Controller
@RequestMapping("/wap/coupon/couponQueryController.do")
public class MCouponQueryAction extends MBaseUserAction{
	
	@RequestMapping(params = "method=showCouponMore")
	public Object showCouponQueryMore(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap)
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
				startPage = Integer.parseInt(currentPage)*5;
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
			
			MCouponCatlog couponCatLog = new MCouponCatlog();
			couponCatLog.setCityid(cityid);
			if (StringUtils.validNull(region)) {
				couponCatLog.setRegionid(Long.parseLong(region));
				Map<String,String> regionMap = regionService.queryRegionInfo(region, String.valueOf(cityid));
				modelMap.addAttribute("regionMap", regionMap);
				modelMap.addAttribute("region", region);//用来传递查询条件的一级菜单,并且查询二级菜单
			}
			if (StringUtils.validNull(region_ext)) {
				if(!StringUtils.validNull(catlog)){
					int regionInt = regionService.queryRegionParentId(region_ext);
					couponCatLog.setRegionid(Long.valueOf(regionInt));
					Map<String,String> regionMap = regionService.queryRegionInfo(String.valueOf(regionInt), String.valueOf(cityid));
					modelMap.addAttribute("regionMap", regionMap);
					modelMap.addAttribute("region",String.valueOf(regionInt));
				}
				couponCatLog.setRegionextid(Long.parseLong(region_ext));
				modelMap.addAttribute("region_ext", region_ext);
			}
			if (StringUtils.validNull(catlog)) {
				couponCatLog.setTagid(Long.parseLong(catlog));
				Map<String,String> tagMap = tagService.queryTagByParentIdInfo(Integer.parseInt(catlog));
				modelMap.addAttribute("tagMap", tagMap);
				modelMap.addAttribute("catlog", catlog);
			}
			if (StringUtils.validNull(catlog_ext)) {
				couponCatLog.setTagextid(Long.parseLong(catlog_ext));
				modelMap.addAttribute("catlog_ext", catlog_ext);
			}
//			//此处增加一个方法用来计算总条数，总页数
			int sum = mCatalogService.getCouponCatalogSum(couponCatLog);
			int pageSize = RandomNumberUtils.calculatePage(sum, 5);
			List<Long> couponIdList = mCatalogService.getAllCouponId(startPage,couponCatLog);
			StringBuilder idSb = new StringBuilder("");
			if(couponIdList != null)
			{
				for (Long couponId : couponIdList) {
					idSb.append(couponId).append(",");
				}
			}
			
			if(idSb.length() != 0)
			{
				idSb = idSb.delete(idSb.length() - 1, idSb.length());
			}
			
			List<MCoupon> couponList = mCouponDetailService.queryCouponByIdS(idSb.toString());
			if(couponList == null)
			{
				sum = 0;
				pageSize = 0;
			}else
			{
				int validy = 60 * 60 * 24;
				for(MCoupon coupon : couponList)
				{
					Long browsecount = (Long) memCacheService
					.get(Constant.MEM_COUPON_BROWCOUNT + coupon.getId());
					Long downcount = (Long) memCacheService
					.get(Constant.MEM_COUPON_DOWNCOUNT + coupon.getId());
					
					if (downcount == null) {
						downcount = coupon.getDownCount();
						memCacheService.set(
								Constant.MEM_COUPON_DOWNCOUNT + coupon.getId(),
								downcount, validy);
					}else
					{
						coupon.setDownCount(downcount);
					}
					if (browsecount == null) {
						browsecount = coupon.getBrowseCounts();
						memCacheService.set(
								Constant.MEM_COUPON_BROWCOUNT + coupon.getId(),
								browsecount, validy);
					}else
					{
						coupon.setBrowseCounts(browsecount);
					}
				}
			}
			modelMap.addAttribute("sum", sum);
			modelMap.addAttribute("couponList", couponList);
			modelMap.addAttribute("pageSize", pageSize);
			modelMap.addAttribute("currentPage", currentPage);
			modelMap.addAttribute("mRegionMap", mRegionMap);
			modelMap.addAttribute("mTagMap", mTagMap);
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/jsp/wap/500.jsp";
		}
		return "wap/query/queryCoupon";
	}
	
	private final MemCacheService memCacheService = MemCacheServiceImpl
	.getInstance();
	
	@Autowired
	private MCatalogService mCatalogService;
	
	@Autowired
	private MCouponDetailService mCouponDetailService;
	
	@Resource(name = "wapTagService")
	private MTagService tagService;
	
	@Resource(name = "wapRegionService")
	private MRegionService regionService;
}
