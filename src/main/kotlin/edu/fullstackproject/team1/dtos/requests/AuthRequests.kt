package edu.fullstackproject.team1.dtos.requests

import edu.fullstackproject.team1.dtos.commands.LoginCommand
import edu.fullstackproject.team1.dtos.commands.RegisterCommand
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LoginRequest(
	@field:NotBlank
	@field:Email
	val email: String?,

	@field:NotBlank
	@field:Size(min = 6)
	val password: String?,
) {
	fun toCommand() = LoginCommand(
		email = email!!,
		password = password!!,
	)
}

data class RegisterRequest(
	@field:NotBlank
	@field:Email
	val email: String?,

	@field:NotBlank
	val firstName: String?,

	@field:NotBlank
	val lastName: String?,

	@field:NotBlank
	@field:Size(min = 6)
	val password: String?,
) {
	fun toCommand() = RegisterCommand(
		email = email!!,
		firstName = firstName!!,
		lastName = lastName!!,
		password = password!!,
	)
}
