package br.edu.uaifood.orders

import br.edu.uaifood.orders.util.ScopeUtils
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.core.env.AbstractEnvironment

@SpringBootApplication
class UaiFoodOrdersApplication

fun main(args: Array<String>) {
	val profile = ScopeUtils.getProfileFromScope()
	System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, profile)
	runApplication<UaiFoodOrdersApplication>(*args)
}
