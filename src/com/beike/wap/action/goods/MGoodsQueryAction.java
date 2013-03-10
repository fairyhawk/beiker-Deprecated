package com.beike.wap.action.goods;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.beike.util.BeanUtils;
import com.beike.util.MCookieKey;
import com.beike.util.RandomNumberUtils;
import com.beike.util.StringUtils;
import com.beike.util.WebUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.wap.action.user.MBaseUserAction;
import com.beike.wap.entity.MGoods;
import com.beike.wap.entity.MGoodsCatlog;
import com.beike.wap.service.MGoodsService;
import com.beike.wap.service.MRegionService;
import com.beike.wap.service.MTagService;
/**
 * Title : GoodsAction
 * <p/>
 * Description :商品分类查询信息Action
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
 * <pre>1     2011-10-13    lvjx            Created
 * 
 * <pre>
 * <p/>
 *
 * @author  lvjx
 * @version 1.0.0.2011-10-13
 */
@Controller
@RequestMapping("/wap/goods/goodsQueryController.do")
public class MGoodsQueryAction extends MBaseUserAction {
	
	private static String CITY_CATLOG="CITY_CATLOG";
	
	@SuppressWarnings("unchecked")
	@RequestMapping(params = "method=queryGoodsInfo")
	public ModelAndView queryIndexShowMes(HttpServletRequest request,
			HttpServletResponse response, ModelMap modelMap) {
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
			}else
			{
				startPage = Integer.parseInt(currentPage)*5;
			}
			
			String cityName = WebUtils.getCookieValue(
					MCookieKey.CITY_COOKIENAME, request);
			
			
			Map<String,Long> mapCity=(Map<String, Long>) memCacheService.get(CITY_CATLOG);
			if(mapCity==null){
				mapCity=BeanUtils.getCity(request, "regionCatlogDao");
				memCacheService.set(CITY_CATLOG, mapCity);
			}
			
			if(cityName==null||"".equals(cityName)){
				cityName="beijing";
			}
			Long cityid=null;
			if(mapCity!=null){
				cityid=mapCity.get(cityName);
			}
			
			//List<Tag> mTagList = tagService.queryTagByParendId(0);
			Map<String,String> mTagMap = tagService.queryTagByParentIdInfo(0);
			Map<String,String> mRegionMap = regionService.queryRegionInfo(String.valueOf(0), String.valueOf(cityid));
			
			
			MGoodsCatlog goodsCatLog = new MGoodsCatlog();
			//设置城市id
			goodsCatLog.setCityid(cityid);
			if (StringUtils.validNull(region)) {
				goodsCatLog.setRegionid(Long.parseLong(region));
				//List<MRegion> regionList = regionService.queryRegion(region, String.valueOf(cityid));
				Map<String,String> regionMap = regionService.queryRegionInfo(region, String.valueOf(cityid));
				modelMap.addAttribute("regionMap", regionMap);
				modelMap.addAttribute("region", region);//用来传递查询条件的一级菜单,并且查询二级菜单
			}
			if (StringUtils.validNull(region_ext)) {
				if(!StringUtils.validNull(region)&&StringUtils.validNull(region_ext)){
					int regionInt = regionService.queryRegionParentId(region_ext);
					if(0==regionInt){
						goodsCatLog.setRegionid(Long.parseLong(region_ext));
						regionInt = Integer.parseInt(region_ext);
					}
					goodsCatLog.setRegionid(Long.valueOf(regionInt));
					Map<String,String> regionMap = regionService.queryRegionInfo(String.valueOf(regionInt), String.valueOf(cityid));
					modelMap.addAttribute("regionMap", regionMap);
					modelMap.addAttribute("region",String.valueOf(regionInt));
				}
				if(goodsCatLog.getRegionid()!=Long.parseLong(region_ext)){
					goodsCatLog.setRegionextid(Long.parseLong(region_ext));
				}
				modelMap.addAttribute("region_ext", region_ext);
			}
			if (StringUtils.validNull(catlog)) {
				goodsCatLog.setTagid(Long.parseLong(catlog));
				//List<Tag> tagList =  tagService.queryTagByParendId(Integer.parseInt(catlog));
				Map<String,String> tagMap = tagService.queryTagByParentIdInfo(Integer.parseInt(catlog));
				modelMap.addAttribute("tagMap", tagMap);
				modelMap.addAttribute("catlog", catlog);
			}
			if (StringUtils.validNull(catlog_ext)) {
				goodsCatLog.setTagextid(Long.parseLong(catlog_ext));
				modelMap.addAttribute("catlog_ext", catlog_ext);
			}
			//此处增加一个方法用来计算总条数，总页数
			int sum = goodsService.queryGoodsIdsSum(goodsCatLog);
			int pageSize = RandomNumberUtils.calculatePage(sum, 5);
			//战略=合适的事情+合适的时机
			List<Long> goodsIdList = goodsService.queryGoodsIds(startPage,goodsCatLog);
			
			List<MGoods> goodsList = goodsService.queryGoodsInfo(goodsIdList);
			modelMap.addAttribute("sum", sum);
			modelMap.addAttribute("goodsList", goodsList);
			modelMap.addAttribute("pageSize", pageSize);
			modelMap.addAttribute("currentPage", currentPage);
			modelMap.addAttribute("mRegionMap", mRegionMap);
			modelMap.addAttribute("mTagMap", mTagMap);
		} catch (Exception e) {
			e.printStackTrace();
			return new ModelAndView("redirect:/jsp/wap/500.jsp");
		}
		ModelAndView modelAndView = new ModelAndView("wap/query/queryGoods");//
		return modelAndView;
	}

	
	private final MemCacheService memCacheService = MemCacheServiceImpl
	.getInstance();
	
	@Resource(name = "wapGoodsService")
	private MGoodsService goodsService;
	
	@Resource(name = "wapTagService")
	private MTagService tagService;
	
	@Resource(name = "wapRegionService")
	private MRegionService regionService;
}
