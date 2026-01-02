package edu.fullstackproject.team1.services

import edu.fullstackproject.team1.models.Country
import edu.fullstackproject.team1.repositories.CountryRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class CountryService(
	private val countryRepository: CountryRepository,
) {
	fun getAllCountries(): List<Country> {
		return countryRepository.findAll()
	}

	fun getCountryById(id: Long): Country {
		return countryRepository.findByIdWithRegionCitiesAndStates(id)
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Country not found")
	}

	fun getCountryByIso2Code(iso2Code: String): Country {
		return countryRepository.findByIso2Code(iso2Code)
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Country not found")
	}

	fun getCountryByIso3Code(iso3Code: String): Country {
		return countryRepository.findByIso3Code(iso3Code)
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Country not found")
	}

	fun getCountriesByName(name: String): List<Country> {
		return countryRepository.findByNameContainingIgnoreCase(name)
	}
}
