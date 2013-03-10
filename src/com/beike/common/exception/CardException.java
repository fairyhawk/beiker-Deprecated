package com.beike.common.exception;

/**
 * @Title: CardException
 * @Package com.beike.common.exception
 * @Description: 千品卡处理异常类
 * @date May 9, 2011 5:51:17 PM
 * @author wh.cheng
 * @version v1.0
 */
public class CardException extends BaseException {
   
	
	private static final long serialVersionUID = -115083475226792518L;

	public CardException() {
		super();
	}

	public CardException(int code) {
		super(code);
	}

}
