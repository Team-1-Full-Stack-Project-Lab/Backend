package edu.fullstackproject.team1.controllers

import edu.fullstackproject.team1.dtos.responses.CityResponse
import edu.fullstackproject.team1.mappers.CityMapper
import edu.fullstackproject.team1.services.CityService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/cities")
@Tag(name = "Geographic - Cities", description = "City information and search endpoints")
class CityController(
	private val cityService: CityService,
	private val cityMapper: CityMapper,
) {
	@GetMapping
	@Operation(
		summary = "Get or search cities",
		description =
			"Retrieve cities with optional filters for name, country, state, and featured status",
	)
	fun getAllCities(
		@Parameter(description = "Filter by city name")
		@RequestParam(required = false)
		name: String?,
		@Parameter(description = "Filter by country ID")
		@RequestParam(required = false)
		country: Long?,
		@Parameter(description = "Filter by state ID")
		@RequestParam(required = false)
		state: Long?,
		@Parameter(description = "Filter featured cities only")
		@RequestParam(required = false)
		featured: Boolean?,
		@Parameter(description = "Page number (0-indexed)")
		@RequestParam(defaultValue = "0")
		page: Int,
		@Parameter(description = "Number of items per page")
		@RequestParam(defaultValue = "20")
		size: Int,
	): ResponseEntity<Page<CityResponse>> {
		val paginable = PageRequest.of(page, size)
		val cities =
			if (name != null || country != null || state != null || featured != null) {
				cityService.searchCities(country, state, name, featured, paginable)
			} else {
				cityService.getAllCities(paginable)
			}

		return ResponseEntity.ok(cityMapper.toResponsePage(cities, true))
	}

	@GetMapping("/{id}")
	@Operation(
		summary = "Get city by ID",
		description = "Retrieve detailed information about a specific city",
	)
	fun getCityById(
		@Parameter(description = "City ID") @PathVariable id: Long,
	): ResponseEntity<CityResponse> {
		val city = cityService.getCityById(id)

		return ResponseEntity.ok(cityMapper.toResponse(city, true))
	}
}
