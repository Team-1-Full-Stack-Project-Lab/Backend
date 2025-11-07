package edu.fullstackproject.team1.dtos

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CountryResponse(
	val id: Long?,
	val name: String,
	val iso2Code: String,
	val iso3Code: String?,
	val phoneCode: String?,
	val currencyCode: String?,
	val currencySymbol: String?,
	val region: RegionResponse?,
	val states: List<StateResponse>?,
	val cities: List<CityResponse>?,
)
