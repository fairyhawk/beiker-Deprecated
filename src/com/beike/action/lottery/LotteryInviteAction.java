package com.beike.action.lottery;

import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.beike.action.user.BaseUserAction;
import com.beike.service.invite.LotteryInviteService;
import com.beike.util.WebUtils;

/**
 * @Title:
 * @Package com.beike.action.lottery
 * @Description: 0元抽奖 2.0版本 邀请Action
 * @author wenjie.mai
 * @date Dec 21, 2011 1:40:48 PM Company:Sinobo
 * @version V1.0
 */
@Controller
public class LotteryInviteAction extends BaseUserAction {

	public LotteryInviteAction() {

	}

	@Autowired
	private LotteryInviteService lotteryInviteService;

	private final Logger log = Logger.getLogger(this.getClass());

	private static String SHORT_URL = "SHORTURL";

	@SuppressWarnings("unchecked")
	@RequestMapping("/showLotteryInvite.do")
	public String getLotteryInviteInfo(HttpServletRequest request,
			HttpServletResponse response) {

		// 1.获得短地址 2.验证短地址 3.将抽奖信息放入memCacheService
		String prizeId = null;
		log.debug("********************** 验证短地址信息 **********************");
		try {
			super.setCookieUrl(request, response);
			String shortUrl = request.getParameter("shorturl"); // 获得短地址
			log.debug("shorturl...." + shortUrl);
			List shortList = lotteryInviteService.getShortUrl(shortUrl);

			if (shortList == null || shortList.size() == 0) {
				return "redirect:../404.html";
			}

			Map mx = (Map) shortList.get(0);
			String shortMessage = (String) mx.get("shortmessage");
			// 根据短地址查找数据库中 对应真正的信息 并把prizeid 解析出来 update by tianye
			if (shortMessage != null && !"".equals(shortMessage)) {
				prizeId = shortMessage
						.substring(shortMessage.lastIndexOf("=") + 1);
			}
			// 短地址加入cookie
			Cookie cookie = WebUtils.cookie(SHORT_URL, shortUrl, 60 * 60 * 24);
			response.addCookie(cookie);

		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:../404.html";
		}
		return "redirect:http://www.qianpin.com/lottery/lotteryNewAction.do?command=showLotteryGoods&prizeid="
				+ prizeId;
	}

}
