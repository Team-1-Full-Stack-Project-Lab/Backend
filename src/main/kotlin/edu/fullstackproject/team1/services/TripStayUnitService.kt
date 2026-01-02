package edu.fullstackproject.team1.services

import edu.fullstackproject.team1.dtos.commands.AddStayUnitToTripCommand
import edu.fullstackproject.team1.mappers.TripStayUnitMapper
import edu.fullstackproject.team1.models.TripStayUnit
import edu.fullstackproject.team1.models.TripStayUnitId
import edu.fullstackproject.team1.repositories.StayUnitRepository
import edu.fullstackproject.team1.repositories.TripRepository
import edu.fullstackproject.team1.repositories.TripStayUnitRepository
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class TripStayUnitService(
	private val tripStayUnitRepository: TripStayUnitRepository,
	private val tripRepository: TripRepository,
	private val stayUnitRepository: StayUnitRepository,
	private val tripStayUnitMapper: TripStayUnitMapper,
) {
	@Transactional
	fun addStayUnitToTrip(email: String, command: AddStayUnitToTripCommand): TripStayUnit {
		val trip = tripRepository.findByIdWithCityAndCountry(command.tripId)
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found")
		if (trip.user.email != email) {
			throw ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to add stay unit to this trip")
		}
		val stayUnit = stayUnitRepository.findById(command.stayUnitId)
			.orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "StayUnit not found") }

		if (tripStayUnitRepository.isStayUnitReserved(command.stayUnitId, command.startDate, command.endDate)) {
			throw ResponseStatusException(
				HttpStatus.BAD_REQUEST,
				"StayUnit is not available for these dates"
			)
		}

		val tripStayUnit = tripStayUnitMapper.toEntity(command, trip, stayUnit)
		return tripStayUnitRepository.save(tripStayUnit)
	}

	fun getStayUnitsForTrip(email: String, tripId: Long): List<TripStayUnit> {
		val trip = tripRepository.findByIdWithUser(tripId)
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found")
		if (trip.user.email != email) {
			throw ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to get stay units for this trip")
		}
		return tripStayUnitRepository.findByTripIdWithRelations(tripId)
	}

	@Transactional
	fun removeStayUnitFromTrip(email: String, tripId: Long, stayUnitId: Long) {
		val trip = tripRepository.findByIdWithUser(tripId)
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found")
		if (trip.user.email != email) {
			throw ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to remove stay unit from this trip")
		}
		val id = TripStayUnitId(tripId, stayUnitId)
		tripStayUnitRepository.deleteById(id)
	}
}
