package edu.fullstackproject.team1.resolver

import edu.fullstackproject.team1.dtos.StayTypeResponse
import edu.fullstackproject.team1.services.StayTypeService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class StayTypeGraphQLController(
	private val stayTypeService: StayTypeService
) {
	@QueryMapping
	fun getStayTypeById(@Argument id:Long): StayTypeResponse?=
		stayTypeService.getStayTypeById(id)

	@QueryMapping
	fun getAllStayTypes(@Argument name:String?): List<StayTypeResponse> =
		if(name!=null){
			stayTypeService.getStayTypesByName(name)
		}else{
			stayTypeService.getAllStayTypes()
		}
}
