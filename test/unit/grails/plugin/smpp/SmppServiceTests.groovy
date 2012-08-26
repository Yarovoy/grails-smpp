package grails.plugin.smpp

import grails.plugin.smpp.meta.SmppConfigValues
import grails.plugin.smpp.meta.SmppConfigurationHolder
import grails.test.mixin.TestFor
import org.jsmpp.bean.Alphabet
import org.junit.After
import org.junit.Before
import org.junit.Test

@TestFor(SmppService)
class SmppServiceTests
{

	String cyrillicAlphabet =
		'АаБбВвГгДд ЕеЕеЖжЗзИи' +
				'ЙйКкЛлМмНн ОоПпРрСсТт' +
				'УуФфХхЦцЧч ШшЩщЪъЫыЬь' +
				'ЭэЮюЯя'

	String latinAlphabet =
		'AaBbCcDdEe FfGgHhIiJj ' +
				'KkLlMmNnOo PpQqRrSsTt ' +
				'UuVvWwXxYy Zz '

	String latin160Symbols = latinAlphabet +
			latinAlphabet +
			'1234567890 1234567890 1234567890 1234567890 '

	String latin161Symbols = latin160Symbols + '1'

	String latin320Symbols = latin160Symbols + latin160Symbols

	String unicode70Symbols = cyrillicAlphabet + '1'
	String unicode71Symbols = unicode70Symbols + '2'
	String unicode140Symbols = cyrillicAlphabet + cyrillicAlphabet + '12'

	String extendedLatin140 = latinAlphabet + latinAlphabet +
			' 1234567890 12345678 Àþÿ'
	String extendedLatin280 = extendedLatin140 + extendedLatin140

	SmppService smppService
	SmppConfigurationHolder smppConfigHolder

	@Before
	void setUp()
	{
		smppConfigHolder = new SmppConfigurationHolder(
				host: SmppConfigValues.HOST,
				port: SmppConfigValues.PORT,

				systemId: SmppConfigValues.SYSTEM_ID,
				password: SmppConfigValues.PASSWORD,
				systemType: SmppConfigValues.SYSTEM_TYPE,

				bindType: SmppConfigValues.BIND_TYPE,
				ton: SmppConfigValues.TON,
				npi: SmppConfigValues.NPI,

				sourceAddr: SmppConfigValues.FROM
		)

		smppService = new SmppService(
				smppConfigHolder: smppConfigHolder
		)
	}

	@After
	void tearDown()
	{
		smppConfigHolder = null
		smppService = null
	}

	@Test
	void testConnectAndBind()
	{
		String sessionId = smppService.connectAndBind()

		assertTrue smppService.connected
		assertNotNull sessionId

		smppService.unbindAndClose()

		assertFalse smppService.connected
	}

	@Test
	void connectAndBindWithParams()
	{
		String sessionId = smppService.connectAndBind(
				SmppConfigValues.HOST,
				SmppConfigValues.PORT,
				SmppConfigValues.SYSTEM_ID,
				SmppConfigValues.PASSWORD,
				SmppConfigValues.SYSTEM_TYPE
		)

		assertTrue smppService.connected
		assertNotNull sessionId

		smppService.unbindAndClose()

		assertFalse smppService.connected
	}

	@Test
	void testDetectAlphabet()
	{
		assertEquals(Alphabet.ALPHA_UCS2, smppService.detectAlphabet(unicode70Symbols))
		assertEquals(Alphabet.ALPHA_UCS2, smppService.detectAlphabet("This is Latin and a little bit of Юnic0də."))
		assertEquals(
				Alphabet.ALPHA_8_BIT,
				smppService.detectAlphabet(
						"This is the Extended Latin only with some special characters: À, Õ, ÿ."
				)
		)
		assertEquals(Alphabet.ALPHA_DEFAULT, smppService.detectAlphabet(latin160Symbols))
	}

	@Test
	void testSplitToChunksByLengths()
	{
		List<String> result = smppService.splitToChunks(latin160Symbols, 160, 154)

		assertEquals(1, result.size())
		assertEquals(160, result[0].length())

		result = smppService.splitToChunks(latin161Symbols, 160, 154)

		assertEquals(2, result.size())
		assertEquals(154, result[0].length())
		assertEquals(7, result[1].length())

		result = smppService.splitToChunks(latin320Symbols, 160, 154)

		assertEquals(3, result.size())
		assertEquals(154, result[0].length())
		assertEquals(154, result[1].length())
		assertEquals(12, result[2].length())

		result = smppService.splitToChunks(unicode70Symbols, 70, 67)

		assertEquals(1, result.size())
		assertEquals(70, result[0].length())

		result = smppService.splitToChunks(unicode71Symbols, 70, 67)

		assertEquals(2, result.size())
		assertEquals(67, result[0].length())
		assertEquals(4, result[1].length())

		result = smppService.splitToChunks(unicode140Symbols, 70, 67)

		assertEquals(3, result.size())
		assertEquals(67, result[0].length())
		assertEquals(67, result[1].length())
		assertEquals(6, result[2].length())
	}

	/*@Test
	void testSplitToChunks()
	{
//		println unicode70Symbols.size()
//		println unicode140Symbols.size()
//		println latin160Symbols.size()
//		println latin320Symbols.size()
//		println extendedLatin140.size()
//		println extendedLatin280.size()

		assertEquals(
				1,
				smppService.splitToChunks(
						unicode70Symbols,
						smppService.detectAlphabet(
								unicode70Symbols
						)
				).size()
		)

		assertEquals(
				2,
				smppService.splitToChunks(
						unicode140Symbols,
						smppService.detectAlphabet(
								unicode140Symbols
						)
				).size()
		)

		assertEquals(
				1,
				smppService.splitToChunks(
						latin160Symbols,
						smppService.detectAlphabet(
								latin160Symbols
						)
				).size()
		)

		assertEquals(
				2,
				smppService.splitToChunks(
						latin320Symbols,
						smppService.detectAlphabet(
								latin320Symbols
						)
				).size()
		)

		assertEquals(
				1,
				smppService.splitToChunks(
						extendedLatin140,
						smppService.detectAlphabet(
								extendedLatin140
						)
				).size()
		)

		assertEquals(
				2,
				smppService.splitToChunks(
						extendedLatin280,
						smppService.detectAlphabet(
								extendedLatin280
						)
				).size()
		)
	}*/

	/*@Test
	void testSend()
	{
		List ids

		smppService.connectAndBind(
				SmppConfigValues.HOST,
				SmppConfigValues.PORT,
				SmppConfigValues.SYSTEM_ID,
				SmppConfigValues.PASSWORD,
				SmppConfigValues.SYSTEM_TYPE
		)

		ids = smppService.send(
				SmppConfigValues.FROM,
				SmppConfigValues.TO_PHONE,
				unicode70Symbols
		)

		assertNotNull(ids)
		assertEquals(1, ids.size())
		println(ids.get(0))

		*//*ids = smppService.send(
				SmppConfigValues.FROM,
				SmppConfigValues.TO_PHONE,
				unicode140Symbols
		)*//*

		*//*assertNotNull(ids)
		assertEquals(2, ids.size())
		assertFalse(ids.get(0) == ids.get(1))
		println(ids)*//*

		*//*smppService.send(
				SmppConfigValues.FROM,
				SmppConfigValues.TO_PHONE,
				latin160Symbols
		)*//*

		*//*smppService.send(
				SmppConfigValues.FROM,
				SmppConfigValues.TO_PHONE,
				latin320Symbols
		)*//*

		*//*smppService.send(
				SmppConfigValues.FROM,
				SmppConfigValues.TO_PHONE,
				extendedLatin140
		)*//*

		smppService.unbindAndClose()
	}*/


}