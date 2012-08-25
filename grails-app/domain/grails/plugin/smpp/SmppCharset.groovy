package grails.plugin.smpp

public enum SmppCharset
{

	US_ASCII('US-ASCII'),
	ISO_8859_1('ISO-8859-1'),
	UTF_16BE('UTF-16BE')

	private static Map<String, SmppCharset> valuesToEnum = [:]

	static {
		values().each {SmppCharset value ->
			valuesToEnum.put(value.toString(), value)
		}
	}

	private String value

	SmppCharset(String value)
	{
		this.value = value
	}

	@Override
	String toString()
	{
		value
	}

	static SmppCharset stringToEnum(String value)
	{
		valuesToEnum.get(value)
	}
}