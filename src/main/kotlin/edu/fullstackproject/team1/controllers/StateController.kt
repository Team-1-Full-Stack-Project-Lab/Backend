package edu.fullstackproject.team1.controllers

import edu.fullstackproject.team1.dtos.responses.StateResponse
import edu.fullstackproject.team1.mappers.StateMapper
import edu.fullstackproject.team1.services.StateService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/states")
class StateController(
	private val stateService: StateService,
	private val stateMapper: StateMapper,
) {
	@GetMapping
	fun getAllStates(
		@RequestParam(required = false) name: String?,
	): ResponseEntity<List<StateResponse>> {
		val states = if (name != null) stateService.getStatesByName(name) else stateService.getAllStates()

		return ResponseEntity.ok(stateMapper.toResponseList(states, true))
	}

	@GetMapping("/{id}")
	fun getStateById(
		@PathVariable id: Long,
	): ResponseEntity<StateResponse> {
		val state = stateService.getStateById(id)

		return ResponseEntity.ok(stateMapper.toResponse(state, true))
	}
}
