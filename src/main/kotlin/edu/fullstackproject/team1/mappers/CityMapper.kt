package edu.fullstackproject.team1.mappers

import edu.fullstackproject.team1.dtos.responses.CityResponse
import edu.fullstackproject.team1.models.City
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component

@Component
class CityMapper(
	private val stateMapper: StateMapper,
	private val countryMapper: CountryMapper,
) {
	fun toResponse(city: City, includeRelations: Boolean = false): CityResponse {
		val countryResp =
			if (includeRelations) countryMapper.toResponse(city.country, includeRelations = false) else null
		val state = city.state
		val stateResp =
			if (includeRelations && state != null) stateMapper.toResponse(state, includeRelations = false) else null

		return CityResponse(
			id = city.id,
			name = city.name,
			nameAscii = city.nameAscii,
			country = countryResp,
			state = stateResp,
			latitude = city.latitude,
			longitude = city.longitude,
			timezone = city.timezone,
			googlePlaceId = city.googlePlaceId,
			population = city.population,
			isCapital = city.isCapital,
			isFeatured = city.isFeatured,
		)
	}

	fun toResponseList(cities: List<City>, includeRelations: Boolean = false): List<CityResponse> =
		cities.map { toResponse(it, includeRelations) }

	fun toResponsePage(cities: Page<City>, includeRelations: Boolean = false): Page<CityResponse> =
		cities.map { toResponse(it, includeRelations) }
}
