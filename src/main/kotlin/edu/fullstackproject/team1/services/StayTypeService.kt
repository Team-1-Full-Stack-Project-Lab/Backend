package edu.fullstackproject.team1.services

import edu.fullstackproject.team1.dtos.StayTypeResponse
import edu.fullstackproject.team1.repositories.StayTypeRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class StayTypeService(
	private val stayTypeRepository: StayTypeRepository,
) {
	fun getAllStayTypes(): List<StayTypeResponse> {
		val stayTypes = stayTypeRepository.findAll()
		return stayTypes.map {
			StayTypeResponse(
				id = it.id,
				name = it.name,
			)
		}
	}

	fun getStayTypeById(id: Long): StayTypeResponse {
		val stayType =
			stayTypeRepository
				.findById(id)
				.orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Stay type not found") }
		return StayTypeResponse(
			id = stayType.id,
			name = stayType.name,
		)
	}

	fun getStayTypesByName(name: String): List<StayTypeResponse> {
		val stayTypes = stayTypeRepository.findByName(name)
		return stayTypes.map {
			StayTypeResponse(
				id = it.id,
				name = it.name,
			)
		}
	}
}
