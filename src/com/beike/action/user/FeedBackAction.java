package com.beike.action.user;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.beike.service.common.EmailService;
import com.beike.util.Constant;
import com.beike.util.PropertyUtil;

/**      
 * project:beiker  
 * Title:意见反馈
 * Description:
 * Copyright:Copyright (c) 2011
 * Company:Sinobo
 * @author qiaowb  
 * @date Dec 1, 2011 4:01:04 PM     
 * @version 1.0
 */
@Controller
public class FeedBackAction {
	private final Log log = LogFactory.getLog(FeedBackAction.class);
	
	@Autowired
	private EmailService emailService;
	
	private final PropertyUtil propertyUtil = PropertyUtil.getInstance(Constant.PROPERTY_FILE_NAME);
	
	@RequestMapping("/feedback/sendFeedbackEmail.do")
	public Object sendFeedbackEmail(ModelMap model, HttpServletRequest request) {
		String callname = request.getParameter("callname");
		String contact = request.getParameter("contact");
		String feedback = request.getParameter("feedback");
		if(StringUtils.isEmpty(callname) || StringUtils.isEmpty(contact) || StringUtils.isEmpty(feedback)){
			return new ModelAndView("redirect:../jsp/help/jianyi.jsp");
		}
		String emailTemplate = "FEEDBACK";
		String emailTitle = "用户建议反馈";
		String feedbackEmail = propertyUtil.getProperty("feedback_email");
		
		Object[] emailParams = new Object[] {callname,contact,feedback};
		
		String sendResult = "false";
		try {
			emailService.send(null, null, null, null, null, emailTitle,
					new String[]{feedbackEmail}, null, null, new Date(),
					emailParams, emailTemplate);
			
			sendResult = "true";
		} catch (Exception e) {
			log.info(e);
			e.printStackTrace();
		}
		
		request.setAttribute("sendResult", sendResult);
		return "/help/jianyi";
	}
}
