package com.yarovoy.smpp

class SmppException extends IOException
{
	int code

	SmppException(String message, int code)
	{
		super(message)

		this.code = code
	}

	SmppException(String message, int code, Throwable e)
	{
		super(message, e)

		this.code = code
	}
}
