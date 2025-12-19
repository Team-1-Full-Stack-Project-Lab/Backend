package edu.fullstackproject.team1.dtos.requests

import edu.fullstackproject.team1.dtos.commands.StayUnitCreateCommand
import edu.fullstackproject.team1.dtos.commands.StayUnitUpdateCommand
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class StayUnitCreateRequest(
	@field:NotNull(message = "Stay ID is required")
	val stayId: Long?,

	@field:NotBlank(message = "Stay number is required")
	@field:Size(max = 50, message = "Stay number must not exceed 50 characters")
	val stayNumber: String?,

	@field:NotNull(message = "Number of beds is required")
	@field:Min(value = 1, message = "Number of beds must be at least 1")
	val numberOfBeds: Int?,

	@field:NotNull(message = "Capacity is required")
	@field:Min(value = 1, message = "Capacity must be at least 1")
	val capacity: Int?,

	@field:NotNull(message = "Price per night is required")
	@field:Min(value = 0, message = "Price per night must be non-negative")
	val pricePerNight: Double?,

	@field:NotBlank(message = "Room type is required")
	@field:Size(max = 50, message = "Room type must not exceed 50 characters")
	val roomType: String?,
) {
	fun toCommand() = StayUnitCreateCommand(
		stayId = stayId!!,
		stayNumber = stayNumber!!,
		numberOfBeds = numberOfBeds!!,
		capacity = capacity!!,
		pricePerNight = pricePerNight!!,
		roomType = roomType!!,
	)
}

data class StayUnitUpdateRequest(
	@field:Size(max = 50, message = "Stay number must not exceed 50 characters")
	val stayNumber: String? = null,

	@field:Min(value = 1, message = "Number of beds must be at least 1")
	val numberOfBeds: Int? = null,

	@field:Min(value = 1, message = "Capacity must be at least 1")
	val capacity: Int? = null,

	@field:Min(value = 0, message = "Price per night must be non-negative")
	val pricePerNight: Double? = null,

	@field:Size(max = 50, message = "Room type must not exceed 50 characters")
	val roomType: String? = null,
) {
	fun toCommand() = StayUnitUpdateCommand(
		stayNumber = stayNumber,
		numberOfBeds = numberOfBeds,
		capacity = capacity,
		pricePerNight = pricePerNight,
		roomType = roomType,
	)
}

