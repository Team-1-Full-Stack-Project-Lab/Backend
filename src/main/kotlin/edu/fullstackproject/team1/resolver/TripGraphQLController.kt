package edu.fullstackproject.team1.resolver

import edu.fullstackproject.team1.dtos.CreateTripRequest
import edu.fullstackproject.team1.dtos.DeleteItineraryResponse
import edu.fullstackproject.team1.dtos.TripResponse
import edu.fullstackproject.team1.dtos.TripsListResponse
import edu.fullstackproject.team1.dtos.UpdateTripRequest
import edu.fullstackproject.team1.services.TripService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller

@Controller
class TripGraphQLController(
	private val tripService: TripService,
) {
	@MutationMapping
	@PreAuthorize("isAuthenticated()")
	fun createItinerary(
		@AuthenticationPrincipal user: UserDetails,
		@Argument request: CreateTripRequest,
	): TripResponse = tripService.createTrip(user.username, request)

	@QueryMapping
	@PreAuthorize("isAuthenticated()")
	fun getUserItineraries(
		@AuthenticationPrincipal user: UserDetails,
	): TripsListResponse = tripService.getUserTrips(user.username)

	@MutationMapping
	@PreAuthorize("isAuthenticated()")
	fun deleteItinerary(
		@AuthenticationPrincipal user: UserDetails,
		@Argument id: Long,
	): DeleteItineraryResponse {
		tripService.deleteTrip(user.username, id)
		return DeleteItineraryResponse(
			success = true,
			message = "Itineraries deleted successfully",
		)
	}

	@MutationMapping
	@PreAuthorize("isAuthenticated()")
	fun updateItinerary(
		@AuthenticationPrincipal user: UserDetails,
		@Argument id: Long,
		@Argument request: UpdateTripRequest,
	): TripResponse = tripService.updateTrip(user.username, id, request)
}
