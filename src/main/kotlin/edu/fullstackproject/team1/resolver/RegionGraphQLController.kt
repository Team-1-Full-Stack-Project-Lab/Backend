package edu.fullstackproject.team1.resolver

import edu.fullstackproject.team1.dtos.responses.RegionResponse
import edu.fullstackproject.team1.mappers.RegionMapper
import edu.fullstackproject.team1.services.RegionService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class RegionGraphQLController(
	private val regionService: RegionService,
	private val regionMapper: RegionMapper,
) {
	@QueryMapping
	fun getRegionById(
		@Argument id: Long,
	): RegionResponse {
		val region = regionService.getRegionById(id)
		return regionMapper.toResponse(region, true)
	}

	@QueryMapping
	fun getRegions(
		@Argument name: String?,
	): List<RegionResponse> {
		val regions = if (name != null) regionService.getRegionsByName(name) else regionService.getAllRegions()
		return regionMapper.toResponseList(regions, true)
	}
}
