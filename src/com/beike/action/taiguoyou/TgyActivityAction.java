package com.beike.action.taiguoyou;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.beike.action.user.BaseUserAction;
import com.beike.entity.user.User;
import com.beike.service.taiguoyou.TgyActivityService;
import com.beike.util.singletonlogin.SingletonLoginUtils;
 /*
 * com.beike.action.taiguoyou.TgyActivityAction.java
 * @description:泰国游抽奖活动Action
 * @Author:xuxiaoxian
 * Copyright ? 2012  All rights reserved.
 * @Compony:Sinobo
 * 
 * @date: 2012-7-3，xuxiaoxian ,create class
 *
 */
@Controller
public class TgyActivityAction extends BaseUserAction{
	
	static final Log logger = LogFactory.getLog(TgyActivityAction.class);
	
	@Autowired
	private TgyActivityService tgyActivityService;
	
	@RequestMapping("/huodong/taiguoyou.do")
	public String gotoDrawPage(HttpServletRequest request,HttpServletResponse response){
		return "../huodong/taiguo/index";
	}
	@RequestMapping("/huodong/participateInDraw.do")
	public String gotoDrawActivity(HttpServletRequest request,HttpServletResponse response){
		
		super.setCookieUrl(request, response);
		try{
			User user = SingletonLoginUtils.getMemcacheUser(request);
			if(user == null){
				return "redirect:/forward.do?param=login";
			}
			
			Long userid = user.getId();
			Map<String,Object> drawMap = tgyActivityService.getActivityMsgByUserId(userid);
			String message = null;
			if(drawMap != null){
				message = "亲，您已经抽过奖了，不能重复抽取！";
			}else{
				boolean flag = tgyActivityService.addActivityMsg(userid);
				if(flag){
					message ="亲，参与抽奖成功，敬请等待开奖结果！";
				}else{
					message = "亲，抽奖失败，重新试试吧！";
				}
			}
			request.setAttribute("message", message);
			return "../huodong/taiguo/index";
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:../500.html";
		}
	}
	
}
