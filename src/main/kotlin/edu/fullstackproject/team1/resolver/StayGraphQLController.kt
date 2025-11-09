package edu.fullstackproject.team1.resolver

import edu.fullstackproject.team1.dtos.StayResponse
import edu.fullstackproject.team1.services.StayService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class StayGraphQLController(
	private val stayService: StayService
) {
	@QueryMapping
	fun getStayById(@Argument id: Long): StayResponse? =
		stayService.getStayById(id)

	@QueryMapping
	fun getAllStays(
		@Argument(name = "page") page: Int?,
		@Argument(name = "size") size: Int?
	): Page<StayResponse> {
		val pageable = PageRequest.of(page ?: 0, size ?: 20)
		return stayService.getAllStays(pageable)
	}

	@QueryMapping
	fun getStaysByCity(
		@Argument cityId: Long,
		@Argument(name = "page") page: Int?,
		@Argument(name = "size") size: Int?
	): Page<StayResponse> {
		val pageable = PageRequest.of(page ?: 0, size ?: 20)
		return stayService.getStaysByCity(cityId, pageable)
	}

	@QueryMapping
	fun searchStaysNearby(
		@Argument latitude: Double,
		@Argument longitude: Double,
		@Argument(name = "radius") radius: Double?,
		@Argument(name = "page") page: Int?,
		@Argument(name = "size") size: Int?
	): Page<StayResponse> {
		val pageable = PageRequest.of(page ?: 0, size ?: 20)
		val stays= stayService.searchStaysNearby(
			latitude,
			longitude,
			radius ?: 10.0,
			pageable
		)
		return stays
	}
}
