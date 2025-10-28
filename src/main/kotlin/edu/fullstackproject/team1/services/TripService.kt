package edu.fullstackproject.team1.services

import edu.fullstackproject.team1.dtos.CreateTripRequest
import edu.fullstackproject.team1.dtos.TripResponse
import edu.fullstackproject.team1.dtos.TripsListResponse
import edu.fullstackproject.team1.models.Trip
import edu.fullstackproject.team1.repositories.CityRepository
import edu.fullstackproject.team1.repositories.TripRepository
import edu.fullstackproject.team1.repositories.UserRepository
import jakarta.validation.constraints.Email
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate

@Service
class TripService (
	private val tripRepository : TripRepository,
	private val userRepository : UserRepository,
	private val cityRepository : CityRepository
){
	fun createTrip(email: String,request: CreateTripRequest): TripResponse{
		validateTripDates(request.startDate, request.endDate)
		val user = userRepository.findByEmail(email)
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
		val city = cityRepository.findByIdWithCountryAndState(request.cityId)
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "City not found")
		val tripName = request.name?.takeIf { it.isNotBlank() } ?: city.name
		val trip = Trip(
			user = user,
			city = city,
			name = tripName,
			startDate = request.startDate,
			finishDate = request.endDate,
		)
		val savedTrip = tripRepository.save(trip)
		return mapToTripResponse(savedTrip)
	}

	fun getUserTrips(email:String): TripsListResponse{
		val user = userRepository.findByEmail(email)
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
		val trips = tripRepository.findByUser(user)
		return TripsListResponse(
			trips = trips.map { mapToTripResponse(it) }
		)
	}

	//DELETE
	fun deleteTrip(userEmail: String, tripID: Long){
		val trip = tripRepository.findById(tripID)
			.orElseThrow{ ResponseStatusException(
				HttpStatus.NOT_FOUND, "User not found") }
		val ownerEmail = trip.user.email
		if(ownerEmail != userEmail){
			throw ResponseStatusException(
				HttpStatus.FORBIDDEN, "User not found")
		}
		tripRepository.delete(trip)
	}

	private fun validateTripDates(startDate: LocalDate, endDate: LocalDate) {
		if (endDate.isBefore(startDate)) {
			throw ResponseStatusException(
				HttpStatus.BAD_REQUEST,
				"End date must be before start date"
			)
		}
		if (startDate.isBefore(LocalDate.now())){
			throw ResponseStatusException(
				HttpStatus.BAD_REQUEST,
				"Start date cannot be in the past"
			)
		}
	}

	private fun mapToTripResponse(trip: Trip): TripResponse {
		return TripResponse(
			id = trip.id!!,
			name = trip.name,
			cityId = trip.city.id!!,
			cityName = trip.city.name,
			countryName = trip.city.country.name,
			startDate = trip.startDate,
			finishDate = trip.finishDate
		)
	}

}
