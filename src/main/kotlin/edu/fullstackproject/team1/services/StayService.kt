package edu.fullstackproject.team1.services

import edu.fullstackproject.team1.models.Stay
import edu.fullstackproject.team1.models.StayImage
import edu.fullstackproject.team1.repositories.StayImageRepository
import edu.fullstackproject.team1.repositories.StayRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class StayService(
	private val stayRepository: StayRepository,
	private val stayImageRepository: StayImageRepository,
) {

	fun getAllStays(
		serviceIds: List<Long>?,
		minPrice: Double?,
		maxPrice: Double?,
		pageable: Pageable
	): Page<Stay> {
		if (serviceIds.isNullOrEmpty() && minPrice == null && maxPrice == null) {
			return stayRepository.findAllWithCityAndType(pageable)
		}

		val serviceCount = if (!serviceIds.isNullOrEmpty()) serviceIds.size.toLong() else null

		return stayRepository.findAllWithFilters(
			serviceIds = if (serviceIds.isNullOrEmpty()) null else serviceIds,
			serviceCount = serviceCount,
			minPrice = minPrice,
			maxPrice = maxPrice,
			pageable = pageable
		)
	}

	fun getStayById(id: Long): Stay {
		return stayRepository.findById(id).orElseThrow {
			ResponseStatusException(HttpStatus.NOT_FOUND, "Stay not found")
		}
	}

	fun getStaysByCity(
		cityId: Long,
		pageable: Pageable
	): Page<Stay> {
		return stayRepository.findByCityIdWithCityAndType(cityId, pageable)
	}

	fun searchStaysNearby(
		latitude: Double,
		longitude: Double,
		radiusKm: Double,
		pageable: Pageable,
	): Page<Stay> {
		return stayRepository.findStaysNearby(latitude, longitude, radiusKm, pageable)
	}

	fun getImagesForStay(id: Long): List<StayImage> {
		if (!stayRepository.existsById(id)) {
			throw ResponseStatusException(HttpStatus.NOT_FOUND, "Stay not found")
		}

		return stayImageRepository.findAllByStayId(id)
	}
}
