package com.beike.action.miaosha;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.beike.action.user.BaseUserAction;
import com.beike.entity.miaosha.MiaoSha;
import com.beike.entity.miaosha.MiaoshaRemind;
import com.beike.entity.user.User;
import com.beike.service.miaosha.MiaoShaService;
import com.beike.service.miaosha.MiaoshaRemindService;
import com.beike.util.DateUtils;
import com.beike.util.MobilePurseSecurityUtils;
import com.beike.util.WebUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.json.JsonUtil;
import com.beike.util.singletonlogin.SingletonLoginUtils;

/**
 * <p>
 * Title:秒杀通知action
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: Sinobo
 * </p>
 * 
 */
@Controller
public class MiaoshaRemindAction extends BaseUserAction {

	private static Log log = LogFactory.getLog(MiaoshaRemindAction.class);
	
	@Autowired
	private MiaoshaRemindService miaoshaRemindService;
	
	@Autowired
	private MiaoShaService miaoShaService;
	
	private final MemCacheService memCacheService = MemCacheServiceImpl.getInstance();
	
	@RequestMapping("/miaosha/addMiaoshaRemind.do")
	public void addMiaoshaRemind(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			String miaoshaId = request.getParameter("msId");
			Cookie requestUrlCookie=WebUtils.cookie("REQUESTURI_REFER_COOKIE", "/miaosha/" + miaoshaId + ".html", -1);
			response.addCookie(requestUrlCookie);
			
			Map<String,String> jsonMap = new HashMap<String,String>();
			
			MiaoSha miaosha = miaoShaService.getMiaoShaById(Long.parseLong(miaoshaId));
			if(miaosha == null){
				//秒杀不存在
				jsonMap.put("isonload", "3");
			}else{
				//离开始秒杀10分钟以上
				if(miaosha.getMsStartTime().compareTo(new Timestamp(System.currentTimeMillis()))==1
						&& DateUtils.countDifSeconds(miaosha.getMsStartTime(),new Timestamp(System.currentTimeMillis()))>600
						){
					// 判断用户是否登录
					User user = SingletonLoginUtils.getMemcacheUser(request);
					if(user == null){
						//未登录
						jsonMap.put("isonload", "0");
					}else{
						//查询是否已经预定提醒
						Long userId = user.getId();
						MiaoshaRemind msRemind = miaoshaRemindService.getMiaoshaRemind(userId, Long.parseLong(miaoshaId));
						if(msRemind == null){
							jsonMap.put("isonload", "1");
						}else{
							jsonMap.put("isonload", "2");
							jsonMap.put("phonenumber", msRemind.getPhone());
						}
					}
				}else{
					jsonMap.put("isonload", "3");
				}
			}
			
			response.setContentType("text/json; charset=UTF-8");
			response.setHeader("progma", "no-cache");
			response.setHeader("Cache-Control", "no-cache");
			
			String text = JsonUtil.mapToJson(jsonMap);
			response.getWriter().print(text);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@RequestMapping("/miaosha/saveMiaoshaRemind.do")
	public void saveMiaoshaRemind(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			Map<String,String> jsonMap = new HashMap<String,String>();
			// 判断用户是否登录
			User user = SingletonLoginUtils.getMemcacheUser(request);
			String status = "0";
			if(user!=null){
				String miaoshaId = request.getParameter("msId");
				String phone = request.getParameter("phone");
				String curDate = DateUtils.getStringDateShort();
				
				Integer iphoneCount = 0;
				Integer iuserCount = 0;
				
				//每个自然日同一手机号不能超过3条;同一用户不能超过10条
				if(MobilePurseSecurityUtils.isJointMobileNumber(phone)){
					iphoneCount = (Integer)memCacheService.get(curDate + "_" + phone);
					if(iphoneCount == null){
						iphoneCount = 0;
					}
					if(iphoneCount>=3){
						status = "2";
					}
					
					if("0".equals(status)){
						iuserCount = (Integer)memCacheService.get(curDate + "_" + user.getId());
						if(iuserCount == null){
							iuserCount = 0;
						}
						if(iuserCount>=10){
							status = "3";
						}
					}

					if("0".equals(status)){
						//同一手机号加1次
						iphoneCount ++;
						memCacheService.set(curDate + "_" + phone, iphoneCount);
						//同一用户加1次
						iuserCount ++;
						memCacheService.set(curDate + "_" + user.getId(), iuserCount);
						
						MiaoshaRemind msRemind = new MiaoshaRemind();
						msRemind.setUserid(user.getId());
						msRemind.setMiaoshaid(Long.parseLong(miaoshaId));
						msRemind.setPhone(phone);
						
						int retValue = miaoshaRemindService.addMiaoshaRemind(msRemind);
						//失败减掉之前的计数
						if(retValue<=0){
							iphoneCount = (Integer)memCacheService.get(curDate + "_" + phone);
							if(iphoneCount == null){
								iphoneCount = 0;
							}
							if(iphoneCount>1){
								iphoneCount--;
							}
							memCacheService.set(curDate + "_" + phone, iphoneCount);
							
							iuserCount = (Integer)memCacheService.get(curDate + "_" + user.getId());
							if(iuserCount == null){
								iuserCount = 0;
							}
							if(iuserCount>1){
								iuserCount--;
							}
							memCacheService.set(curDate + "_" + user.getId(), iuserCount);
							status = "0";
						}else{
							status = "1";
						}
					}
				}else{
					status = "0";
				}
			}else{
				status = "0";
			}
			jsonMap.put("status", status);
			response.setContentType("text/json; charset=UTF-8");
			response.setHeader("progma", "no-cache");
			response.setHeader("Cache-Control", "no-cache");
			
			String text = JsonUtil.mapToJson(jsonMap);
			response.getWriter().print(text);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}