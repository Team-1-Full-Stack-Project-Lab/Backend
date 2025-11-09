package edu.fullstackproject.team1.services

import edu.fullstackproject.team1.dtos.CityResponse
import edu.fullstackproject.team1.dtos.ServiceResponse
import edu.fullstackproject.team1.dtos.StayResponse
import edu.fullstackproject.team1.dtos.StayTypeResponse
import edu.fullstackproject.team1.dtos.StayUnitResponse
import edu.fullstackproject.team1.models.Stay
import edu.fullstackproject.team1.models.StayUnit
import edu.fullstackproject.team1.repositories.StayServiceRepository
import edu.fullstackproject.team1.repositories.StayUnitRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal

@Service
class StayUnitService(
	private val stayUnitRepository: StayUnitRepository,
	private val stayServiceRepository: StayServiceRepository
) {
	fun getStayUnitsByStayId(stayId: Long): List<StayUnitResponse> {
		val units = stayUnitRepository.findByStayIdWithStay(stayId)
		return units.map { mapToStayUnitResponse(it, includeStay = true) }
	}

	fun getStayUnitById(id: Long): StayUnitResponse {
		val unit = stayUnitRepository.findById(id)
			.orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Stay unit not found") }
		return mapToStayUnitResponse(unit, includeStay = true)
	}

	fun searchAvailableUnits(
		stayId: Long,
		minCapacity: Int,
		maxPrice: BigDecimal,
	): List<StayUnitResponse> {
		val units = stayUnitRepository.findAvailableUnits(stayId, minCapacity, maxPrice)
		return units.map { mapToStayUnitResponse(it, includeStay = true) }
	}

	private fun mapToStayUnitResponse(
		unit: StayUnit,
		includeStay: Boolean,
	): StayUnitResponse =
		StayUnitResponse(
			id = unit.id,
			stayNumber = unit.stayNumber,
			numberOfBeds = unit.numberOfBeds,
			capacity = unit.capacity,
			pricePerNight = unit.pricePerNight,
			roomType = unit.roomType,
			stay = if (includeStay) {
				mapToStayResponse(unit.stay)
			} else {
				null
			},
		)

	private fun mapToStayResponse(stay: Stay): StayResponse {
		val services = stayServiceRepository.findByStayIdWithService(stay.id!!).map {
			ServiceResponse(it.service.id, it.service.name, it.service.icon)
		}

		return StayResponse(
			id = stay.id,
			name = stay.name,
			address = stay.address,
			latitude = stay.latitude,
			longitude = stay.longitude,
			city = CityResponse(
				id = stay.city.id,
				name = stay.city.name,
				nameAscii = stay.city.nameAscii,
				country = null,
				state = null,
				latitude = stay.city.latitude,
				longitude = stay.city.longitude,
				timezone = stay.city.timezone,
				googlePlaceId = stay.city.googlePlaceId,
				population = stay.city.population,
				isCapital = stay.city.isCapital,
				isFeatured = stay.city.isFeatured,
			),
			stayType = StayTypeResponse(
				id = stay.stayType.id,
				name = stay.stayType.name,
			),
			services = services,
			units = null,
		)
	}
}
