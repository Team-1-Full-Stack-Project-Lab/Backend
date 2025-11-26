package edu.fullstackproject.team1.controllers

import edu.fullstackproject.team1.dtos.responses.CountryResponse
import edu.fullstackproject.team1.mappers.CountryMapper
import edu.fullstackproject.team1.services.CountryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/countries")
class CountryController(
	private val countryService: CountryService,
	private val countryMapper: CountryMapper,
) {
	@GetMapping
	fun getCountries(
		@RequestParam(required = false) name: String?,
	): ResponseEntity<List<CountryResponse>> {
		val countries = if (name != null) countryService.getCountriesByName(name) else countryService.getAllCountries()

		return ResponseEntity.ok(countryMapper.toResponseList(countries))
	}

	@GetMapping("/{id}")
	fun getCountryById(
		@PathVariable id: Long,
	): ResponseEntity<CountryResponse> {
		val country = countryService.getCountryById(id)

		return ResponseEntity.ok(countryMapper.toResponse(country, true))
	}
}
