package com.yarovoy.smpp

import org.jsmpp.extra.SessionState

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

	static final GeneralDataCoding dataCoding = new GeneralDataCoding(8)

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

	List<String> send(String from, String phone, String text)
	{
		String partId = _smppSession.submitShortMessage(
				'',
				TypeOfNumber.UNKNOWN,
				NumberingPlanIndicator.UNKNOWN,
				'MFComm',
				TypeOfNumber.UNKNOWN,
				NumberingPlanIndicator.UNKNOWN,
				phone,
				new ESMClass(),
				(byte) 0,
				(byte) 0,
				null,
				null,
				new RegisteredDelivery(SMSCDeliveryReceipt.DEFAULT),
				(byte) 0,
				dataCoding,
				(byte) 0,
				text.getBytes('UTF-16BE')
		)

		// Выяснить, как оптавляется сообщение, порезанное на куски.
		null
	}

	Charset detectEncoding(String text)
	{
		if (text == null)
		{
			throw new IllegalArgumentException('Analyzing text must be specified')
		}

		if (text.matches(UNICODE_PATTERN))
		{
			return Charset.UTF_16BE
		}
		else if (text.matches(LATIN_EXTENDED_PATTERN))
		{
			return Charset.ISO_8859_1
		}

		Charset.US_ASCII
	}

	List<String> splitToChunks(String text, Charset charset)
	{
		if (charset == Charset.US_ASCII)
		{
			return text.split("(?<=\\G.{$LATIN_BASIC_MESSAGE_LENGTH})") as List<String>
		}
		else if (charset == Charset.ISO_8859_1)
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