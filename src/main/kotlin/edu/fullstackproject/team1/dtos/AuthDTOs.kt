package edu.fullstackproject.team1.dtos

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LoginRequest(
	@field:NotBlank
	@field:Email
	val email: String,

	@field:NotBlank
	@field:Size(min = 6)
	val password: String
)

data class RegisterRequest(
	@field:NotBlank
	@field:Email
	val email: String,

	@field:NotBlank
	val firstName: String,

	@field:NotBlank
	val lastName: String,

	@field:NotBlank
	@field:Size(min = 6)
	val password: String
)

data class AuthResponse(
	@field:NotBlank
	val token: String
)
data class UserProfileDto(
	val firstName: String,
	val lastName: String,
	val email: String
)
data class UserUpdateRequestDto(
	val firstName: String,
	val lastName: String
)

