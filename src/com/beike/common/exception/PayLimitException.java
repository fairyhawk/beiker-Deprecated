package com.beike.common.exception;

/**
 * @Title: PayLimitException.java
 * @Package com.beike.common.exception
 * @Description: 购买数量限制异常
 * @author wh.cheng@sinobogroup.com
 * @date May 4, 2011 3:54:00 PM
 * @version V1.0
 */
public class PayLimitException extends BaseException {
	

	private static final long serialVersionUID = 6476808432585084425L;

	public PayLimitException(int code) {

		super(code);

	}

	public PayLimitException() {

		super();

	}

}
