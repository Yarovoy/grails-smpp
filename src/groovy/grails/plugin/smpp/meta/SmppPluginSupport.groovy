package grails.plugin.smpp.meta

import grails.plugin.smpp.utils.ConfigurationReader
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.jsmpp.bean.BindType
import org.jsmpp.bean.NumberingPlanIndicator as NPI
import org.jsmpp.bean.TypeOfNumber

class SmppPluginSupport
{

	// --------------------------------------------------------------------------------
	// Private props
	// --------------------------------------------------------------------------------

	private static def log = Logger.getLogger(SmppPluginSupport.class)
	private static def configReader = new ConfigurationReader(ConfigurationHolder.config)

	// --------------------------------------------------------------------------------
	// Private methods
	// --------------------------------------------------------------------------------

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

	// --------------------------------------------------------------------------------
	// Event handlers
	// --------------------------------------------------------------------------------

	static def doWithWebDescriptor = { xml -> }
	static def doWithApplicationContext = { applicationContext -> }

	static def doWithSpring = {
		log.debug('Configuring "smppConfigHolder" bean with Spring.')

		smppConfigHolder(SmppConfigurationHolder) {
			host = SmppPluginSupport.configReader.read('grails.plugin.smpp.host')
			port = SmppPluginSupport.configReader.read('grails.plugin.smpp.port')
			systemId = SmppPluginSupport.configReader.read('grails.plugin.smpp.systemId')
			password = SmppPluginSupport.configReader.read('grails.plugin.smpp.password')

			sourceAddr = SmppPluginSupport.configReader.read('grails.plugin.smpp.sourceAddr')

			systemType = SmppPluginSupport.configReader.read('grails.plugin.smpp.systemType')
			bindType = SmppPluginSupport.parseBindType(SmppPluginSupport.configReader.read('grails.plugin.smpp.bindType') as String)
			ton = SmppPluginSupport.parseTon(SmppPluginSupport.configReader.read('grails.plugin.smpp.ton') as String)
			npi = SmppPluginSupport.parseNpi(SmppPluginSupport.configReader.read('grails.plugin.smpp.npi') as String)
		}
	}

	static def onConfigChange = { event ->
		log.debug('Update SMPP session parameters after config reloading.')

		configReader = new ConfigurationReader(ConfigurationHolder.config)

		def smppConfigHolderBean = event.ctx.getBean('smppConfigHolder')

		smppConfigHolderBean.host = SmppPluginSupport.configReader.read('grails.plugin.smpp.host')
		smppConfigHolderBean.port = SmppPluginSupport.configReader.read('grails.plugin.smpp.port')
		smppConfigHolderBean.systemId = SmppPluginSupport.configReader.read('grails.plugin.smpp.systemId')
		smppConfigHolderBean.password = SmppPluginSupport.configReader.read('grails.plugin.smpp.password')

		smppConfigHolderBean.sourceAddr = SmppPluginSupport.configReader.read('grails.plugin.smpp.sourceAddr')

		smppConfigHolderBean.systemType = SmppPluginSupport.configReader.read('grails.plugin.smpp.systemType')
		smppConfigHolderBean.bindType = SmppPluginSupport.parseBindType(SmppPluginSupport.configReader.read('grails.plugin.smpp.bindType') as String)
		smppConfigHolderBean.ton = SmppPluginSupport.parseTon(SmppPluginSupport.configReader.read('grails.plugin.smpp.ton') as String)
		smppConfigHolderBean.npi = SmppPluginSupport.parseNpi(SmppPluginSupport.configReader.read('grails.plugin.smpp.npi') as String)

	}

	static def onChange = { event ->
		// TODO Implement code that is executed when any artefact that this plugin is
		// watching is modified and reloaded. The event contains: event.source,
		// event.application, event.manager, event.ctx, and event.plugin.
	}

	static def doWithDynamicMethods = { ctx ->
		// TODO Implement registering dynamic methods to classes (optional)
	}

	static def onShutdown = { event -> }

}
