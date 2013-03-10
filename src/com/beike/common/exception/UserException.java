package com.beike.common.exception;

/**
 * <p>
 * Title:用户异常信息
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
 * @date May 4, 2011
 * @author ye.tian
 * @version 1.0
 */

public class UserException extends BaseException {
	
	
	private static final long serialVersionUID = 635918895066435380L;

	// 密码错误
	public static final int PASSWORD_ERROR = 2001;

	// 用户不存在
	public static final int USER_NOT_EXIST = 2002;

	public static final int USER_SYSTEM_ERROR = 2003;

	public UserException() {
		super();
	}

	public UserException(int code) {
		super(code);
	}

}
