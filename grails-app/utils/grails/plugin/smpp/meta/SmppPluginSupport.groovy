package grails.plugin.smpp.meta

import org.apache.log4j.Logger

class SmppPluginSupport
{
	private static def log = Logger.getLogger(SmppPluginSupport.class)

	static def doWithWebDescriptor = { xml -> }

	static def doWithSpring = {
	}

	static def doWithDynamicMethods = { ctx ->
		// TODO Implement registering dynamic methods to classes (optional)
	}

	static def doWithApplicationContext = { applicationContext ->
		// TODO Implement post initialization spring config (optional)
	}

	static def onChange = { event ->
		// TODO Implement code that is executed when any artefact that this plugin is
		// watching is modified and reloaded. The event contains: event.source,
		// event.application, event.manager, event.ctx, and event.plugin.
	}

	static def onConfigChange = { event ->
		// TODO Implement code that is executed when the project configuration changes.
		// The event is the same as for 'onChange'.


	}

	static def onShutdown = { event ->
		// TODO Implement code that is executed when the application shuts down (optional)
	}
}
