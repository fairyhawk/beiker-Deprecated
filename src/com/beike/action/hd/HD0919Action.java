package com.beike.action.hd;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HD0919Action {
	
	private static Log log = LogFactory.getLog(HD0919Action.class);
	
	@RequestMapping("/hd/0919.do")
	public String hd0919(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		log.info("0919.do");
		return "huodong/0919/index";

	}
}
