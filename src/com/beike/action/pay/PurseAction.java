package com.beike.action.pay;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.beike.common.entity.trx.Account;
import com.beike.common.entity.trx.AccountHistory;
import com.beike.common.entity.vm.SubAccount;
import com.beike.common.enums.trx.AccountType;
import com.beike.common.exception.AccountException;
import com.beike.core.service.trx.AccountHistoryService;
import com.beike.core.service.trx.AccountService;
import com.beike.core.service.trx.notify.AccountNotifyService;
import com.beike.entity.user.User;
import com.beike.page.Pager;
import com.beike.page.PagerHelper;
import com.beike.util.Amount;
import com.beike.util.Constant;
import com.beike.util.RequestUtil;
import com.beike.util.WebUtils;
import com.beike.util.singletonlogin.SingletonLoginUtils;

/**
 * @Title: PurseAction.java
 * @Package com.beike.action.pay
 * @Description: TODO
 * @date Jun 19, 2011 7:34:52 AM
 * @author wh.cheng
 * @version v1.0
 */
@Controller
public class PurseAction extends BaseTrxAction {

	private static Log logger = LogFactory.getLog(PurseAction.class);
	@Autowired
	private AccountService accountService;


	@Autowired
	private AccountNotifyService accountNotifyService;

	@Autowired
	private AccountHistoryService accountHistoryService;

	@RequestMapping("/ucenter/showPurse.do")
	public ModelAndView showPurse(HttpServletRequest request,
			HttpServletResponse response, ModelMap modelMap) throws Exception {
		ModelAndView mav = new ModelAndView(VIEW_SHOW_PURSE);
		try {
			String qryType = request.getParameter("qryType");
			int cpage = RequestUtil.getUnsignedInt(request, Constant.CPAGE, 0);
			// TODO mem中取
			User user = SingletonLoginUtils.getMemcacheUser(request);
			Long userId = 0L;
			if (user != null) {
				if (user.getId() != 0) {
					userId = user.getId();
				}
			}
			double balance = 0;
			try {
				balance = accountService.findBalance(userId);
			} catch (AccountException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//加入余额提醒
			List<SubAccount> remindList = new ArrayList<SubAccount>();
			if (0 == cpage)
			{
				remindList = accountNotifyService.getRemindAccountBalance(userId);
			}
			
			// 跟据类型查出总记录数
			// int totalRows = accountService.findRowsByUserId(userId,qryType);
			List<AccountHistory> allRsList = accountHistoryService
					.getHistoryInfoByUserId(userId);
			List<AccountHistory> accList = new ArrayList<AccountHistory>();
			int totalRows = 0;
			if (allRsList != null) {
				totalRows = allRsList.size();

			}

			Pager pager = PagerHelper.getPager(cpage, totalRows,
					Constant.PURSE_PAGE_SIZE);
			request.setAttribute("pager", pager);
			int startRow = pager.getStartRow();
			int paperSize = Constant.PURSE_PAGE_SIZE;
			int endRow = startRow + paperSize > totalRows ? totalRows
					: startRow + paperSize;

			for (int i = startRow; i < endRow; i++) {
				accList.add(allRsList.get(i));
			}

			modelMap.addAttribute(Constant.PB, pager);
			modelMap.addAttribute("cpage", cpage);
			// modelMap.addAttribute("resultList", resultList);
			modelMap.addAttribute("accList", accList);
			modelMap.addAttribute("balance", balance);
			modelMap.addAttribute("totalRows", totalRows);
			modelMap.addAttribute("remindList", remindList);
			modelMap.addAttribute("qryType", qryType);

			/**
			 * 删除余额提示
			 * add by wangweijie 2012-07-09
			 */
			response.addCookie(WebUtils.removeableCookie("NEW_COUPON_TIPS_"+user.getId(),".qianpin.com"));
			
			return mav;

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("++++++++++++Trx-Exception:" + e + "+++++++++");
			throw new Exception();
		}
	}

	@RequestMapping("/ucenter/showRebate.do")
	public ModelAndView showRebate(HttpServletRequest request,
			HttpServletResponse response, ModelMap modelMap) throws Exception {

		ModelAndView mav = new ModelAndView(VIEW_SHOW_REBATE);
		
		try {
			String qryType = request.getParameter("qryType");
			int cpage = RequestUtil.getUnsignedInt(request, Constant.CPAGE, 0);
			// TODO mem中取
			User user = SingletonLoginUtils.getMemcacheUser(request);
			Long userId = 0L;
			if (user != null) {
				if (user.getId() != 0) {
					userId = user.getId();
				}
			}
			double rebateAmount = 0;
			double totalRebateAmount = 0;
			List<AccountHistory> viewListAccHistory = new ArrayList<AccountHistory>();
			Account account = accountService.findByUserIdAndType(userId,
					AccountType.VC);

			List<AccountHistory> listAccHistory = accountHistoryService
					.listAccountHistory(account.getId());

			int totalRows = 0;
			if (listAccHistory != null) {
				totalRows = listAccHistory.size();
				for (AccountHistory ah : listAccHistory) {
					rebateAmount = ah.getTrxAmount();
					totalRebateAmount = Amount.add(totalRebateAmount,
							rebateAmount);
				}
			}
			Pager pager = PagerHelper.getPager(cpage, totalRows,
					Constant.PURSE_PAGE_SIZE);
			request.setAttribute("pager", pager);
			int startRow = pager.getStartRow();
			int paperSize = Constant.PURSE_PAGE_SIZE;

			int endRow = startRow + paperSize > totalRows ? totalRows
					: startRow + paperSize;
			for (int i = startRow; i < endRow; i++) {
				viewListAccHistory.add(listAccHistory.get(i));
			}
			modelMap.addAttribute(Constant.PB, pager);
			modelMap.addAttribute("cpage", cpage);
			modelMap.addAttribute("resultList", viewListAccHistory);
			modelMap.addAttribute("totalRebateAmount", totalRebateAmount);
			modelMap.addAttribute("rebateAmount", rebateAmount);
			modelMap.addAttribute("totalRows", totalRows);
			modelMap.addAttribute("qryType", qryType);
			return mav;

		} catch (Exception e) {

			e.printStackTrace();
			logger.error("++++++++++++++Trx-Exception:" + e);
			throw new Exception();
		}

	}
}
