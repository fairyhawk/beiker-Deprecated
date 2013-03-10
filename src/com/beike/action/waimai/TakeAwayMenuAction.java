package com.beike.action.waimai;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.beike.entity.merchant.Merchant;
import com.beike.form.TakeAwayDetailForm;
import com.beike.service.merchant.MerchantService;
import com.beike.service.waimai.WaiMaiService;

@Controller
public class TakeAwayMenuAction {

	@Autowired
	private WaiMaiService waiMaiService;
	@Autowired
	private MerchantService merchantService;

	Logger logger = Logger.getLogger(TakeAwayMenuAction.class);

	@RequestMapping("/takeaway/showmenu.do")
	public String showTakeAwayMenu(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			//分店ID
			Long merchantId = Long.valueOf(request.getParameter("merchantId"));

			Merchant merchant = merchantService.getMerchantById(merchantId);
			TakeAwayDetailForm takeAwayDetail = waiMaiService.getTakeAwayDetailByMerchantId(merchantId);
			if (merchant == null || takeAwayDetail == null) {
				throw new RuntimeException("未找到相应的分店或分店不支持外卖");
			}

			request.setAttribute("merchant", merchant);
			request.setAttribute("takeAwayDetail", takeAwayDetail);
		} catch (Exception e) {
			logger.error("外卖单页异常: ");
			e.printStackTrace();
			response.sendRedirect("/404.html");
			return null;
		}
		return "takeaway/menu";
	}
}
