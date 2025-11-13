package edu.fullstackproject.team1.resolver

import edu.fullstackproject.team1.dtos.*
import edu.fullstackproject.team1.services.TripService
import edu.fullstackproject.team1.services.TripStayUnitService
import jakarta.validation.Valid
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
	private val tripStayUnitService: TripStayUnitService,
) {
	@MutationMapping
	@PreAuthorize("isAuthenticated()")
	fun createItinerary(
		@AuthenticationPrincipal user: UserDetails,
		@Argument @Valid request: CreateTripRequest,
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
		@Argument @Valid request: UpdateTripRequest,
	): TripResponse = tripService.updateTrip(user.username, id, request)

	@QueryMapping
	@PreAuthorize("isAuthenticated()")
	fun getItineraryStayUnits(
		@AuthenticationPrincipal user: UserDetails,
		@Argument tripId: Long
	): TripStayUnitsListResponse = tripStayUnitService.getStayUnitsForTrip(tripId)

	@MutationMapping
	@PreAuthorize("isAuthenticated()")
	fun addStayUnitToItinerary(
		@AuthenticationPrincipal user: UserDetails,
		@Argument tripId: Long,
		@Argument @Valid request: AddStayUnitRequest
	): TripStayUnitResponse = tripStayUnitService.addStayUnitToTrip(
		tripId = tripId,
		stayUnitId = request.stayUnitId,
		startDate = request.startDate,
		endDate = request.endDate
	)

	@MutationMapping
	@PreAuthorize("isAuthenticated()")
	fun removeStayUnitFromItinerary(
		@AuthenticationPrincipal user: UserDetails,
		@Argument tripId: Long,
		@Argument stayUnitId: Long
	): RemoveStayUnitResponse {
		tripStayUnitService.removeStayUnitFromTrip(tripId, stayUnitId)
		return RemoveStayUnitResponse(
			success = true,
			message = "Stay unit removed successfully"
		)
	}
}
