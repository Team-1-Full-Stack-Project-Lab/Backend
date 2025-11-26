package edu.fullstackproject.team1.mappers

import edu.fullstackproject.team1.dtos.responses.RegionResponse
import edu.fullstackproject.team1.models.Region
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

@Component
class RegionMapper(
	@Lazy private val countryMapper: CountryMapper,
) {
	fun toResponse(region: Region, includeRelations: Boolean = false): RegionResponse {
		val countriesResp = if (includeRelations) countryMapper.toResponseList(region.countries) else null

		return RegionResponse(
			id = region.id,
			name = region.name,
			code = region.code,
			countries = countriesResp,
		)
	}

	fun toResponseList(regions: List<Region>, includeRelations: Boolean = false): List<RegionResponse> =
		regions.map { toResponse(it, includeRelations) }
}
