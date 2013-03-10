package com.beike.common.exception;

/**
 * @Title: RebateException.java
 * @Package com.beike.common.exception
 * @Description: 返现异常
 * @date May 9, 2011 6:42:42 PM
 * @author wh.cheng
 * @version v1.0
 */
public class RebateException extends BaseException {
	

	private static final long serialVersionUID = -1811820096451547442L;

	public RebateException() {
		super();
	}

	public RebateException(int code) {

		super(code);
	}

	public RebateException(String errorMsg) {
		super(errorMsg);
	}

}
