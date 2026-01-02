package edu.fullstackproject.team1.services

import edu.fullstackproject.team1.models.State
import edu.fullstackproject.team1.repositories.StateRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class StateService(
	private val stateRepository: StateRepository,
) {
	fun getAllStates(): List<State> {
		return stateRepository.findAll()
	}

	fun getStateById(id: Long): State {
		return stateRepository.findByIdWithCountry(id)
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "State not found")
	}

	fun getStatesByName(name: String): List<State> {
		return stateRepository.findByNameContainingIgnoreCase(name)
	}
}
