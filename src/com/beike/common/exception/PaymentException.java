package com.beike.common.exception;

/**
 * @Title: PaymentException.java
 * @Package com.beike.common.exception
 * @Description: 支付信息异常
 * @date May 17, 2011 2:51:36 PM
 * @author wh.cheng
 * @version v1.0
 */
public class PaymentException extends BaseException {
	

	private static final long serialVersionUID = 1151310505887182185L;

	public PaymentException() {

		super();
	}

	public PaymentException(int code) {

		super(code);
	}

}
