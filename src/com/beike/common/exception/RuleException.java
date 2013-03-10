package com.beike.common.exception;

/**
 * @Title: RuleException.java
 * @Package com.beike.common.exception
 * @Description: 交易表达式解析异常
 * @date May 25, 2011 1:31:34 AM
 * @author wh.cheng
 * @version v1.0
 */
public class RuleException extends BaseException {
	
	
	private static final long serialVersionUID = 3301115080988894990L;

	public RuleException() {

		super();
	}

	public RuleException(int code) {

		super(code);
	}

}
