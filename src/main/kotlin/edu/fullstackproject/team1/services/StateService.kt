package edu.fullstackproject.team1.services

import edu.fullstackproject.team1.dtos.CountryResponse
import edu.fullstackproject.team1.dtos.StateResponse
import edu.fullstackproject.team1.repositories.StateRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class StateService(
	private val stateRepository: StateRepository
) {
	fun getAllStates(): List<StateResponse> {
		val states = stateRepository.findAll()

		return states.map { state ->
			StateResponse(
				id = state.id,
				name = state.name,
				code = state.code,
				country = null,
				latitude = state.latitude,
				longitude = state.longitude
			)
		}
	}

	fun getStateById(id: Long): StateResponse {
		val state = stateRepository.findByIdWithCountry(id)
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "State not found")

		return StateResponse(
			id = state.id,
			name = state.name,
			code = state.code,
			country = CountryResponse(
				id = state.country.id,
				name = state.country.name,
				iso2Code = state.country.iso2Code,
				iso3Code = state.country.iso3Code,
				phoneCode = state.country.phoneCode,
				currencyCode = state.country.currencyCode,
				currencySymbol = state.country.currencySymbol,
				region = null,
				states = null,
				cities = null
			),
			latitude = state.latitude,
			longitude = state.longitude
		)
	}

	fun getStatesByName(name: String): List<StateResponse> {
		val states = stateRepository.findByNameContainingIgnoreCase(name)

		return states.map { state ->
			StateResponse(
				id = state.id,
				name = state.name,
				code = state.code,
				country = null,
				latitude = state.latitude,
				longitude = state.longitude
			)
		}
	}
}
