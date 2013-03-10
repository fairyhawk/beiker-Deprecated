/**  
* @Title: FlagshipAction.java
* @Package com.beike.action.flagship
* @Description: TODO(用一句话描述该文件做什么)
* @author Grace Guo guoqingcun@gmail.com  
* @date 2013-1-16 下午2:34:35
* @version V1.0  
*/
package com.beike.action.flagship;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import com.beike.entity.flagship.Flagship;
import com.beike.entity.goods.Goods;
import com.beike.entity.onlineorder.OrderMenu;
import com.beike.entity.takeaway.TakeAway;
import com.beike.entity.takeaway.TakeAwayMenu;
import com.beike.form.GoodsForm;
import com.beike.form.MerchantForm;
import com.beike.page.Pager;
import com.beike.page.PagerHelper;
import com.beike.service.diancai.DianCaiService;
import com.beike.service.flagship.FlagshipService;
import com.beike.service.goods.GoodsService;
import com.beike.service.merchant.ShopsBaoService;
import com.beike.service.waimai.WaiMaiService;
import com.beike.util.BeanUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.ipparser.CityUtils;
import com.beike.util.json.JsonUtil;

/**
 * @ClassName: FlagshipAction
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Grace Guo guoqingcun@gmail.com
 * @date 2013-1-16 下午2:34:35
 *
 */
@Controller
public class FlagshipAction {

	@Autowired
	private FlagshipService flagshipService;
	
	@Autowired
	private GoodsService goodsService;
	
	@Autowired
	private ShopsBaoService shopsBaoService;
	
	@Autowired
	private DianCaiService dianCaiService;

	@Autowired
	private WaiMaiService waiMaiService;
	
	private final MemCacheService memCacheService = MemCacheServiceImpl.getInstance();
	
	private final static String FLAGSHIP_KEY = "FLAGSHIP_KEY_";
	
	public void setWaiMaiService(WaiMaiService waiMaiService) {
		this.waiMaiService = waiMaiService;
	}
	
	public void setFlagshipService(FlagshipService flagshipService) {
		this.flagshipService = flagshipService;
	}
	public void setGoodsService(GoodsService goodsService) {
		this.goodsService = goodsService;
	}
	public void setShopsBaoService(ShopsBaoService shopsBaoService) {
		this.shopsBaoService = shopsBaoService;
	}
	public void setDianCaiService(DianCaiService dianCaiService) {
		this.dianCaiService = dianCaiService;
	}
	/**
	 * 
	* @Title: gotoFlagship
	* @Description: 转向旗舰店
	* @param @return    设定文件
	* @return String    返回类型
	* @throws
	 */
	@RequestMapping("/gotoFlagship.do")
	public String gotoFlagship(HttpServletRequest request,HttpServletResponse response){
		Flagship flagship = null;//旗舰店
		Boolean isPreview = false;//预览
		try {
			String realmName = request.getParameter("realmName");
//			String realmName = "temaijiu";
			String preview = request.getParameter("view");
			if(StringUtils.isNotBlank(preview)){
				isPreview = true;
			}
			String flagshipKey = FLAGSHIP_KEY+realmName;
			if(!isPreview){
				flagship = (Flagship)memCacheService.get(flagshipKey);
			}
			//旗舰店数据
			if(null == flagship){
				flagship = getFlagshipByRealmName(realmName,isPreview);
				Long merchantId = flagship.getBrandId();
				//招牌推荐
				Goods recommondGoods = recommendProduct(merchantId);
				if(null!=recommondGoods){
					String salescount = goodsService.salesCount(recommondGoods.getGoodsId());
					flagship.setRecommendGoods(recommondGoods);
					Integer recommendSalecount = Integer.parseInt(salescount)+recommondGoods.getVirtualCount();
					flagship.setRecommendSalecount(recommendSalecount);
				}
				//热销
				String branchids = flagship.getBranchs();
				flagship.setHotGoodsList(hotProducts(branchids));
				//网上点菜
				flagship.setOrderMenus(getBranchMenuList(branchids));
				//外卖
				flagship.setTakeaways(getBranchsTakeAway(branchids,Flagship.TAKEAWAY_MENU_COUNT));
				//相册
				flagship.setMerchant(getMerchantBymerchantId(merchantId));
				
				//author wenjie.mai 优惠信息
				flagship.setOffersContents(getOfferContent(merchantId));
				
				if(!isPreview){
					memCacheService.set(flagshipKey, flagship,60 * 60);
				}
			}
			request.setAttribute("flagship", flagship);
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/404.html";
		}
		if(null == flagship)
			return "redirect:/404.html";
		else
		    return flagship.getMouldUrl();
	}
	
	public List<String> getOfferContent(Long merchantId){
		
		List<String> offerlist = flagshipService.getOfferContentByMerchantId(merchantId);
		return offerlist;
	}
	
	/**
	 * 
	* @Title: getMerchantMapList
	* @Description: TODO(这里用一句话描述这个方法的作用)
	* @param @param model
	* @param @param request
	* @param @param response
	* @param @return    设定文件
	* @return String    返回类型
	* @throws
	 */
	@RequestMapping("/branch/getBranchsMapList.do")
	public String getBranchsMapList(ModelMap model,HttpServletRequest request, HttpServletResponse response) {
		String branchs = request.getParameter("branchs");
		if (branchs == null || "".equals(branchs)) {
			try {
				print(request,response, "PARAM_ERROR");
				return null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String result = "";
		List<MerchantForm> listForm = null;
		String currentPage = request.getParameter("mpage");

		if (currentPage == null || "".equals(currentPage)) {
			currentPage = "1";
		}
//		int size = flagshipService.getChildMerchantCount(branchs);

		int size = branchs.split(",").length;
		Pager pager = PagerHelper.getPager(Integer.valueOf(currentPage), size,5);
		try {
			listForm = flagshipService.getBranchsById(branchs, pager);
		} catch (Exception e) {
			e.printStackTrace();
			result = "PARAM_ERROR";
			try {
				print(request,response, result);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return null;
		}

		if (listForm == null || listForm.size() == 0) {
			// 此种返回不符合常理
			try {
				print(request,response, "NO_MERCHANT");
				return null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		DecimalFormat df = new DecimalFormat(".00");
		List<Map> list = new ArrayList<Map>();
		for (MerchantForm merchantForm : listForm) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("merchantId", merchantForm.getId());
			map.put("merchantName", merchantForm.getMerchantname());
			map.put("addr", merchantForm.getAddr());
			map.put("buinesstime", merchantForm.getBuinesstime());
			map.put("tel", merchantForm.getTel());
			map.put("latitude", merchantForm.getLatitude());
			map.put("city", merchantForm.getCity());
			map.put("rate", df.format(merchantForm.getSatisfyRate()));
			map.put("is_support_takeaway", merchantForm.getIs_Support_Takeaway());
			map.put("is_support_online_meal", merchantForm.getIs_Support_Online_Meal());
			map.put("environment", merchantForm.getEnvironment());
			map.put("capacity", merchantForm.getCapacity());
			map.put("otherservice", merchantForm.getOtherservice());
			list.add(map);
		}
		Map<String, String> mpage = new HashMap<String, String>();
		mpage.put("currentPage", pager.getCurrentPage() + "");
		mpage.put("totalPage", pager.getTotalPages() + "");
		mpage.put("totalsize", size + "");
		list.add(mpage);
		String jsonResult = JsonUtil.listToJson(list);
		try {
			print(request,response, jsonResult);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
	private void print(HttpServletRequest request,HttpServletResponse response, String content)
			throws IOException {
		response.setCharacterEncoding("utf-8");
		String jsoncallback = request.getParameter("jsoncallback");
		if(StringUtils.isNotBlank(jsoncallback)){
			response.getWriter().write(jsoncallback+"("+content+")");
		}
		response.getWriter().write(content);
	}
	/**
	 * 
	* @Title: getFlagshipByRealmName
	* @Description: 旗舰店
	* @param @param realmName
	* @param @return
	* @param @throws Exception    设定文件
	* @return Flagship    返回类型
	* @throws
	 */
	public Flagship getFlagshipByRealmName(String realmName,Boolean isPreview) throws Exception{
//		Flagship flagship = flagshipService.getFlagshipByRealmName(realmName);
//		String branchs = flagshipService.getOnLineBranchsId(flagship.getBranchs());
//		flagship.setBranchs(branchs);
		return flagshipService.getFlagshipByRealmName(realmName,isPreview);
	}
	/**
	 * 
	* @Title: recommendProduct
	* @Description: 招牌推荐
	* @param @param merchantId
	* @param @return    设定文件
	* @return Goods    返回类型
	* @throws
	 */
	public Goods recommendProduct(Long merchantId){
		return goodsService.getGoodsByBrandId(merchantId);
	}
	/**
	 * 
	* @Title: getMerchantBymerchantId
	* @Description: 品牌
	* @param @param merchantId
	* @param @return    设定文件
	* @return MerchantForm    返回类型
	* @throws
	 */
	public MerchantForm getMerchantBymerchantId(Long merchantId){
		return shopsBaoService.getShangpubaoDetailById(merchantId);
	}
	/**
	 * 
	* @Title: getAlbum
	* @Description: 相册
	* @param @param merchantId
	* @param @return    设定文件
	* @return List<String[]>    返回类型
	* @throws
	 */
	public List<String[]> getAlbum(Long merchantId){
		return getMerchantBymerchantId(merchantId).getListMerchantbaoLogo();
	}
	/**
	 * 
	* @Title: hotProducts
	* @Description: 热销
	* @param     设定文件
	* @return void    返回类型
	* @throws
	 */
	public List<GoodsForm> hotProducts(String branchids){
		//author wenjie.mai 热销商品有多少显示多少、并且不添加分页
		int totalCount = shopsBaoService.getGoodsIdTotalCount(branchids);
		if(totalCount == 0)
			return new LinkedList<GoodsForm>();
		Pager pager = PagerHelper.getPager(1,totalCount,totalCount);
		List<Long> listIds = shopsBaoService.getGoodsCountIds(branchids, pager); // 不包括下架商品、售完商品
		return goodsService.getGoodsFormByChildId(listIds);
	}
	/**
	 * 
	* @Title: getBranchMenuList
	* @Description: 从可点菜的分店中取菜
	* @param @param branchids
	* @param @return    设定文件
	* @return List<OrderMenu>    返回类型
	* @throws
	 */
	public List<OrderMenu> getBranchMenuList(String branchids){
		return dianCaiService.getBranchMenuList(branchids,Flagship.ORDER_MENU_COUNT);
	}
	/**
	 * 
	* @Title: getBranchsTakeAway
	* @Description: 分店外卖
	* @param @param branchids
	* @param @param menuCount
	* @param @return
	* @param @throws Exception    设定文件
	* @return Map<TakeAway,List<TakeAwayMenu>>    返回类型
	* @throws
	 */
	public Map<TakeAway,List<TakeAwayMenu>> getBranchsTakeAway(String branchids,Integer menuCount) throws Exception{
		
		return waiMaiService.getBranchsTakeAway(branchids,menuCount);
	}
	
	@RequestMapping("/getFlagShipList.do")
	public String getFlagShipList(HttpServletRequest request,HttpServletResponse response){
		
		Map<String,Long> mapCity = (Map<String,Long>) memCacheService.get("CITY_CATLOG");
		if (mapCity == null) {
			mapCity = BeanUtils.getCity(request, "regionCatlogDao");
			memCacheService.set("CITY_CATLOG", mapCity);
		}
		String city = CityUtils.getCity(request, response);
		if (city == null || "".equals(city)) {
			city = "beijing";
		}
		Long cityId = null;
		if (mapCity != null) {
			cityId = mapCity.get(city.trim());
		}
		
//		String currentPage = request.getParameter("cpage");暂无分页功能
		//1.查询旗舰店总数
		int flagshipCount = flagshipService.getFlagShipTotalCountForCity(cityId);
		
		Pager pager = PagerHelper.getPager(1,flagshipCount,1000);//由于暂无分页、每页显示个数设置1000
		
		List<Flagship> flaglist =  flagshipService.getFlagShipInfo(cityId,pager);
		
		request.setAttribute("flaglist", flaglist);
		
		return "/flagship/flagshipList";
	}
}
