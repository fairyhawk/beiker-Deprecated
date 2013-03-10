package com.beike.action.lottery.full;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.beike.action.user.BaseUserAction;
import com.beike.common.entity.trx.lottery.full.FullLottery;
import com.beike.common.exception.BaseException;
import com.beike.core.service.trx.lottery.full.FullLotteryService;
import com.beike.entity.goods.Goods;
import com.beike.entity.user.User;
import com.beike.util.LotteryConstant;
import com.beike.util.WebUtils;
import com.beike.util.ipparser.CityUtils;
import com.beike.util.singletonlogin.SingletonLoginUtils;

/**
 * 满额活动抽奖
 * 
 * @author jianjun.huo
 * 
 */
@Controller
@RequestMapping("/fullLottery/fullLottery.do")
public class FullLotteryAction extends BaseUserAction
{

	@Autowired
	private FullLotteryService fullLotteryService;

	/**
	 * 跳转到登录
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "command=forwardLogin")
	public String getForwardLogin(HttpServletRequest request, HttpServletResponse response)
	{

		// 写入cookie ,回跳页面
		String requesturl = "/fullLottery/fullLottery.do?command=queryFullLotteryList";
		Cookie requestUrlCookie = WebUtils.cookie("REQUESTURI_REFER_COOKIE", requesturl, 60 * 3);
		response.addCookie(requestUrlCookie);
		return "redirect:../forward.do?param=login";

	}

	/**
	 * 查询抽奖信息
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "command=queryFullLotteryList")
	public String findFullLotteryList(HttpServletRequest request, HttpServletResponse response)
	{

		long userId = 0;
		int userLotteryCount = 0;

		// 判断登录 ,没登录,则跳转到登录页面
		User user = SingletonLoginUtils.getMemcacheUser(request);
		if (null != user)
		{
			userId = user.getId();
			userLotteryCount = fullLotteryService.findTrxorderGoodsAndFullLottery(userId, LotteryConstant.fullBeginDate, LotteryConstant.fullEndDate);
		}

		// 奖项展示图片列表
		String localCityName = CityUtils.getCity(request, response);
		List<Goods> fullGoodsList = fullLotteryService.findLotteryGoodsList(localCityName);

		// 中奖列表
		List<FullLottery> fullLotteryList = fullLotteryService.findFullLotteryList(LotteryConstant.FULLLOTTERY_NUM);

		// 参与人数
		long lotteryTotal = fullLotteryService.getFullLotteryTotal();

		// 图片显示标识
		int picFlag = fullLotteryService.getFlagForLotteryButton(fullGoodsList.size());

		// js时间控件所需时间
		long nowEndGapTime = fullLotteryService.getNowEndGapTimeForLottery(fullGoodsList.size());

		request.setAttribute("picFlag", picFlag);
		request.setAttribute("nowEndGapTime", nowEndGapTime);
		request.setAttribute("fullLotteryList", fullLotteryList);
		request.setAttribute("lotteryTotal", lotteryTotal);
		request.setAttribute("userId", userId);
		request.setAttribute("userLotteryCount", userLotteryCount);
		request.setAttribute("fullGoodsList", fullGoodsList);

		return "/fullLottery/dw";
	}

	/**
	 * 处理满额抽奖
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "command=processFullLottery")
	public void processLotteryFull(HttpServletRequest request, HttpServletResponse response)
	{
		PrintWriter out = null;
		StringBuilder msg = new StringBuilder();

		try
		{
			response.setContentType("text/html ");
			response.setCharacterEncoding("UTF-8");
			out = response.getWriter();

			// 判断登录
			User user = SingletonLoginUtils.getMemcacheUser(request);
			if (null == user)
			{
				msg.append("-1,");
				return;
			}

			// 抽奖
			if (null != user)
			{
				String localCity = CityUtils.getCity(request, response);
				// 处理抽奖
				FullLottery fullLottery = fullLotteryService.processFullLottery(user.getId(), localCity, 0L);
				// 获取抽奖剩余次数
				int userLotteryCount = fullLotteryService.findTrxorderGoodsAndFullLottery(user.getId(), LotteryConstant.fullBeginDate, LotteryConstant.fullEndDate);
				// 中奖的商品title
				String goodsTitle = fullLotteryService.getGoodsNameForLottery(Long.parseLong(fullLottery.getLotteryContent()));
				msg.append("1,");
				msg.append(userLotteryCount).append(",");
				msg.append(goodsTitle).append(",");
			}

		} catch (BaseException el)
		{
			el.printStackTrace();
			msg.append(el.getCode()).append(",");
		}

		catch (Exception e)
		{
			e.printStackTrace();
			msg.append("-2,");

		} finally
		{
			out.print(msg.toString());
		}

	}
}
