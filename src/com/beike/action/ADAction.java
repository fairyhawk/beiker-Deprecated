package com.beike.action;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;

@Controller
public class ADAction {
	private static final Log log = LogFactory.getLog(ADAction.class);

	@RequestMapping("/ad.do")
	public ModelAndView ad(HttpServletRequest request,
			HttpServletResponse response) {
		String sid = request.getParameter("sid");
		String site = request.getParameter("site");
		String url = request.getParameter("url");
		log.info("ad sid=" + sid + ",site=" + site + ",url=" + url);
		try {
			if (url != null && !url.trim().equals("")) {
				response.sendRedirect(url);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// return new ModelAndView("redirect:.." + url);
		return null;
	}
}
