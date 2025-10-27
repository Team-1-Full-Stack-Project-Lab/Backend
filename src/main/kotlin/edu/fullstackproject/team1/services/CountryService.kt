package edu.fullstackproject.team1.services

import edu.fullstackproject.team1.dtos.CityResponse
import edu.fullstackproject.team1.dtos.CountryResponse
import edu.fullstackproject.team1.dtos.RegionResponse
import edu.fullstackproject.team1.dtos.StateResponse
import edu.fullstackproject.team1.repositories.CountryRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class CountryService(
	private val countryRepository: CountryRepository
) {
	fun getAllCountries(): List<CountryResponse> {
		val countries = countryRepository.findAll()

		return countries.map {
			CountryResponse(
				id = it.id,
				name = it.name,
				iso2Code = it.iso2Code,
				iso3Code = it.iso3Code,
				phoneCode = it.phoneCode,
				currencyCode = it.currencyCode,
				currencySymbol = it.currencySymbol,
				region = null,
				states = null,
				cities = null
			)
		}
	}

	fun getCountryById(id: Long): CountryResponse {
		val country = countryRepository.findByIdWithRegionCitiesAndStates(id)
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Country not found")

		return CountryResponse(
			id = country.id,
			name = country.name,
			iso2Code = country.iso2Code,
			iso3Code = country.iso3Code,
			phoneCode = country.phoneCode,
			currencyCode = country.currencyCode,
			currencySymbol = country.currencySymbol,
			region = country.region?.let {
				RegionResponse(
					id = it.id,
					name = it.name,
					code = it.code,
					countries = null
				)
			},
			states = country.states.map {
				StateResponse(
					id = it.id,
					name = it.name,
					code = it.code,
					country = null,
					latitude = it.latitude,
					longitude = it.longitude,
				)
			},
			cities = country.cities.map {
				CityResponse(
					id = it.id,
					name = it.name,
					nameAscii = it.nameAscii,
					country = null,
					state = null,
					latitude = it.latitude,
					longitude = it.longitude,
					timezone = it.timezone,
					googlePlaceId = it.googlePlaceId,
					population = it.population,
					isCapital = it.isCapital,
					isFeatured = it.isFeatured
				)
			}
		)
	}

	fun getCountryByIso2Code(iso2Code: String): CountryResponse {
		val country = countryRepository.findByIso2Code(iso2Code)
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Country not found")

		return CountryResponse(
			id = country.id,
			name = country.name,
			iso2Code = country.iso2Code,
			iso3Code = country.iso3Code,
			phoneCode = country.phoneCode,
			currencyCode = country.currencyCode,
			currencySymbol = country.currencySymbol,
			region = null,
			states = null,
			cities = null
		)
	}

	fun getCountryByIso3Code(iso3Code: String): CountryResponse {
		val country = countryRepository.findByIso3Code(iso3Code)
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Country not found")

		return CountryResponse(
			id = country.id,
			name = country.name,
			iso2Code = country.iso2Code,
			iso3Code = country.iso3Code,
			phoneCode = country.phoneCode,
			currencyCode = country.currencyCode,
			currencySymbol = country.currencySymbol,
			region = null,
			states = null,
			cities = null
		)
	}

	fun getCountriesByName(name: String): List<CountryResponse> {
		val countries = countryRepository.findByNameContainingIgnoreCase(name)

		return countries.map {
			CountryResponse(
				id = it.id,
				name = it.name,
				iso2Code = it.iso2Code,
				iso3Code = it.iso3Code,
				phoneCode = it.phoneCode,
				currencyCode = it.currencyCode,
				currencySymbol = it.currencySymbol,
				region = null,
				states = null,
				cities = null
			)
		}
	}
}
