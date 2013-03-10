package com.beike.common.exception;

/**
 * @Title: ExpiredException.java
 * @Package com.beike.common.exception
 * @Description: 过期异常
 * @date May 25, 2011 1:31:34 AM
 * @author wh.cheng
 * @version v1.0
 */
public class ExpiredException extends BaseException {
	

	private static final long serialVersionUID = -8473285428076452629L;

	public ExpiredException() {
		super();
	}

	public ExpiredException(int code) {
		super(code);
	}

}
