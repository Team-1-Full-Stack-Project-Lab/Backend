package edu.fullstackproject.team1.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.filter.CorsFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
	private val corsFilter: CorsFilter,
	private val jwtAuthFilter: JwtAuthFilter,
) {
	@Bean
	fun filterChain(http: HttpSecurity): SecurityFilterChain =
		http
			.cors { }
			.csrf { csrf -> csrf.disable() }
			.sessionManagement { session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
			.formLogin { form -> form.disable() }
			.httpBasic { basic -> basic.disable() }
			.addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter::class.java)
			.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)
			.authorizeHttpRequests { requests ->
				requests
					.requestMatchers(
						"/user/profile/**",
						"/trips/itineraries/**",
						"/companies/**",
					)
					.authenticated()
					.requestMatchers(
						org.springframework.http.HttpMethod.POST,
						"/stays",
						"/stay-units",
					)
					.authenticated()
					.requestMatchers(
						org.springframework.http.HttpMethod.PUT,
						"/stays/**",
						"/stay-units/**",
					)
					.authenticated()
					.requestMatchers(
						org.springframework.http.HttpMethod.DELETE,
						"/stays/**",
						"/stay-units/**",
					)
					.authenticated()
					.anyRequest()
					.permitAll()
			}.build()
}
