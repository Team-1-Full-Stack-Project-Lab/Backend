package edu.fullstackproject.team1.resolver

import edu.fullstackproject.team1.dtos.CityResponse
import edu.fullstackproject.team1.services.CityService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class CityGraphQLController(
	private val cityService: CityService,
) {
	@QueryMapping
	fun getCityById(
		@Argument id: Long,
	): CityResponse? = cityService.getCityById(id)

	@QueryMapping
	fun getAllCities(
		@Argument name: String?,
		@Argument country: Long?,
		@Argument state: Long?,
		@Argument featured: Boolean?,
		@Argument page: Int?,
		@Argument size: Int?,
	): Page<CityResponse> {
		val paginable = PageRequest.of(page ?: 0, size ?: 20)
		return if (name != null || country != null || state != null || featured != null) {
			cityService.searchCities(country, state, name, featured, paginable)
		} else {
			cityService.getAllCities(paginable)
		}
	}
}
