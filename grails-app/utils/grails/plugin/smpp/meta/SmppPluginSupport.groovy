package grails.plugin.smpp.meta

class SmppPluginSupport
{
	static def doWithWebDescriptor = { xml ->
		// TODO Implement additions to web.xml (optional), this event occurs before
	}

	static def doWithSpring = {
		// TODO Implement runtime spring config (optional)
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
