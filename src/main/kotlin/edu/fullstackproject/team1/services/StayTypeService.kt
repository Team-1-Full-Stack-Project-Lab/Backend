package edu.fullstackproject.team1.services

import edu.fullstackproject.team1.models.StayType
import edu.fullstackproject.team1.repositories.StayTypeRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class StayTypeService(
	private val stayTypeRepository: StayTypeRepository,
) {
	fun getAllStayTypes(): List<StayType> {
		return stayTypeRepository.findAll()
	}

	fun getStayTypeById(id: Long): StayType {
		return stayTypeRepository
			.findById(id)
			.orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Stay type not found") }
	}

	fun getStayTypesByName(name: String): List<StayType> {
		return stayTypeRepository.findByName(name)
	}
}
