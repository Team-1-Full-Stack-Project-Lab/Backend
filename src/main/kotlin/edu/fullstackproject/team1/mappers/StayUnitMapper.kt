package edu.fullstackproject.team1.mappers

import edu.fullstackproject.team1.dtos.responses.StayUnitResponse
import edu.fullstackproject.team1.models.StayUnit
import org.springframework.stereotype.Component

@Component
class StayUnitMapper(
	private val stayMapper: StayMapper,
) {
	fun toResponse(stayUnit: StayUnit, includeRelations: Boolean = false): StayUnitResponse {
		val stayResp = if (includeRelations) stayMapper.toResponse(stayUnit.stay, includeRelations = false) else null

		return StayUnitResponse(
			id = stayUnit.id,
			stayNumber = stayUnit.stayNumber,
			numberOfBeds = stayUnit.numberOfBeds,
			capacity = stayUnit.capacity,
			pricePerNight = stayUnit.pricePerNight,
			roomType = stayUnit.roomType,
			stay = stayResp,
		)
	}

	fun toResponseList(stayUnits: List<StayUnit>, includeRelations: Boolean = false): List<StayUnitResponse> =
		stayUnits.map { toResponse(it, includeRelations) }
}
