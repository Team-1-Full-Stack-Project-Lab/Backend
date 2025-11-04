package edu.fullstackproject.team1.controllers

import edu.fullstackproject.team1.dtos.StayTypeResponse
import edu.fullstackproject.team1.services.StayTypeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/stay-types")
class StayTypeController(
	private val stayTypeService: StayTypeService
) {
	@GetMapping
	fun getAllStayTypes(@RequestParam(required = false) name: String?): ResponseEntity<List<StayTypeResponse>> {
		val stayTypes = if (name != null) {
			stayTypeService.getStayTypesByName(name)
		} else {
			stayTypeService.getAllStayTypes()
		}
		return ResponseEntity.ok(stayTypes)
	}

	@GetMapping("/{id}")
	fun getStayTypeById(@PathVariable id: Long): ResponseEntity<StayTypeResponse> {
		val stayType = stayTypeService.getStayTypeById(id)
		return ResponseEntity.ok(stayType)
	}
}
