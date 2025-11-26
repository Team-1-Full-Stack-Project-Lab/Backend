package edu.fullstackproject.team1.mappers

import edu.fullstackproject.team1.dtos.responses.StayTypeResponse
import edu.fullstackproject.team1.models.StayType
import org.springframework.stereotype.Component

@Component
class StayTypeMapper {
	fun toResponse(stayType: StayType): StayTypeResponse {
		return StayTypeResponse(
			id = stayType.id,
			name = stayType.name,
		)
	}

	fun toResponseList(stayTypes: List<StayType>): List<StayTypeResponse> =
		stayTypes.map { toResponse(it) }
}
