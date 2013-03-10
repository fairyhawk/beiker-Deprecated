package com.beike.common.exception;

/**
 * @Title: VoucherException.java
 * @Package com.beike.common.exception
 * @Description: 凭证异常
 * @date May 26, 2011 6:51:59 PM
 * @author wh.cheng
 * @version v1.0
 */
public class VoucherException extends BaseException {

	private static final long serialVersionUID = 1951397773730083594L;

	public VoucherException() {
		super();

	}

	public VoucherException(int code){
		super(code);
	}
}
