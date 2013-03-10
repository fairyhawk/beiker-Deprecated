package com.beike.common.exception;

/**
 * @Title: BizManagerException.java
 * @Package com.beike.common.exception
 * @Description: TODO
 * @date May 9, 2011 6:18:31 PM
 * @author wh.cheng
 * @version v1.0
 */
public class BizManagerException extends BaseException {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 713409918245628446L;

	public BizManagerException() {
		super();

	}

	public BizManagerException(int code) {

		super(code);
	}

	public BizManagerException(String errorMsg) {
		super(errorMsg);
	}

}
