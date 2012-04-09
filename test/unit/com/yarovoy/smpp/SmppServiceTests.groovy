package com.yarovoy.smpp

import grails.test.mixin.TestFor

@TestFor(SmppService)
class SmppServiceTests
{

	SmppService smppService

	void testConnectAndBind()
	{
		smppService = new SmppService()

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
	}
}