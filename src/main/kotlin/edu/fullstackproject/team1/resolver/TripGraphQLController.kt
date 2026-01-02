package edu.fullstackproject.team1.resolver

import edu.fullstackproject.team1.dtos.requests.AddStayUnitToTripRequest
import edu.fullstackproject.team1.dtos.requests.CreateTripRequest
import edu.fullstackproject.team1.dtos.requests.UpdateTripRequest
import edu.fullstackproject.team1.dtos.responses.TripResponse
import edu.fullstackproject.team1.dtos.responses.TripStayUnitResponse
import edu.fullstackproject.team1.mappers.TripMapper
import edu.fullstackproject.team1.mappers.TripStayUnitMapper
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
	private val tripMapper: TripMapper,
	private val tripStayUnitMapper: TripStayUnitMapper,
) {
	@MutationMapping
	@PreAuthorize("isAuthenticated()")
	fun createItinerary(
		@AuthenticationPrincipal user: UserDetails,
		@Argument @Valid request: CreateTripRequest,
	): TripResponse {
		val trip = tripService.createTrip(user.username, request.toCommand())
		return tripMapper.toResponse(trip, true)
	}

	@QueryMapping
	@PreAuthorize("isAuthenticated()")
	fun getUserItineraries(
		@AuthenticationPrincipal user: UserDetails,
	): List<TripResponse> {
		val trips = tripService.getUserTrips(user.username)
		return tripMapper.toResponseList(trips, true)
	}

	@MutationMapping
	@PreAuthorize("isAuthenticated()")
	fun deleteItinerary(
		@AuthenticationPrincipal user: UserDetails,
		@Argument id: Long,
	) {
		tripService.deleteTrip(user.username, id)
	}

	@MutationMapping
	@PreAuthorize("isAuthenticated()")
	fun updateItinerary(
		@AuthenticationPrincipal user: UserDetails,
		@Argument id: Long,
		@Argument @Valid request: UpdateTripRequest,
	): TripResponse {
		val trip = tripService.updateTrip(user.username, id, request.toCommand())
		return tripMapper.toResponse(trip, true)
	}

	@QueryMapping
	@PreAuthorize("isAuthenticated()")
	fun getItineraryStayUnits(
		@AuthenticationPrincipal user: UserDetails,
		@Argument id: Long
	): List<TripStayUnitResponse> {
		val tripStayUnits = tripStayUnitService.getStayUnitsForTrip(user.username, id)
		return tripStayUnitMapper.toResponseList(tripStayUnits, true)
	}

	@MutationMapping
	@PreAuthorize("isAuthenticated()")
	fun addStayUnitToItinerary(
		@AuthenticationPrincipal user: UserDetails,
		@Argument id: Long,
		@Argument @Valid request: AddStayUnitToTripRequest
	): TripStayUnitResponse {
		val tripStayUnit = tripStayUnitService.addStayUnitToTrip(user.username, request.toCommand(id))
		return tripStayUnitMapper.toResponse(tripStayUnit, true)
	}

	@MutationMapping
	@PreAuthorize("isAuthenticated()")
	fun removeStayUnitFromItinerary(
		@AuthenticationPrincipal user: UserDetails,
		@Argument tripId: Long,
		@Argument stayUnitId: Long
	) {
		tripStayUnitService.removeStayUnitFromTrip(user.username, tripId, stayUnitId)
	}
}
