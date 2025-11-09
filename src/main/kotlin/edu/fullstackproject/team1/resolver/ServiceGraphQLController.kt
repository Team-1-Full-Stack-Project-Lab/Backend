package edu.fullstackproject.team1.resolver

import edu.fullstackproject.team1.dtos.ServiceResponse
import edu.fullstackproject.team1.services.ServiceService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class ServiceGraphQLController(
	private val serviceService: ServiceService
) {
	@QueryMapping
	fun getServiceById(@Argument id:Long): ServiceResponse?=
		serviceService.getServiceById(id)

	@QueryMapping
	fun getAllServices(@Argument name:String?): List<ServiceResponse> =
		if(name!=null){
			serviceService.getServicesByName(name)
		}else {
			serviceService.getAllServices()
		}
}
