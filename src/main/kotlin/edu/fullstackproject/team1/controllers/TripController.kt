package edu.fullstackproject.team1.controllers

import edu.fullstackproject.team1.dtos.requests.AddStayUnitToTripRequest
import edu.fullstackproject.team1.dtos.requests.CreateTripRequest
import edu.fullstackproject.team1.dtos.requests.UpdateTripRequest
import edu.fullstackproject.team1.dtos.responses.TripResponse
import edu.fullstackproject.team1.dtos.responses.TripStayUnitResponse
import edu.fullstackproject.team1.mappers.TripMapper
import edu.fullstackproject.team1.mappers.TripStayUnitMapper
import edu.fullstackproject.team1.services.TripService
import edu.fullstackproject.team1.services.TripStayUnitService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.ArraySchema
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
	private val tripService: TripService,
	private val tripStayUnitService: TripStayUnitService,
	private val tripMapper: TripMapper,
	private val tripStayUnitMapper: TripStayUnitMapper,
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
		val trip = tripService.createTrip(user.username, request.toCommand())
		return ResponseEntity.status(HttpStatus.CREATED).body(tripMapper.toResponse(trip, true))
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
					content = [Content(array = ArraySchema(schema = Schema(implementation = TripResponse::class)))],
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
	): ResponseEntity<List<TripResponse>> {
		val trips = tripService.getUserTrips(user.username)
		return ResponseEntity.ok(tripMapper.toResponseList(trips, true))
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
	): ResponseEntity<Void> {
		tripService.deleteTrip(user.username, id)
		return ResponseEntity.noContent().build()
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
		val trip = tripService.updateTrip(user.username, id, request.toCommand())
		return ResponseEntity.ok(tripMapper.toResponse(trip, true))
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
				content = [Content(array = ArraySchema(schema = Schema(implementation = TripStayUnitResponse::class)))]
			),
			ApiResponse(responseCode = "401", description = "Unauthorized", content = [Content()]),
			ApiResponse(responseCode = "404", description = "Itinerary not found", content = [Content()])
		]
	)
	fun getItineraryStayUnits(
		@AuthenticationPrincipal user: UserDetails,
		@Parameter(description = "Itinerary ID") @PathVariable id: Long
	): ResponseEntity<List<TripStayUnitResponse>> {
		val tripStayUnits = tripStayUnitService.getStayUnitsForTrip(user.username, id)
		return ResponseEntity.ok(tripStayUnitMapper.toResponseList(tripStayUnits, true))
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
		@RequestBody @Valid request: AddStayUnitToTripRequest
	): ResponseEntity<TripStayUnitResponse> {
		val tripStayUnit = tripStayUnitService.addStayUnitToTrip(user.username, request.toCommand(id))
		return ResponseEntity.status(HttpStatus.CREATED).body(tripStayUnitMapper.toResponse(tripStayUnit, true))
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
	): ResponseEntity<Void> {
		tripStayUnitService.removeStayUnitFromTrip(user.username, tripId, stayUnitId)
		return ResponseEntity.noContent().build()
	}
}
