package com.beike.action.pay.hessianclient;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import com.beike.util.PropertyUtil;
import com.caucho.hessian.client.HessianProxyFactory;

/**
 * @Title: ClientHessianProxyFactory.java
 * @Package com.beike.biz.service.hessian.ServerHessianServiceExporter
 * @Description: Hessian接口权限认证客户端
 * @author wh.cheng@sinobogroup.com
 * @date May 6, 2011 8:18:52 PM
 * @version V1.0
 */
public class ClientHessianProxyFactory extends HessianProxyFactory {

	PropertyUtil propertyUtil = PropertyUtil.getInstance("hessianAuth");

	private final String clientHessianAuth = propertyUtil
			.getProperty("clientHessianAuth");

	@Override
	protected URLConnection openConnection(URL url) throws IOException {
		URLConnection conn = super.openConnection(url);
		conn.setRequestProperty("hessianAuth", clientHessianAuth);
		return conn;
	}

}
