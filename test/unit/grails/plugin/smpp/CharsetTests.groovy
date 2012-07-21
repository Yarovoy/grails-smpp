package grails.plugin.smpp

import grails.test.mixin.TestFor
import org.junit.Test

@TestFor(SmppService)
class CharsetTests
{

	@Test
	void testToString()
	{
		assertEquals(Charset.ISO_8859_1.toString(), 'ISO-8859-1')
		assertEquals(Charset.US_ASCII.toString(), 'US-ASCII')
		assertEquals(Charset.UTF_16BE.toString(), 'UTF-16BE')
	}

	@Test
	void testStringToEnum()
	{
		assertEquals(Charset.ISO_8859_1, Charset.stringToEnum('ISO-8859-1'))
		assertEquals(Charset.US_ASCII, Charset.stringToEnum('US-ASCII'))
		assertEquals(Charset.UTF_16BE, Charset.stringToEnum('UTF-16BE'))
	}

}
