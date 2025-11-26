package edu.fullstackproject.team1.dtos.responses

import java.time.LocalDate

data class TripResponse(
	val id: Long,
	val name: String,
	val city: CityResponse?,
	val country: CountryResponse?,
	val startDate: LocalDate,
	val endDate: LocalDate,
)
