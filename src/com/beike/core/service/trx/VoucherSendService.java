package com.beike.core.service.trx;

import com.beike.common.bean.trx.VoucherParam;

/**
 * @Title: VoucherSendService.java
 * @Package com.beike.core.service.trx
 * @Description: 
 *               发送凭证码服务类接口（此功能隶属于voucherService的一部分，但voucherService的凭证生成、校验等功能并不需工厂生产需求
 *               ，故独立发送service）
 * @date 3 8, 2012 11:24:04 AM
 * @author wh.cheng
 * @version v1.0
 */
public interface VoucherSendService {
	/**
	 * 
	 * 发送凭证码
	 * 
	 * @param voucherParam
	 */
	public VoucherParam sendVoucher(VoucherParam voucherParam);

	/**
	 * 重发
	 * 
	 * @param voucherParam
	 */
	public void reSendVoucher(VoucherParam voucherParam);

	/**
	 * 转发
	 * 
	 * @param voucherParam
	 */
	public void transSendVoucher(VoucherParam voucherParam);

}
