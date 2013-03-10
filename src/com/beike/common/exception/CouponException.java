package com.beike.common.exception;

/**   
 * @title: CouponException.java
 * @package com.beike.common.exception
 * @description: 优惠券异常
 * @author wangweijie  
 * @date 2012-11-1 下午03:48:37
 * @version v1.0   
 */
public class CouponException extends BaseException{

	private static final long serialVersionUID = -5430477694544712724L;

	public CouponException(int code){
		super(code);
	}
}
