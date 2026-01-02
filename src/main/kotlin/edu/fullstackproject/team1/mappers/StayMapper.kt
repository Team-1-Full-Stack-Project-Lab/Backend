package edu.fullstackproject.team1.mappers

import edu.fullstackproject.team1.dtos.commands.StayCreateCommand
import edu.fullstackproject.team1.dtos.responses.StayResponse
import edu.fullstackproject.team1.models.City
import edu.fullstackproject.team1.models.Company
import edu.fullstackproject.team1.models.Stay
import edu.fullstackproject.team1.models.StayImage
import edu.fullstackproject.team1.models.StayType
import org.springframework.context.annotation.Lazy
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component

@Component
class StayMapper(
	private val cityMapper: CityMapper,
	private val stayTypeMapper: StayTypeMapper,
	private val serviceMapper: ServiceMapper,
	@Lazy private val companyMapper: CompanyMapper,
	@Lazy private val stayUnitMapper: StayUnitMapper,
	@Lazy private val stayImageMapper: StayImageMapper,
) {
	fun toEntity(
		command: StayCreateCommand,
		city: City,
		stayType: StayType,
		company: Company,
	): Stay {
		val stay = Stay(
			city = city,
			stayType = stayType,
			company = company,
			name = command.name,
			address = command.address,
			latitude = command.latitude,
			longitude = command.longitude,
			description = command.description,
		)

		if (!command.imageUrls.isNullOrEmpty()) {
			for (imageUrl in command.imageUrls) {
				val stayImage = StayImage(
					link = imageUrl,
					stay = stay,
				)
				stay.images.add(stayImage)
			}
		}

		return stay
	}

	fun toResponse(
		stay: Stay,
		includeRelations: Boolean = false,
		minPrice: Double? = null,
		maxPrice: Double? = null
	): StayResponse {
		val cityResp =
			if (includeRelations) cityMapper.toResponse(stay.city, true) else null
		val stayTypeResp = if (includeRelations) stayTypeMapper.toResponse(stay.stayType) else null

		val company = stay.company
		val companyResp = if (includeRelations && company != null) {
			companyMapper.toResponse(company, includeRelations = false)
		} else {
			null
		}

		val services = stay.stayServices.map { it.service }
		val servicesResp = if (includeRelations) serviceMapper.toResponseList(services) else null

		val unitsToMap = if (minPrice != null || maxPrice != null) {
			stay.stayUnits.filter { unit ->
				(minPrice == null || unit.pricePerNight >= minPrice) &&
				(maxPrice == null || unit.pricePerNight <= maxPrice)
			}
		} else {
			stay.stayUnits
		}

		val unitsResp = if (includeRelations) stayUnitMapper.toResponseList(unitsToMap) else null
		val imagesResp = if (includeRelations) stayImageMapper.toResponseList(stay.images) else null

		return StayResponse(
			id = stay.id!!,
			name = stay.name,
			address = stay.address,
			latitude = stay.latitude,
			longitude = stay.longitude,
			city = cityResp,
			stayType = stayTypeResp,
			company = companyResp,
			services = servicesResp,
			units = unitsResp,
			description = stay.description,
			images = imagesResp,
		)
	}

	fun toResponseList(
		stays: List<Stay>,
		includeRelations: Boolean = false,
		minPrice: Double? = null,
		maxPrice: Double? = null
	): List<StayResponse> =
		stays.map { toResponse(it, includeRelations, minPrice, maxPrice) }

	fun toResponsePage(
		stays: Page<Stay>,
		includeRelations: Boolean = false,
		minPrice: Double? = null,
		maxPrice: Double? = null
	): Page<StayResponse> =
		stays.map { toResponse(it, includeRelations, minPrice, maxPrice) }
}
