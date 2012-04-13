package com.yarovoy.smpp

import grails.test.mixin.TestFor
import org.junit.After
import org.junit.Before
import org.junit.Test

@TestFor(SmppService)
class SmppServiceTests
{

	String cyrillicAlphabet =
		'АаБбВвГгДдЕеЁёЖжЗзИи' +
				'ЙйКкЛлМмНнОоПпРрСсТт' +
				'УуФфХхЦцЧчШшЩщЪъЫыЬь' +
				'ЭэЮюЯя'

	String latinAlphabet =
		'AaBbCcDdEeFfGgHhIiJj' +
				'KkLlMmNnOoPpQqRrSsTt' +
				'UuVvWwXxYyZz'

	String unicode70Symbols = cyrillicAlphabet + '1234'
	String unicode140Symbols = cyrillicAlphabet + cyrillicAlphabet + '12345678'
	String latin160Symbols = latinAlphabet + latinAlphabet + latinAlphabet + '1234'
	String latin320Symbols = latin160Symbols + latin160Symbols
	String extendedLatin140 = latinAlphabet + latinAlphabet +
			'12345678901234567890' +
			'1234567890ÀÆÐÑþÿ'
	String extendedLatin280 = extendedLatin140 + extendedLatin140

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
		assertEquals(Charset.UTF_16BE, smppService.detectEncoding(unicode70Symbols))
		assertEquals(Charset.UTF_16BE, smppService.detectEncoding("This is Latin and a little bit of Юnicode."))
		assertEquals(
				Charset.ISO_8859_1,
				smppService.detectEncoding(
						"This is the Extended Latin only with some special symbols: À, Õ, ÿ."
				)
		)
		assertEquals(Charset.US_ASCII, smppService.detectEncoding(latin160Symbols))
	}

	@Test
	void testSplitToChunks()
	{
		assertEquals(
				1,
				smppService.splitToChunks(
						unicode70Symbols,
						smppService.detectEncoding(
								unicode70Symbols
						)
				).size()
		)

		assertEquals(
				2,
				smppService.splitToChunks(
						unicode140Symbols,
						smppService.detectEncoding(
								unicode140Symbols
						)
				).size()
		)

		assertEquals(
				1,
				smppService.splitToChunks(
						latin160Symbols,
						smppService.detectEncoding(
								latin160Symbols
						)
				).size()
		)

		assertEquals(
				2,
				smppService.splitToChunks(
						latin320Symbols,
						smppService.detectEncoding(
								latin320Symbols
						)
				).size()
		)

		assertEquals(
				1,
				smppService.splitToChunks(
						extendedLatin140,
						smppService.detectEncoding(
								extendedLatin140
						)
				).size()
		)

		assertEquals(
				2,
				smppService.splitToChunks(
						extendedLatin280,
						smppService.detectEncoding(
								extendedLatin280
						)
				).size()
		)
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
				longUnicodeMessage)

		smppService.unbindAndClose()
	}*/

	/*@Test
	void testSplitToChunks()
	{
		println smppService.splitToChunks('Тест тест тест тест')
	}*/
}