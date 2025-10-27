package edu.fullstackproject.team1.services

import edu.fullstackproject.team1.dtos.CityResponse
import edu.fullstackproject.team1.dtos.CountryResponse
import edu.fullstackproject.team1.dtos.StateResponse
import edu.fullstackproject.team1.repositories.CityRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import kotlin.collections.map

@Service
class CityService(
	private val cityRepository: CityRepository
) {
	fun getAllCities(pageable: Pageable): Page<CityResponse> {
		val cities = cityRepository.findAllWithCountryAndState(pageable)

		return cities.map {
			CityResponse(
				id = it.id,
				name = it.name,
				nameAscii = it.nameAscii,
				country = CountryResponse(
					id = it.country.id,
					name = it.country.name,
					iso2Code = it.country.iso2Code,
					iso3Code = it.country.iso3Code,
					phoneCode = it.country.phoneCode,
					currencyCode = it.country.currencyCode,
					currencySymbol = it.country.currencySymbol,
					region = null,
					states = null,
					cities = null
				),
				state = it.state?.let { state ->
					StateResponse(
						id = state.id,
						name = state.name,
						code = state.code,
						country = null,
						latitude = state.latitude,
						longitude = state.longitude
					)
				},
				latitude = it.latitude,
				longitude = it.longitude,
				timezone = it.timezone,
				googlePlaceId = it.googlePlaceId,
				population = it.population,
				isCapital = it.isCapital,
				isFeatured = it.isFeatured
			)
		}
	}

	fun getCityById(id: Long): CityResponse {
		val city = cityRepository.findByIdWithCountryAndState(id)
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "City not found")

		return CityResponse(
			id = city.id,
			name = city.name,
			nameAscii = city.nameAscii,
			country = CountryResponse(
				id = city.country.id,
				name = city.country.name,
				iso2Code = city.country.iso2Code,
				iso3Code = city.country.iso3Code,
				phoneCode = city.country.phoneCode,
				currencyCode = city.country.currencyCode,
				currencySymbol = city.country.currencySymbol,
				region = null,
				states = null,
				cities = null
			),
			state = city.state?.let { state ->
				StateResponse(
					id = state.id,
					name = state.name,
					code = state.code,
					country = null,
					latitude = state.latitude,
					longitude = state.longitude
				)
			},
			latitude = city.latitude,
			longitude = city.longitude,
			timezone = city.timezone,
			googlePlaceId = city.googlePlaceId,
			population = city.population,
			isCapital = city.isCapital,
			isFeatured = city.isFeatured
		)
	}

	fun getCitiesByCountry(countryId: Long, pageable: Pageable): Page<CityResponse> {
		val cities = cityRepository.findByCountryIdWithState(countryId, pageable)

		return cities.map {
			CityResponse(
				id = it.id,
				name = it.name,
				nameAscii = it.nameAscii,
				country = null,
				state = it.state?.let { state ->
					StateResponse(
						id = state.id,
						name = state.name,
						code = state.code,
						country = null,
						latitude = state.latitude,
						longitude = state.longitude
					)
				},
				latitude = it.latitude,
				longitude = it.longitude,
				timezone = it.timezone,
				googlePlaceId = it.googlePlaceId,
				population = it.population,
				isCapital = it.isCapital,
				isFeatured = it.isFeatured
			)
		}
	}

	fun getFeaturedCities(): List<CityResponse> {
		val cities = cityRepository.findFeaturedWithCountryAndState()

		return cities.map {
			CityResponse(
				id = it.id,
				name = it.name,
				nameAscii = it.nameAscii,
				country = CountryResponse(
					id = it.country.id,
					name = it.country.name,
					iso2Code = it.country.iso2Code,
					iso3Code = it.country.iso3Code,
					phoneCode = it.country.phoneCode,
					currencyCode = it.country.currencyCode,
					currencySymbol = it.country.currencySymbol,
					region = null,
					states = null,
					cities = null
				),
				state = it.state?.let { state ->
					StateResponse(
						id = state.id,
						name = state.name,
						code = state.code,
						country = null,
						latitude = state.latitude,
						longitude = state.longitude
					)
				},
				latitude = it.latitude,
				longitude = it.longitude,
				timezone = it.timezone,
				googlePlaceId = it.googlePlaceId,
				population = it.population,
				isCapital = it.isCapital,
				isFeatured = it.isFeatured
			)
		}
	}

	fun getCapitalCities(): List<CityResponse> {
		val cities = cityRepository.findCapitalsWithCountryAndState()

		return cities.map {
			CityResponse(
				id = it.id,
				name = it.name,
				nameAscii = it.nameAscii,
				country = CountryResponse(
					id = it.country.id,
					name = it.country.name,
					iso2Code = it.country.iso2Code,
					iso3Code = it.country.iso3Code,
					phoneCode = it.country.phoneCode,
					currencyCode = it.country.currencyCode,
					currencySymbol = it.country.currencySymbol,
					region = null,
					states = null,
					cities = null
				),
				state = it.state?.let { state ->
					StateResponse(
						id = state.id,
						name = state.name,
						code = state.code,
						country = null,
						latitude = state.latitude,
						longitude = state.longitude
					)
				},
				latitude = it.latitude,
				longitude = it.longitude,
				timezone = it.timezone,
				googlePlaceId = it.googlePlaceId,
				population = it.population,
				isCapital = it.isCapital,
				isFeatured = it.isFeatured
			)
		}
	}

	fun searchCities(
		countryId: Long?,
		stateId: Long?,
		search: String?,
		featured: Boolean?,
		pageable: Pageable
	): Page<CityResponse> {
		val cities = cityRepository.searchCitiesWithCountryAndState(countryId, stateId, search, featured, pageable)

		return cities.map {
			CityResponse(
				id = it.id,
				name = it.name,
				nameAscii = it.nameAscii,
				country = CountryResponse(
					id = it.country.id,
					name = it.country.name,
					iso2Code = it.country.iso2Code,
					iso3Code = it.country.iso3Code,
					phoneCode = it.country.phoneCode,
					currencyCode = it.country.currencyCode,
					currencySymbol = it.country.currencySymbol,
					region = null,
					states = null,
					cities = null
				),
				state = it.state?.let { state ->
					StateResponse(
						id = state.id,
						name = state.name,
						code = state.code,
						country = null,
						latitude = state.latitude,
						longitude = state.longitude
					)
				},
				latitude = it.latitude,
				longitude = it.longitude,
				timezone = it.timezone,
				googlePlaceId = it.googlePlaceId,
				population = it.population,
				isCapital = it.isCapital,
				isFeatured = it.isFeatured
			)
		}
	}

	fun findCitiesNearby(latitude: Double, longitude: Double, radiusKm: Double): List<CityResponse> {
		val cities = cityRepository.findCitiesNearby(latitude, longitude, radiusKm)

		return cities.map {
			CityResponse(
				id = it.id,
				name = it.name,
				nameAscii = it.nameAscii,
				country = CountryResponse(
					id = it.country.id,
					name = it.country.name,
					iso2Code = it.country.iso2Code,
					iso3Code = it.country.iso3Code,
					phoneCode = it.country.phoneCode,
					currencyCode = it.country.currencyCode,
					currencySymbol = it.country.currencySymbol,
					region = null,
					states = null,
					cities = null
				),
				state = it.state?.let { state ->
					StateResponse(
						id = state.id,
						name = state.name,
						code = state.code,
						country = null,
						latitude = state.latitude,
						longitude = state.longitude
					)
				},
				latitude = it.latitude,
				longitude = it.longitude,
				timezone = it.timezone,
				googlePlaceId = it.googlePlaceId,
				population = it.population,
				isCapital = it.isCapital,
				isFeatured = it.isFeatured
			)
		}
	}
}
