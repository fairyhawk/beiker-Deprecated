package com.beike.common.exception;

/**
 * @Title:LotteryRegException.java
 * @Package com.beike.common.exception
 * @Description: 抽奖活动
 * @date May 17, 2011 2:50:42 PM
 * @author jianjun.huo
 * @version v1.0
 */
public class LotteryRegException extends BaseException {

	
	private static final long serialVersionUID = 3702637877386451709L;

	public LotteryRegException() {
		super();
	}

	public LotteryRegException(int code) {
		super(code);
	}

	public LotteryRegException(String errorMsg) {
		super(errorMsg);
	}
}
