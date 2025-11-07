package edu.fullstackproject.team1.services

import edu.fullstackproject.team1.dtos.StayUnitResponse
import edu.fullstackproject.team1.models.StayUnit
import edu.fullstackproject.team1.repositories.StayUnitRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal

@Service
class StayUnitService(
	private val stayUnitRepository: StayUnitRepository,
) {
	fun getStayUnitsByStayId(stayId: Long): List<StayUnitResponse> {
		val units = stayUnitRepository.findByStayId(stayId)
		return units.map { mapToStayUnitResponse(it) }
	}

	fun getStayUnitById(id: Long): StayUnitResponse {
		val unit =
			stayUnitRepository
				.findById(id)
				.orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Stay unit not found") }
		return mapToStayUnitResponse(unit)
	}

	fun searchAvailableUnits(
		stayId: Long,
		minCapacity: Int,
		maxPrice: BigDecimal,
	): List<StayUnitResponse> {
		val units = stayUnitRepository.findAvailableUnits(stayId, minCapacity, maxPrice)
		return units.map { mapToStayUnitResponse(it) }
	}

	private fun mapToStayUnitResponse(unit: StayUnit): StayUnitResponse =
		StayUnitResponse(
			id = unit.id,
			stayNumber = unit.stayNumber,
			numberOfBeds = unit.numberOfBeds,
			capacity = unit.capacity,
			pricePerNight = unit.pricePerNight,
			roomType = unit.roomType,
			stay = null,
		)
}
