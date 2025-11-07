package edu.fullstackproject.team1.controllers

import edu.fullstackproject.team1.dtos.StateResponse
import edu.fullstackproject.team1.services.StateService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/states")
class StateController(
	private val stateService: StateService,
) {
	@GetMapping
	fun getAllStates(
		@RequestParam(required = false) name: String?,
	): ResponseEntity<List<StateResponse>> {
		val states = if (name != null) stateService.getStatesByName(name) else stateService.getAllStates()

		return ResponseEntity.ok(states)
	}

	@GetMapping("/{id}")
	fun getStateById(
		@PathVariable id: Long,
	): ResponseEntity<StateResponse> {
		val state = stateService.getStateById(id)

		return ResponseEntity.ok(state)
	}
}
