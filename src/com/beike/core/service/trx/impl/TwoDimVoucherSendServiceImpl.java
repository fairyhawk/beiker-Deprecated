package com.beike.core.service.trx.impl;

import org.springframework.stereotype.Service;

import com.beike.common.bean.trx.VoucherParam;
import com.beike.core.service.trx.VoucherSendService;

/**
 * @Title: TwoDimVoucherSendServiceImpl.java
 * @Package com.beike.core.service.trx.impl
 * @Description: 发送二维凭证码服务类
 * @date 3 8, 2012 11:24:04 AM
 * @author wh.cheng
 * @version v1.0
 */
@Service("twoDimVoucherSendService")
public class TwoDimVoucherSendServiceImpl implements VoucherSendService {
	/**
	 * 
	 * 发送凭证码
	 * 
	 * @param voucherParam
	 */
	public VoucherParam sendVoucher(VoucherParam voucherParam) {

		return voucherParam;
	}

	/**
	 * 重发
	 * 
	 * @param voucherParam
	 */
	public void reSendVoucher(VoucherParam voucherParam) {
	}

	/**
	 * 转发
	 * 
	 * @param voucherParam
	 */
	public void transSendVoucher(VoucherParam voucherParam) {
	}

}
