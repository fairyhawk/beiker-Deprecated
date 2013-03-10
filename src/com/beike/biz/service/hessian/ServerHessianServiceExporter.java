package com.beike.biz.service.hessian;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.caucho.HessianServiceExporter;

import com.beike.form.SmsInfo;
import com.beike.service.common.SmsService;
import com.beike.util.PropertyUtil;

/**
 * @Title: TrxHessianServiceImpl.java
 * @Package com.beike.biz.service.hessian.ServerHessianServiceExporter
 * @Description: Hessian接口权限认证服务器端
 * @author wh.cheng@sinobogroup.com
 * @date May 6, 2011 8:18:52 PM
 * @version V1.0
 */
public class ServerHessianServiceExporter extends HessianServiceExporter {

	public static final Log logger = LogFactory
			.getLog(ServerHessianServiceExporter.class);

	PropertyUtil propertyUtil = PropertyUtil.getInstance("hessianAuth");
	public String serverHessianAuth = propertyUtil
			.getProperty("serverHessianAuth");

	@Autowired
	private SmsService smsService;

	@Override
	public void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String auth = request.getHeader("hessianAuth");
		logger.info("++++clientIp:" + request.getRemoteAddr()// 请求IP
				+ "++++requestData:" + request.getRequestURL());// 目标应用路径
		if (auth == null || !auth.equalsIgnoreCase(serverHessianAuth)) {
			// 记录异常日志
			logger.info("+++++hessianAuth->fail");
			SmsInfo sourceBean = new SmsInfo("13683334717",
					"主人，有人试图非法调用Heesian接口。让马云咬死他，咬死他。", "15", "1");
			smsService.sendSms(sourceBean);// 短信报警
			return;
		}

		super.handleRequest(request, response);
	}
}
