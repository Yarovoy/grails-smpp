package grails.plugin.smpp.utils

import org.apache.log4j.Logger

class ConfigurationReader
{

	def config

	private static def log = Logger.getLogger(ConfigurationReader.class)

	public ConfigurationReader(ConfigObject configObject) {
		config = configObject.toProperties()
	}

	public def read(key, defaultValue = null) {
		def value = config[key] ?: defaultValue
		log.debug "returning value (${value}) for key (${key})"
		return value
	}
}
