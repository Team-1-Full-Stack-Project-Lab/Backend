package edu.fullstackproject.team1.resolver

import edu.fullstackproject.team1.dtos.requests.LoginRequest
import edu.fullstackproject.team1.dtos.requests.RegisterRequest
import edu.fullstackproject.team1.dtos.responses.AuthResponse
import edu.fullstackproject.team1.mappers.AuthMapper
import edu.fullstackproject.team1.services.AuthService
import jakarta.validation.Valid
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.stereotype.Controller

@Controller
class AuthGraphQLController(
	private val authService: AuthService,
	private val authMapper: AuthMapper,
) {
	@MutationMapping
	fun login(
		@Argument @Valid request: LoginRequest,
	): AuthResponse {
		val token = authService.login(request.toCommand())
		return authMapper.toResponse(token)
	}

	@MutationMapping
	fun register(
		@Argument @Valid request: RegisterRequest,
	): AuthResponse {
		val token = authService.register(request.toCommand())
		return authMapper.toResponse(token)
	}
}
