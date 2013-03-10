package com.beike.common.exception;    
/**   
 * @Title: RefundException.java
 * @Package com.beike.common.exception
 * @Description: 退款异常
 * @date May 25, 2011 1:31:34 AM
 * @author wh.cheng
 * @version v1.0   
 */
public class RefundException extends BaseException{
	

	private static final long serialVersionUID = 2147815359364737110L;

	public RefundException(){
		super();
	}
	
	public RefundException(int code){
		super(code);
	}
	

}
 