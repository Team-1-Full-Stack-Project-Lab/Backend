package edu.fullstackproject.team1.dtos

import com.fasterxml.jackson.annotation.JsonInclude
import jakarta.validation.constraints.NotBlank

@JsonInclude(JsonInclude.Include.NON_NULL)
data class StayTypeResponse(
	val id: Long?,
	val name: String,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ServiceResponse(
	val id: Long?,
	val name: String,
	val icon: String?,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class StayImageResponse(
	val id: Long?,
	val link: String,
	val stayId: Long?,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AddStayImageRequest(
	@field:NotBlank
	val link: String
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class StayResponse(
	val id: Long?,
	val name: String,
	val address: String,
	val latitude: Double,
	val longitude: Double,
	val city: CityResponse?,
	val stayType: StayTypeResponse?,
	val services: List<ServiceResponse>?,
	val units: List<StayUnitResponse>?,
	val description: String?,
	val images: List<StayImageResponse>?,
)

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
