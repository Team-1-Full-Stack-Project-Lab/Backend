package edu.fullstackproject.team1.resolver

import edu.fullstackproject.team1.dtos.AuthResponse
import edu.fullstackproject.team1.dtos.LoginRequest
import edu.fullstackproject.team1.dtos.RegisterRequest
import edu.fullstackproject.team1.services.AuthService
import jakarta.validation.Valid
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.stereotype.Controller

@Controller
class AuthGraphQLController(
	private val authService: AuthService,
) {
	@MutationMapping
	fun login(
		@Argument @Valid request: LoginRequest,
	): AuthResponse = authService.login(request)

	@MutationMapping
	fun register(
		@Argument @Valid request: RegisterRequest,
	): AuthResponse = authService.register(request)
}
