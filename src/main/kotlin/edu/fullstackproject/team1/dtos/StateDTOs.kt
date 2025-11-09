package edu.fullstackproject.team1.dtos

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class StateResponse(
	val id: Long?,
	val name: String,
	val code: String?,
	val country: CountryResponse?,
	val latitude: Double?,
	val longitude: Double?,
)
