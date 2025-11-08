package edu.fullstackproject.team1.resolver

import edu.fullstackproject.team1.dtos.CountryResponse
import edu.fullstackproject.team1.services.CountryService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class CountryGraphQLController(
	private val countryService: CountryService,
) {
	@QueryMapping
	fun getCountryById(
		@Argument id: Long,
	): CountryResponse? = countryService.getCountryById(id)

	@QueryMapping
	fun getCountries(
		@Argument name: String?,
	): List<CountryResponse> =
		if (name != null) {
			countryService.getCountriesByName(name)
		} else {
			countryService.getAllCountries()
		}
}
