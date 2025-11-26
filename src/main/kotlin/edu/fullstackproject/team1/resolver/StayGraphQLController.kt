package edu.fullstackproject.team1.resolver

import edu.fullstackproject.team1.dtos.responses.StayResponse
import edu.fullstackproject.team1.mappers.StayMapper
import edu.fullstackproject.team1.services.StayService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class StayGraphQLController(
	private val stayService: StayService,
	private val stayMapper: StayMapper,
) {
	@QueryMapping
	fun getStayById(@Argument id: Long): StayResponse {
		val stay = stayService.getStayById(id)
		return stayMapper.toResponse(stay, true)
	}

	@QueryMapping
	fun getAllStays(
		@Argument(name = "page") page: Int = 0,
		@Argument(name = "size") size: Int = 20,
	): Page<StayResponse> {
		val pageable = PageRequest.of(page, size)
		val stays = stayService.getAllStays(pageable)
		return stayMapper.toResponsePage(stays, true)
	}

	@QueryMapping
	fun getStaysByCity(
		@Argument cityId: Long,
		@Argument(name = "page") page: Int = 0,
		@Argument(name = "size") size: Int = 20,
	): Page<StayResponse> {
		val pageable = PageRequest.of(page, size)
		val stays = stayService.getStaysByCity(cityId, pageable)
		return stayMapper.toResponsePage(stays, true)
	}

	@QueryMapping
	fun searchStaysNearby(
		@Argument latitude: Double,
		@Argument longitude: Double,
		@Argument(name = "radius") radius: Double,
		@Argument(name = "page") page: Int = 0,
		@Argument(name = "size") size: Int = 20,
	): Page<StayResponse> {
		val pageable = PageRequest.of(page, size)
		val stays = stayService.searchStaysNearby(latitude, longitude, radius, pageable)
		return stayMapper.toResponsePage(stays, true)
	}
}
