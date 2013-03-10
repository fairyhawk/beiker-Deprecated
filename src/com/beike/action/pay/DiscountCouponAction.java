package com.beike.action.pay;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.beike.action.pay.hessianclient.TrxHessianServiceGateWay;
import com.beike.common.entity.coupon.TrxCoupon;
import com.beike.core.service.trx.coupon.TrxCouponService;
import com.beike.entity.user.User;
import com.beike.page.Pager;
import com.beike.util.Constant;
import com.beike.util.RequestUtil;
import com.beike.util.StringUtils;
import com.beike.util.WebUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.json.JSONException;
import com.beike.util.json.JsonUtil;
import com.beike.util.singletonlogin.SingletonLoginUtils;

/**   
 * @title: DiscountCouponAction
 * @package com.beike.action.pay
 * @description: 线下优惠券Action
 * @author wangweijie  
 * @date 2012-7-12 下午03:33:18
 * @version v1.0   
 */

@Controller("discountCoupon")
public class DiscountCouponAction {
	private static final Log log = LogFactory.getLog(DiscountCouponAction.class);

	private final MemCacheService memCacheService = MemCacheServiceImpl.getInstance();

	@Resource(name = "webClient.trxHessianServiceGateWay")
	private TrxHessianServiceGateWay trxHessianServiceGateWay;
	
	@Autowired
	private TrxCouponService trxCouponService;
	
	private static final int PAGE_SIZE = 10;
	
	
	/**
	 * 首页跳转到优惠券充值页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/ucenter/toActivateCoupon.do")
	public String toActivateCoupon(HttpServletRequest request,HttpServletResponse response) throws Exception {
		//判断是否登录，未登录跳转至登录页面，登录后直接进入千品卡充值页面
		User user = SingletonLoginUtils.getMemcacheUser(request);
		if (null == user) {
			String requesturl = WebUtils.getRequestPath(request);
			Cookie requestUrlCookie = WebUtils.cookie("REQUESTURI_REFER_COOKIE",requesturl, -1);
			response.addCookie(requestUrlCookie);
			request.getSession().setAttribute("REQUESTURI_REFER_COOKIE", requesturl);
			return "redirect:/forward.do?param=login";
		}
		
		return "/ucenter/activateCoupon";
	}
	
	/**
	 * 首页跳转到查看所有优惠券界面
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/ucenter/allCoupon.do")
	public String allCoupon(HttpServletRequest request,HttpServletResponse response, ModelMap modelMap) throws Exception {
		//判断是否登录，未登录跳转至登录页面，登录后直接进入千品查看所有优惠券界面
		User user = SingletonLoginUtils.getMemcacheUser(request);
		if (null == user) {
			String requesturl = WebUtils.getRequestPath(request);
			Cookie requestUrlCookie = WebUtils.cookie("REQUESTURI_REFER_COOKIE",requesturl, -1);
			response.addCookie(requestUrlCookie);
			request.getSession().setAttribute("REQUESTURI_REFER_COOKIE", requesturl);
			return "redirect:/forward.do?param=login";
		}
		int cpage = RequestUtil.getUnsignedInt(request, Constant.CPAGE, 0);
		
		int totalRows = trxCouponService.queryCountAllTrxCouponsByUserId(user.getId());
		Pager pager = new Pager(cpage, totalRows, PAGE_SIZE);

		List<TrxCoupon> couponList = trxCouponService.queryAllTrxCouponsByUserId(user.getId(),pager.getStartRow(),PAGE_SIZE);
		modelMap.addAttribute(Constant.PB, pager);
		modelMap.addAttribute("cpage", cpage);
		modelMap.addAttribute("couponList", couponList);
		return "/ucenter/showCoupon";
	}
	
	@RequestMapping("/ucenter/activateCoupon.do")
	public void activateCopon(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String validCode = request.getParameter(Constant.USER_REGIST_CODE);
		String cookieCode = WebUtils.getCookieValue("RANDOM_VALIDATE_CODE",request);
		String sessionCode = (String) memCacheService.get("validCode_" + cookieCode);
		
		
		if (StringUtils.isEmpty(validCode) || null == cookieCode || StringUtils.isEmpty(sessionCode) || !validCode.equalsIgnoreCase(sessionCode)) {
			/*
			 * 验证码错误
			 */
			Map<String, String> map = new HashMap<String, String>();
			map.put("RSPCODE", "2"); 	// 验证码不正确
			String jsonStr = "";
			try {
				jsonStr = JsonUtil.mapToJson(map);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			response.getWriter().write(jsonStr);
			return;
		}
		
		//判断用户是否正常登陆
		User user = SingletonLoginUtils.getMemcacheUser(request);
		if(null == user){
			throw new Exception("user is null!");
		}
		
		
		String couponCode = StringUtils.toTrim(request.getParameter("couponCode"));	//优惠券密码
		Map<String, String> sourceMap = new HashMap<String, String>();
		sourceMap.put("couponPwd", couponCode);		//优惠券密码
		sourceMap.put("userId", String.valueOf(user.getId()));	//用户ID
		sourceMap.put("reqChannel","WEB");	//web
		log.info("++++activity coupon+++sourceMap=" +sourceMap);
		Map<String, String> returnMap = trxHessianServiceGateWay.activateCoupon(sourceMap);
		log.info("+++++++++coupon top up return:" + returnMap);
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(couponInfoStr(returnMap));
		return;
	}
	
	
	/**
	 * 组装页面AJAX需求信息
	 * 
	 * @param returnMap
	 * @param cardNo
	 * @return
	 */
	public String couponInfoStr(Map<String, String> returnMap) {

		String rspcode = returnMap.get("rspCode");
		String type = returnMap.get("type");
		String str = new String();
		Map<String, String> map = new HashMap<String, String>();
		if ("1".equals(rspcode)) {
			map.put("RSPCODE", "1");
			map.put("TYPE", type);
			if("TOPON".equals(type)){
				double couponValue = Double.parseDouble(returnMap.get("couponValue"));	//充值金额
				String startDate = returnMap.get("startDate");	//过期时间
				String lostDate = returnMap.get("loseDate");	//过期时间
				//获取余额
				double balance = Double.parseDouble(returnMap.get("balance"));
				
				log.info("+++++++++++rspCode:" + rspcode + " + ++couponLoseDate ："+ lostDate + "+++balance:"+balance+"+++++++++++++++++++++++++");
				
			
				map.put("couponValue", String.valueOf(couponValue));
				map.put("startDate", startDate);
				map.put("loseDate", lostDate);	
				map.put("balance", String.valueOf(balance));
			}else if("BINDING".equals(type)){
				String couponValue = returnMap.get("couponValue");
				String couponName = returnMap.get("couponName");
				String limitInfo = returnMap.get("limitInfo");
				String validDate = returnMap.get("validDate");
				map.put("couponValue", couponValue);
				map.put("couponName", couponName);
				map.put("limitInfo", limitInfo);	
				map.put("validDate", validDate);
			}
			try {
				str = JsonUtil.mapToJson(map);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return str.toString();
		} else {
			/*
			 *2100优惠券状态无效
			 *2101优惠券已使用
			 *2102优惠券密码无效（非已使用、过期）
			 *2103优惠券过期
			 *2104非首次使用优惠券
			 **/
			map.put("RSPCODE", rspcode);
			map.put("TYPE", type);
			try {
				str = JsonUtil.mapToJson(map);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return str.toString();
		}
	}
}
