package edu.fullstackproject.team1.controllers

import edu.fullstackproject.team1.dtos.responses.ServiceResponse
import edu.fullstackproject.team1.mappers.ServiceMapper
import edu.fullstackproject.team1.services.ServiceService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/services")
class ServiceController(
	private val serviceService: ServiceService,
	private val serviceMapper: ServiceMapper,
) {
	@GetMapping
	fun getAllServices(
		@RequestParam(required = false) name: String?,
	): ResponseEntity<List<ServiceResponse>> {
		val services =
			if (name != null) {
				serviceService.getServicesByName(name)
			} else {
				serviceService.getAllServices()
			}
		return ResponseEntity.ok(serviceMapper.toResponseList(services))
	}

	@GetMapping("/{id}")
	fun getServiceById(
		@PathVariable id: Long,
	): ResponseEntity<ServiceResponse> {
		val service = serviceService.getServiceById(id)
		return ResponseEntity.ok(serviceMapper.toResponse(service))
	}
}
