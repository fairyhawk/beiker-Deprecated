package com.beike.wap.action.goods;

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

import com.beike.util.Constant;
import com.beike.util.MCookieKey;
import com.beike.util.StringUtils;
import com.beike.util.WebUtils;
import com.beike.util.htmlparse.MParseBrandScroll;
import com.beike.util.htmlparse.MParseGoods;
import com.beike.util.htmlparse.MParseRegion;
import com.beike.wap.action.user.MBaseUserAction;
import com.beike.wap.entity.MGoods;
import com.beike.wap.entity.MRegion;
import com.beike.wap.entity.MWapType;
import com.beike.wap.service.MBrandService;
import com.beike.wap.service.MGoodsService;
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
@RequestMapping("/wap/goods/goodsIndexController.do")
public class MGoodsIndexAction extends MBaseUserAction {

	@RequestMapping(params = "method=queryIndexShowMes")
	public ModelAndView queryIndexShowMes(HttpServletRequest request,
			HttpServletResponse response, ModelMap modelMap) {
		super.setCookieUrl(request, response);
//		WebUtils.setMCookieByKey(response, MCookieKey.CATALOG_CLICK_STATE, Constant.WAP_INDEX_PAGE, 60 * 10);// 
		Constant.CLICK_STATE = Constant.WAP_INDEX_PAGE;// 记录当前点击，前台页面按钮变换
		String[] floor = { "mainlistone", "mainlisttwo", "mainlistthree", "mainlistfour" };
		try {
			String cityName = WebUtils.getCookieValue(
					MCookieKey.CITY_COOKIENAME, request);
			if(!StringUtils.validNull(cityName)){
				cityName = "beijing";
				int validy = 60 * 60 * 24 * 30 * 12 * 10;
				WebUtils.setMCookieByKey(response, MCookieKey.CITY_COOKIENAME, cityName, validy);
			}
			int areaId = regionService.queryAreaId(cityName.toUpperCase());
			this.isExistHotRegionSaveData(cityName,areaId);
			this.isExistScrollBrandSaveData(cityName);
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

			List<MGoods> foodsList = goodsService.queryIndexShowMes(2, 1, 1,
					currentDate, cityName);
			if(null==foodsList){
				Date foodsMaxDate = goodsService.queryMaxDate(2, 1, 1, cityName);
				foodsList = goodsService.queryIndexShowMes(2, 1, 1,foodsMaxDate, cityName);
			}
			List<MGoods> leisureList = goodsService.queryIndexShowMes(2, 2, 1,
					currentDate, cityName);
			if(null==leisureList){
				Date leisureMaxDate = goodsService.queryMaxDate(2, 2, 1, cityName);
				leisureList = goodsService.queryIndexShowMes(2, 2, 1,leisureMaxDate, cityName);
			}
			List<MGoods> beautyList = goodsService.queryIndexShowMes(2, 3, 1,
					currentDate, cityName);
			if(null==beautyList){
				Date beautyMaxDate = goodsService.queryMaxDate(2, 2, 1, cityName);
				beautyList = goodsService.queryIndexShowMes(2, 3, 1,beautyMaxDate, cityName);
			}
			List<MGoods> serviceList = goodsService.queryIndexShowMes(2, 4, 1,
					currentDate, cityName);
			if(null==serviceList){
				Date serviceMaxDate = goodsService.queryMaxDate(2, 2, 1, cityName);
				serviceList = goodsService.queryIndexShowMes(2, 4, 1,serviceMaxDate, cityName);
			}
			List<MGoods> brandScrollList = brandScrollService
					.queryIndexShowMes(4, 0, 1, currentDate, cityName);
			if(null==brandScrollList){
				Date scrollMaxDate = goodsService.queryMaxDate(4, 0, 1, cityName);
				brandScrollList = brandScrollService.queryIndexShowMes(4, 0, 1, scrollMaxDate, cityName);
				
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
		ModelAndView modelAndView = new ModelAndView("wap/index/goodsIndex");//
		return modelAndView;
	}

	private boolean isExistSaveData(String[] floor, String typeArea) {
		boolean flag = false;
		Map<String, String> goodsIds = null;
		MWapType wapType = null;
		try {
			Date currentDate = new Date();
			java.sql.Date nowDate = new java.sql.Date(currentDate.getTime());
			int sum = wapTypeService.queryWapType(2, 1, currentDate, typeArea);
			if (sum > 0) {
				flag = false;
			} else{
				for (int i = 0; i < floor.length; i++) {
					List<MWapType> wapGoodsTypeList = new ArrayList<MWapType>();
					goodsIds = MParseGoods.getGoodsMes("", typeArea, floor[i]);

					int floorInt = i + 1;
					for (String key : goodsIds.keySet()) {
						wapType = new MWapType();
						wapType.setTypeId(Integer.parseInt(key));
						wapType.setTypeUrl(goodsIds.get(key));
						wapType.setTypeType(2);
						wapType.setTypeFloor(floorInt);
						wapType.setTypePage(1);
						wapType.setTypeDate(nowDate);
						wapType.setTypeArea(typeArea);
						wapGoodsTypeList.add(wapType);
					}
					List<MWapType> finalWapTypeList = wapGoodsTypeList;
					wapTypeService.addWapType(finalWapTypeList);
					flag = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 判断是否已存在当天滚动品牌信息，如果不存在，那么就解析html， 保存到数据库，如果存在就直接访问数据库
	 * 
	 * @param floor
	 * @return
	 */
	private boolean isExistScrollBrandSaveData(String typeArea) {
		boolean flag = false;
		try {
			Date currentDate = new Date();
			java.sql.Date nowDate = new java.sql.Date(currentDate.getTime());
			int sum = wapTypeService.queryWapType(4, 1, currentDate, typeArea);
			if (sum > 0) {
				flag = false;
			} else {
				MWapType wapType = null;
				List<MWapType> wapGoodsTypeList = new ArrayList<MWapType>();
				Map<Integer, Map<String, String>> brandMap = MParseBrandScroll
						.getBrandInfo("", typeArea, "recommendBrand");

				int size = brandMap.size();
				int[] la = MParseBrandScroll.randomNum(size);
				for (int v : la) {
					Map<String, String> bMap = brandMap.get(v);
					for (String key : bMap.keySet()) {
						wapType = new MWapType();
						wapType.setTypeId(Integer.parseInt(key));
						wapType.setTypeUrl(bMap.get(key));
						wapType.setTypeType(4);
						wapType.setTypeFloor(0);
						wapType.setTypePage(1);
						wapType.setTypeDate(nowDate);
						wapType.setTypeArea(typeArea);
						wapGoodsTypeList.add(wapType);
					}
				}
				List<MWapType> finalWapTypeList = wapGoodsTypeList;
				wapTypeService.addWapType(finalWapTypeList);
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * Description : 查询是否存在该地区的热门地标
	 * 
	 * @param regionArea
	 * @return
	 */

	private boolean isExistHotRegionSaveData(String regionArea,int areaId) {
		boolean flag = false;
		try {
			Date currentDate = new Date();
			java.sql.Date nowDate = new java.sql.Date(currentDate.getTime());
			int sum = regionService.queryWapHotRegion(currentDate, regionArea);
			if (sum > 0) {
				flag = false;
			} else {
				String hotRegionEnName = MParseRegion.getRegionInfo(
						"", regionArea, "hotcircle");
				List<MRegion> regionList = regionService.queryHotRegionData(nowDate, regionArea, hotRegionEnName, areaId);
				if(null!=regionList&&regionList.size()>0){
					final List<MRegion> mRegionList = regionList;
					regionService.addWapHotRegion(mRegionList);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	@Resource(name = "wapGoodsService")
	private MGoodsService goodsService;

	@Resource(name = "wapBrandService")
	private MBrandService brandScrollService;

	@Resource(name = "wapTypeService")
	private MWapTypeService wapTypeService;

	@Resource(name = "wapRegionService")
	private MRegionService regionService;
	
	@Resource(name = "wapTagService")
	private MTagService tagService;

}
