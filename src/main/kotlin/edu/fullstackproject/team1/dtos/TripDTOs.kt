package edu.fullstackproject.team1.dtos

import com.fasterxml.jackson.annotation.JsonInclude
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.NotNull
import java.time.LocalDate


data class CreateTripRequest(
	@field:NotNull(message = "Destination (city ID) is required")
	val cityId: Long,

	@field:NotNull(message = "Start date is required")
	@field:FutureOrPresent(message = "Start date must be today or in the future")
	val startDate: LocalDate,

	@field:NotNull(message = "End date is required")
	@field:Future(message = "End date must be in the future")
	val endDate: LocalDate,

	val name: String? = null
)

data class TripResponse(
	val id: Long,
	val name: String,
	val cityId: Long,
	val cityName: String,
	val countryName: String,
	val startDate: LocalDate,
	val finishDate: LocalDate
)
data class UpdateTripRequest(
	val name: String? = null,
	val cityId: Long? = null,
	@field:FutureOrPresent(message = "Start date must be today or in the future")
	val startDate: LocalDate? = null,
	@field:Future(message = "End date must be in the future")
	val endDate: LocalDate? = null
)

data class TripsListResponse(
	val trips: List<TripResponse>
)
