package edu.fullstackproject.team1.controllers

import edu.fullstackproject.team1.dtos.responses.StayUnitResponse
import edu.fullstackproject.team1.mappers.StayUnitMapper
import edu.fullstackproject.team1.services.StayUnitService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@RestController
@RequestMapping("/stay-units")
class StayUnitController(
	private val stayUnitService: StayUnitService,
	private val stayUnitMapper: StayUnitMapper,
) {
	@GetMapping("/stay/{stayId}")
	fun getStayUnitsByStayId(
		@PathVariable stayId: Long,
	): ResponseEntity<List<StayUnitResponse>> {
		val units = stayUnitService.getStayUnitsByStayId(stayId)
		return ResponseEntity.ok(stayUnitMapper.toResponseList(units, true))
	}

	@GetMapping("/{id}")
	fun getStayUnitById(
		@PathVariable id: Long,
	): ResponseEntity<StayUnitResponse> {
		val unit = stayUnitService.getStayUnitById(id)
		return ResponseEntity.ok(stayUnitMapper.toResponse(unit, true))
	}

	@GetMapping("/stay/{stayId}/available")
	fun searchAvailableUnits(
		@PathVariable stayId: Long,
		@RequestParam minCapacity: Int,
		@RequestParam maxPrice: BigDecimal,
	): ResponseEntity<List<StayUnitResponse>> {
		val units = stayUnitService.searchAvailableUnits(stayId, minCapacity, maxPrice)
		return ResponseEntity.ok(stayUnitMapper.toResponseList(units, true))
	}
}
