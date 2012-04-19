package grails.plugin.smpp

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

	String unicode70Symbols = cyrillicAlphabet + '1'
	String unicode140Symbols = cyrillicAlphabet + cyrillicAlphabet + '12'
	String latin160Symbols = latinAlphabet + latinAlphabet + '1234567890 1234567890 1234567890 1234567890 '
	String latin320Symbols = latin160Symbols + latin160Symbols
	String extendedLatin140 = latinAlphabet + latinAlphabet +
			' 1234567890 12345678 Àþÿ'
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
	void testDetectAlphabet()
	{
		assertEquals(Alphabet.ALPHA_UCS2, smppService.detectAlphabet(unicode70Symbols))
		assertEquals(Alphabet.ALPHA_UCS2, smppService.detectAlphabet("This is Latin and a little bit of Юnicode."))
		assertEquals(
				Alphabet.ALPHA_8_BIT,
				smppService.detectAlphabet(
						"This is the Extended Latin only with some special symbols: À, Õ, ÿ."
				)
		)
		assertEquals(Alphabet.ALPHA_DEFAULT, smppService.detectAlphabet(latin160Symbols))
	}

	@Test
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
	}

	@Test
	void testSend()
	{
		List ids

		smppService.connectAndBind(
				SmppConfig.HOST,
				SmppConfig.PORT,
				SmppConfig.SYSTEM_ID,
				SmppConfig.PASSWORD,
				SmppConfig.SYSTEM_TYPE
		)

		/*ids = smppService.send(
				SmppConfig.FROM,
				SmppConfig.TO_PHONE,
				unicode70Symbols
		)

		assertNotNull(ids)
		assertEquals(1, ids.size())
		println(ids.get(0))*/

		/*ids = smppService.send(
				SmppConfig.FROM,
				SmppConfig.TO_PHONE,
				unicode140Symbols
		)*/

		/*assertNotNull(ids)
		assertEquals(2, ids.size())
		assertFalse(ids.get(0) == ids.get(1))
		println(ids)*/

		smppService.send(
				SmppConfig.FROM,
				SmppConfig.TO_PHONE,
				latin160Symbols
		)

		/*smppService.send(
				SmppConfig.FROM,
				SmppConfig.TO_PHONE,
				extendedLatin140
		)*/

		smppService.unbindAndClose()
	}

}