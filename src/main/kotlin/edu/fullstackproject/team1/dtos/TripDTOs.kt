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
	val name: String? = null,
)

data class TripResponse(
	val id: Long,
	val name: String,
	val cityId: Long,
	val cityName: String,
	val countryName: String,
	val startDate: LocalDate,
	val finishDate: LocalDate,
)

data class UpdateTripRequest(
	val name: String? = null,
	val cityId: Long? = null,
	@field:FutureOrPresent(message = "Start date must be today or in the future")
	val startDate: LocalDate? = null,
	@field:Future(message = "End date must be in the future")
	val endDate: LocalDate? = null,
)

data class TripsListResponse(
	val trips: List<TripResponse>,
)

data class DeleteItineraryResponse(
	val success: Boolean,
	val message: String,
)

data class AddStayUnitRequest(
	@field:NotNull(message = "Stay Unit is required")
	val stayUnitId: Long,
	@field:NotNull(message = "Start date is required")
	@field:FutureOrPresent(message = "Start date must be today or in the future")
	val startDate: LocalDate,
	@field:NotNull(message = "End date is required")
	@field:Future(message = "End date must be in the future")
	val endDate: LocalDate,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class TripStayUnitResponse(
	val trip: TripResponse?,
	val stayUnit: StayUnitResponse?,
	val startDate: LocalDate,
	val endDate: LocalDate,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class TripStayUnitsListResponse(
	val tripStayUnits: List<TripStayUnitResponse>,
)

data class RemoveStayUnitResponse(
	val success: Boolean,
	val message: String,
)
