package edu.fullstackproject.team1.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
class CorsConfig(
	@Value("\${app.cors.allowed-origins}") private val allowedOrigins: String
) {
	@Bean
	fun corsFilter(): CorsFilter {
		val config = CorsConfiguration()

		val origins = allowedOrigins.split(",").map { it.trim() }
		config.allowedOrigins = origins

		config.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
		config.allowedHeaders = listOf("*")
		config.allowCredentials = true

		val source = UrlBasedCorsConfigurationSource()
		source.registerCorsConfiguration("/**", config)
		return CorsFilter(source)
	}
}
