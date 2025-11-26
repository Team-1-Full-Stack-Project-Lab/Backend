package edu.fullstackproject.team1.resolver

import edu.fullstackproject.team1.dtos.responses.StateResponse
import edu.fullstackproject.team1.mappers.StateMapper
import edu.fullstackproject.team1.services.StateService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class StateGraphQLController(
	private val stateService: StateService,
	private val stateMapper: StateMapper,
) {
	@QueryMapping
	fun getStateById(
		@Argument id: Long,
	): StateResponse {
		val state = stateService.getStateById(id)
		return stateMapper.toResponse(state, true)
	}

	@QueryMapping
	fun getStates(
		@Argument name: String?,
	): List<StateResponse> {
		val states = if (name != null) stateService.getStatesByName(name) else stateService.getAllStates()
		return stateMapper.toResponseList(states, true)
	}
}
