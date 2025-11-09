package edu.fullstackproject.team1.controllers

import edu.fullstackproject.team1.dtos.StayUnitResponse
import edu.fullstackproject.team1.services.StayUnitService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
@RequestMapping("/stay-units")
class StayUnitController(
	private val stayUnitService: StayUnitService,
) {
	@GetMapping("/stay/{stayId}")
	fun getStayUnitsByStayId(
		@PathVariable stayId: Long,
	): ResponseEntity<List<StayUnitResponse>> {
		val units = stayUnitService.getStayUnitsByStayId(stayId)
		return ResponseEntity.ok(units)
	}

	@GetMapping("/{id}")
	fun getStayUnitById(
		@PathVariable id: Long,
	): ResponseEntity<StayUnitResponse> {
		val unit = stayUnitService.getStayUnitById(id)
		return ResponseEntity.ok(unit)
	}

	@GetMapping("/stay/{stayId}/available")
	fun searchAvailableUnits(
		@PathVariable stayId: Long,
		@RequestParam minCapacity: Int,
		@RequestParam maxPrice: BigDecimal,
	): ResponseEntity<List<StayUnitResponse>> {
		val units = stayUnitService.searchAvailableUnits(stayId, minCapacity, maxPrice)
		return ResponseEntity.ok(units)
	}
}
