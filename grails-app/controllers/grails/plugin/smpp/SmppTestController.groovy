package grails.plugin.smpp

class SmppTestController
{

	def smppService

	def index = { }

	def showSmppConfigHolder = {
		render("<pre>${smppService.smppConfigHolder.toString()}</pre>")
	}
}
