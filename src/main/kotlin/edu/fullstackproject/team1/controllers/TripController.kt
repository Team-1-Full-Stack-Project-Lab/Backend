package edu.fullstackproject.team1.controllers

import edu.fullstackproject.team1.dtos.CreateTripRequest
import edu.fullstackproject.team1.dtos.TripResponse
import edu.fullstackproject.team1.dtos.TripsListResponse
import edu.fullstackproject.team1.services.TripService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/trips")
class TripController(
	val tripService: TripService
) {
	@PostMapping("/itineraries")
	fun createItinerary(
		@AuthenticationPrincipal user: UserDetails,
		@RequestBody @Valid request: CreateTripRequest
	): ResponseEntity<TripResponse> {
		val response = tripService.createTrip(user.username, request)
		return ResponseEntity.status(HttpStatus.CREATED).body(response)
	}

	@GetMapping("/itineraries")
	fun getUserItineraries(
		@AuthenticationPrincipal user: UserDetails
	): ResponseEntity<TripsListResponse> {
		val response = tripService.getUserTrips(user.username)
		return ResponseEntity.ok(response)
	}

	//DELETE
	@DeleteMapping("/itineraries/{id}")
	fun deleteItinerary(
		@AuthenticationPrincipal user: UserDetails,
		@PathVariable id: Long
	): ResponseEntity<Map<String,String>>{
		tripService.deleteTrip(user.username, id)
		return ResponseEntity.ok(mapOf("message" to "Itineraries deleted successfully"))
	}
}
