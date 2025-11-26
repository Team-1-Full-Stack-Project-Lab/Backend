package edu.fullstackproject.team1.services

import edu.fullstackproject.team1.models.Service
import edu.fullstackproject.team1.repositories.ServiceRepository
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import org.springframework.stereotype.Service as SpringService

@SpringService
class ServiceService(
	private val serviceRepository: ServiceRepository,
) {
	fun getAllServices(): List<Service> {
		return serviceRepository.findAll()
	}

	fun getServiceById(id: Long): Service {
		return serviceRepository
			.findById(id)
			.orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Service not found") }
	}

	fun getServicesByName(name: String): List<Service> {
		return serviceRepository.findByName(name)
	}
}
