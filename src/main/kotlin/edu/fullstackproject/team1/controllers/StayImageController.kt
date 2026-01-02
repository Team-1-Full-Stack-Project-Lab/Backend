package edu.fullstackproject.team1.controllers

import edu.fullstackproject.team1.dtos.requests.CreateStayImageRequest
import edu.fullstackproject.team1.dtos.responses.StayImageResponse
import edu.fullstackproject.team1.mappers.StayImageMapper
import edu.fullstackproject.team1.services.StayImageService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/stay-images")
class StayImageController(
	private val stayImageService: StayImageService,
	private val stayImageMapper: StayImageMapper,
) {
	@GetMapping
	fun getAllStayImages(): ResponseEntity<List<StayImageResponse>> {
		val stayImages = stayImageService.getAllStayImages()
		return ResponseEntity.ok(stayImageMapper.toResponseList(stayImages, true))
	}

	@PostMapping
	fun createStayImage(@Valid @RequestBody request: CreateStayImageRequest): ResponseEntity<StayImageResponse> {
		val stayImage = stayImageService.createStayImage(request.toCommand())
		return ResponseEntity.status(HttpStatus.CREATED).body(stayImageMapper.toResponse(stayImage, true))
	}

	@DeleteMapping("/{id}")
	fun deleteStayImage(@PathVariable id: Long): ResponseEntity<Void> {
		stayImageService.deleteStayImage(id)
		return ResponseEntity.noContent().build()
	}
}
