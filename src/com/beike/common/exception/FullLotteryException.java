package com.beike.common.exception;

/**
 * @Title:FullLotteryException.java
 * @Package com.beike.common.exception
 * @Description: 满额抽奖活动
 * @date May 17, 2011 2:50:42 PM
 * @author jianjun.huo
 * @version v1.0
 */
public class FullLotteryException extends BaseException {

	
	private static final long serialVersionUID = 5008896354501195409L;

	public FullLotteryException() {
		super();
	}

	public FullLotteryException(int code) {
		super(code);
	}

	public FullLotteryException(String errorMsg) {
		super(errorMsg);
	}
}
