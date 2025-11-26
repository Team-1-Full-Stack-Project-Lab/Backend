package edu.fullstackproject.team1.mappers

import edu.fullstackproject.team1.dtos.responses.ServiceResponse
import edu.fullstackproject.team1.models.Service
import org.springframework.stereotype.Component

@Component
class ServiceMapper {
	fun toResponse(service: Service): ServiceResponse {
		return ServiceResponse(
			id = service.id,
			name = service.name,
			icon = service.icon,
		)
	}

	fun toResponseList(services: List<Service>): List<ServiceResponse> =
		services.map { toResponse(it) }
}
