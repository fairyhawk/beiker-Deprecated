package com.beike.wap.action.city;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.beike.util.MCookieKey;
import com.beike.util.WebUtils;
import com.beike.util.ipparser.CityUtils;
import com.beike.wap.action.user.MBaseUserAction;


/**
 * <p>
 * Title:手机用户相关action
 * </p>
 * <p>
 * Description:城市选择
 * </p>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: Sinobo
 * </p>
 * 
 * @date 8-11
 * @author kun.wang
 * @version 1.0
 */

@Controller
public class MCityAction extends MBaseUserAction{
	/** 日志记录 */
	private static Log log = LogFactory.getLog(MCityAction.class);
	
//	@Autowired
//	private AreaService mAreaService;
	
	@RequestMapping("/wap/city/mForward.do")
	public String cityForward(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		super.setCookieUrl(request, response);
		String param = request.getParameter("param");
		String cityStr = CityUtils.getCity(request, response);
		log.info(param + "\n" + cityStr);
		String cityName = WebUtils.getCookieValue(MCookieKey.CITY_COOKIENAME, request);
		if(param == null)
		{
			log.info("param is null, return 404.html");
			return "redirect:/jsp/wap/404.jsp";
		}
		// 检查Memcache中是否存在地市信息，若不存在，存入信息
		
		if (param.indexOf(".") != -1) {
			log.info("choose city");
			
			String prefix = param.substring(0, param.indexOf("."));
			String after = param.substring(param.indexOf(".") + 1);
			
			String monthState = WebUtils.getCookieValue(MCookieKey.MEM_MONTH_KEY_ID, request);
			// 第一次进入时，自动判断是否展现城市选择页
			if(cityName != null && !cityName .equals("") && after.equals("index"))
			{
				// 进入首页 TODO
				log.info("the city in cookie is existed, return index page");
				String msg = request.getParameter("ERRMSG");
				if((monthState == null || !monthState.equals("true")) && msg == null)
				{
					return "redirect:/wap/user/logout.do";
				}
				return "redirect:/wap/goods/goodsIndexController.do?method=queryIndexShowMes";
			}
			
			// 固定跳转到城市选择页面
			model.addAttribute("cityList", CityUtils.cityList);
			model.addAttribute("cityMap", CityUtils.cityMap);
			model.addAttribute("last_city", CityUtils.getCityName(cityStr));
			
			return "wap/city/city";
		} else {
			return "wap/city/city";
		}
	}
	
	@RequestMapping("/wap/city/mCityChoose.do")
	public String cityChoose(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		String city = request.getParameter("city");
		if(city != null)
		{
			city = city.trim();
		}
		
		if(city == null || city.equals(""))
		{
			// TODO city 无值的时候处理
			log.info("city param is null");
			return "redirect:/jsp/wap/500.jsp";
		}
		int validy = 60 * 60 * 24 * 30 * 12 * 10;
		WebUtils.setMCookieByKey(response, MCookieKey.CITY_COOKIENAME, city, validy);
		
		// 返回首页
		return "redirect:/wap/goods/goodsIndexController.do?method=queryIndexShowMes";
	}
}
