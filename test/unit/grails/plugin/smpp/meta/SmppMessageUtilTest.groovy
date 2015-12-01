package grails.plugin.smpp.meta

import grails.plugin.smpp.utils.SmppMessageUtil as SMU
import org.jsmpp.bean.Alphabet
import org.junit.Test

class SmppMessageUtilTest extends GroovyTestCase {

	String cyrillicAlphabet =
		'АаБбВвГгДд ЕеЁёЖжЗзИи' +
				'ЙйКкЛлМмНн ОоПпРрСсТт' +
				'УуФфХхЦцЧч ШшЩщЪъЫыЬь' +
				'ЭэЮюЯя'

	String latinAlphabet =
		'AaBbCcDdEe' +
				'FfGgHhIiJj' +
				'KkLlMmNnOo' +
				'PpQqRrSsTt' +
				'UuVvWwXxYy' +
				'Zz12345678'

	String latin160Symbols =
		latinAlphabet +
				latinAlphabet +
				'1234567890' +
				'1234567890' +
				'1234567890' +
				'1234567890'
	String latin161Symbols = latin160Symbols + '1'
	String latin320Symbols = latin160Symbols + latin160Symbols

	String extendedLatin140 = latinAlphabet + latinAlphabet +
			'ÀþÿÀþÿÀþÿ 1234567890'
	String extendedLatin280 = extendedLatin140 + extendedLatin140

	String unicode70Symbols = cyrillicAlphabet + '1'
	String unicode71Symbols = unicode70Symbols + '2'
	String unicode140Symbols = cyrillicAlphabet + cyrillicAlphabet + '12'


	@Test
	void testDetectAlphabet() {
		assertEquals(Alphabet.ALPHA_UCS2, SMU.detectAlphabet(unicode70Symbols))
		assertEquals(
				Alphabet.ALPHA_UCS2,
				SMU.detectAlphabet("This is a Basic Latin with a little bit of Юnic0də.")
		)
		assertEquals(
				Alphabet.ALPHA_8_BIT,
				SMU.detectAlphabet(
						"This is an Extended Latin with some special characters: À, Õ, ÿ."
				)
		)
		assertEquals(Alphabet.ALPHA_DEFAULT, SMU.detectAlphabet(latin160Symbols))
	}

	@Test
	void testSplitToSegments() {
		List<SmppMessageSegment> chunks = SMU.splitToSegments(latin160Symbols)

		assertEquals(1, chunks.size())
		assertEquals(160, chunks[0].text.length())

		chunks = SMU.splitToSegments(latin161Symbols)

		assertEquals(2, chunks.size())
		assertEquals(153, chunks[0].text.length())
		assertEquals(8, chunks[1].text.length())

		chunks = SMU.splitToSegments(latin320Symbols)

		assertEquals(3, chunks.size())
		assertEquals(153, chunks[0].text.length())
		assertEquals(153, chunks[1].text.length())
		assertEquals(14, chunks[2].text.length())

		chunks = SMU.splitToSegments(extendedLatin140)

		assertEquals(1, chunks.size())
		assertEquals(140, chunks[0].text.length())

		chunks = SMU.splitToSegments(extendedLatin280)

		assertEquals(3, chunks.size())
		assertEquals(134, chunks[0].text.length())
		assertEquals(134, chunks[1].text.length())
		assertEquals(12, chunks[2].text.length())

		chunks = SMU.splitToSegments(unicode70Symbols)

		assertEquals(1, chunks.size())
		assertEquals(70, chunks[0].text.length())

		chunks = SMU.splitToSegments(unicode71Symbols)

		assertEquals(2, chunks.size())
		assertEquals(67, chunks[0].text.length())
		assertEquals(4, chunks[1].text.length())

		chunks = SMU.splitToSegments(unicode140Symbols)

		assertEquals(3, chunks.size())
		assertEquals(67, chunks[0].text.length())
		assertEquals(67, chunks[1].text.length())
		assertEquals(6, chunks[2].text.length())
	}
}
