package com.beike.common.exception;

/**   
 * @title: DiscountCouponException.java
 * @package com.beike.common.exception
 * @description: 
 * @author wangweijie  
 * @date 2012-7-11 下午09:36:34
 * @version v1.0   
 */
public class DiscountCouponException extends BaseException {

	/**
	 * @fields serialVersionUID : 
	 */
	private static final long serialVersionUID = 658441052768973882L;
	
//	public DiscountCouponException(){
//		super();
//	}
	
	public DiscountCouponException(int code){
		super(code);
	}
}
