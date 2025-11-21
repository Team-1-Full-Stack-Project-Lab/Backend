package edu.fullstackproject.team1.controllers

import edu.fullstackproject.team1.dtos.StayResponse
import edu.fullstackproject.team1.dtos.StayImageResponse
import edu.fullstackproject.team1.dtos.AddStayImageRequest
import edu.fullstackproject.team1.services.StayService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/stays")
@Tag(name = "Stays", description = "Accommodation and stay search endpoints")
class StayController(
	private val stayService: StayService,
) {
	@GetMapping
	@Operation(
		summary = "Get all stays",
		description = "Retrieve a paginated list of all available stays",
	)
	@ApiResponses(
		value =
			[
				ApiResponse(
					responseCode = "200",
					description = "Stays retrieved successfully",
					content =
						[Content(schema = Schema(implementation = Page::class))],
				),
			],
	)
	fun getAllStays(
		@Parameter(description = "Page number (0-indexed)")
		@RequestParam(defaultValue = "0")
		page: Int,
		@Parameter(description = "Number of items per page")
		@RequestParam(defaultValue = "20")
		size: Int,
	): ResponseEntity<Page<StayResponse>> {
		val pageable = PageRequest.of(page, size)
		val stays = stayService.getAllStays(pageable)
		return ResponseEntity.ok(stays)
	}

	@GetMapping("/{id}")
	@Operation(
		summary = "Get stay by ID",
		description = "Retrieve detailed information about a specific stay",
	)
	@ApiResponses(
		value =
			[
				ApiResponse(
					responseCode = "200",
					description = "Stay found",
					content =
						[
							Content(
								schema =
									Schema(
										implementation =
											StayResponse::class,
									),
							),
						],
				),
				ApiResponse(
					responseCode = "404",
					description = "Stay not found",
					content = [Content()],
				),
			],
	)
	fun getStayById(
		@Parameter(description = "Stay ID") @PathVariable id: Long,
	): ResponseEntity<StayResponse> {
		val stay = stayService.getStayById(id)
		return ResponseEntity.ok(stay)
	}

	@GetMapping("/city/{cityId}")
	@Operation(
		summary = "Get stays by city",
		description = "Retrieve all stays in a specific city",
	)
	@ApiResponses(
		value =
			[
				ApiResponse(
					responseCode = "200",
					description = "Stays retrieved successfully",
					content =
						[Content(schema = Schema(implementation = Page::class))],
				),
			],
	)
	fun getStaysByCity(
		@Parameter(description = "City ID") @PathVariable cityId: Long,
		@Parameter(description = "Page number (0-indexed)")
		@RequestParam(defaultValue = "0")
		page: Int,
		@Parameter(description = "Number of items per page")
		@RequestParam(defaultValue = "20")
		size: Int,
	): ResponseEntity<Page<StayResponse>> {
		val pageable = PageRequest.of(page, size)
		val stays = stayService.getStaysByCity(cityId, pageable)
		return ResponseEntity.ok(stays)
	}

	@GetMapping("/nearby")
	@Operation(
		summary = "Find stays nearby",
		description = "Search for stays within a specified radius of a geographic location",
	)
	@ApiResponses(
		value =
			[
				ApiResponse(
					responseCode = "200",
					description = "Stays found",
					content =
						[Content(schema = Schema(implementation = Page::class))],
				),
			],
	)
	fun getStaysNearby(
		@Parameter(description = "Latitude coordinate") @RequestParam latitude: Double,
		@Parameter(description = "Longitude coordinate") @RequestParam longitude: Double,
		@Parameter(description = "Search radius in kilometers")
		@RequestParam(defaultValue = "10.0")
		radiusKm: Double,
		@Parameter(description = "Page number (0-indexed)")
		@RequestParam(defaultValue = "0")
		page: Int,
		@Parameter(description = "Number of items per page")
		@RequestParam(defaultValue = "20")
		size: Int,
	): ResponseEntity<Page<StayResponse>> {
		val pageable = PageRequest.of(page, size)
		val stays = stayService.searchStaysNearby(latitude, longitude, radiusKm, pageable)
		return ResponseEntity.ok(stays)
	}

	@GetMapping("/{id}/images")
	@Operation(summary = "Get images for a stay", description = "Retrieve all images associated with a stay")
	fun getImagesForStay(@Parameter(description = "Stay ID") @PathVariable id: Long): ResponseEntity<List<StayImageResponse>> {
		val images = stayService.getImagesForStay(id)
		return ResponseEntity.ok(images)
	}
	@PostMapping("/{id}/images")
	@Operation(summary = "Add image to a stay", description = "Add a new image link associated to a stay")
	fun addImageToStay(
		@Parameter(description = "Stay ID") @PathVariable id: Long,
		@Valid @RequestBody request: AddStayImageRequest
	): ResponseEntity<StayImageResponse> {
		val created = stayService.addImageToStay(id, request.link)
		return ResponseEntity.status(201).body(created)
	}
	@DeleteMapping("/{id}/images/{imageId}")
	@Operation(summary = "Delete image from a stay", description = "Delete an image associated to a stay")
	fun deleteImageFromStay(
		@Parameter(description = "Stay ID") @PathVariable id: Long,
		@Parameter(description = "Image ID") @PathVariable imageId: Long
	): ResponseEntity<Map<String, String>> {
		stayService.deleteImageFromStay(id, imageId)
		return ResponseEntity.ok(mapOf("message" to "Image deleted successfully"))
	}
}
