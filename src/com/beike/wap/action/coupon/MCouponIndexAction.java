package com.beike.wap.action.coupon;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.beike.util.DateUtils;
import com.beike.util.MCookieKey;
import com.beike.util.StringUtils;
import com.beike.util.WebUtils;
import com.beike.util.htmlparse.MParseCoupon;
import com.beike.wap.action.user.MBaseUserAction;
import com.beike.wap.entity.MGoods;
import com.beike.wap.entity.MRegion;
import com.beike.wap.entity.MWapType;
import com.beike.wap.service.MBrandService;
import com.beike.wap.service.MCouponService;
import com.beike.wap.service.MRegionService;
import com.beike.wap.service.MTagService;
import com.beike.wap.service.MWapTypeService;

/**
 * Title : GoodsAction
 * <p/>
 * Description :商品信息Action
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
 * <pre>1     2011-06-14    lvjx            Created
 * 
 * <pre>
 * <p/>
 *
 * @author  lvjx
 * @version 1.0.0.2011-06-14
 */
@Controller
@RequestMapping("/wap/coupon/couponIndexController.do")
public class MCouponIndexAction extends MBaseUserAction {

	@RequestMapping(params = "method=queryIndexShowMes")
	public ModelAndView queryIndexShowMes(HttpServletRequest request,
			HttpServletResponse response, ModelMap modelMap) {
		super.setCookieUrl(request, response);
//		WebUtils.setMCookieByKey(response,  MCookieKey.CATALOG_CLICK_STATE, Constant.WAP_COUPON_PAGE, 60 * 10);// 
//		Constant.CLICK_STATE = Constant.WAP_COUPON_PAGE;
		String[] floor = { "coupon_list", "coupon_list2", "coupon_list3",
				"coupon_list4" };
		try {
			String cityName = WebUtils.getCookieValue(
					MCookieKey.CITY_COOKIENAME, request);
			if(!StringUtils.validNull(cityName)){
				cityName = "beijing";
				int validy = 60 * 60 * 24 * 30 * 12 * 10;
				WebUtils.setMCookieByKey(response, MCookieKey.CITY_COOKIENAME, cityName, validy);
			}
			this.isExistSaveData(floor, cityName);
			Date currentDate = new Date();
			Map<String,String> mTagMap = tagService.queryTagByParentIdInfo(0);
			List<MRegion> mRegionList = regionService.queryWapHotRegionData(
					currentDate, cityName);
			if(null==mRegionList){
				Date maxDate = regionService.queryMaxDate(cityName);
				mRegionList = regionService.queryWapHotRegionData(
						maxDate, cityName);
			}
			
			List<MGoods> foodsList = couponService.queryIndexShowMes(3, 1, 1,
					currentDate, cityName);
			if(null==foodsList){
				foodsList = couponService.queryIndexShowMes(3, 1, 1,
						DateUtils.parseToDate(DateUtils.formatTime(-1, "yyyy-MM-dd"),"yyyy-MM-dd"), cityName);
			}
			List<MGoods> leisureList = couponService.queryIndexShowMes(3, 2, 1,
					currentDate, cityName);
			if(null==leisureList){
				leisureList = couponService.queryIndexShowMes(3, 2, 1,
						DateUtils.parseToDate(DateUtils.formatTime(-1, "yyyy-MM-dd"),"yyyy-MM-dd"), cityName);
			}
			List<MGoods> beautyList = couponService.queryIndexShowMes(3, 3, 1,
					currentDate, cityName);
			if(null==beautyList){
				beautyList = couponService.queryIndexShowMes(3, 3, 1,
						DateUtils.parseToDate(DateUtils.formatTime(-1, "yyyy-MM-dd"),"yyyy-MM-dd"), cityName);
			}
			List<MGoods> serviceList = couponService.queryIndexShowMes(3, 4, 1,
					currentDate, cityName);
			if(null==serviceList){
				serviceList = couponService.queryIndexShowMes(3, 4, 1,
						DateUtils.parseToDate(DateUtils.formatTime(-1, "yyyy-MM-dd"),"yyyy-MM-dd"), cityName);
			}
			List<MGoods> brandScrollList = brandScrollService
					.queryIndexShowMes(4, 0, 1, currentDate, cityName);
			if(null==brandScrollList){
				brandScrollList = brandScrollService
				.queryIndexShowMes(4, 0, 1, DateUtils.parseToDate(DateUtils.formatTime(-1, "yyyy-MM-dd"),"yyyy-MM-dd"), cityName);
			}
			
			modelMap.addAttribute("mRegionList", mRegionList);
			modelMap.addAttribute("foodsList", foodsList);
			modelMap.addAttribute("leisureList", leisureList);
			modelMap.addAttribute("beautyList", beautyList);
			modelMap.addAttribute("serviceList", serviceList);
			modelMap.addAttribute("brandScrollList", brandScrollList);
			modelMap.addAttribute("mTagMap", mTagMap);

		} catch (Exception e) {
			e.printStackTrace();
			return new ModelAndView("redirect:/jsp/wap/500.jsp");
		}
		ModelAndView modelAndView = new ModelAndView("wap/index/couponIndex");
		return modelAndView;
	}

	/**
	 * 判断是否已存在当天数据，如果不存在，那么就解析html，保存到数据库，如果存在就直接访问数据库
	 * 
	 * @param floor
	 * @return
	 */
	private boolean isExistSaveData(String[] floor, String typeArea) {
		boolean flag = false;
		try {
			Date currentDate = new Date();
			java.sql.Date nowDate = new java.sql.Date(currentDate.getTime());
			int sum = wapTypeService.queryWapType(3, 1, currentDate, typeArea);
			if (sum > 0) {
				flag = false;
			} else {
				for (int i = 0; i < floor.length; i++) {
					List<MWapType> wapGoodsTypeList = new ArrayList<MWapType>();
					MWapType wapType = null;
					Map<String, String> couponMap = MParseCoupon.getCouponInfo(
							"", typeArea, floor[i]);
					int floorInt = i + 1;
					for (String key : couponMap.keySet()) {
						wapType = new MWapType();
						wapType.setTypeId(Integer.parseInt(key));
						wapType.setTypeUrl(couponMap.get(key));
						wapType.setTypeType(3);
						wapType.setTypeFloor(floorInt);
						wapType.setTypePage(1);
						wapType.setTypeDate(nowDate);
						wapType.setTypeArea(typeArea);
						wapGoodsTypeList.add(wapType);
					}
					List<MWapType> finalWapTypeList = wapGoodsTypeList;
					wapTypeService.addWapType(finalWapTypeList);
				}
				flag = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	@Resource(name = "wapTypeService")
	private MWapTypeService wapTypeService;

	@Resource(name = "wapCouponService")
	private MCouponService couponService;

	@Resource(name = "wapBrandService")
	private MBrandService brandScrollService;

	@Resource(name = "wapRegionService")
	private MRegionService regionService;
	
	@Resource(name = "wapTagService")
	private MTagService tagService;
}
