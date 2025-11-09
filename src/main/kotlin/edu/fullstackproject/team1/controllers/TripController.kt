package edu.fullstackproject.team1.controllers

import edu.fullstackproject.team1.dtos.CreateTripRequest
import edu.fullstackproject.team1.dtos.TripResponse
import edu.fullstackproject.team1.dtos.TripsListResponse
import edu.fullstackproject.team1.dtos.UpdateTripRequest
import edu.fullstackproject.team1.services.CityService
import edu.fullstackproject.team1.services.TripService
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
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/trips")
@Tag(name = "Trips & Itineraries", description = "Trip and itinerary management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
class TripController(
	val tripService: TripService,
	val cityService: CityService,
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
}
