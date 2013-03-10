package com.beike.common.enums.trx;

/**
 * @Title: VoucherType.java
 * @Package com.beike.common.enums.trx
 * @Description:凭证类型
 * @date 3 8, 2012 11:24:04 AM
 * @author wh.cheng
 * @version v1.0
 */
public enum VoucherType {

	PLATFORM, // 千品平台自有凭证码（含商户上传到千品平台的凭证码）

	TWO_DIM, // 二维码
	MERCHANT_API,// 通过商家API，请求商家发送的凭证码
	FILM_API;//通过网票网发送凭证码
}
