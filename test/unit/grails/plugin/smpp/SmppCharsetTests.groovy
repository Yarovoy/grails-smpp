package grails.plugin.smpp

import grails.test.mixin.TestFor
import org.junit.Test

@TestFor(SmppService)
class SmppCharsetTests
{

	@Test
	void testToString()
	{
		assertEquals(SmppCharset.ISO_8859_1.toString(), 'ISO-8859-1')
		assertEquals(SmppCharset.US_ASCII.toString(), 'US-ASCII')
		assertEquals(SmppCharset.UTF_16BE.toString(), 'UTF-16BE')
	}

	@Test
	void testStringToEnum()
	{
		assertEquals(SmppCharset.ISO_8859_1, SmppCharset.stringToEnum('ISO-8859-1'))
		assertEquals(SmppCharset.US_ASCII, SmppCharset.stringToEnum('US-ASCII'))
		assertEquals(SmppCharset.UTF_16BE, SmppCharset.stringToEnum('UTF-16BE'))
	}

}
