package edu.fullstackproject.team1.dtos.responses

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class StayImageResponse(
	val id: Long?,
	val link: String,
	val stay: StayResponse?,
)
