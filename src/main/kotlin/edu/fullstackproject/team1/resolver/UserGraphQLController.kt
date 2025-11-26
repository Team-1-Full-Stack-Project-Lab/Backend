package edu.fullstackproject.team1.resolver

import edu.fullstackproject.team1.dtos.requests.UserUpdateRequest
import edu.fullstackproject.team1.dtos.responses.UserResponse
import edu.fullstackproject.team1.mappers.UserMapper
import edu.fullstackproject.team1.services.UserService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller
import org.springframework.web.server.ResponseStatusException

@Controller
class UserGraphQLController(
	private val userService: UserService,
	private val userMapper: UserMapper,
) {
	@QueryMapping
	@PreAuthorize("isAuthenticated()")
	fun getUser(
		@AuthenticationPrincipal user: UserDetails?,
	): UserResponse {
		val user = userService.getUserByEmail(user.requireAuthenticated().username)
		return userMapper.toResponse(user)
	}

	@MutationMapping
	@PreAuthorize(value = "isAuthenticated()")
	fun updateUser(
		@AuthenticationPrincipal user: UserDetails?,
		@Argument request: UserUpdateRequest,
	): UserResponse {
		val user = userService.updateUser(user.requireAuthenticated().username, request.toCommand())
		return userMapper.toResponse(user)
	}

	@MutationMapping
	@PreAuthorize("isAuthenticated()")
	fun deleteUser(
		@AuthenticationPrincipal user: UserDetails?,
	) {
		userService.deleteUser(user.requireAuthenticated().username)
	}
}

private fun UserDetails?.requireAuthenticated(): UserDetails {
	return this ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required")
}
