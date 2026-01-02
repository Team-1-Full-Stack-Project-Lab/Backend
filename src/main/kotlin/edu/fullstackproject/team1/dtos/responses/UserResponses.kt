package edu.fullstackproject.team1.dtos.responses

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserResponse(
	val email: String,
	val firstName: String,
	val lastName: String,
	val company: CompanyResponse? = null,
)
