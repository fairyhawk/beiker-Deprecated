/**
 * 
 */
package com.beike.common.enums.trx;

/**
 * @author wenhua.cheng
 * 
 */
public enum VoucherVerifySource {

	SELFSERVICE, // 商家WEB自助
	IVR, // 商家IVR

	SYSTEMAUTO,// 系统自动（发送商家自有验证码，相当于千品发实际商品）
	
	SMS//商家上行短信校验
}
