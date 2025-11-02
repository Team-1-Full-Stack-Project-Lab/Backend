package edu.fullstackproject.team1.services

import edu.fullstackproject.team1.dtos.ServiceResponse
import edu.fullstackproject.team1.repositories.ServiceRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class ServiceService(
	private val serviceRepository: ServiceRepository
) {
	fun getAllServices(): List<ServiceResponse> {
		val services = serviceRepository.findAll()
		return services.map {
			ServiceResponse(
				id = it.id,
				name = it.name,
				icon = it.icon
			)
		}
	}
	fun getServiceById(id: Long): ServiceResponse {
		val service = serviceRepository.findById(id)
			.orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Service not found") }
		return ServiceResponse(
			id = service.id,
			name = service.name,
			icon = service.icon
		)
	}
	fun getServicesByName(name: String): List<ServiceResponse> {
		val services = serviceRepository.findByName(name)
		return services.map {
			ServiceResponse(
				id = it.id,
				name = it.name,
				icon = it.icon
			)
		}
	}
}
