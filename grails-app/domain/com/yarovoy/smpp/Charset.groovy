package com.yarovoy.smpp

public enum Charset
{

	ISO_8859_1('ISO-8859-1'),
	US_ASCII('US-ASCII'),
	UTF_16BE('UTF-16BE')

	private static Map<String, Charset> valuesToEnum = [:]

	static {
		values().each {
			valuesToEnum.put(it.toString(), it)
		}
	}

	private String value

	Charset(String value)
	{
		this.value = value
	}

	@Override
	String toString()
	{
		value
	}

	static Charset getByValue(String value)
	{
		valuesToEnum.get(value)
	}
}