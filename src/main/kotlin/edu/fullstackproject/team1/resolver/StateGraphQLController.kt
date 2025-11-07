package edu.fullstackproject.team1.resolver

import edu.fullstackproject.team1.dtos.StateResponse
import edu.fullstackproject.team1.services.StateService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class StateGraphQLController(
	private val stateService: StateService,
) {
	@QueryMapping
	fun getStateById(
		@Argument id: Long,
	): StateResponse? = stateService.getStateById(id)

	@QueryMapping
	fun getStates(
		@Argument name: String?,
	): List<StateResponse> =
		if (name != null) {
			stateService.getStatesByName(name)
		} else {
			stateService.getAllStates()
		}
}
