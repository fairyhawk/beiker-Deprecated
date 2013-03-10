package com.beike.common.exception;

/**
 * <p>
 * Title:乐观锁异常（DAO层）
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: Sinobo
 * </p>
 * 
 * @date 2011-11-16 14:14:07
 * @author wenhua.cheng
 * @version 1.0
 */
public class StaleObjectStateException extends BaseException {

	private static final long serialVersionUID = 4039314423110012567L;

	public StaleObjectStateException() {

		super();
	}

	public StaleObjectStateException(int code) {

		super(code);
	}

	public StaleObjectStateException(String errorMsg) {

		super(errorMsg);
	}

}
