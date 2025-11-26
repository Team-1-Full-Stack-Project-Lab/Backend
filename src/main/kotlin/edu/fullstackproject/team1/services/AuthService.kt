package edu.fullstackproject.team1.services

import edu.fullstackproject.team1.dtos.commands.LoginCommand
import edu.fullstackproject.team1.dtos.commands.RegisterCommand
import edu.fullstackproject.team1.mappers.AuthMapper
import edu.fullstackproject.team1.repositories.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class AuthService(
	private val userRepository: UserRepository,
	private val passwordEncoder: PasswordEncoder,
	private val jwtService: JwtService,
	private val authenticationManager: AuthenticationManager,
	private val authMapper: AuthMapper,
) {
	fun login(command: LoginCommand): String {
		authenticationManager.authenticate(
			UsernamePasswordAuthenticationToken(command.email, command.password),
		)
		val user =
			userRepository.findByEmail(command.email)
				?: throw BadCredentialsException("Invalid credentials")

		return jwtService.generateToken(user)
	}

	fun register(command: RegisterCommand): String {
		if (userRepository.findByEmail(command.email) != null) {
			throw ResponseStatusException(HttpStatus.CONFLICT, "A user with that email already exists")
		}

		val encodedPassword = passwordEncoder.encode(command.password)
		val user = authMapper.toEntity(command, encodedPassword)
		val savedUser = userRepository.save(user)

		return jwtService.generateToken(savedUser)
	}
}
