package edu.fullstackproject.team1.controllers

import edu.fullstackproject.team1.dtos.*
import edu.fullstackproject.team1.services.TripService
import edu.fullstackproject.team1.services.TripStayUnitService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/trips")
@Tag(name = "Trips & Itineraries", description = "Trip and itinerary management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
class TripController(
	val tripService: TripService,
	val tripStayUnitService: TripStayUnitService,
) {
	@PostMapping("/itineraries")
	@Operation(
		summary = "Create itinerary",
		description = "Create a new travel itinerary for the authenticated user",
	)
	@ApiResponses(
		value =
			[
				ApiResponse(
					responseCode = "201",
					description = "Itinerary created successfully",
					content =
						[
							Content(
								schema =
									Schema(
										implementation =
											TripResponse::class,
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
			],
	)
	fun createItinerary(
		@AuthenticationPrincipal user: UserDetails,
		@RequestBody @Valid request: CreateTripRequest,
	): ResponseEntity<TripResponse> {
		val response = tripService.createTrip(user.username, request)
		return ResponseEntity.status(HttpStatus.CREATED).body(response)
	}

	@GetMapping("/itineraries")
	@Operation(
		summary = "Get user itineraries",
		description = "Retrieve all itineraries for the authenticated user",
	)
	@ApiResponses(
		value =
			[
				ApiResponse(
					responseCode = "200",
					description = "Itineraries retrieved successfully",
					content =
						[
							Content(
								schema =
									Schema(
										implementation =
											TripsListResponse::class,
									),
							),
						],
				),
				ApiResponse(
					responseCode = "401",
					description = "Unauthorized",
					content = [Content()],
				),
			],
	)
	fun getUserItineraries(
		@AuthenticationPrincipal user: UserDetails,
	): ResponseEntity<TripsListResponse> {
		val response = tripService.getUserTrips(user.username)
		return ResponseEntity.ok(response)
	}

	@DeleteMapping("/itineraries/{id}")
	@Operation(
		summary = "Delete itinerary",
		description = "Delete a specific itinerary owned by the authenticated user",
	)
	@ApiResponses(
		value =
			[
				ApiResponse(
					responseCode = "200",
					description = "Itinerary deleted successfully",
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
					description = "Itinerary not found",
					content = [Content()],
				),
			],
	)
	fun deleteItinerary(
		@AuthenticationPrincipal user: UserDetails,
		@Parameter(description = "Itinerary ID") @PathVariable id: Long,
	): ResponseEntity<Map<String, String>> {
		tripService.deleteTrip(user.username, id)
		return ResponseEntity.ok(mapOf("message" to "Itineraries deleted successfully"))
	}

	@PutMapping("/itineraries/{id}")
	@Operation(
		summary = "Update itinerary",
		description = "Update an existing itinerary owned by the authenticated user",
	)
	@ApiResponses(
		value =
			[
				ApiResponse(
					responseCode = "200",
					description = "Itinerary updated successfully",
					content =
						[
							Content(
								schema =
									Schema(
										implementation =
											TripResponse::class,
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
					description = "Itinerary not found",
					content = [Content()],
				),
			],
	)
	fun updateItinerary(
		@AuthenticationPrincipal user: UserDetails,
		@Parameter(description = "Itinerary ID") @PathVariable id: Long,
		@RequestBody @Valid request: UpdateTripRequest,
	): ResponseEntity<TripResponse> {
		val response = tripService.updateTrip(user.username, id, request)
		return ResponseEntity.ok(response)
	}

	@GetMapping("/itineraries/{id}/stayunits")
	@Operation(
		summary = "Get itinerary stay units",
		description = "Retrieve all stay unit reservations for a specific itinerary"
	)
	@ApiResponses(
		value = [
			ApiResponse(
				responseCode = "200",
				description = "Stay units retrieved successfully",
				content = [Content(schema = Schema(implementation = TripStayUnitsListResponse::class))]
			),
			ApiResponse(responseCode = "401", description = "Unauthorized", content = [Content()]),
			ApiResponse(responseCode = "404", description = "Itinerary not found", content = [Content()])
		]
	)
	fun getItineraryStayUnits(
		@AuthenticationPrincipal user: UserDetails,
		@Parameter(description = "Itinerary ID") @PathVariable id: Long
	): ResponseEntity<TripStayUnitsListResponse> {
		val stayUnits = tripStayUnitService.getStayUnitsForTrip(id)
		return ResponseEntity.ok(stayUnits)
	}

	@PostMapping("/itineraries/{id}/stayunits")
	@Operation(
		summary = "Add stay unit to itinerary",
		description = "Add a new stay unit reservation to an itinerary"
	)
	@ApiResponses(
		value = [
			ApiResponse(
				responseCode = "201",
				description = "Stay unit added successfully",
				content = [Content(schema = Schema(implementation = TripStayUnitResponse::class))]
			),
			ApiResponse(responseCode = "400", description = "Invalid input or unavailable", content = [Content()]),
			ApiResponse(responseCode = "401", description = "Unauthorized", content = [Content()]),
			ApiResponse(responseCode = "404", description = "Trip or stay unit not found", content = [Content()])
		]
	)
	fun addStayUnitToItinerary(
		@AuthenticationPrincipal user: UserDetails,
		@Parameter(description = "Itinerary ID") @PathVariable id: Long,
		@RequestBody @Valid request: AddStayUnitRequest
	): ResponseEntity<TripStayUnitResponse> {
		val tripStayUnit = tripStayUnitService.addStayUnitToTrip(
			tripId = id,
			stayUnitId = request.stayUnitId,
			startDate = request.startDate,
			endDate = request.endDate
		)
		return ResponseEntity.status(HttpStatus.CREATED).body(tripStayUnit)
	}

	@DeleteMapping("/itineraries/{tripId}/stayunits/{stayUnitId}")
	@Operation(
		summary = "Remove stay unit from itinerary",
		description = "Remove a stay unit reservation from an itinerary"
	)
	@ApiResponses(
		value = [
			ApiResponse(responseCode = "200", description = "Stay unit removed successfully"),
			ApiResponse(responseCode = "401", description = "Unauthorized", content = [Content()]),
			ApiResponse(responseCode = "404", description = "Reservation not found", content = [Content()])
		]
	)
	fun removeStayUnitFromItinerary(
		@AuthenticationPrincipal user: UserDetails,
		@Parameter(description = "Trip ID") @PathVariable tripId: Long,
		@Parameter(description = "Stay Unit ID") @PathVariable stayUnitId: Long
	): ResponseEntity<Map<String, String>> {
		tripStayUnitService.removeStayUnitFromTrip(tripId, stayUnitId)
		return ResponseEntity.ok(mapOf("message" to "Stay unit removed successfully"))
	}
}
