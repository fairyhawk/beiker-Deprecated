package com.beike.action.edm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.beike.service.edm.CancelEdmEmailService;
import com.beike.util.Constant;
import com.beike.util.CryptUtilImpl;
import com.beike.util.PropertyUtil;
import com.beike.util.StringUtils;

/**
 * @author 赵静龙 创建时间：2012-10-18
 * 
 */
@Controller
public class CancelOrSubscribeAction {
	private static Log log = LogFactory.getLog(CancelOrSubscribeAction.class);
	@Autowired
	private CancelEdmEmailService cancelEdmEmailService;
	private static CryptUtilImpl cryptUtil = new CryptUtilImpl();
	private static PropertyUtil propertyUtil = PropertyUtil.getInstance(Constant.PROPERTY_FILE_NAME);
	private static String edmmailCryptkey = propertyUtil.getProperty("edm_mail_Cryptkey");
	
	
	/**
	 * 用户取消订阅EDM邮箱，步骤一
	 * @param request
	 * @param response
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/edm/userCancelSubEDM.do")
	public ModelAndView userCancelSubEDMMail(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
		ModelAndView modelAndView = new ModelAndView("edm/cancelSubscribe");
		
		try {
			String emailencry = request.getParameter("email");      //退订邮箱
			String type = request.getParameter("type");             //退订类型
			modelMap.addAttribute("email", emailencry);
			modelMap.addAttribute("type", type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return modelAndView;
	}
	
	/**
	 * 提交反馈，退订成功，步骤二
	 * @param request
	 * @param response
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/edm/userCancelSuccess.do")
	public ModelAndView userCancelSuccess(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
		ModelAndView modelAndView = new ModelAndView("edm/cancelSubscribeSuc");
		try {
			String[] reasons = request.getParameterValues("reason");
			String emailencry = request.getParameter("email");      //退订邮箱
			if(emailencry == null || "".equals(emailencry.replace(" ", ""))){
				return modelAndView;
			}
			String email = cryptUtil.decryptDes(emailencry, edmmailCryptkey);
			
			if(email == null || !StringUtils.validEmail(email)){
				return modelAndView;
			}
			
			String type = request.getParameter("type");             //退订类型
			int itype = 0;
			try {
				itype = Integer.parseInt(type);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			//log.info("==============原因:" + StringUtils.arrayToString(reasons, "==="));
			String handwritingReason = request.getParameter("handwritingReason");
			Map<String,Object> edmCancelEmailInfo = new HashMap<String, Object>();
			StringBuffer cancelReason = new StringBuffer("");
			if(reasons != null && reasons.length > 0){
				String temp;
				for(int i = 0; i < reasons.length; i++){
					temp = reasons[i];
					temp = temp.replaceAll("\\s+", " "); //多个连续空格替换为一个空格
					temp = temp.replaceAll("\\|+", "");  //过滤竖线 
			        if(!"".equals(temp)){
			        	cancelReason.append(temp).append("|");
			        }
				}
			}
			if(StringUtils.validNull(handwritingReason)){
				handwritingReason = handwritingReason.replaceAll("\\s+", " "); //多个连续空格替换为一个空格
				handwritingReason = handwritingReason.replaceAll("\\|+", "");  //过滤竖线
				cancelReason.append(handwritingReason);
			}
			
			String rea = cancelReason.toString();
			if(rea.length() > 200){
				rea = rea.substring(0, 200);
			}
			
			if(rea.endsWith("|")){
				rea = rea.substring(0, rea.length() - 1);
			}
			
			//log.info("==============原因:" + rea);
			//log.info("==============解密后的email:" + email + "\ttype:" + itype);
			edmCancelEmailInfo.put("email", email);
			edmCancelEmailInfo.put("type", itype);
			edmCancelEmailInfo.put("cancelReason", rea);
			cancelEdmEmailService.addCancelEdmMail(edmCancelEmailInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return modelAndView;
	}
	
}
