package edu.fullstackproject.team1.resolver

import edu.fullstackproject.team1.dtos.requests.CreateStayImageRequest
import edu.fullstackproject.team1.dtos.responses.StayImageResponse
import edu.fullstackproject.team1.mappers.StayImageMapper
import edu.fullstackproject.team1.services.StayImageService
import jakarta.validation.Valid
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class StayImageGraphQLController(
	private val stayImageService: StayImageService,
	private val stayImageMapper: StayImageMapper,
) {
	@QueryMapping
	fun getAllStayImages(): List<StayImageResponse> {
		val stayImages = stayImageService.getAllStayImages()
		return stayImageMapper.toResponseList(stayImages, true)
	}

	@MutationMapping
	fun createStayImage(@Valid @Argument request: CreateStayImageRequest): StayImageResponse {
		val stayImage = stayImageService.createStayImage(request.toCommand())
		return stayImageMapper.toResponse(stayImage, true)
	}

	@MutationMapping
	fun deleteStayImage(@Argument id: Long) {
		stayImageService.deleteStayImage(id)
	}
}
