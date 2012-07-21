grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6

grails.project.dependency.resolution = {

	inherits "global"

	log "warn"

	repositories {
		mavenCentral()
		grailsCentral()
	}

	dependencies {
		compile 'com.googlecode.jsmpp:jsmpp:2.1.0'
	}

	plugins {
		build(":tomcat:$grailsVersion",
		      ":release:1.0.0") {
			export = false
		}
	}
}
