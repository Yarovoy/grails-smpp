import grails.plugin.smpp.meta.SmppPluginSupport

class SmppGrailsPlugin
{
	def version = '0.1'
	def grailsVersion = '2.0 > *'
	def dependsOn = [:]
	def loadAfter = ['services', 'controllers']
	def observe = ['services', 'controllers']
	def watchedResources = ["grails-app/services/**/*Service.groovy", "grails-app/controllers/**/*Controller.groovy"]
	def pluginExcludes = [
			'grails-app/conf/Config.groovy',
			'grails-app/conf/DataSource.groovy',
			'grails-app/conf/UrlMappings.groovy',
			'grails-app/controllers/**/*Controller.groovy',
			'grails-app/services/**/*Service.groovy',
			'grails-app/views/**/*.gsp'
	]

	def title = 'Grails Smpp Plugin'
	def description = 'Grails SMPP plug-in provides easy-way communicating with SMPP-chanel by taking advantage of JSMPP Java library.'
	def documentation = 'https://github.com/Yarovoy/grails-smpp/wiki'
	def author = 'Yuriy Yarovoy'
	def authorEmail = 'yuriy.yarovoy@gmail.com'

	def developers = [[name: 'Yuriy Yarovoy', email: 'yuriy.yarovoy@gmail.com']]
	def issueManagement = [system: 'GitHub', url: 'https://github.com/Yarovoy/grails-smpp/issues']
	def scm = [url: 'https://github.com/Yarovoy/grails-smpp']

	def doWithWebDescriptor = SmppPluginSupport.doWithWebDescriptor
	def doWithSpring = SmppPluginSupport.doWithSpring
	def doWithDynamicMethods = SmppPluginSupport.doWithDynamicMethods
	def doWithApplicationContext = SmppPluginSupport.doWithApplicationContext
	def onChange = SmppPluginSupport.onChange
	def onConfigChange = SmppPluginSupport.onConfigChange
	def onShutdown = SmppPluginSupport.onShutdown
}
