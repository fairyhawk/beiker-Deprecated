package com.beike.common.exception;

/**
 * @Title: ProcessServiceException.java
 * @Package com.beike.common.exception
 * @Description: TODO
 * @date May 10, 2011 1:52:50 AM
 * @author wh.cheng
 * @version v1.0
 */
public class ProcessServiceException extends BaseException {
	
	
	private static final long serialVersionUID = -6976911025267510049L;

	public ProcessServiceException() {
		super();
	}

	public ProcessServiceException(int code) {
		super(code);

	}

	public ProcessServiceException(String errorMsg) {

		super(errorMsg);
	}

}
