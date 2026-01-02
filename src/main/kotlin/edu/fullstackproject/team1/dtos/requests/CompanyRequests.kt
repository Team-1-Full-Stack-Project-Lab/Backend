package edu.fullstackproject.team1.dtos.requests

import edu.fullstackproject.team1.dtos.commands.CompanyCreateCommand
import edu.fullstackproject.team1.dtos.commands.CompanyUpdateCommand
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CompanyCreateRequest(
	@field:NotBlank(message = "Name is required")
	@field:Size(max = 255, message = "Name must not exceed 255 characters")
	val name: String?,

	@field:NotBlank(message = "Email is required")
	@field:Email(message = "Email must be valid")
	@field:Size(max = 255, message = "Email must not exceed 255 characters")
	val email: String?,

	@field:Size(max = 50, message = "Phone must not exceed 50 characters")
	val phone: String? = null,

	val description: String? = null,
) {
	fun toCommand() = CompanyCreateCommand(
		name = name!!,
		email = email!!,
		phone = phone,
		description = description,
	)
}

data class CompanyUpdateRequest(
	@field:Size(max = 255, message = "Name must not exceed 255 characters")
	val name: String? = null,

	@field:Email(message = "Email must be valid")
	@field:Size(max = 255, message = "Email must not exceed 255 characters")
	val email: String? = null,

	@field:Size(max = 50, message = "Phone must not exceed 50 characters")
	val phone: String? = null,

	val description: String? = null,
) {
	fun toCommand() = CompanyUpdateCommand(
		name = name,
		email = email,
		phone = phone,
		description = description,
	)
}
