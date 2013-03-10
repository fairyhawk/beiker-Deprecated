package com.beike.common.exception;

/** 
 * 鉴权失败异常类
* @ClassName: VerifyBelongException 
* @Description: TODO
* @author yurenli
* @date 2012-3-31 下午05:16:31 
* @version V1.0 
*/ 
public class VerifyBelongException extends BaseException {
	
	
	private static final long serialVersionUID = -57123797947960609L;

	public VerifyBelongException() {
		super();
	}
	
	public VerifyBelongException(String e) {
		super(e);
	}
	
	public VerifyBelongException(int e) {
		super(e);
	}
}
