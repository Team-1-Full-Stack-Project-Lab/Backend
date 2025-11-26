package edu.fullstackproject.team1.mappers

import edu.fullstackproject.team1.dtos.responses.CountryResponse
import edu.fullstackproject.team1.models.Country
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

@Component
class CountryMapper(
	@Lazy private val stateMapper: StateMapper,
	@Lazy private val cityMapper: CityMapper,
	private val regionMapper: RegionMapper,
) {
	fun toResponse(country: Country, includeRelations: Boolean = false): CountryResponse {
		val statesResp =
			if (includeRelations) stateMapper.toResponseList(country.states, includeRelations = false) else null
		val citiesResp =
			if (includeRelations) cityMapper.toResponseList(country.cities, includeRelations = false) else null
		val region = country.region
		val regionResp =
			if (includeRelations && region != null) regionMapper.toResponse(region, includeRelations = false) else null

		return CountryResponse(
			id = country.id,
			name = country.name,
			iso2Code = country.iso2Code,
			iso3Code = country.iso3Code,
			phoneCode = country.phoneCode,
			currencyCode = country.currencyCode,
			currencySymbol = country.currencySymbol,
			region = regionResp,
			states = statesResp,
			cities = citiesResp,
		)
	}

	fun toResponseList(countries: List<Country>, includeRelations: Boolean = false): List<CountryResponse> =
		countries.map { toResponse(it, includeRelations) }
}
