package edu.fullstackproject.team1.dtos.responses

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ServiceResponse(
	val id: Long?,
	val name: String,
	val icon: String?,
)
