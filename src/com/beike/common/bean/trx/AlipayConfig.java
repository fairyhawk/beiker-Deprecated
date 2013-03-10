package com.beike.common.bean.trx;

import com.beike.util.Configuration;
/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *版本：3.2
 *日期：2011-09-01
 */

public class AlipayConfig {
	
	//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	
	// 签约支付宝账号或卖家收款支付宝帐户
	public static String seller_email = Configuration.getInstance().getValue("alipaySeller");
	
	// 支付宝服务器通知的页面 要用 http://格式的完整路径，不允许加?id=123这类自定义参数
	// 必须保证其地址能够在互联网中访问的到
	public static String notify_url = "http://www.qianpin.com//pay/aliCallBack.do";
	
	// 当前页面跳转后的页面 要用 http://格式的完整路径，不允许加?id=123这类自定义参数
	// 域名不能写成http://localhost/create_direct_pay_by_user_jsp_utf8/return_url.jsp ，否则会导致return_url执行无效
	public static String return_url = "http://www.qianpin.com//pay/aliCallBack.do";
	
		// 支付宝WAP:  商户（MD5）KEY
	public static final String ALIPAY_WAP_KEY = "";

//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
	
	// 字符编码格式 目前支持 gbk 或 utf-8
	public static String input_charset = "UTF-8";
	
	// 签名方式 不需修改
	public static String sign_type = "MD5";
	
	//访问模式,根据自己的服务器是否支持ssl访问，若支持请选择https；若不支持请选择http
	public static String transport = "http";

}
