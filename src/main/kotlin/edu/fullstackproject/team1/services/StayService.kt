package edu.fullstackproject.team1.services

import edu.fullstackproject.team1.dtos.CityResponse
import edu.fullstackproject.team1.dtos.ServiceResponse
import edu.fullstackproject.team1.dtos.StayResponse
import edu.fullstackproject.team1.dtos.StayTypeResponse
import edu.fullstackproject.team1.dtos.StayUnitResponse
import edu.fullstackproject.team1.models.Stay
import edu.fullstackproject.team1.repositories.StayRepository
import edu.fullstackproject.team1.repositories.StayServiceRepository
import edu.fullstackproject.team1.repositories.StayUnitRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class StayService(
	private val stayRepository: StayRepository,
	private val stayServiceRepository: StayServiceRepository,
	private val stayUnitRepository: StayUnitRepository,
) {
	fun getAllStays(pageable: Pageable): Page<StayResponse> {
		val stays = stayRepository.findAllWithCityAndType(pageable)
		return stays.map { stay ->
			val services =
				stayServiceRepository.findByStayIdWithService(stay.id!!).map {
					ServiceResponse(it.service.id, it.service.name, it.service.icon)
				}
			val units =
				stayUnitRepository.findByStayId(stay.id!!).map {
					mapToStayUnitResponse(it, includeStay = false)
				}
			mapToStayResponse(stay, includeRelations = true, services = services, units = units)
		}
	}

	fun getStayById(id: Long): StayResponse {
		val stay =
			stayRepository.findById(id).orElseThrow {
				ResponseStatusException(HttpStatus.NOT_FOUND, "Stay not found")
			}
		val services =
			stayServiceRepository.findByStayIdWithService(id).map {
				ServiceResponse(it.service.id, it.service.name, it.service.icon)
			}
		val units =
			stayUnitRepository.findByStayId(id).map {
				mapToStayUnitResponse(it, includeStay = false)
			}
		return mapToStayResponse(stay, includeRelations = true, services = services, units = units)
	}

	fun getStaysByCity(
		cityId: Long,
		pageable: Pageable,
	): Page<StayResponse> {
		val stays = stayRepository.findByCityIdWithCityAndType(cityId, pageable)
		return stays.map { stay ->
			val services =
				stayServiceRepository.findByStayIdWithService(stay.id!!).map {
					ServiceResponse(it.service.id, it.service.name, it.service.icon)
				}
			val units =
				stayUnitRepository.findByStayId(stay.id!!).map {
					mapToStayUnitResponse(it, includeStay = false)
				}
			mapToStayResponse(stay, includeRelations = true, services = services, units = units)
		}
	}

	fun searchStaysNearby(
		latitude: Double,
		longitude: Double,
		radiusKm: Double,
		pageable: Pageable,
	): Page<StayResponse> {
		val stays = stayRepository.findStaysNearby(latitude, longitude, radiusKm, pageable)
		return stays.map { stay ->
			val services =
				stayServiceRepository.findByStayIdWithService(stay.id!!).map {
					ServiceResponse(it.service.id, it.service.name, it.service.icon)
				}
			val units =
				stayUnitRepository.findByStayId(stay.id!!).map {
					mapToStayUnitResponse(it, includeStay = false)
				}
			mapToStayResponse(stay, includeRelations = true, services = services, units = units)
		}
	}

	private fun mapToStayResponse(
		stay: Stay,
		includeRelations: Boolean,
		services: List<ServiceResponse>? = null,
		units: List<StayUnitResponse>? = null,
	): StayResponse =
		StayResponse(
			id = stay.id,
			name = stay.name,
			address = stay.address,
			latitude = stay.latitude,
			longitude = stay.longitude,
			city =
				if (includeRelations) {
					CityResponse(
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
					)
				} else {
					null
				},
			stayType =
				if (includeRelations) {
					StayTypeResponse(
						id = stay.stayType.id,
						name = stay.stayType.name,
					)
				} else {
					null
				},
			services = services,
			units = units,
		)

	private fun mapToStayUnitResponse(
		unit: edu.fullstackproject.team1.models.StayUnit,
		includeStay: Boolean,
	): StayUnitResponse =
		StayUnitResponse(
			id = unit.id,
			stayNumber = unit.stayNumber,
			numberOfBeds = unit.numberOfBeds,
			capacity = unit.capacity,
			pricePerNight = unit.pricePerNight,
			roomType = unit.roomType,
			stay =
				if (includeStay) {
					mapToStayResponse(unit.stay, includeRelations = true, services = null, units = null)
				} else {
					null
				},
		)
}
