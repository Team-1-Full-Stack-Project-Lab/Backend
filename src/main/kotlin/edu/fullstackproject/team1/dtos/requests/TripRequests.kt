package edu.fullstackproject.team1.dtos.requests

import edu.fullstackproject.team1.dtos.commands.CreateTripCommand
import edu.fullstackproject.team1.dtos.commands.UpdateTripCommand
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class CreateTripRequest(
	@field:Size(max = 255, message = "Name must not exceed 255 characters")
	val name: String? = null,

	@field:NotNull(message = "Destination is required")
	val cityId: Long?,

	@field:NotNull(message = "Start date is required")
	@field:FutureOrPresent(message = "Start date must be today or in the future")
	val startDate: LocalDate?,

	@field:NotNull(message = "End date is required")
	@field:Future(message = "End date must be in the future")
	val endDate: LocalDate?,
) {
	fun toCommand() = CreateTripCommand(
		name = name,
		cityId = cityId!!,
		startDate = startDate!!,
		endDate = endDate!!,
	)
}

data class UpdateTripRequest(
	@field:Size(max = 255, message = "Name must not exceed 255 characters")
	val name: String? = null,

	val cityId: Long? = null,

	@field:FutureOrPresent(message = "Start date must be today or in the future")
	val startDate: LocalDate? = null,

	@field:Future(message = "End date must be in the future")
	val endDate: LocalDate? = null,
) {
	fun toCommand() = UpdateTripCommand(
		name = name,
		cityId = cityId,
		startDate = startDate,
		endDate = endDate,
	)
}
