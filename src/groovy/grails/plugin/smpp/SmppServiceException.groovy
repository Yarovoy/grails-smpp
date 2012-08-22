package grails.plugin.smpp

class SmppServiceException extends IOException
{
	int code

	SmppServiceException(String message, int code)
	{
		super(message)

		this.code = code
	}

	SmppServiceException(String message, int code, Throwable e)
	{
		super(message, e)

		this.code = code
	}
}
