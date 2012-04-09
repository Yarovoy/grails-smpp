package com.yarovoy.smpp

import org.jsmpp.extra.SessionState
import org.jsmpp.bean.*
import org.jsmpp.session.*

class SmppService implements MessageReceiverListener
{

	SMPPSession _smppSession
	String _sessionId

	boolean getConnected()
	{
		_smppSession ? (_smppSession.sessionState == SessionState.BOUND_TX ||
				_smppSession.sessionState == SessionState.BOUND_RX ||
				_smppSession.sessionState == SessionState.BOUND_TRX) : false
	}

	String connectAndBind(String host,
	                      int port,
	                      String systemId,
	                      String password,
	                      String systemType,
	                      BindType bindType = BindType.BIND_TRX,
	                      TypeOfNumber ton = TypeOfNumber.UNKNOWN,
	                      NumberingPlanIndicator npi = NumberingPlanIndicator.UNKNOWN,
	                      String addressRange = null) throws UnknownHostException, ConnectException, IOException
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
			_smppSession = new SMPPSession(messageReceiverListener: this)
			_sessionId = _smppSession.connectAndBind(host, port, bindParameter)
			return _sessionId
		}
		catch (Exception e)
		{
			releaseSessionStuff()

			log.error "Failed to connect and bind to $host:$port\n.", e

			throw e
		}
	}

	void unbindAndClose()
	{
		_smppSession.unbindAndClose()
	}

	private void releaseSessionStuff()
	{
		_smppSession = null
		_sessionId = null

		//		_isConnected = false
		//		_isBroadcasting = false

		//		_autoUnbind = false

		//		_sessionLogsDirPath = null
		//		_sessionLogFile = null
	}

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