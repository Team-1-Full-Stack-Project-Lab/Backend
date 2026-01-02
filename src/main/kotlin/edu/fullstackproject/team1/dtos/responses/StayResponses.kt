package edu.fullstackproject.team1.dtos.responses

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class StayResponse(
	val id: Long?,
	val name: String,
	val address: String,
	val latitude: Double,
	val longitude: Double,
	val city: CityResponse?,
	val stayType: StayTypeResponse?,
	val company: CompanyResponse?,
	val services: List<ServiceResponse>?,
	val units: List<StayUnitResponse>?,
	val description: String?,
	val images: List<StayImageResponse>?,
)
