package com.beike.common.exception;

/**
 * @Title:VmAccountException.java
 * @Package com.beike.common.exception
 * @Description: 虚拟款项异常
 * @date May 17, 2011 2:50:42 PM
 * @author wh.cheng
 * @version v1.0
 */
public class VmAccountException extends BaseException {
	
	
	private static final long serialVersionUID = 1207455584458892357L;

	public VmAccountException() {
		super();
	}

	public VmAccountException(int code) {
		super(code);
	}

	public VmAccountException(String errorMsg) {
		super(errorMsg);
	}
}
