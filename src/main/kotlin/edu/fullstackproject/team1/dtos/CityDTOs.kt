package edu.fullstackproject.team1.dtos

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CityResponse(
	val id: Long?,
	val name: String,
	val nameAscii: String?,
	val country: CountryResponse?,
	val state: StateResponse?,
	val latitude: Double,
	val longitude: Double,
	val timezone: String?,
	val googlePlaceId: String?,
	val population: Int?,
	val isCapital: Boolean,
	val isFeatured: Boolean
)
