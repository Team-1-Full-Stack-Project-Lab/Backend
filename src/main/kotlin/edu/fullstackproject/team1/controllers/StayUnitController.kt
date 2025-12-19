package edu.fullstackproject.team1.controllers

import edu.fullstackproject.team1.dtos.requests.StayUnitCreateRequest
import edu.fullstackproject.team1.dtos.requests.StayUnitUpdateRequest
import edu.fullstackproject.team1.dtos.responses.StayUnitResponse
import edu.fullstackproject.team1.mappers.StayUnitMapper
import edu.fullstackproject.team1.services.StayUnitService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
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

	@PostMapping
	fun createStayUnit(
		@AuthenticationPrincipal user: UserDetails,
		@Valid @RequestBody request: StayUnitCreateRequest,
	): ResponseEntity<StayUnitResponse> {
		val stayUnit = stayUnitService.createStayUnit(user.username, request.toCommand())
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(stayUnitMapper.toResponse(stayUnit, true))
	}

	@PutMapping("/{id}")
	fun updateStayUnit(
		@AuthenticationPrincipal user: UserDetails,
		@PathVariable id: Long,
		@Valid @RequestBody request: StayUnitUpdateRequest,
	): ResponseEntity<StayUnitResponse> {
		val stayUnit = stayUnitService.updateStayUnit(user.username, id, request.toCommand())
		return ResponseEntity.ok(stayUnitMapper.toResponse(stayUnit, true))
	}

	@DeleteMapping("/{id}")
	fun deleteStayUnit(
		@AuthenticationPrincipal user: UserDetails,
		@PathVariable id: Long,
	): ResponseEntity<Void> {
		stayUnitService.deleteStayUnit(user.username, id)
		return ResponseEntity.noContent().build()
	}
}
