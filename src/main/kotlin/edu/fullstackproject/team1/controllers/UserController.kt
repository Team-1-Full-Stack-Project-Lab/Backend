package edu.fullstackproject.team1.controllers

import edu.fullstackproject.team1.dtos.UserResponse
import edu.fullstackproject.team1.dtos.UserUpdateRequest
import edu.fullstackproject.team1.services.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
@Tag(name = "User Profile", description = "User profile management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
class UserController(
	private val userService: UserService,
) {
	@GetMapping("/profile")
	@Operation(
		summary = "Get user profile",
		description = "Retrieve the authenticated user's profile information",
	)
	@ApiResponses(
		value =
			[
				ApiResponse(
					responseCode = "200",
					description = "Profile retrieved successfully",
					content =
						[
							Content(
								schema =
									Schema(
										implementation =
											UserResponse::class,
									),
							),
						],
				),
				ApiResponse(
					responseCode = "401",
					description = "Unauthorized",
					content = [Content()],
				),
			],
	)
	fun getUser(
		@AuthenticationPrincipal user: UserDetails,
	): ResponseEntity<UserResponse> {
		val response = userService.getUserByEmail(user.username)

		return ResponseEntity.ok(response)
	}

	@PutMapping("/profile")
	@Operation(
		summary = "Update user profile",
		description = "Update the authenticated user's profile information",
	)
	@ApiResponses(
		value =
			[
				ApiResponse(
					responseCode = "200",
					description = "Profile updated successfully",
					content =
						[
							Content(
								schema =
									Schema(
										implementation =
											UserResponse::class,
									),
							),
						],
				),
				ApiResponse(
					responseCode = "400",
					description = "Invalid input data",
					content = [Content()],
				),
				ApiResponse(
					responseCode = "401",
					description = "Unauthorized",
					content = [Content()],
				),
			],
	)
	fun updateUser(
		@AuthenticationPrincipal user: UserDetails,
		@RequestBody request: UserUpdateRequest,
	): ResponseEntity<UserResponse> {
		val response = userService.updateUser(user.username, request.firstName, request.lastName)

		return ResponseEntity.ok(response)
	}

	@DeleteMapping("/profile")
	@Operation(
		summary = "Delete user account",
		description = "Permanently delete the authenticated user's account",
	)
	@ApiResponses(
		value =
			[
				ApiResponse(
					responseCode = "204",
					description = "Account deleted successfully",
				),
				ApiResponse(
					responseCode = "401",
					description = "Unauthorized",
					content = [Content()],
				),
			],
	)
	fun deleteUser(
		@AuthenticationPrincipal user: UserDetails,
	): ResponseEntity<Void> {
		userService.deleteUser(user.username)

		return ResponseEntity.noContent().build()
	}
}
