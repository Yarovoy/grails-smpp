package grails.plugin.smpp.utils

import grails.plugin.smpp.meta.SmppMessageSegment
import org.jsmpp.bean.Alphabet

import java.util.regex.Pattern

class SmppMessageUtil {

	// ----------------------------------------------------------------------
	// Constants
	// ----------------------------------------------------------------------

	private static final int LATIN_BASIC_BITS_ON_CHAR = 7
	private static final int LATIN_EXTENDED_BITS_ON_CHAR = 8
	private static final int UNICODE_BITS_ON_CHAR = 16

	private static final int BITS_AT_ALL = 1120
	private static final int UDH_BITS = 48

	private static final Pattern LATIN_EXTENDED_PATTERN = ~/.*[\u007f-\u00ff].*/
	private static final Pattern UNICODE_PATTERN = ~/.*[\u0100-\ufffe].*/

	// ----------------------------------------------------------------------
	// Public methods
	// ----------------------------------------------------------------------

	public static Alphabet detectAlphabet(String text) {
		if (text == null) {
			throw new IllegalArgumentException('Analyzing text must be specified')
		}

		if (text.matches(UNICODE_PATTERN)) {
			return Alphabet.ALPHA_UCS2
		} else if (text.matches(LATIN_EXTENDED_PATTERN)) {
			return Alphabet.ALPHA_8_BIT
		}

		Alphabet.ALPHA_DEFAULT
	}
}
