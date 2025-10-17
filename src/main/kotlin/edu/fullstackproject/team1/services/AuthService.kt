package edu.fullstackproject.team1.services

import edu.fullstackproject.team1.dtos.AuthResponse
import edu.fullstackproject.team1.dtos.LoginRequest
import edu.fullstackproject.team1.dtos.RegisterRequest
import edu.fullstackproject.team1.models.User
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
	private val authenticationManager: AuthenticationManager
) {
	fun login(request: LoginRequest): AuthResponse {
		authenticationManager.authenticate(
			UsernamePasswordAuthenticationToken(request.email, request.password)
		)
		val user = userRepository.findByEmail(request.email)
			?: throw BadCredentialsException("Invalid credentials")

		val token = jwtService.generateToken(user)
		return AuthResponse(token)
	}

	fun register(request: RegisterRequest): AuthResponse {
		if (userRepository.findByEmail(request.email) != null)
			throw ResponseStatusException(HttpStatus.CONFLICT, "A user with that email already exists")

		val user = User(
			email = request.email,
			firstName = request.firstName,
			lastName = request.lastName,
			password = passwordEncoder.encode(request.password)
		)
		val savedUser = userRepository.save(user)
		val token = jwtService.generateToken(savedUser)

		return AuthResponse(token)
	}
}
