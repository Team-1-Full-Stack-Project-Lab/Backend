package edu.fullstackproject.team1.controllers

import edu.fullstackproject.team1.dtos.requests.StayCreateRequest
import edu.fullstackproject.team1.dtos.requests.StayUpdateRequest
import edu.fullstackproject.team1.dtos.responses.StayResponse
import edu.fullstackproject.team1.mappers.StayMapper
import edu.fullstackproject.team1.services.StayService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/stays")
@Tag(name = "Stays", description = "Accommodation and stay search endpoints")
class StayController(
	private val stayService: StayService,
	private val stayMapper: StayMapper,
) {
	@GetMapping
	@Operation(
		summary = "Get all stays",
		description = "Retrieve a paginated list of all available stays, optionally filtered by services and price range",
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
		@Parameter(description = "Company ID")
		@RequestParam(required = false)
		companyId: Long?,
		@Parameter(description = "City ID")
		@RequestParam(required = false)
		cityId: Long?,
		@Parameter(description = "List of service IDs to filter by (stays must have ALL specified services)")
		@RequestParam(required = false)
		serviceIds: List<Long>?,
		@Parameter(description = "Minimum price per night")
		@RequestParam(required = false)
		minPrice: Double?,
		@Parameter(description = "Maximum price per night")
		@RequestParam(required = false)
		maxPrice: Double?,
		@Parameter(description = "Page number (0-indexed)")
		@RequestParam(defaultValue = "0")
		page: Int,
		@Parameter(description = "Number of items per page")
		@RequestParam(defaultValue = "20")
		size: Int,
	): ResponseEntity<Page<StayResponse>> {
		val pageable = PageRequest.of(page, size)
		val stays = stayService.getAllStays(companyId, cityId, serviceIds, minPrice, maxPrice, pageable)
		return ResponseEntity.ok(stayMapper.toResponsePage(stays, true, minPrice, maxPrice))
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
		return ResponseEntity.ok(stayMapper.toResponse(stay, true))
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
		return ResponseEntity.ok(stayMapper.toResponsePage(stays, true))
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
	fun searchStaysNearby(
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
		return ResponseEntity.ok(stayMapper.toResponsePage(stays, true))
	}

	@PostMapping
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(
		summary = "Create stay",
		description = "Create a new stay (accommodation). The authenticated user must own the company.",
	)
	@ApiResponses(
		value =
			[
				ApiResponse(
					responseCode = "201",
					description = "Stay created successfully",
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
					responseCode = "400",
					description = "Invalid input data",
					content = [Content()],
				),
				ApiResponse(
					responseCode = "401",
					description = "Unauthorized",
					content = [Content()],
				),
				ApiResponse(
					responseCode = "403",
					description = "Forbidden - not the company owner",
					content = [Content()],
				),
				ApiResponse(
					responseCode = "404",
					description = "City, StayType, or Company not found",
					content = [Content()],
				),
			],
	)
	fun createStay(
		@AuthenticationPrincipal user: UserDetails,
		@RequestBody @Valid request: StayCreateRequest,
	): ResponseEntity<StayResponse> {
		val stay = stayService.createStay(user.username, request.toCommand())
		return ResponseEntity.status(HttpStatus.CREATED).body(stayMapper.toResponse(stay, true))
	}

	@PutMapping("/{id}")
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(
		summary = "Update stay",
		description = "Update an existing stay. The authenticated user must own the company that owns the stay.",
	)
	@ApiResponses(
		value =
			[
				ApiResponse(
					responseCode = "200",
					description = "Stay updated successfully",
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
					responseCode = "400",
					description = "Invalid input data",
					content = [Content()],
				),
				ApiResponse(
					responseCode = "401",
					description = "Unauthorized",
					content = [Content()],
				),
				ApiResponse(
					responseCode = "403",
					description = "Forbidden - not the owner",
					content = [Content()],
				),
				ApiResponse(
					responseCode = "404",
					description = "Stay, City, or StayType not found",
					content = [Content()],
				),
			],
	)
	fun updateStay(
		@AuthenticationPrincipal user: UserDetails,
		@Parameter(description = "Stay ID") @PathVariable id: Long,
		@RequestBody @Valid request: StayUpdateRequest,
	): ResponseEntity<StayResponse> {
		val stay = stayService.updateStay(user.username, id, request.toCommand())
		return ResponseEntity.ok(stayMapper.toResponse(stay, true))
	}

	@DeleteMapping("/{id}")
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(
		summary = "Delete stay",
		description = "Delete a stay. The authenticated user must own the company that owns the stay.",
	)
	@ApiResponses(
		value =
			[
				ApiResponse(
					responseCode = "204",
					description = "Stay deleted successfully",
				),
				ApiResponse(
					responseCode = "401",
					description = "Unauthorized",
					content = [Content()],
				),
				ApiResponse(
					responseCode = "403",
					description = "Forbidden - not the owner",
					content = [Content()],
				),
				ApiResponse(
					responseCode = "404",
					description = "Stay not found",
					content = [Content()],
				),
			],
	)
	fun deleteStay(
		@AuthenticationPrincipal user: UserDetails,
		@Parameter(description = "Stay ID") @PathVariable id: Long,
	): ResponseEntity<Void> {
		stayService.deleteStay(user.username, id)
		return ResponseEntity.noContent().build()
	}
}
