package edu.fullstackproject.team1.dtos.responses

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CompanyResponse(
	val id: Long,
	val userId: Long,
	val name: String,
	val email: String,
	val phone: String?,
	val description: String?,
	val createdAt: Instant,
	val updatedAt: Instant,
	val user: UserResponse? = null,
	val stays: List<StayResponse>? = null,
)
