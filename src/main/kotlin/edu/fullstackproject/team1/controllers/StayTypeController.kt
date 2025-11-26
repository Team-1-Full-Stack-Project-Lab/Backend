package edu.fullstackproject.team1.controllers

import edu.fullstackproject.team1.dtos.responses.StayTypeResponse
import edu.fullstackproject.team1.mappers.StayTypeMapper
import edu.fullstackproject.team1.services.StayTypeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/stay-types")
class StayTypeController(
	private val stayTypeService: StayTypeService,
	private val stayTypeMapper: StayTypeMapper,
) {
	@GetMapping
	fun getAllStayTypes(
		@RequestParam(required = false) name: String?,
	): ResponseEntity<List<StayTypeResponse>> {
		val stayTypes =
			if (name != null) {
				stayTypeService.getStayTypesByName(name)
			} else {
				stayTypeService.getAllStayTypes()
			}
		return ResponseEntity.ok(stayTypeMapper.toResponseList(stayTypes))
	}

	@GetMapping("/{id}")
	fun getStayTypeById(
		@PathVariable id: Long,
	): ResponseEntity<StayTypeResponse> {
		val stayType = stayTypeService.getStayTypeById(id)
		return ResponseEntity.ok(stayTypeMapper.toResponse(stayType))
	}
}
