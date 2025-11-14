package edu.fullstackproject.team1.services

import edu.fullstackproject.team1.dtos.StayUnitResponse
import edu.fullstackproject.team1.dtos.TripResponse
import edu.fullstackproject.team1.dtos.TripStayUnitResponse
import edu.fullstackproject.team1.dtos.TripStayUnitsListResponse
import edu.fullstackproject.team1.models.TripStayUnit
import edu.fullstackproject.team1.models.TripStayUnitId
import edu.fullstackproject.team1.repositories.StayUnitRepository
import edu.fullstackproject.team1.repositories.TripRepository
import edu.fullstackproject.team1.repositories.TripStayUnitRepository
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate

@Service
class TripStayUnitService(
	private val tripStayUnitRepository: TripStayUnitRepository,
	private val tripRepository: TripRepository,
	private val stayUnitRepository: StayUnitRepository
) {
	@Transactional
	fun addStayUnitToTrip(
		tripId: Long,
		stayUnitId: Long,
		startDate: LocalDate,
		endDate: LocalDate
	): TripStayUnitResponse {
		val trip = tripRepository.findById(tripId)
			.orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found") }
		val stayUnit = stayUnitRepository.findById(stayUnitId)
			.orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "StayUnit not found") }

		if (tripStayUnitRepository.isStayUnitReserved(stayUnitId, startDate, endDate)) {
			throw ResponseStatusException(HttpStatus.BAD_REQUEST, "StayUnit is not available for these dates")
		}

		val tripStayUnit = TripStayUnit(
			trip = trip,
			stayUnit = stayUnit,
			startDate = startDate,
			endDate = endDate
		)

		tripStayUnitRepository.save(tripStayUnit)

		return TripStayUnitResponse(
			trip = TripResponse(
				id = trip.id!!,
				name = trip.name,
				cityId = trip.city.id!!,
				cityName = trip.city.name,
				countryName = trip.city.country.name,
				startDate = trip.startDate,
				finishDate = trip.finishDate
			),
			stayUnit = StayUnitResponse(
				id = stayUnit.id!!,
				stayNumber = stayUnit.stayNumber,
				numberOfBeds = stayUnit.numberOfBeds,
				capacity = stayUnit.capacity,
				pricePerNight = stayUnit.pricePerNight,
				roomType = stayUnit.roomType,
				stay = null
			),
			startDate = startDate,
			endDate = endDate
		)
	}

	fun getStayUnitsForTrip(tripId: Long): TripStayUnitsListResponse {
		val tripStayUnits = tripStayUnitRepository.findByTripId(tripId)
		return TripStayUnitsListResponse(
			tripStayUnits = tripStayUnits.map { tsu ->
				TripStayUnitResponse(
					trip = TripResponse(
						id = tsu.trip.id!!,
						name = tsu.trip.name,
						cityId = tsu.trip.city.id!!,
						cityName = tsu.trip.city.name,
						countryName = tsu.trip.city.country.name,
						startDate = tsu.trip.startDate,
						finishDate = tsu.trip.finishDate
					),
					stayUnit = StayUnitResponse(
						id = tsu.stayUnit.id!!,
						stayNumber = tsu.stayUnit.stayNumber,
						numberOfBeds = tsu.stayUnit.numberOfBeds,
						capacity = tsu.stayUnit.capacity,
						pricePerNight = tsu.stayUnit.pricePerNight,
						roomType = tsu.stayUnit.roomType,
						stay = null
					),
					startDate = tsu.startDate,
					endDate = tsu.endDate
				)
			}
		)
	}

	@Transactional
	fun removeStayUnitFromTrip(tripId: Long, stayUnitId: Long) {
		val id = TripStayUnitId(tripId, stayUnitId)
		tripStayUnitRepository.deleteById(id)
	}
}
