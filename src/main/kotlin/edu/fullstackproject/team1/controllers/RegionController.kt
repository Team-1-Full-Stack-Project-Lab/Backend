package edu.fullstackproject.team1.controllers

import edu.fullstackproject.team1.dtos.RegionResponse
import edu.fullstackproject.team1.services.RegionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/regions")
class RegionController(
	private val regionService: RegionService
) {
	@GetMapping
	fun getRegions(@RequestParam(required = false) name: String?): ResponseEntity<List<RegionResponse>> {
		val regions = if (name != null) regionService.getRegionsByName(name) else regionService.getAllRegions()

		return ResponseEntity.ok(regions)
	}

	@GetMapping("/{id}")
	fun getRegionById(@PathVariable id: Long): ResponseEntity<RegionResponse> {
		val region = regionService.getRegionById(id)

		return ResponseEntity.ok(region)
	}
}
