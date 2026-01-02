package edu.fullstackproject.team1.mappers

import edu.fullstackproject.team1.dtos.commands.CreateStayImageCommand
import edu.fullstackproject.team1.dtos.responses.StayImageResponse
import edu.fullstackproject.team1.models.Stay
import edu.fullstackproject.team1.models.StayImage
import org.springframework.stereotype.Component

@Component
class StayImageMapper(
	private val stayMapper: StayMapper,
) {
	fun toEntity(command: CreateStayImageCommand, stay: Stay): StayImage {
		return StayImage(
			link = command.link,
			stay = stay,
		)
	}

	fun toResponse(stayImage: StayImage, includeRelations: Boolean = false): StayImageResponse {
		val stayResp = if (includeRelations) stayMapper.toResponse(stayImage.stay) else null

		return StayImageResponse(
			id = stayImage.id,
			link = stayImage.link,
			stay = stayResp,
		)
	}

	fun toResponseList(stayImages: List<StayImage>, includeRelations: Boolean = false): List<StayImageResponse> =
		stayImages.map { toResponse(it, includeRelations) }
}
