package edu.fullstackproject.team1.dtos.requests

import edu.fullstackproject.team1.dtos.commands.StayCreateCommand
import edu.fullstackproject.team1.dtos.commands.StayUpdateCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class StayCreateRequest(
	@field:NotNull(message = "City ID is required")
	val cityId: Long?,

	@field:NotNull(message = "Stay type ID is required")
	val stayTypeId: Long?,

	@field:NotBlank(message = "Name is required")
	@field:Size(max = 255, message = "Name must not exceed 255 characters")
	val name: String?,

	@field:NotBlank(message = "Address is required")
	@field:Size(max = 500, message = "Address must not exceed 500 characters")
	val address: String?,

	@field:NotNull(message = "Latitude is required")
	val latitude: Double?,

	@field:NotNull(message = "Longitude is required")
	val longitude: Double?,

	val description: String? = null,

	val serviceIds: List<Long>? = null,

	val imageUrls: List<String>? = null,
) {
	fun toCommand() = StayCreateCommand(
		cityId = cityId!!,
		stayTypeId = stayTypeId!!,
		name = name!!,
		address = address!!,
		latitude = latitude!!,
		longitude = longitude!!,
		description = description,
		serviceIds = serviceIds,
		imageUrls = imageUrls,
	)
}

data class StayUpdateRequest(
	val cityId: Long? = null,

	val stayTypeId: Long? = null,

	@field:Size(max = 255, message = "Name must not exceed 255 characters")
	val name: String? = null,

	@field:Size(max = 500, message = "Address must not exceed 500 characters")
	val address: String? = null,

	val latitude: Double? = null,

	val longitude: Double? = null,

	val description: String? = null,

	val serviceIds: List<Long>? = null,

	val imageUrls: List<String>? = null,
) {
	fun toCommand() = StayUpdateCommand(
		cityId = cityId,
		stayTypeId = stayTypeId,
		name = name,
		address = address,
		latitude = latitude,
		longitude = longitude,
		description = description,
		serviceIds = serviceIds,
		imageUrls = imageUrls,
	)
}
