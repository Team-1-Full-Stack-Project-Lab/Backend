package edu.fullstackproject.team1.resolver

import edu.fullstackproject.team1.dtos.responses.StayUnitResponse
import edu.fullstackproject.team1.mappers.StayUnitMapper
import edu.fullstackproject.team1.services.StayUnitService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import java.math.BigDecimal

@Controller
class StayUnitGraphQLController(
	private val stayUnitService: StayUnitService,
	private val stayUnitMapper: StayUnitMapper,
) {
	@QueryMapping
	fun getStayUnitById(@Argument id: Long): StayUnitResponse {
		val unit = stayUnitService.getStayUnitById(id)
		return stayUnitMapper.toResponse(unit, true)
	}

	@QueryMapping
	fun getStayUnitsByStayId(@Argument stayId: Long): List<StayUnitResponse> {
		val units = stayUnitService.getStayUnitsByStayId(stayId)
		return stayUnitMapper.toResponseList(units)
	}

	@QueryMapping
	fun searchAvailableUnits(
		@Argument stayId: Long,
		@Argument minCapacity: Int,
		@Argument maxPrice: BigDecimal
	): List<StayUnitResponse> {
		val units = stayUnitService.searchAvailableUnits(stayId, minCapacity, maxPrice)
		return stayUnitMapper.toResponseList(units, true)
	}
}
