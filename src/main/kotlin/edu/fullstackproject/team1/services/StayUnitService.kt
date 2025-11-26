package edu.fullstackproject.team1.services

import edu.fullstackproject.team1.models.StayUnit
import edu.fullstackproject.team1.repositories.StayServiceRepository
import edu.fullstackproject.team1.repositories.StayUnitRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal

@Service
class StayUnitService(
	private val stayUnitRepository: StayUnitRepository,
	private val stayServiceRepository: StayServiceRepository
) {
	fun getStayUnitsByStayId(stayId: Long): List<StayUnit> {
		return stayUnitRepository.findByStayIdWithStay(stayId)
	}

	fun getStayUnitById(id: Long): StayUnit {
		return stayUnitRepository.findById(id)
			.orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Stay unit not found") }
	}

	fun searchAvailableUnits(stayId: Long, minCapacity: Int, maxPrice: BigDecimal): List<StayUnit> {
		return stayUnitRepository.findAvailableUnits(stayId, minCapacity, maxPrice)
	}
}
