package edu.fullstackproject.team1.resolver

import edu.fullstackproject.team1.dtos.responses.ServiceResponse
import edu.fullstackproject.team1.mappers.ServiceMapper
import edu.fullstackproject.team1.services.ServiceService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class ServiceGraphQLController(
	private val serviceService: ServiceService,
	private val serviceMapper: ServiceMapper,
) {
	@QueryMapping
	fun getServiceById(@Argument id: Long): ServiceResponse {
		val service = serviceService.getServiceById(id)
		return serviceMapper.toResponse(service)
	}

	@QueryMapping
	fun getAllServices(@Argument name: String?): List<ServiceResponse> {
		val services =
			if (name != null) {
				serviceService.getServicesByName(name)
			} else {
				serviceService.getAllServices()
			}
		return serviceMapper.toResponseList(services)
	}
}
