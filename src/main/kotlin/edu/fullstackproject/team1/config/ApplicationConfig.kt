package edu.fullstackproject.team1.config

import edu.fullstackproject.team1.repositories.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class ApplicationConfig(
	private val userRepository: UserRepository,
) {
	@Bean
	fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager = config.authenticationManager

	@Bean
	fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

	@Bean
	fun userDetailsService(): UserDetailsService =
		UserDetailsService { username ->
			userRepository.findByEmail(username) ?: throw UsernameNotFoundException("User not found")
		}
}
