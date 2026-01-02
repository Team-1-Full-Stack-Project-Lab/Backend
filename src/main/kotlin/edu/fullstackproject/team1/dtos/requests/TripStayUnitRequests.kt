package edu.fullstackproject.team1.dtos.requests

import edu.fullstackproject.team1.dtos.commands.AddStayUnitToTripCommand
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class AddStayUnitToTripRequest(
	@field:NotNull(message = "Stay Unit is required")
	val stayUnitId: Long?,

	@field:NotNull(message = "Start date is required")
	@field:FutureOrPresent(message = "Start date must be today or in the future")
	val startDate: LocalDate?,

	@field:NotNull(message = "End date is required")
	@field:Future(message = "End date must be in the future")
	val endDate: LocalDate?,
) {
	fun toCommand(tripId: Long) = AddStayUnitToTripCommand(
		tripId = tripId,
		stayUnitId = stayUnitId!!,
		startDate = startDate!!,
		endDate = endDate!!,
	)
}
