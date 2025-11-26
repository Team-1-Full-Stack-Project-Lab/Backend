package edu.fullstackproject.team1.resolver

import edu.fullstackproject.team1.dtos.responses.CountryResponse
import edu.fullstackproject.team1.mappers.CountryMapper
import edu.fullstackproject.team1.services.CountryService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class CountryGraphQLController(
	private val countryService: CountryService,
	private val countryMapper: CountryMapper,
) {
	@QueryMapping
	fun getCountryById(
		@Argument id: Long,
	): CountryResponse {
		val country = countryService.getCountryById(id)
		return countryMapper.toResponse(country, true)
	}

	@QueryMapping
	fun getCountries(
		@Argument name: String?,
	): List<CountryResponse> {
		val countries = if (name != null) countryService.getCountriesByName(name) else countryService.getAllCountries()
		return countryMapper.toResponseList(countries, true)
	}
}
