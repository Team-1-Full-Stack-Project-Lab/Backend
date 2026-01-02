package edu.fullstackproject.team1.dtos.responses

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class StayUnitResponse(
	val id: Long?,
	val stayNumber: String,
	val numberOfBeds: Int,
	val capacity: Int,
	val pricePerNight: Double,
	val roomType: String,
	val stay: StayResponse?,
)
