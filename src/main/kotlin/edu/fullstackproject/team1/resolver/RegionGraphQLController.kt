package edu.fullstackproject.team1.resolver

import edu.fullstackproject.team1.dtos.RegionResponse
import edu.fullstackproject.team1.services.RegionService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class RegionGraphQLController(
	private val regionService: RegionService,
) {
	@QueryMapping
	fun getRegionById(
		@Argument id: Long,
	): RegionResponse? = regionService.getRegionById(id)

	@QueryMapping
	fun getRegions(
		@Argument name: String?,
	): List<RegionResponse> =
		if (name != null) {
			regionService.getRegionsByName(name)
		} else {
			regionService.getAllRegions()
		}
}
