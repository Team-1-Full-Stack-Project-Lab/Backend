package edu.fullstackproject.team1.services

import edu.fullstackproject.team1.dtos.commands.StayCreateCommand
import edu.fullstackproject.team1.dtos.commands.StayUpdateCommand
import edu.fullstackproject.team1.mappers.StayMapper
import edu.fullstackproject.team1.models.Stay
import edu.fullstackproject.team1.models.StayImage
import edu.fullstackproject.team1.models.StayService
import edu.fullstackproject.team1.repositories.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
@Transactional
class StayService(
	private val stayRepository: StayRepository,
	private val stayImageRepository: StayImageRepository,
	private val cityRepository: CityRepository,
	private val stayTypeRepository: StayTypeRepository,
	private val userRepository: UserRepository,
	private val serviceRepository: ServiceRepository,
	private val stayServiceRepository: StayServiceRepository,
	private val stayMapper: StayMapper,
) {
	fun getAllStays(
		companyId: Long?,
		cityId: Long?,
		serviceIds: List<Long>?,
		minPrice: Double?,
		maxPrice: Double?,
		pageable: Pageable
	): Page<Stay> {
		return stayRepository.findAllWithFilters(
			companyId = companyId,
			cityId = cityId,
			serviceIds = if (serviceIds.isNullOrEmpty()) null else serviceIds,
			serviceCount = if (!serviceIds.isNullOrEmpty()) serviceIds.size.toLong() else null,
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
		return stayRepository.findByCityIdWithCityAndTypeAndCompany(cityId, pageable)
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

	fun createStay(email: String, command: StayCreateCommand): Stay {
		val user = userRepository.findWithCompanyByEmail(email)
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")

		val company = user.company
			?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "User doesn't have a company. Please create a company first.")

		val city = cityRepository.findById(command.cityId)
			.orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "City not found") }

		val stayType = stayTypeRepository.findById(command.stayTypeId)
			.orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Stay type not found") }

		val stay = stayMapper.toEntity(command, city, stayType, company)

		if (!command.serviceIds.isNullOrEmpty()) {
			val services = serviceRepository.findAllById(command.serviceIds)
			if (services.size != command.serviceIds.size) {
				throw ResponseStatusException(HttpStatus.BAD_REQUEST, "One or more service IDs are invalid")
			}

			for (service in services) {
				val stayService = StayService(
					stay = stay,
					service = service,
				)
				stay.stayServices.add(stayService)
			}
		}

		return stayRepository.save(stay)
	}

	fun updateStay(email: String, id: Long, command: StayUpdateCommand): Stay {
		val existingStay = stayRepository.findById(id)
			.orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Stay not found") }

		if (existingStay.company?.user?.email != email) {
			throw ResponseStatusException(HttpStatus.FORBIDDEN, "You don't own this stay")
		}

		val updatedCity = if (command.cityId != null) {
			cityRepository.findById(command.cityId)
				.orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "City not found") }
		} else {
			existingStay.city
		}

		val updatedStayType = if (command.stayTypeId != null) {
			stayTypeRepository.findById(command.stayTypeId)
				.orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Stay type not found") }
		} else {
			existingStay.stayType
		}

		val updatedStay = existingStay.copy(
			city = updatedCity,
			stayType = updatedStayType,
			name = command.name ?: existingStay.name,
			address = command.address ?: existingStay.address,
			latitude = command.latitude ?: existingStay.latitude,
			longitude = command.longitude ?: existingStay.longitude,
			description = command.description ?: existingStay.description,
		)

		if (command.serviceIds != null) {
			val services = serviceRepository.findAllById(command.serviceIds)
			if (services.size != command.serviceIds.size) {
				throw ResponseStatusException(HttpStatus.BAD_REQUEST, "One or more service IDs are invalid")
			}

			stayServiceRepository.deleteByStayId(existingStay.id!!)
			stayServiceRepository.flush()
			updatedStay.stayServices.clear()

			for (service in services) {
				val stayService = StayService(
					stay = updatedStay,
					service = service,
				)
				updatedStay.stayServices.add(stayService)
			}
		}

		if (command.imageUrls != null) {
			updatedStay.images.clear()

			for (imageUrl in command.imageUrls) {
				val stayImage = StayImage(
					link = imageUrl,
					stay = updatedStay,
				)
				updatedStay.images.add(stayImage)
			}
		}

		return stayRepository.save(updatedStay)
	}

	fun deleteStay(email: String, id: Long) {
		val stay = stayRepository.findById(id)
			.orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Stay not found") }

		if (stay.company?.user?.email != email) {
			throw ResponseStatusException(HttpStatus.FORBIDDEN, "You don't own this stay")
		}

		stayRepository.delete(stay)
	}

	fun getPopularStays(limit: Int = 6): List<Stay> {
		try {
			val popularStayData = stayRepository.findMostPopularStayIds()

			if (popularStayData.isEmpty()) {
				val fallbackStays = stayRepository.findAllWithCityAndType(PageRequest.of(0, limit))
				return fallbackStays.content.take(limit)
			}

			val popularStayWithCounts = popularStayData.mapNotNull { row ->
				val stayId = when (val id = row[0]) {
					is Long -> id
					is Int -> id.toLong()
					is java.math.BigInteger -> id.toLong()
					is java.math.BigDecimal -> id.toLong()
					else -> null
				}
				val tripCount = when (val count = row[1]) {
					is Long -> count
					is Int -> count.toLong()
					is java.math.BigInteger -> count.toLong()
					is java.math.BigDecimal -> count.toLong()
					else -> 0L
				}

				if (stayId != null) {
					Pair(stayId, tripCount)
				} else {
					null
				}
			}.take(limit)

			val popularStayIds = popularStayWithCounts.map { it.first }

			if (popularStayIds.isEmpty()) {
				val fallbackStays = stayRepository.findAllWithCityAndType(PageRequest.of(0, limit))
				return fallbackStays.content.take(limit)
			}

			if (popularStayIds.size >= limit) {
				val stays = stayRepository.findByIdsWithRelations(popularStayIds)
				return popularStayIds.mapNotNull { id ->
					stays.find { it.id == id }
				}
			}

			val allStays = stayRepository.findAllWithCityAndType(PageRequest.of(0, limit * 2))
			val allStayIds = allStays.content.mapNotNull { it.id }

			if (allStayIds. isEmpty()) {
				return emptyList()
			}

			val remainingCount = limit - popularStayIds.size
			val availableIds = allStayIds.filterNot { it in popularStayIds }
			val fillerStayIds = availableIds.take(remainingCount)
			val combinedIds = popularStayIds + fillerStayIds

			if (combinedIds.isEmpty()) {
				return emptyList()
			}

			val stays = stayRepository.findByIdsWithRelations(combinedIds)
			return combinedIds. mapNotNull { id -> stays.find { it.id == id } }

		} catch (e: Exception) {
			val fallbackStays = stayRepository.findAllWithCityAndType(PageRequest.of(0, limit))
			return fallbackStays.content.take(limit)
		}
	}
}
