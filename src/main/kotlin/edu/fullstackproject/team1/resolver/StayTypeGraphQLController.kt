package edu.fullstackproject.team1.resolver

import edu.fullstackproject.team1.dtos.responses.StayTypeResponse
import edu.fullstackproject.team1.mappers.StayTypeMapper
import edu.fullstackproject.team1.services.StayTypeService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class StayTypeGraphQLController(
	private val stayTypeService: StayTypeService,
	private val stayTypeMapper: StayTypeMapper,
) {
	@QueryMapping
	fun getStayTypeById(@Argument id: Long): StayTypeResponse {
		val stayType = stayTypeService.getStayTypeById(id)
		return stayTypeMapper.toResponse(stayType)
	}

	@QueryMapping
	fun getAllStayTypes(@Argument name: String?): List<StayTypeResponse> {
		val stayTypes =
			if (name != null) {
				stayTypeService.getStayTypesByName(name)
			} else {
				stayTypeService.getAllStayTypes()
			}
		return stayTypeMapper.toResponseList(stayTypes)
	}
}
