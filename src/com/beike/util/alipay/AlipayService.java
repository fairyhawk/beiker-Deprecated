package com.beike.util.alipay;

import java.util.HashMap;
import java.util.Map;

import com.beike.common.bean.trx.AlipayConfig;
import com.beike.common.bean.trx.PaymentInfoGeneratorAlipay;
import com.beike.util.Configuration;
import com.beike.util.Constant;
import com.beike.util.PropertyUtil;

/* *
 *类名：AlipayService
 *功能：支付宝各接口构造类
 *详细：构造支付宝各接口请求参数
 *版本：3.2
 *修改日期：2011-03-17
 */

public class AlipayService {
    
    /**
     * 支付宝提供给商户的服务接入网关URL(新)
     */
	private static String ALIPAY_GATEWAY_NEW = Configuration.getInstance().getValue("alipayCommonReqURL");
	
	private static final PropertyUtil property = PropertyUtil.getInstance(Constant.PROPERTY_FILE_NAME);
    
	private static String partner = Configuration.getInstance().getValue("partner");

	/**
     * 构造快捷登录接口
     * @param sParaTemp 请求参数集合
     * @return 表单提交HTML信息
     */
    public static String alipay_auth_authorize(Map<String, String> sParaTemp) {
   
    	//增加基本配置
        sParaTemp.put("service", "alipay.auth.authorize");
        sParaTemp.put("target_service", "user.auth.quick.login");
        sParaTemp.put("partner", partner);
        sParaTemp.put("return_url",property.getProperty("ALIPAY_CALLBACK_URL"));
        sParaTemp.put("_input_charset", AlipayConfig.input_charset);

        String strButtonName = "确认";
        return AlipaySubmit.buildForm(sParaTemp, ALIPAY_GATEWAY_NEW, "get", strButtonName);
    }
    
    public static String getUrl(){
    	Map<String,String> sParaTemp = new HashMap<String,String>();
    	sParaTemp.put("service", "alipay.auth.authorize");
        sParaTemp.put("target_service", "user.auth.quick.login");
        sParaTemp.put("partner", partner);
        sParaTemp.put("return_url",property.getProperty("ALIPAY_CALLBACK_URL"));
        sParaTemp.put("_input_charset", AlipayConfig.input_charset);
    	
    	Map<String, String> sPara = PaymentInfoGeneratorAlipay.paraFilter(sParaTemp);
    	String sign=PaymentInfoGeneratorAlipay.buildMysign(sPara);
    	StringBuilder sb=new StringBuilder(ALIPAY_GATEWAY_NEW);
    	sb.append("?");
    	sb.append("service=");
    	sb.append("alipay.auth.authorize");
    	sb.append("&");
    	sb.append("target_service=");
    	sb.append("user.auth.quick.login");
    	sb.append("&");
    	sb.append("partner=");
    	sb.append(partner);
    	sb.append("&");
    	sb.append("return_url=");
    	sb.append(property.getProperty("ALIPAY_CALLBACK_URL"));
    	sb.append("&sign_type=");
    	sb.append("MD5");
    	sb.append("&_input_charset=");
    	sb.append(AlipayConfig.input_charset);
    	sb.append("&sign=");
    	sb.append(sign);
    	return sb.toString();
    }
}
