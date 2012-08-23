package grails.plugin.smpp.meta

import org.jsmpp.bean.BindType
import org.jsmpp.bean.NumberingPlanIndicator
import org.jsmpp.bean.TypeOfNumber

class SmppConfigurationHolder
{

	String host
	int port
	String systemId
	String password

	String systemType
	BindType bindType
	TypeOfNumber ton
	NumberingPlanIndicator npi

	String sourceAddr

}
