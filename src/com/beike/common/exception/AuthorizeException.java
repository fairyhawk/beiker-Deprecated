package com.beike.common.exception;    
/**   
 * @Title: AuthorizeException.java
 * @Package com.beike.common.exception
 * @Description: 扣款授权异常
 * @author wh.cheng@sinobogroup.com
 * @date May 4, 2011 3:44:51 PM
 * @version V1.0   
 */
public class AuthorizeException extends BaseException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4569136195531085067L;
	public AuthorizeException(){
		super();
	}
	public AuthorizeException(int code){
		
		this.code=code;
	}

}
 