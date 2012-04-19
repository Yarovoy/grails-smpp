package grails.plugin.smpp

import com.yarovoy.smpp.SmppException
import org.jsmpp.InvalidResponseException
import org.jsmpp.PDUException
import org.jsmpp.extra.NegativeResponseException
import org.jsmpp.extra.ResponseTimeoutException
import org.jsmpp.extra.SessionState
import org.jsmpp.util.RelativeTimeFormatter
import org.jsmpp.util.TimeFormatter

import java.util.regex.Pattern

import org.jsmpp.bean.*
import org.jsmpp.session.*

class SmppService implements MessageReceiverListener
{

	// ----------------------------------------------------------------------
	// Constants
	// ----------------------------------------------------------------------

	static final Pattern LATIN_EXTENDED_PATTERN = ~/.*[\u007f-\u00ff].*/
	static final Pattern UNICODE_PATTERN = ~/.*[\u0100-\ufffe].*/

	static final String serviceType = 'CMT'

	static final int LATIN_BASIC_MESSAGE_LENGTH = 160
	static final int LATIN_EXTENDED_MESSAGE_LENGTH = 140
	static final int UNICODE_MESSAGE_LENGTH = 70

	// ----------------------------------------------------------------------
	// Private props
	// ----------------------------------------------------------------------

	SMPPSession _smppSession
	String _sessionId

	// ----------------------------------------------------------------------
	// Getters and setters
	// ----------------------------------------------------------------------

	boolean getConnected()
	{
		_smppSession ? (_smppSession.sessionState == SessionState.BOUND_TX ||
				_smppSession.sessionState == SessionState.BOUND_RX ||
				_smppSession.sessionState == SessionState.BOUND_TRX) : false
	}

	// ----------------------------------------------------------------------
	// Private methods
	// ----------------------------------------------------------------------

	private void releaseSessionStuff()
	{
		if (connected)
		{
			_smppSession.unbindAndClose()
			_smppSession = null
		}

		_sessionId = null
	}

	// ----------------------------------------------------------------------
	// Public methods
	// ----------------------------------------------------------------------

	String connectAndBind(String host,
	                      int port,
	                      String systemId,
	                      String password,
	                      String systemType,
	                      BindType bindType = BindType.BIND_TRX,
	                      TypeOfNumber ton = TypeOfNumber.UNKNOWN,
	                      NumberingPlanIndicator npi = NumberingPlanIndicator.UNKNOWN,
	                      String addressRange = null) throws SmppException,
	                                                         UnknownHostException,
	                                                         ConnectException,
	                                                         IOException
	{
		BindParameter bindParameter = new BindParameter(
				bindType,
				systemId,
				password,
				systemType,
				ton,
				npi,
				addressRange
		)

		// Connecting...
		try
		{
			log.info "Connecting to $host:$port"

			_smppSession = new SMPPSession(messageReceiverListener: this)
			_sessionId = _smppSession.connectAndBind(host, port, bindParameter)

			log.info "Connection established to $host:$port. Session ID is $_sessionId."

			return _sessionId
		}
		catch (Exception e)
		{
			releaseSessionStuff()

			log.error "Failed to connect and bind to $host:$port.", e

			throw e
		}
	}

	void unbindAndClose()
	{
		_smppSession.unbindAndClose()
	}

	List<String> send(String from, String phone, String text) throws PDUException,
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

			println(dataCoding.value())
			println(new GeneralDataCoding().value())
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
		final List<String> parts = splitToChunks(
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
	}

	Alphabet detectAlphabet(String text)
	{
		if (text == null)
		{
			throw new IllegalArgumentException('Analyzing text must be specified')
		}

		if (text.matches(UNICODE_PATTERN))
		{
			return Alphabet.ALPHA_UCS2
		}
		else if (text.matches(LATIN_EXTENDED_PATTERN))
		{
			return Alphabet.ALPHA_8_BIT
		}

		Alphabet.ALPHA_DEFAULT
	}

	List<String> splitToChunks(String text, Alphabet alphabet)
	{
		if (alphabet == Alphabet.ALPHA_DEFAULT)
		{
			return text.split("(?<=\\G.{$LATIN_BASIC_MESSAGE_LENGTH})") as List<String>
		}
		else if (alphabet == Alphabet.ALPHA_8_BIT)
		{
			return text.split("(?<=\\G.{$LATIN_EXTENDED_MESSAGE_LENGTH})") as List<String>
		}

		text.split("(?<=\\G.{$UNICODE_MESSAGE_LENGTH})") as List<String>
	}

	// ----------------------------------------------------------------------
	// Event handlers
	// ----------------------------------------------------------------------

	void onAcceptDeliverSm(DeliverSm deliverSm)
	{
		//To change body of implemented methods use File | Settings | File Templates.
	}

	void onAcceptAlertNotification(AlertNotification alertNotification)
	{
		//To change body of implemented methods use File | Settings | File Templates.
	}

	DataSmResult onAcceptDataSm(DataSm dataSm, Session session)
	{
		return null //To change body of implemented methods use File | Settings | File Templates.
	}
}