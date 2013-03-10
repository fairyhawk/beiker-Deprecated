package com.beike.common.exception;

/**
 * @Title: TrxorderGoodsException.java
 * @Package com.beike.common.exception
 * @Description: 订单商品明细异常
 * @date May 17, 2011 2:52:52 PM
 * @author wh.cheng
 * @version v1.0
 */
public class TrxorderGoodsException extends BaseException {
	

	private static final long serialVersionUID = 8387292310226124340L;

	public TrxorderGoodsException() {

		super();
	}

	public TrxorderGoodsException(int code) {

		super(code);
	}

}
