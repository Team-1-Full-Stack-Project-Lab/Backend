package edu.fullstackproject.team1.controllers

import edu.fullstackproject.team1.dtos.StayResponse
import edu.fullstackproject.team1.services.StayService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/stays")
class StayController(
	private val stayService: StayService
) {
	@GetMapping
	fun getAllStays(
		@RequestParam(defaultValue = "0") page: Int,
		@RequestParam(defaultValue = "20") size: Int
	): ResponseEntity<Page<StayResponse>> {
		val pageable = PageRequest.of(page, size)
		val stays = stayService.getAllStays(pageable)
		return ResponseEntity.ok(stays)
	}

	@GetMapping("/{id}")
	fun getStayById(@PathVariable id: Long): ResponseEntity<StayResponse> {
		val stay = stayService.getStayById(id)
		return ResponseEntity.ok(stay)
	}

	@GetMapping("/city/{cityId}")
	fun getStaysByCity(
		@PathVariable cityId: Long,
		@RequestParam(defaultValue = "0") page: Int,
		@RequestParam(defaultValue = "20") size: Int
	): ResponseEntity<Page<StayResponse>> {
		val pageable = PageRequest.of(page, size)
		val stays = stayService.getStaysByCity(cityId, pageable)
		return ResponseEntity.ok(stays)
	}

	@GetMapping("/nearby")
	fun getStaysNearby(
		@RequestParam latitude: Double,
		@RequestParam longitude: Double,
		@RequestParam(defaultValue = "10.0") radiusKm: Double,
		@RequestParam(defaultValue = "0") page: Int,
		@RequestParam(defaultValue = "20") size: Int
	): ResponseEntity<Page<StayResponse>> {
		val pageable = PageRequest.of(page, size)
		val stays = stayService.searchStaysNearby(latitude, longitude, radiusKm, pageable)
		return ResponseEntity.ok(stays)
	}
}
