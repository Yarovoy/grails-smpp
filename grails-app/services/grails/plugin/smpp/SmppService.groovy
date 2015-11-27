package grails.plugin.smpp

import grails.plugin.smpp.meta.SmppConfigurationHolder
import org.jsmpp.bean.*
import org.jsmpp.extra.SessionState
import org.jsmpp.session.*
import org.jsmpp.util.AbsoluteTimeFormatter
import org.jsmpp.util.TimeFormatter

import java.util.regex.Pattern

class SmppService implements MessageReceiverListener {

	// ----------------------------------------------------------------------
	// Constants
	// ----------------------------------------------------------------------

	private static final Pattern LATIN_EXTENDED_PATTERN = ~/.*[\u007f-\u00ff].*/
	private static final Pattern UNICODE_PATTERN = ~/.*[\u0100-\ufffe].*/

	private static final int LATIN_BASIC_BITS_ON_CHAR = 7 // TODO: It's only for working from Easy SMS provider.
	private static final int LATIN_EXTENDED_BITS_ON_CHAR = 8
	private static final int UNICODE_BITS_ON_CHAR = 16

	private static final int BITS_AT_ALL = 1120
	private static final int UDH_BITS = 48

	// ----------------------------------------------------------------------
	// Protected props
	// ----------------------------------------------------------------------

	protected SMPPSession _smppSession

	protected final TimeFormatter timeFormatter = new AbsoluteTimeFormatter()

	protected final Random random = new Random()

	// ----------------------------------------------------------------------
	// Public props
	// ----------------------------------------------------------------------

	SmppConfigurationHolder smppConfigHolder

	public String serviceType = 'CMT'

	static def transactional = false

	// ----------------------------------------------------------------------
	// Getters and setters
	// ----------------------------------------------------------------------

	String getSessionId() {
		_smppSession?.sessionId
	}

	boolean getConnected() {
		_smppSession ? (_smppSession.sessionState == SessionState.BOUND_TX ||
				_smppSession.sessionState == SessionState.BOUND_RX ||
				_smppSession.sessionState == SessionState.BOUND_TRX) : false
	}

	// ----------------------------------------------------------------------
	// Private methods
	// ----------------------------------------------------------------------

	private void releaseSessionStuff() {
		if (connected) {
			_smppSession.unbindAndClose()
			_smppSession = null
		}
	}

	// ----------------------------------------------------------------------
	// Public methods
	// ----------------------------------------------------------------------

	String connectAndBind() {
		BindParameter bindParameter = new BindParameter(
				smppConfigHolder.bindType,
				smppConfigHolder.systemId,
				smppConfigHolder.password,
				smppConfigHolder.systemType,
				smppConfigHolder.ton,
				smppConfigHolder.npi,
				smppConfigHolder.addressRange
		)

		// Connecting...
		try {
			log.info "Connecting to $smppConfigHolder.host:${smppConfigHolder.port}…"

			_smppSession = new SMPPSession(messageReceiverListener: this)
			_smppSession.connectAndBind(
					smppConfigHolder.host,
					smppConfigHolder.port,
					bindParameter
			)

			log.info "Connection established to $smppConfigHolder.host:$smppConfigHolder.port. Session ID is $sessionId."

			return sessionId
		}
		catch (Exception e) {
			releaseSessionStuff()

			log.error "Failed to connect and bind to $smppConfigHolder.host:$smppConfigHolder.port.", e

			throw e
		}
	}

	String connectAndBind(String host,
	                      int port,
	                      String systemId,
	                      String password,
	                      String systemType,
	                      BindType bindType = BindType.BIND_TRX,
	                      TypeOfNumber ton = TypeOfNumber.UNKNOWN,
	                      NumberingPlanIndicator npi = NumberingPlanIndicator.UNKNOWN,
	                      String addressRange = null) throws SmppServiceException,
			UnknownHostException,
			ConnectException,
			IOException {
		smppConfigHolder = new SmppConfigurationHolder(
				host: host,
				port: port,
				systemId: systemId,
				password: password,
				systemType: systemType,
				bindType: bindType,
				ton: ton,
				npi: npi,
				addressRange: addressRange
		)

		BindParameter bindParameter = new BindParameter(
				smppConfigHolder.bindType,
				smppConfigHolder.systemId,
				smppConfigHolder.password,
				smppConfigHolder.systemType,
				smppConfigHolder.ton,
				smppConfigHolder.npi,
				smppConfigHolder.addressRange
		)

		// Connecting...
		try {
			log.info "Connecting to $smppConfigHolder.host:${smppConfigHolder.port}…"

			_smppSession = new SMPPSession(messageReceiverListener: this)
			_smppSession.connectAndBind(
					smppConfigHolder.host,
					smppConfigHolder.port,
					bindParameter,
					30000
			)

			log.info "Connection established to $smppConfigHolder.host:$smppConfigHolder.port. Session ID is $sessionId."

			return sessionId
		}
		catch (Exception e) {
			releaseSessionStuff()

			log.error "Failed to connect and bind to $smppConfigHolder.host:$smppConfigHolder.port.", e

			throw e
		}
	}

	void unbindAndClose() {
		releaseSessionStuff()
	}

	Alphabet detectAlphabet(String text) {
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

	List<String> splitToSegments(String text, int maxLength, int chunkLength) {
		if (text.length() <= maxLength) {
			return [text]
		}

		text.split("(?<=\\G.{$chunkLength})") as List<String>
	}

	List<String> splitToSegments(String text, Alphabet alphabet) {
		int bitsOnChar

		switch (alphabet) {
			case Alphabet.ALPHA_DEFAULT:
				bitsOnChar = LATIN_BASIC_BITS_ON_CHAR
				break
			case (Alphabet.ALPHA_8_BIT):
				bitsOnChar = LATIN_EXTENDED_BITS_ON_CHAR
				break
			default:
				bitsOnChar = UNICODE_BITS_ON_CHAR
		}

		return splitToSegments(
				text,
				Math.floor(BITS_AT_ALL / bitsOnChar).toInteger(),
				Math.floor((BITS_AT_ALL - UDH_BITS) / bitsOnChar).toInteger()
		)
	}

	List<String> splitToSegments(String text) {
		return splitToSegments(
				text,
				detectAlphabet(text)
		)
	}

	String submitSegment(String from, String to, byte[] data, DataCoding dataCoding, int currentNumber, int totalNumber) {
		if (totalNumber > 1) {
			OptionalParameter sarMsgRefNum = OptionalParameters.newSarMsgRefNum((short) random.nextInt())
			OptionalParameter sarSegmentSeqNum = OptionalParameters.newSarSegmentSeqnum(currentNumber)
			OptionalParameter sarTotalSegments = OptionalParameters.newSarTotalSegments(totalNumber)

			return _smppSession.submitShortMessage(
					serviceType,
					TypeOfNumber.UNKNOWN,
					NumberingPlanIndicator.UNKNOWN,
					from,
					TypeOfNumber.UNKNOWN,
					NumberingPlanIndicator.UNKNOWN,
					to,
					new ESMClass(),
					(byte) 0, (byte) 1,
					timeFormatter.format(new Date()),
					null,
					new RegisteredDelivery(SMSCDeliveryReceipt.DEFAULT),
					(byte) 0,
					dataCoding,
					(byte) 0,
					data,
					sarMsgRefNum,
					sarSegmentSeqNum,
					sarTotalSegments
			)
		}

		_smppSession.submitShortMessage(
				serviceType,
				TypeOfNumber.UNKNOWN,
				NumberingPlanIndicator.UNKNOWN,
				from,
				TypeOfNumber.UNKNOWN,
				NumberingPlanIndicator.UNKNOWN,
				to,
				new ESMClass(),
				(byte) 0, (byte) 1,
				timeFormatter.format(new Date()),
				null,
				new RegisteredDelivery(SMSCDeliveryReceipt.DEFAULT),
				(byte) 0,
				dataCoding,
				(byte) 0,
				data
		)
	}

	/*List<String> submitMessage(String from, String phone, String text) throws PDUException,
	                                                                 ResponseTimeoutException,
	                                                                 InvalidResponseException,
	                                                                 NegativeResponseException,
	                                                                 IOException
	{
		if (!from)
		{
			new IllegalArgumentException('You must specify "from" parameter.')
		}
		if (!phone)
		{
			new IllegalArgumentException('You must specify "phone" parameter.')
		}
		if (!text)
		{
			new IllegalArgumentException('You must specify "text" parameter.')
		}

		final TimeFormatter timeFormatter = new RelativeTimeFormatter()

		// Variables related to encoding.
		final Alphabet alphabet = detectAlphabet(
				text
		)

		String charset
		DataCoding dataCoding

		if (alphabet == Alphabet.ALPHA_DEFAULT)
		{
			charset = 'US-ASCII'

			dataCoding = new GeneralDataCoding(
					false,
					false,
					MessageClass.CLASS0,
					Alphabet.ALPHA_DEFAULT
			)
		}
		else if (alphabet == Alphabet.ALPHA_8_BIT)
		{
			charset = 'ISO-8859-1'

			dataCoding = new GeneralDataCoding(
					false,
					false,
					MessageClass.CLASS0,
					Alphabet.ALPHA_8_BIT
			)
		}
		else
		{
			charset = 'UTF-16BE'

			dataCoding = new GeneralDataCoding(
					false,
					false,
					MessageClass.CLASS0,
					Alphabet.ALPHA_UCS2
			)
		}

		// Variables related to the message.
		final List<String> parts = splitToSegments(
				text,
				alphabet
		)
		final int partsNum = parts.size()
		final List<String> partIds = []

		if (partsNum > 1)
		{
			final Random random = new Random()
			final short msgRefNum = (short) Math.abs(random.nextInt())

			final int segmentsNum = parts.size()

			OptionalParameter sarMsgRefNum = OptionalParameters.newSarMsgRefNum(msgRefNum)
			OptionalParameter sarTotalSegments = OptionalParameters.newSarTotalSegments(segmentsNum)

			log.info("Sending multipart message. The referense number is $msgRefNum and total number of segments is $segmentsNum.")

			parts.eachWithIndex {String part, index ->

				int seqNum = index + 1;
				OptionalParameter sarSegmentSeqNum = OptionalParameters.newSarSegmentSeqnum(seqNum)

				log.info "Sending message part $seqNum of $segmentsNum"

				println part.length()

				partIds << _smppSession.submitShortMessage(
						serviceType,
						TypeOfNumber.UNKNOWN,
						NumberingPlanIndicator.UNKNOWN,
						from,
						TypeOfNumber.UNKNOWN,
						NumberingPlanIndicator.UNKNOWN,
						phone,
						new ESMClass(),
						(byte) 0, (byte) 1,
						timeFormatter.format(new Date()),
						null,
						new RegisteredDelivery(SMSCDeliveryReceipt.DEFAULT),
						(byte) 0,
						dataCoding,
						(byte) 0,
						part.getBytes(charset),
						sarMsgRefNum,
						sarSegmentSeqNum,
						sarTotalSegments
				)

				log.info "Message part sended"

			}
		}
		else
		{
			partIds << _smppSession.submitShortMessage(
					serviceType,
					TypeOfNumber.UNKNOWN,
					NumberingPlanIndicator.UNKNOWN,
					from,
					TypeOfNumber.UNKNOWN,
					NumberingPlanIndicator.UNKNOWN,
					phone,
					new ESMClass(),
					(byte) 0, (byte) 1,
					timeFormatter.format(new Date()),
					null,
					new RegisteredDelivery(SMSCDeliveryReceipt.DEFAULT),
					(byte) 0,
					dataCoding,
					(byte) 0,
					text.getBytes(charset)
			)
		}

		partIds
	}*/

	// ----------------------------------------------------------------------
	// Event handlers
	// ----------------------------------------------------------------------

	void onAcceptDeliverSm(DeliverSm deliverSm) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	void onAcceptAlertNotification(AlertNotification alertNotification) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	DataSmResult onAcceptDataSm(DataSm dataSm, Session session) {
		return null //To change body of implemented methods use File | Settings | File Templates.
	}

}
