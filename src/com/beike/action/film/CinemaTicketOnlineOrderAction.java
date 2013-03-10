package com.beike.action.film;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.beike.action.user.BaseUserAction;

/**
 * 电影票在线预订action
 * @author weiwei
 */
@Controller
public class CinemaTicketOnlineOrderAction extends BaseUserAction {

	//影院详情
	@RequestMapping("/cinematicket/bookticket.do")
	public String cinemaDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return "cinema/cinema_detail";
	}
}
