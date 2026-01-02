package edu.fullstackproject.team1.services

import edu.fullstackproject.team1.dtos.commands.StayUnitCreateCommand
import edu.fullstackproject.team1.dtos.commands.StayUnitUpdateCommand
import edu.fullstackproject.team1.mappers.StayUnitMapper
import edu.fullstackproject.team1.models.StayUnit
import edu.fullstackproject.team1.repositories.StayRepository
import edu.fullstackproject.team1.repositories.StayUnitRepository
import edu.fullstackproject.team1.repositories.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal

@Service
class StayUnitService(
	private val stayUnitRepository: StayUnitRepository,
	private val stayRepository: StayRepository,
	private val userRepository: UserRepository,
	private val stayUnitMapper: StayUnitMapper,
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

	@Transactional
	fun createStayUnit(email: String, command: StayUnitCreateCommand): StayUnit {
		val stay = stayRepository.findById(command.stayId)
			.orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Stay not found") }

		if (stay.company?.user?.email != email) {
			throw ResponseStatusException(HttpStatus.FORBIDDEN, "You don't own this stay")
		}

		val stayUnit = stayUnitMapper.toEntity(command, stay)

		return stayUnitRepository.save(stayUnit)
	}

	@Transactional
	fun updateStayUnit(email: String, id: Long, command: StayUnitUpdateCommand): StayUnit {
		val existingStayUnit = stayUnitRepository.findById(id)
			.orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Stay unit not found") }

		if (existingStayUnit.stay.company?.user?.email != email) {
			throw ResponseStatusException(HttpStatus.FORBIDDEN, "You don't own this stay")
		}

		val updatedStayUnit = existingStayUnit.copy(
			stayNumber = command.stayNumber ?: existingStayUnit.stayNumber,
			numberOfBeds = command.numberOfBeds ?: existingStayUnit.numberOfBeds,
			capacity = command.capacity ?: existingStayUnit.capacity,
			pricePerNight = command.pricePerNight ?: existingStayUnit.pricePerNight,
			roomType = command.roomType ?: existingStayUnit.roomType,
		)

		return stayUnitRepository.save(updatedStayUnit)
	}

	@Transactional
	fun deleteStayUnit(email: String, id: Long) {
		val stayUnit = stayUnitRepository.findById(id)
			.orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Stay unit not found") }

		if (stayUnit.stay.company?.user?.email != email) {
			throw ResponseStatusException(HttpStatus.FORBIDDEN, "You don't own this stay")
		}

		stayUnitRepository.delete(stayUnit)
	}
}
