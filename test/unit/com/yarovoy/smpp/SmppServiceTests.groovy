package com.yarovoy.smpp

import grails.test.mixin.TestFor
import org.junit.After
import org.junit.Before
import org.junit.Test

@TestFor(SmppService)
class SmppServiceTests
{

	SmppService smppService

	@Before
	void setUp()
	{
		smppService = new SmppService()
	}

	@After
	void tearDown()
	{
		smppService = null
	}

	/*@Test
	void testConnectAndBind()
	{
		String sessionId = smppService.connectAndBind(
				SmppConfig.HOST,
				SmppConfig.PORT,
				SmppConfig.SYSTEM_ID,
				SmppConfig.PASSWORD,
				SmppConfig.SYSTEM_TYPE
		)

		assertTrue smppService.connected
		assertNotNull sessionId

		smppService.unbindAndClose()

		assertFalse smppService.connected
	}*/

	@Test
	void testDetectCharset()
	{
		assertEquals(Charset.UTF_16BE, smppService.detectEncoding('Это текст с символами, поддерживаемыми Юникодом. Это текст с символами, поддерживаемыми Юникодом. Это текст с символами, поддерживаемыми Юникодом. '))
		assertEquals(Charset.UTF_16BE, smppService.detectEncoding("This is Latin and a little bit of Юnicode."))
		assertEquals(
				Charset.ISO_8859_1,
				smppService.detectEncoding(
						"This is the Extended Latin only with some special symbols: À, Õ, ÿ."
				)
		)
		assertEquals(Charset.US_ASCII, smppService.detectEncoding("This is the Basic Latin only."))
	}

	/*@Test
	void testSend()
	{
		smppService.connectAndBind(
				SmppConfig.HOST,
				SmppConfig.PORT,
				SmppConfig.SYSTEM_ID,
				SmppConfig.PASSWORD,
				SmppConfig.SYSTEM_TYPE
		)

		smppService.send(
				'MFComm',
				'79162778505',
				'Это текст с символами, поддерживаемыми Юникодом. Это текст с символами, поддерживаемыми Юникодом. Это текст с символами, поддерживаемыми Юникодом. ')

		smppService.unbindAndClose()
	}*/

	/*@Test
	void testSplitToChunks()
	{
		println smppService.splitToChunks('Тест тест тест тест')
	}*/
}