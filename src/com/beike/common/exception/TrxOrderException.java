package com.beike.common.exception;    
/**   
 * @Title: TrxOrderException.java
 * @Package com.beike.common.exception
 * @Description: 交易订单异常
 * @date May 17, 2011 2:50:42 PM
 * @author wh.cheng
 * @version v1.0   
 */
public class TrxOrderException extends BaseException {
	
	
	private static final long serialVersionUID = 6936661942111020234L;


	public TrxOrderException(){
		
		super();
	}
	
	
	public TrxOrderException(int code){
		
		super(code);
	}
	
}
 