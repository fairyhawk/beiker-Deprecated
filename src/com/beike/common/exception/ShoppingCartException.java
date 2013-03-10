package com.beike.common.exception;

public class ShoppingCartException extends BaseException
{


	private static final long serialVersionUID = 6301957228904549669L;

	public ShoppingCartException()
	{
		super();
	}

	public ShoppingCartException(int code)
	{
		super(code);
	}

	public ShoppingCartException(String errorMsg)
	{
		super(errorMsg);
	}
}
