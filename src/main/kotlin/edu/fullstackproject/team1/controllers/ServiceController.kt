package edu.fullstackproject.team1.controllers

import edu.fullstackproject.team1.dtos.ServiceResponse
import edu.fullstackproject.team1.services.ServiceService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/services")
class ServiceController(
	private val serviceService: ServiceService
) {
	@GetMapping
	fun getAllServices(@RequestParam(required = false) name: String?): ResponseEntity<List<ServiceResponse>> {
		val services = if (name != null) {
			serviceService.getServicesByName(name)
		} else {
			serviceService.getAllServices()
		}
		return ResponseEntity.ok(services)
	}

	@GetMapping("/{id}")
	fun getServiceById(@PathVariable id: Long): ResponseEntity<ServiceResponse> {
		val service = serviceService.getServiceById(id)
		return ResponseEntity.ok(service)
	}
}
