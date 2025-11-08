package edu.fullstackproject.team1.resolver

import edu.fullstackproject.team1.dtos.StayUnitResponse
import edu.fullstackproject.team1.services.StayUnitService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import java.math.BigDecimal

@Controller
class StayUnitGraphQLController(
	private val stayUnitService: StayUnitService
) {
	@QueryMapping
	fun getStayUnitById(@Argument id: Long): StayUnitResponse? =
		stayUnitService.getStayUnitById(id)

	@QueryMapping
	fun getStayUnitsByStayId(@Argument stayId: Long): List<StayUnitResponse> =
		stayUnitService.getStayUnitsByStayId(stayId)

	@QueryMapping
	fun searchAvailableUnits(
		@Argument stayId: Long,
		@Argument minCapacity: Int,
		@Argument maxPrice: BigDecimal
	): List<StayUnitResponse> =
		stayUnitService.searchAvailableUnits(stayId, minCapacity, maxPrice)
}
