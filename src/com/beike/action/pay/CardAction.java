package com.beike.action.pay;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.beike.action.pay.hessianclient.TrxHessianServiceGateWay;
import com.beike.entity.user.User;
import com.beike.util.Amount;
import com.beike.util.Constant;
import com.beike.util.WebUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.json.JSONException;
import com.beike.util.json.JsonUtil;
import com.beike.util.singletonlogin.SingletonLoginUtils;

@Controller
public class CardAction extends BaseTrxAction {

	private final MemCacheService memCacheService = MemCacheServiceImpl
			.getInstance();

	private final Log logger = LogFactory.getLog(CardAction.class);

	@Resource(name = "webClient.trxHessianServiceGateWay")
	private TrxHessianServiceGateWay trxHessianServiceGateWay;

	/**
	 * 根据卡号密码查询充值
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("/ucenter/updateCard.do")
	public void updateCard(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String cardNo = request.getParameter("cardNo");// 卡号
		String cardPwd = request.getParameter("cardPwd"); // 卡密

		logger.info("++++++++++++updateCard.do++++++cardNo=" + cardNo
				+ "++++++++++++++++++++++++++++");

		User user = SingletonLoginUtils.getMemcacheUser(request);

		if (user != null) {
			String userId = String.valueOf(user.getId());
			Map<String, String> sourceMap = new HashMap<String, String>();
			sourceMap.put("cardNo", cardNo);
			sourceMap.put("cardPwd", cardPwd);
			sourceMap.put("userId", userId);
			sourceMap.put("reqChannel","WEB");
			Map<String, String> returnMap = trxHessianServiceGateWay
					.topupCard(sourceMap);

			String returnStr = this.cardInfoStr(returnMap, cardNo,"TOPUP");
			response.getWriter().write(returnStr);
			return;

		} else {
			throw new Exception("user is null!");
		}

	}

	/**
	 * 根据卡号密码查询
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("/ucenter/queryCard.do")
	public void queryCard(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String validCode = request.getParameter(Constant.USER_REGIST_CODE);

		String cookieCode = WebUtils.getCookieValue("RANDOM_VALIDATE_CODE",
				request);
		String sessionCode = (String) memCacheService.get("validCode_"
				+ cookieCode);
		Map<String, String> map = new HashMap<String, String>();
		map.put("RSPCODE", "2"); // 验证码不正确
		String jsonStr = "";
		try {
			jsonStr = JsonUtil.mapToJson(map);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (validCode == null || validCode.length() == 0 || cookieCode == null
				|| sessionCode.length() == 0 || !validCode.equalsIgnoreCase(sessionCode)) {
			response.getWriter().write(jsonStr);
			return;

		}

		String cardNo = request.getParameter("cardNo");// 卡号
		String cardPwd = request.getParameter("cardPwd"); // 卡密

		logger.info("++++++++++++getCard.do++++++cardNo=" + cardNo
				+ "++++++++++++++++++++++++++++");
		Map<String, String> sourceMap = new HashMap<String, String>();
		sourceMap.put("cardNo", cardNo);
		sourceMap.put("cardPwd", cardPwd);
		sourceMap.put("reqChannel","WEB");
		
		Map<String, String> returnMap = trxHessianServiceGateWay
				.queryCardInfo(sourceMap);
		String returnStr = this.cardInfoStr(returnMap, cardNo,"QUERY");
		response.getWriter().write(returnStr);
		return;
	}

	/**
	 * 首页跳转到千品卡充值页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/ucenter/toCard.do")
	public String toCard(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		//add by qiaowb 2012-03-06 判断是否登录，未登录跳转至登录页面，登录后直接进入千品卡充值页面
		User user = SingletonLoginUtils.getMemcacheUser(request);
		if (user == null) {
			String requesturl = WebUtils.getRequestPath(request);
			Cookie requestUrlCookie = WebUtils.cookie("REQUESTURI_REFER_COOKIE",requesturl, -1);
			response.addCookie(requestUrlCookie);
			request.getSession().setAttribute("REQUESTURI_REFER_COOKIE", requesturl);
			return "redirect:/forward.do?param=login";
		}
		return "/ucenter/qpCard";
	}

	/**
	 * 组装页面AJAX需求信息
	 * 
	 * @param returnMap
	 * @param cardNo
	 * @return
	 */
	public String cardInfoStr(Map<String, String> returnMap, String cardNo,
			String opType) {

		String rspcode = returnMap.get("rspCode");
		String str = new String();
		Map<String, String> map = new HashMap<String, String>();
		if ("1".equals(rspcode)) {

			String cardPwd = returnMap.get("cardPwd");
			String cardValue = returnMap.get("cardValue");
			double balance = 0.0;
			if ("TOPUP".equals(opType)) {// 如果是充值，则获取余额
				balance = Double.parseDouble(returnMap.get("balance"));
			}
			double cardValueDou = Amount.add(Double.parseDouble(cardValue),balance);
			String loseDate = returnMap.get("loseDate");

			logger.info("+++++++++++rspCode:" + rspcode + " + ++cardNo ："
					+ cardNo + "++++++++++++++++++++++++++++");

			map.put("RSPCODE", "1");
			map.put("cardNo", cardNo);
			map.put("cardPwd", cardPwd);
			map.put("loseDate", loseDate);
			map.put("cardValue", String.valueOf(cardValueDou));
			try {
				str = JsonUtil.mapToJson(map);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return str.toString();
		} else {
			// returnMap.get("RSPCODE"));//1700卡被充值过 1701卡密无效 1702卡过期 1703卡状态无效
			map.put("RSPCODE", rspcode);
			map.put("cardNo", cardNo);
			try {
				str = JsonUtil.mapToJson(map);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return str.toString();
		}
	}
	
	
}
