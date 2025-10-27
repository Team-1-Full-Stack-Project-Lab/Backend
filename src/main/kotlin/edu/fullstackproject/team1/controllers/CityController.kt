package edu.fullstackproject.team1.controllers

import edu.fullstackproject.team1.dtos.CityResponse
import edu.fullstackproject.team1.services.CityService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/cities")
class CityController(
	private val cityService: CityService
) {
	@GetMapping
	fun getAllCities(
		@RequestParam(required = false) name: String?,
		@RequestParam(required = false) country: Long?,
		@RequestParam(required = false) state: Long?,
		@RequestParam(required = false) featured: Boolean?,
		@RequestParam(defaultValue = "0") page: Int,
		@RequestParam(defaultValue = "20") size: Int,
	): ResponseEntity<Page<CityResponse>> {
		val paginable = PageRequest.of(page, size)
		val cities = if (name != null || country != null || state != null || featured != null)
			cityService.searchCities(country, state, name, featured, paginable)
		else
			cityService.getAllCities(paginable)

		return ResponseEntity.ok(cities)
	}

	@GetMapping("/{id}")
	fun getCityById(@PathVariable id: Long): ResponseEntity<CityResponse> {
		val city = cityService.getCityById(id)

		return ResponseEntity.ok(city)
	}
}
