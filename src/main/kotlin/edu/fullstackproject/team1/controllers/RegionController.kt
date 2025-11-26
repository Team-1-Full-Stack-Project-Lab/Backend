package edu.fullstackproject.team1.controllers

import edu.fullstackproject.team1.dtos.responses.RegionResponse
import edu.fullstackproject.team1.mappers.RegionMapper
import edu.fullstackproject.team1.services.RegionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/regions")
class RegionController(
	private val regionService: RegionService,
	private val regionMapper: RegionMapper,
) {
	@GetMapping
	fun getRegions(
		@RequestParam(required = false) name: String?,
	): ResponseEntity<List<RegionResponse>> {
		val regions = if (name != null) regionService.getRegionsByName(name) else regionService.getAllRegions()

		return ResponseEntity.ok(regionMapper.toResponseList(regions))
	}

	@GetMapping("/{id}")
	fun getRegionById(
		@PathVariable id: Long,
	): ResponseEntity<RegionResponse> {
		val region = regionService.getRegionById(id)

		return ResponseEntity.ok(regionMapper.toResponse(region, true))
	}
}
