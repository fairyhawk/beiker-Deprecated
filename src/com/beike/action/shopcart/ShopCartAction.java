package com.beike.action.shopcart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.beike.action.LogAction;
import com.beike.core.service.trx.limit.PayLimitService;
import com.beike.entity.miaosha.MiaoSha;
import com.beike.entity.shopcart.ShopCart;
import com.beike.entity.shopcart.ShopItem;
import com.beike.entity.shopcart.ShopcartSummary;
import com.beike.entity.user.User;
import com.beike.form.GoodsForm;
import com.beike.service.goods.GoodsService;
import com.beike.service.miaosha.MiaoShaService;
import com.beike.service.shopcart.ShopCartService;
import com.beike.util.DateUtils;
import com.beike.util.WebUtils;
import com.beike.util.shopcart.ShopJsonUtil;
import com.beike.util.singletonlogin.SingletonLoginUtils;

/**
 * 购物车的Action
 * 
 * @author zx.liu
 */
@Controller("shopcartController")
public class ShopCartAction {

	private final Log logger = LogFactory.getLog(ShopCartAction.class);

	@Autowired
	private ShopCartService shopCartService;

	@Autowired
	private PayLimitService payLimitService;

	@Autowired
	private GoodsService goodsService;
	
	@Autowired
	private MiaoShaService miaoShaService;


	@RequestMapping(value = "/shopcart/shopcart.do", params = "command=addShopitem")
	public String goToShopCart(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String miaoshaId = request.getParameter("miaoshaId");
		String goodsId = request.getParameter("goodsid");
		String merchantId = request.getParameter("merchantId");
		String buyCount = request.getParameter("buyCount");
		logger.info("++++++++++miaoshaId="+miaoshaId+"++++++++++goodsId="+goodsId+"+++++++merchantId="+merchantId+"++++++buyCount="+buyCount);
		if(miaoshaId==null||"".equals(miaoshaId)){
			miaoshaId="0";
		}
		if(!"0".equals(miaoshaId)){//秒杀信息校验
			MiaoSha miaosha = miaoShaService.findById(Long.valueOf(miaoshaId));
			boolean boo = this.getMiaoshaBoo(miaosha,goodsId);
			if(boo){
				return "redirect:/shopcart/shopcart.do?command=queryShopCart";
			}
		}
		// 购买数量
		Long lBuyCount = 1L;
		if (StringUtils.isNotEmpty(buyCount)) {
			lBuyCount = Long.parseLong(buyCount);
		}

		// 判断用户是否登录
		User user = SingletonLoginUtils.getMemcacheUser(request);
		if (user == null) {

			// 从Cookie获取购物车信息
			String shopJson = WebUtils.getCookieValue(
					ShopJsonUtil.ShopCookieKey, request);

			String jsonShopString = shopCartService.addTempShopItem(goodsId,
					merchantId, lBuyCount, shopJson,miaoshaId);
			// 添加临时购物车信息到用户端的Cookie 中去
			Cookie tempCookie = WebUtils.cookie(ShopJsonUtil.ShopCookieKey,
					jsonShopString, ShopJsonUtil.SECONDS_HALF_YEAR);
			response.addCookie(tempCookie);
			

			// 以下的else表示用户登录后的相关操作
		} else {

			shopCartService.addShopItem(goodsId, merchantId, lBuyCount,
					Long.toString(user.getId()),miaoshaId);//新添加如秒杀ID
			

		}
		// 打印日志
		Map<String, String> logMap = LogAction.getLogMap(request, response);
		logMap.put("action", "BuyAddtoChart");
		logMap.put("prdid", goodsId);
		LogAction.printLog(logMap);
		// 日志埋点2.0
		Map<String, String> logMap2 = LogAction.getLogMap(request, response);
		logMap2.put("action", "t_dpclick");
		logMap2.put("goodid", goodsId);
		LogAction.printLog(logMap2);

		return "redirect:/shopcart/shopcart.do?command=queryShopCart";

	}
	
	/**
	 * 判断是否符合秒杀商品
	 * @param miaoSha
	 * @return
	 */
	public boolean getMiaoshaBoo(MiaoSha miaoSha,String goodsId){
		boolean boo = true;
		if(miaoSha!=null){
		int miaoshaStatus = miaoSha.getMsStatus();
		String miaoShaGoodsId = miaoSha.getGoodsId().toString();
		Date startTime = new Date(miaoSha.getMsStartTime().getTime());
		Date endTime =new Date(miaoSha.getMsEndTime().getTime());
		boolean booDate = DateUtils.betweenBeginAndEnd(new Date(),startTime,endTime);
			 if(miaoshaStatus==1&&booDate&&miaoShaGoodsId.equals(goodsId)){
				boo = false;
			 }
		}else{
			boo = true;
		}
		return boo;
	}

	/**
	 * 查询购物车（临时购物车,用户购物车）
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/shopcart/shopcart.do", params = "command=queryShopCart")
	public String queryShopCart(HttpServletRequest request,
			HttpServletResponse response) {

		List<ShopItem> listShopItem = null;
		List<ShopCart> listShopCart = null;

		// 判断用户是否登录
		User user = SingletonLoginUtils.getMemcacheUser(request);
		if (user == null) {
			// 仅仅从Cookie 获取购物车信息
			String shopJson = WebUtils.getCookieValue(
					ShopJsonUtil.ShopCookieKey, request);
			// 将json 解析为List<ShopItem> 的集合
			listShopItem = shopCartService.getListShopItem(shopJson);
			listShopCart = shopCartService.getTempShopCart(listShopItem); // Service

		} else {
			// 以下的else表示用户登录后的相关操作
			listShopCart = shopCartService.queryShopCartByUserID(Long
					.toString(user.getId()));
			for (int i = 0; i < listShopCart.size(); i++) {
				Long canCount = 0L;
				Long ssCount = listShopCart.get(i).getMaxCount()
						- listShopCart.get(i).getSalesCount();// 商品上限量与已购买量差
				if (listShopCart.get(i).getUserBuyCount() != 0) {
					canCount = payLimitService.allowPayCount(listShopCart
							.get(i).getUserBuyCount(), user.getId(),
							listShopCart.get(i).getGoodsId(),listShopCart.get(i).getMiaoshaid());
					if (canCount > ssCount) {
						listShopCart.get(i).setCanCount(ssCount);
					} else {
						listShopCart.get(i).setCanCount(canCount);
					}
				} else {
					listShopCart.get(i).setCanCount(ssCount);
				}

			}
		}

		// 记录调整返回路径
		String requesturl = WebUtils.getRequestPath(request);
		Cookie requestUrlCookie = WebUtils.cookie("REQUESTURI_REFER_COOKIE",
				requesturl, -1);
		response.addCookie(requestUrlCookie);
		request.getSession()
				.setAttribute("REQUESTURI_REFER_COOKIE", requesturl);

		// 根据merchantId 来分类List<ShopCart> 集合
		List<ShopCart> shopCartList = shopCartService
				.classifyShopCartByMerid(listShopCart);
		request.setAttribute("shopcartList", shopCartList);
		request.setAttribute("shopcartSummary",
				shopCartService.getShopCartSummary(listShopCart));

		// 最近浏览商品
		String viewedGoods = WebUtils.getCookieValue("RECENTLY_VIEWED_GOODS",
				request);
		logger.info("/shopcart/shopcart.do viewedGoods:" + viewedGoods);
		if (StringUtils.isNotEmpty(viewedGoods)) {
			try {
				List<Long> lstViewedGoodsId = new ArrayList<Long>();
				String[] aryViewed = org.apache.commons.lang.StringUtils.split(
						viewedGoods, "_");
				logger.info("/shopcart/shopcart.do aryViewed:"
						+ ArrayUtils.toString(aryViewed));
				for (int i = 0; i < aryViewed.length; i++) {
					logger.info("/shopcart/shopcart.do aryViewed:"
							+ aryViewed[i]);
					if (!StringUtils.isBlank(aryViewed[i])) {
						lstViewedGoodsId.add(Long.parseLong(aryViewed[i]));
					}
				}
				if (lstViewedGoodsId != null && lstViewedGoodsId.size() > 0) {
					List<GoodsForm> lstViewedGoodsForm = goodsService
							.getGoodsFormByChildId(lstViewedGoodsId);
					request.setAttribute("lstViewedGoodsForm",
							lstViewedGoodsForm);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "shopcart/usershopcart";
	}

	/**
	 * 修改购物车中的信息,修改数量的同时修改日期为最新的日期值 通过Ajax来进行实时的响应,这里需要用到Ajax以保证界面不刷新
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * 
	 */
	@RequestMapping(value = "/shopcart/shopcart.do", params = "command=modifyShopitem")
	public void modifyShopItem(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// String merchantId = request.getParameter("merchantId"); // 品牌ID
		String goodsId = request.getParameter("goodsId"); // 商品ID
		String miaoshaId = request.getParameter("miaoshaId"); // 秒杀ID
		
		
		if(miaoshaId==null||"".equals(miaoshaId)){
			miaoshaId = "0";
		}
			
		String buyCount = request.getParameter("buyCount");
		// 判断用户是否登录
		User user = SingletonLoginUtils.getMemcacheUser(request);
		if (user == null) {
			// 从Cookie 获取购物车信息
			String shopJson = WebUtils.getCookieValue(
					ShopJsonUtil.ShopCookieKey, request);
			// 修改相应的购物车条目信息后返回Json 串
			String jsonShopString = shopCartService.modifyTempShopItem(goodsId,
					buyCount, shopJson,miaoshaId);
			Cookie tempCookie = null;
			if (null == jsonShopString) {
				tempCookie = WebUtils
						.removeableCookie(ShopJsonUtil.ShopCookieKey);
			} else {
				tempCookie = WebUtils.cookie(ShopJsonUtil.ShopCookieKey,
						jsonShopString, ShopJsonUtil.SECONDS_HALF_YEAR);
			}
			response.addCookie(tempCookie);

		} else {
			// 用户登录时需要做的的操作
			shopCartService.updateShopItem(goodsId, buyCount,
					SingletonLoginUtils.getLoginUserid(request).toString(),miaoshaId);

		}

	}

	/**
	 * 删除购物车的一个Item ,此处需要Ajax 完成实时的 操作
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/shopcart/shopcart.do", params = "command=deleteShopitem")
	public void deleteShopItem(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// 获取商品ID,基于此来删除购物车的一个Item
		String gMsId = request.getParameter("goodsId"); // 商品IDAnd秒杀ID
		String goodsId = ""; // 商品ID
		String miaoshaId = "0"; // 秒杀ID
		if(gMsId.contains("_")){
			String[] gMsArray = gMsId.split("_");
			goodsId = gMsArray[0];
			miaoshaId = gMsArray[1];
		}
		if(miaoshaId==null||"".equals(miaoshaId)){
			miaoshaId = "0";
		}
		// 判断用户是否登录
		User user = SingletonLoginUtils.getMemcacheUser(request);
		if (user == null) {
			// 从Cookie获取购物车信息
			String shopJson = WebUtils.getCookieValue(
					ShopJsonUtil.ShopCookieKey, request);
			// 删除相应的购物车Item 信息后返回Json 串
			String jsonShopString = shopCartService.removeTempShopItem(goodsId,
					shopJson,miaoshaId);
			Cookie tempCookie = null;
			if (null == jsonShopString) {
				tempCookie = WebUtils
						.removeableCookie(ShopJsonUtil.ShopCookieKey);
			} else {
				tempCookie = WebUtils.cookie(ShopJsonUtil.ShopCookieKey,
						jsonShopString, ShopJsonUtil.SECONDS_HALF_YEAR);
			}
			response.addCookie(tempCookie);

		} else {
			// 用户登录时需要做的的操作
			shopCartService.removeShopItem(goodsId, SingletonLoginUtils
					.getLoginUserid(request).toString(),miaoshaId);
		}

	}

	/**
	 * 批量删除购物车的多个Item ,此处需要Ajax 完成实时的操作
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/shopcart/shopcart.do", params = "command=delBatchShopitem")
	public void delBatchShopItem(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// 获取批量商品ID组合,基于此来删除购物车的多个Item
		String batchGoodsId = request.getParameter("batchGoodsId");
		String miaoshaId = request.getParameter("miaoshaId"); // 商品ID必须传值，就算是0
		// 判断用户是否登录
		User user = SingletonLoginUtils.getMemcacheUser(request);
		if (user == null) {

			// 从Cookie获取购物车信息
			String shopJson = WebUtils.getCookieValue(
					ShopJsonUtil.ShopCookieKey, request);
			String shopJsonString = shopCartService.removeBatchShopItem(
					batchGoodsId, shopJson,miaoshaId);
			Cookie tempCookie = null;
			if (null == shopJsonString) {
				tempCookie = WebUtils
						.removeableCookie(ShopJsonUtil.ShopCookieKey);
			} else {
				tempCookie = WebUtils.cookie(ShopJsonUtil.ShopCookieKey,
						shopJsonString, ShopJsonUtil.SECONDS_HALF_YEAR);
			}
			response.addCookie(tempCookie);

		} else {

			// 用户登录时需要做的的操作
			shopCartService.removeBatchGoods(batchGoodsId, SingletonLoginUtils
					.getLoginUserid(request).toString(),miaoshaId);
		}

	}
	
	
	/**
	 * 迷你购物车立即购买
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/shopcart/shopcart.do", params = "command=addShoppingCart")
	public String goToShoppingCart(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String miaoshaId = request.getParameter("miaoshaId");
		String goodsId = request.getParameter("goodsId");
		String merchantId = request.getParameter("merchantId");
		String buyCount = request.getParameter("buyCount");
		logger.info("++++++++++miaoshaId="+miaoshaId+"++++++++++goodsId="+goodsId+"+++++++merchantId="+merchantId+"++++++buyCount="+buyCount);
		if(miaoshaId==null||"".equals(miaoshaId)){
			miaoshaId="0";
		}
		if(!"0".equals(miaoshaId)){//秒杀信息校验
			MiaoSha miaosha = miaoShaService.findById(Long.valueOf(miaoshaId));
			boolean boo = this.getMiaoshaBoo(miaosha,goodsId);
			if(boo){
				return "redirect:/shopcart/shopcart.do?command=queryShopCart";
			}
		}
		// 购买数量
		Long lBuyCount = 1L;
		if (StringUtils.isNotEmpty(buyCount)) {
			lBuyCount = Long.parseLong(buyCount);
		}
		
		User user = SingletonLoginUtils.getMemcacheUser(request);
		String userId = "";
		if(user!=null){
			userId = user.getId()+"";
		}
		//商品详情页支付限购数量
		String limitCount = payLimitService.toPayLimitCountNew(goodsId,miaoshaId,userId);
		
		ShopItem listShopItem = null;
		// 判断用户是否登录
		if (user == null) {

			// 从Cookie获取购物车信息
			String shopJson = WebUtils.getCookieValue(
					ShopJsonUtil.ShopCookieKey, request);

			String jsonShopString = shopCartService.addTempShopItemNew(goodsId,
					merchantId, lBuyCount, shopJson,miaoshaId,limitCount);
			String[] jsonStr = jsonShopString.split("\\|");
			// 添加临时购物车信息到用户端的Cookie 中去
			Cookie tempCookie = WebUtils.cookie(ShopJsonUtil.ShopCookieKey,
					jsonStr[0], ShopJsonUtil.SECONDS_HALF_YEAR);
			
			response.addCookie(tempCookie);
			
			// 仅仅从Cookie 获取购物车信息
			// 将json 解析为List<ShopItem> 的集合
			 listShopItem = shopCartService.getListShopItem(jsonStr[0],goodsId,miaoshaId);

			// 以下的else表示用户登录后的相关操作
		} else {

			shopCartService.addShopItemNew(goodsId, merchantId, lBuyCount,
					Long.toString(user.getId()),miaoshaId,limitCount);//新添加如秒杀ID
			
				listShopItem = shopCartService.preQryInWtDBShopCartByUserID(Long
					.toString(user.getId()),goodsId,merchantId,miaoshaId);

		}
		StringBuffer goodsidCount = new StringBuffer();
		if(listShopItem!=null){
		goodsidCount.append(listShopItem.getGoodsid().toString());
		goodsidCount.append(".");
		goodsidCount.append(listShopItem.getBuy_count().toString());
		goodsidCount.append(".");
		goodsidCount.append(listShopItem.getMiaoshaid().toString());
		}else{
			return "redirect:/shopcart/shopcart.do?command=queryShopCart";
		}
		if(0==listShopItem.getBuy_count()){
			response.setCharacterEncoding("utf-8");
			response.getWriter().write("<!DOCTYPE>");
			response.getWriter().write("<html>");
			response.getWriter().write("<head>");
			response.getWriter().write("<meta charset=\"utf-8\" />");
			response.getWriter().write("<title>error</title>");
			response.getWriter().write("</head>");
			response.getWriter().write("<body>");
			response.getWriter().write("<script type=\"text/javascript\">");
			response.getWriter().write("alert('对不起，您最多还可购买0件此商品'); history.go(-1);");
			response.getWriter().write("</script>");
			response.getWriter().write("</body>");
			response.getWriter().write("</html>");
			return null;
		}
		// 打印日志
		Map<String, String> logMap = LogAction.getLogMap(request, response);
		logMap.put("action", "BuyAddtoChart");
		logMap.put("prdid", goodsId);
		LogAction.printLog(logMap);
		// 日志埋点2.0
		Map<String, String> logMap2 = LogAction.getLogMap(request, response);
		logMap2.put("action", "t_dpclick");
		logMap2.put("goodid", goodsId);
		LogAction.printLog(logMap2);
		logger.info("+++++++++++++++++"+goodsidCount.toString());
		return "redirect:/pay/goshopping.do?goodsidCount="+goodsidCount.toString();

	}
	
	/**
	 * 迷你购物车添加功能
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/shopcart/shopcart.do", params = "command=addShopitemNew")
	public void addShopCartNew(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	
		String miaoshaId = request.getParameter("miaoshaId");
		String goodsId = request.getParameter("goodsId");
		String merchantId = request.getParameter("merchantId");
		String buyCount = request.getParameter("buyCount");
		logger.info("++++++++++miaoshaId="+miaoshaId+"++++++++++goodsId="+goodsId+"+++++++merchantId="+merchantId+"++++++buyCount="+buyCount);
		if(miaoshaId==null||"".equals(miaoshaId)){
			miaoshaId="0";
		}
		String limitStatus = "";
		if(!"0".equals(miaoshaId)){//秒杀信息校验
			MiaoSha miaosha = miaoShaService.findById(Long.valueOf(miaoshaId));
			boolean boo = this.getMiaoshaBoo(miaosha,goodsId);
			if(boo){
				limitStatus = "limitError";
			}
		}
		// 购买数量
		Long lBuyCount = 1L;
		if (StringUtils.isNotEmpty(buyCount)) {
			lBuyCount = Long.parseLong(buyCount);
		}
		User user = SingletonLoginUtils.getMemcacheUser(request);
		String userId = "";
		if(user!=null){
			userId = user.getId()+"";
		}
		//商品详情页支付限购数量
		String limitCount = payLimitService.toPayLimitCountNew(goodsId,miaoshaId,userId);
		
		if(Long.valueOf(limitCount).longValue()>0){
		// 判断用户是否登录
		if (user == null) {

			// 从Cookie获取购物车信息
			String shopJson = WebUtils.getCookieValue(
					ShopJsonUtil.ShopCookieKey, request);

			String jsonShopString = shopCartService.addTempShopItemNew(goodsId,
					merchantId, lBuyCount, shopJson,miaoshaId,limitCount);
			// 添加临时购物车信息到用户端的Cookie 中去
			String[] jsonStr = jsonShopString.split("\\|");
			
			Cookie tempCookie = WebUtils.cookie(ShopJsonUtil.ShopCookieKey,
					jsonStr[0], ShopJsonUtil.SECONDS_HALF_YEAR);
			response.addCookie(tempCookie);
			
			limitStatus = jsonStr[1];
			// 以下的else表示用户登录后的相关操作	
		} else {

			limitStatus = shopCartService.addShopItemNew(goodsId, merchantId, lBuyCount,
					Long.toString(user.getId()),miaoshaId,limitCount);//新添加如秒杀ID
			

		}
		}else{
			 limitStatus = "limitError";
		}
		try {
			response.getWriter().write(limitStatus);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 打印日志
		Map<String, String> logMap = LogAction.getLogMap(request, response);
		logMap.put("action", "BuyAddtoChart");
		logMap.put("prdid", goodsId);
		LogAction.printLog(logMap);
		// 日志埋点2.0
		Map<String, String> logMap2 = LogAction.getLogMap(request, response);
		logMap2.put("action", "t_dpclick");
		logMap2.put("goodid", goodsId);
		LogAction.printLog(logMap2);

		return ;

	}

	/**
	 * 查询迷你购物车（临时购物车,用户购物车）
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/shopcart/shopcart.do", params = "command=queryShopCartNew")
	public void queryShopCartNew(HttpServletRequest request,
			HttpServletResponse response) {

		List<ShopItem> listShopItem = null;
		List<ShopCart> listShopCart = null;

		// 判断用户是否登录
		User user = SingletonLoginUtils.getMemcacheUser(request);
		if (user == null) {
			// 仅仅从Cookie 获取购物车信息
			String shopJson = WebUtils.getCookieValue(
					ShopJsonUtil.ShopCookieKey, request);
			// 将json 解析为List<ShopItem> 的集合
			listShopItem = shopCartService.getListShopItem(shopJson);
			listShopCart = shopCartService.getTempShopCart(listShopItem); // Service

		} else {
			// 以下的else表示用户登录后的相关操作
			listShopCart = shopCartService.queryShopCartByUserID(Long
					.toString(user.getId()));
			for (int i = 0; i < listShopCart.size(); i++) {
				Long canCount = 0L;
				Long ssCount = listShopCart.get(i).getMaxCount()
						- listShopCart.get(i).getSalesCount();// 商品上限量与已购买量差
				if (listShopCart.get(i).getUserBuyCount() != 0) {
					canCount = payLimitService.allowPayCount(listShopCart
							.get(i).getUserBuyCount(), user.getId(),
							listShopCart.get(i).getGoodsId(),listShopCart.get(i).getMiaoshaid());
					if (canCount > ssCount) {
						listShopCart.get(i).setCanCount(ssCount);
					} else {
						listShopCart.get(i).setCanCount(canCount);
					}
				} else {
					listShopCart.get(i).setCanCount(ssCount);
				}

			}
		}
		String jsonStr = "";
		if(listShopCart!=null&&listShopCart.size()>0){
		listShopCart = this.getListShopCart(listShopCart);
		 jsonStr = ShopJsonUtil.writeJsonShopCart(listShopCart);
		}
		logger.info("+++++++++++++++++jsonStr="+jsonStr);
		try {
			response.setCharacterEncoding("utf-8");
			if(jsonStr!=null){
			response.getWriter().write(jsonStr);
			}else{
				response.getWriter().write("");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ;
	
	}
	
	/**
	 * 迷你购物车处理最多显示10行
	 * @param listShopCart
	 * @return
	 */
	public List<ShopCart> getListShopCart(List<ShopCart> listShopCart){
		List<ShopCart> shopTrueCartList = new ArrayList<ShopCart>();//为在线商品
		List<ShopCart> shopFalseCartList = new ArrayList<ShopCart>();//已下线商品
		List<ShopCart> shopCartList = new ArrayList<ShopCart>();//迷你购物车展示所有商品
		
		if(listShopCart.size()<11){
			return listShopCart;
		}else{
			for(ShopCart sc:listShopCart){
				if(sc.getIsavaliable()==1){
					shopTrueCartList.add(sc);
				}else{
					shopFalseCartList.add(sc);
			}
			}
			if(shopTrueCartList!=null&&shopTrueCartList.size()>0){
				if(shopTrueCartList.size()<11){
					shopCartList.addAll(shopTrueCartList);
				}else{
					int k = shopTrueCartList.size() - 10;
					for(int i=k;i<shopTrueCartList.size();i++){
						shopCartList.add(shopTrueCartList.get(i));
					}
				}
			}
			shopCartList.addAll(shopFalseCartList);
		return shopCartList;
	}
	}
	
	@RequestMapping(value = "/shopcart/shopcart.do", params = "command=queryCount")
	public void getUserLoginInfoDiv(HttpServletRequest request,HttpServletResponse response) {
		User user = SingletonLoginUtils.getMemcacheUser(request);
		if (user != null) {
			request.setAttribute("DIV_QIANPIN_USER", user);
		}
		ShopcartSummary shopcartSummary = shopCartService
				.getShopSummary(request);
		try {
			String count = shopcartSummary.getTotalProduct()+"";
			response.setCharacterEncoding("utf-8");
			response.getWriter().write(count);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
