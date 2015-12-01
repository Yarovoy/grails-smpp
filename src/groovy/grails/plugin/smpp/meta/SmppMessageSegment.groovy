package grails.plugin.smpp.meta

import org.jsmpp.bean.DataCoding
import org.jsmpp.bean.NumberingPlanIndicator
import org.jsmpp.bean.OptionalParameter
import org.jsmpp.bean.TypeOfNumber

class SmppMessageSegment {

	String text
	DataCoding dataCoding

	OptionalParameter sarMsgRefNum
	OptionalParameter sarSegmentSeqNum
	OptionalParameter sarTotalSegments

	TypeOfNumber sourceAddrTon
	NumberingPlanIndicator sourceAddrNpi
	String sourceAddr

	TypeOfNumber destAddrTon
	NumberingPlanIndicator destAddrNpi
	String destAddr

	byte[] getData() {
		text.getBytes('UTF-16BE')
	}
}
