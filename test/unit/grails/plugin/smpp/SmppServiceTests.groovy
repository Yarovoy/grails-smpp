package grails.plugin.smpp

import grails.plugin.smpp.meta.SmppConfigValues
import grails.plugin.smpp.meta.SmppConfigurationHolder
import grails.test.mixin.TestFor
import org.jsmpp.bean.Alphabet
import org.jsmpp.bean.DataCoding
import org.jsmpp.bean.GeneralDataCoding
import org.junit.After
import org.junit.Before
import org.junit.Test

@TestFor(SmppService)
class SmppServiceTests {

	SmppService smppService
	SmppConfigurationHolder smppConfigHolder

	@Before
	void setUp() {
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
	void tearDown() {
		smppConfigHolder = null
		smppService = null
	}

	/*@Test
	void testConnectAndBind()
	{
		smppService.connectAndBind()

		assertTrue smppService.connected
		assertNotNull smppService.sessionId

		smppService.unbindAndClose()

		assertFalse smppService.connected
		assertNull smppService.sessionId
	}*/

	/*@Test
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
	}*/

	/*


	@Test
	void testSplitToSegmentsByLengths()
	{
		List<String> result = smppService.splitToSegments(latin160Symbols, 160, 154)

		assertEquals(1, result.size())
		assertEquals(160, result[0].length())

		result = smppService.splitToSegments(latin161Symbols, 160, 154)

		assertEquals(2, result.size())
		assertEquals(154, result[0].length())
		assertEquals(7, result[1].length())

		result = smppService.splitToSegments(latin320Symbols, 160, 154)

		assertEquals(3, result.size())
		assertEquals(154, result[0].length())
		assertEquals(154, result[1].length())
		assertEquals(12, result[2].length())

		result = smppService.splitToSegments(unicode70Symbols, 70, 67)

		assertEquals(1, result.size())
		assertEquals(70, result[0].length())

		result = smppService.splitToSegments(unicode71Symbols, 70, 67)

		assertEquals(2, result.size())
		assertEquals(67, result[0].length())
		assertEquals(4, result[1].length())

		result = smppService.splitToSegments(unicode140Symbols, 70, 67)

		assertEquals(3, result.size())
		assertEquals(67, result[0].length())
		assertEquals(67, result[1].length())
		assertEquals(6, result[2].length())
	}

	@Test
	void testSplitToSegmentsByAlphabet()
	{
		def chunks = smppService.splitToSegments(
				latin160Symbols,
				Alphabet.ALPHA_DEFAULT
		)

		assertEquals(1, chunks.size())
		assertEquals(160, chunks[0].length())

		chunks = smppService.splitToSegments(
				latin161Symbols,
				Alphabet.ALPHA_DEFAULT
		)

		assertEquals(2, chunks.size())
		assertEquals(153, chunks[0].length())
		assertEquals(8, chunks[1].length())

		chunks = smppService.splitToSegments(
				latin320Symbols,
				Alphabet.ALPHA_DEFAULT
		)

		assertEquals(3, chunks.size())
		assertEquals(153, chunks[0].length())
		assertEquals(153, chunks[1].length())
		assertEquals(14, chunks[2].length())

		chunks = smppService.splitToSegments(
				extendedLatin140,
				Alphabet.ALPHA_8_BIT
		)

		assertEquals(1, chunks.size())
		assertEquals(140, chunks[0].length())

		chunks = smppService.splitToSegments(
				extendedLatin280,
				Alphabet.ALPHA_8_BIT
		)

		assertEquals(3, chunks.size())
		assertEquals(134, chunks[0].length())
		assertEquals(134, chunks[1].length())
		assertEquals(12, chunks[2].length())

		chunks = smppService.splitToSegments(
				unicode70Symbols,
				Alphabet.ALPHA_UCS2
		)

		assertEquals(1, chunks.size())
		assertEquals(70, chunks[0].length())

		chunks = smppService.splitToSegments(
				unicode71Symbols,
				Alphabet.ALPHA_UCS2
		)

		assertEquals(2, chunks.size())
		assertEquals(67, chunks[0].length())
		assertEquals(4, chunks[1].length())

		chunks = smppService.splitToSegments(
				unicode140Symbols,
				Alphabet.ALPHA_UCS2
		)

		assertEquals(3, chunks.size())
		assertEquals(67, chunks[0].length())
		assertEquals(67, chunks[1].length())
		assertEquals(6, chunks[2].length())
	}



	@Test
	void testSubmitSegment()
	{
		smppService.connectAndBind()

		byte[] data = unicode70Symbols.getBytes('UTF-16BE')
		DataCoding dataCoding = new GeneralDataCoding(Alphabet.ALPHA_UCS2)
		smppService.submitSegment(SmppConfigValues.FROM, SmppConfigValues.TO_PHONE, data, dataCoding, 1, 1)

		/*smppService.splitToSegments(unicode71Symbols).eachWithIndex {String segment, int index ->
			smppService.submitSegment(
					SmppConfigValues.FROM,
					SmppConfigValues.TO_PHONE,
					segment.getBytes('UTF-16BE'),
					dataCoding,
					index + 1,
					2
			)
		}*/

		smppService.unbindAndClose()
	}

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

		ids = smppService.submitMessage(
				SmppConfigValues.FROM,
				SmppConfigValues.TO_PHONE,
				unicode70Symbols
		)

		assertNotNull(ids)
		assertEquals(1, ids.size())
		println(ids.get(0))

		*//*ids = smppService.submitMessage(
				SmppConfigValues.FROM,
				SmppConfigValues.TO_PHONE,
				unicode140Symbols
		)*//*

		*//*assertNotNull(ids)
		assertEquals(2, ids.size())
		assertFalse(ids.get(0) == ids.get(1))
		println(ids)*//*

		*//*smppService.submitMessage(
				SmppConfigValues.FROM,
				SmppConfigValues.TO_PHONE,
				latin160Symbols
		)*//*

		*//*smppService.submitMessage(
				SmppConfigValues.FROM,
				SmppConfigValues.TO_PHONE,
				latin320Symbols
		)*//*

		*//*smppService.submitMessage(
				SmppConfigValues.FROM,
				SmppConfigValues.TO_PHONE,
				extendedLatin140
		)*//*

		smppService.unbindAndClose()
	}*/


}