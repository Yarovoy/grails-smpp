package grails.plugin.smpp.meta

import grails.plugin.smpp.utils.ConfigurationReader
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.jsmpp.bean.BindType
import org.jsmpp.bean.NumberingPlanIndicator as NPI
import org.jsmpp.bean.TypeOfNumber

class SmppPluginSupport
{

	private static def log = Logger.getLogger(SmppPluginSupport.class)
	private static def configReader = new ConfigurationReader(ConfigurationHolder.config)

	private static BindType parseBindType(String bindType)
	{
		switch (bindType)
		{
			case 'rx':
				return BindType.BIND_RX
			case 'tx':
				return BindType.BIND_TX
			default:
				BindType.BIND_TRX
		}
	}

	private static TypeOfNumber parseTon(String ton)
	{
		switch (ton)
		{
			case 'abbreviated':
				return TypeOfNumber.ABBREVIATED
			case 'alphanumeric':
				return TypeOfNumber.ALPHANUMERIC
			case 'international':
				return TypeOfNumber.INTERNATIONAL
			case 'national':
				return TypeOfNumber.NATIONAL
			case 'specific':
				return TypeOfNumber.NETWORK_SPECIFIC
			case 'subscriber':
				return TypeOfNumber.SUBSCRIBER_NUMBER
			default:
				return TypeOfNumber.UNKNOWN
		}
	}

	private static NPI parseNpi(String npi)
	{
		switch (npi)
		{
			case 'data':
				return NPI.DATA
			case 'ermes':
				return NPI.ERMES
			case 'internet':
				return NPI.INTERNET
			case 'isdn':
				return NPI.ISDN
			case 'land':
				return NPI.LAND_MOBILE
			case 'national':
				return NPI.NATIONAL
			case 'private':
				return NPI.PRIVATE
			case 'telex':
				return NPI.TELEX
			case 'wap':
				return NPI.WAP
			default:
				return NPI.UNKNOWN
		}
	}

	static def doWithWebDescriptor = { xml -> }
	static def doWithApplicationContext = { applicationContext -> }

	static def doWithSpring = {
		smppConfigHolder(SmppConfigurationHolder) {
			host = configReader.read('grails.plugin.smpp.host')
			port = configReader.read('grails.plugin.smpp.port')
			systemId = configReader.read('grails.plugin.smpp.systemId')
			password = configReader.read('grails.plugin.smpp.password')

			sourceAddr = configReader.read('grails.plugin.smpp.sourceAddr')

			systemType = configReader.read('grails.plugin.smpp.systemType')
			bindType = parseBindType(configReader.read('grails.plugin.smpp.bindType') as String)
			ton = parseTon(configReader.read('grails.plugin.smpp.port') as String)
			npi = parseNpi(configReader.read('grails.plugin.smpp.port') as String)
		}
	}

	static def onChange = { event ->
		// TODO Implement code that is executed when any artefact that this plugin is
		// watching is modified and reloaded. The event contains: event.source,
		// event.application, event.manager, event.ctx, and event.plugin.
	}

	static def onConfigChange = { event ->
		println(configReader.read('grails.plugin.smpp.host'))
		println(configReader.read('grails.views.default.codec'))
	}

	static def doWithDynamicMethods = { ctx ->
		// TODO Implement registering dynamic methods to classes (optional)
	}

	static def onShutdown = { event -> }

}
