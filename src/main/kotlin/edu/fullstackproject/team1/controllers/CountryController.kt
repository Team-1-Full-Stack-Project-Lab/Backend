package edu.fullstackproject.team1.controllers

import edu.fullstackproject.team1.dtos.CountryResponse
import edu.fullstackproject.team1.services.CountryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/countries")
class CountryController(
	private val countryService: CountryService
) {
	@GetMapping
	fun getCountries(@RequestParam(required = false) name: String?): ResponseEntity<List<CountryResponse>> {
		val countries = if (name != null) countryService.getCountriesByName(name) else countryService.getAllCountries()

		return ResponseEntity.ok(countries)
	}

	@GetMapping("/{id}")
	fun getCountryById(@PathVariable id: Long): ResponseEntity<CountryResponse> {
		val country = countryService.getCountryById(id)

		return ResponseEntity.ok(country)
	}
}
