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
}
