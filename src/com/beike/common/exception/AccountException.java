package com.beike.common.exception;

/**
 * @Title: AccountException.java
 * @Package com.beike.common.exception
 * @Description: TODO
 * @author wh.cheng@sinobogroup.com
 * @date May 4, 2011 3:54:00 PM
 * @version V1.0
 */
public class AccountException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3592176225300398365L;
	public AccountException() {
		super();
	}

	public AccountException(int code) {

		super(code);
	}
	public AccountException(String errorMsg) {
		super(errorMsg);
		// TODO Auto-generated constructor stub
	}

}
