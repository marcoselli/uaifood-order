package br.edu.uaifood.orders.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AppConfig {

    @Bean
    fun objectMapper(): ObjectMapper {
        return ObjectMapper().apply {
            registerKotlinModule()
            propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE
        }
    }
}