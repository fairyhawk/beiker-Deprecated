package com.beike.action.lottery.reg;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.beike.action.user.BaseUserAction;
import com.beike.common.entity.trx.lottery.reg.LotteryReg;
import com.beike.common.exception.LotteryRegException;
import com.beike.core.service.trx.lottery.reg.LotteryRegService;
import com.beike.entity.user.User;
import com.beike.util.TrxConstant;
import com.beike.util.singletonlogin.SingletonLoginUtils;

@Controller
@RequestMapping("/lotteryReg/lotteryRegAction.do")
public class LotteryRegAction extends BaseUserAction
{

	@Autowired
	private LotteryRegService lotteryRegService;

	/**
	 * 跳转到登录
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "command=forwardLogin")
	public ModelAndView getForwardLogin(HttpServletRequest request)
	{

		return new ModelAndView("forward:../forward.do?param=regLotteryLogin");

	}

	/**
	 * 跳转到绑定手机
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "command=forwardBinging")
	public ModelAndView getForwardBinging(HttpServletRequest request)
	{

		return new ModelAndView("forward:../forward.do?param=regLotteryBinging");

	}

	/**
	 * 查询抽奖信息
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "command=showLotteryRegInfo")
	public String getLotteryRegListInfo(HttpServletRequest request)
	{
		List<LotteryReg> lotteryRegList = lotteryRegService.findLotteryRegList(3);

		request.setAttribute("lotteryRegList", lotteryRegList);

		return "/zccj/lotteryRegInfo";
	}

	/**
	 * 处理抽奖
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "command=processLotteryReg")
	public String processLotteryReg(HttpServletRequest request)
	{
		StringBuilder error = new StringBuilder();
		LotteryReg lotteryReg = null;
		String lottery_content = "";
		String registUrl = "";
		String mobileUrl = "";
		try
		{
			// 判断登录
			User user = SingletonLoginUtils.getMemcacheUser(request);
			if (null == user)
			{
				registUrl = "/lotteryReg/lotteryRegAction.do?command=forwardLogin";
			} else
			{
				if (1 != user.getMobile_isavalible())
				{
					mobileUrl = "/lotteryReg/lotteryRegAction.do?command=forwardBinging";// 未绑定手机
				}
			}
			request.setAttribute("registUrl", registUrl);
			request.setAttribute("mobileUrl", mobileUrl);

			if (null != user)
			{
				// 抽奖
				lotteryReg = lotteryRegService.processLotteryReg(user.getId());
				if (null != lotteryReg)
				{
					lottery_content = lotteryReg.getLotteryContent();
				}
				request.setAttribute("lottery_content", lottery_content);
			}

		} catch (LotteryRegException le)
		{
			if (1800 == le.getCode())
			{
				error.append("不能抽奖:已经参与过抽奖");
			}
			if (1801 == le.getCode())
			{
				error.append("不能抽奖:注册时间超过24小时");
			}
			le.printStackTrace();
		} catch (Exception e)
		{
			error.append("抽奖的用户过多,请稍后再试");
			e.printStackTrace();
		}
		
		
		if (null == error || "".equals(error.toString()))
		{
			if ("".equals(lottery_content))
				error.append("很遗憾: 您没有中奖");
		}

		request.setAttribute("errorInfo", error.toString());
		List<LotteryReg> lotteryRegList = lotteryRegService.findLotteryRegList(TrxConstant.LOTTERYREGLIST_NUM);
		request.setAttribute("lotteryRegList", lotteryRegList);

		return "/zccj/lotteryRegInfo";
	}
}
