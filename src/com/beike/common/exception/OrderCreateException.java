package com.beike.common.exception;    
/**   
 * @Title: OrderCreateException.java
 * @Package com.beike.common.exception
 * @Description: 订单处理异常类
 * @date May 9, 2011 5:51:17 PM
 * @author wh.cheng
 * @version v1.0   
 */
public class OrderCreateException extends BaseException {
	
	
	private static final long serialVersionUID = -8077156609182536711L;

	public OrderCreateException(){
		super();
	}
	
	public OrderCreateException(int code){
		super(code);
	}
	
	public OrderCreateException(String errorMsg){
		super(errorMsg);
	}

}
 