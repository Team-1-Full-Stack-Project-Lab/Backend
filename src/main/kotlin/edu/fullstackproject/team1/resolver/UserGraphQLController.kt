package edu.fullstackproject.team1.resolver

import edu.fullstackproject.team1.dtos.UserResponse
import edu.fullstackproject.team1.dtos.UserUpdateRequestGraphQL
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
) {
	@QueryMapping
	@PreAuthorize("isAuthenticated()")
	fun getUser(
		@AuthenticationPrincipal user: UserDetails?,
	): UserResponse = userService.getUserByEmail(user.requireAuthenticated().username)

	@MutationMapping
	@PreAuthorize(value = "isAuthenticated()")
	fun updateUser(
		@AuthenticationPrincipal user: UserDetails?,
		@Argument request: UserUpdateRequestGraphQL,
	): UserResponse =
		userService.updateUser(
			email = user.requireAuthenticated().username,
			firstName = request.firstName,
			lastName = request.lastName,
		)

	@MutationMapping
	@PreAuthorize("isAuthenticated()")
	fun deleteUser(
		@AuthenticationPrincipal user: UserDetails?,
	): DeleteUserResponse {
		userService.deleteUser(user.requireAuthenticated().username)
		return DeleteUserResponse(
			success = true,
			message = "User deleted successfully",
		)
	}

	data class DeleteUserResponse(
		val success: Boolean,
		val message: String,
	)
}

// Extension function for cleaner code
private fun UserDetails?.requireAuthenticated(): UserDetails {
	return this ?: throw ResponseStatusException(
		HttpStatus.UNAUTHORIZED,
		"Authentication required"
	)
}
