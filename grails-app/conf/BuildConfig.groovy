grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6

grails.project.dependency.resolution = {

	inherits "global"

	log "warn"

	repositories {
		mavenCentral()
		grailsHome()
		grailsCentral()
		grailsPlugins()
	}

	dependencies {
		runtime 'org.slf4j:slf4j-api:1.6.6'
		runtime 'org.slf4j:slf4j-simple:1.6.6'

		runtime 'com.googlecode.jsmpp:jsmpp:2.1.0'
	}

	plugins {
		build(":tomcat:$grailsVersion", ":release:2.0.2") { export = false }
	}
}
