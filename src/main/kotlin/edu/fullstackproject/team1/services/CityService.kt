package edu.fullstackproject.team1.services

import edu.fullstackproject.team1.models.City
import edu.fullstackproject.team1.repositories.CityRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class CityService(
	private val cityRepository: CityRepository,
) {
	fun getAllCities(pageable: Pageable): Page<City> {
		return cityRepository.findAllWithCountryAndState(pageable)
	}

	fun getCityById(id: Long): City {
		return cityRepository.findByIdWithCountryAndState(id)
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "City not found")
	}

	fun getCitiesByCountry(countryId: Long, pageable: Pageable): Page<City> {
		return cityRepository.findByCountryIdWithState(countryId, pageable)
	}

	fun getFeaturedCities(): List<City> {
		return cityRepository.findFeaturedWithCountryAndState()
	}

	fun getCapitalCities(): List<City> {
		return cityRepository.findCapitalsWithCountryAndState()
	}

	fun searchCities(
		countryId: Long?,
		stateId: Long?,
		search: String?,
		featured: Boolean?,
		pageable: Pageable,
	): Page<City> {
		return cityRepository.searchCitiesWithCountryAndState(countryId, stateId, search, featured, pageable)
	}

	fun findCitiesNearby(latitude: Double, longitude: Double, radiusKm: Double): List<City> {
		return cityRepository.findCitiesNearby(latitude, longitude, radiusKm)
	}
}
