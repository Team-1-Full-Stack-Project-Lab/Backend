package edu.fullstackproject.team1.mappers

import edu.fullstackproject.team1.dtos.responses.StateResponse
import edu.fullstackproject.team1.models.State
import org.springframework.stereotype.Component

@Component
class StateMapper(
	private val countryMapper: CountryMapper,
) {
	fun toResponse(state: State, includeRelations: Boolean = false): StateResponse {
		val countryResp =
			if (includeRelations) countryMapper.toResponse(state.country, includeRelations = false) else null

		return StateResponse(
			id = state.id,
			name = state.name,
			code = state.code,
			country = countryResp,
			latitude = state.latitude,
			longitude = state.longitude,
		)
	}

	fun toResponseList(states: List<State>, includeRelations: Boolean = false): List<StateResponse> =
		states.map { toResponse(it, includeRelations) }
}
