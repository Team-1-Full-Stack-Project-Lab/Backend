package edu.fullstackproject.team1.dtos.requests

import com.fasterxml.jackson.annotation.JsonInclude
import edu.fullstackproject.team1.dtos.commands.CreateStayImageCommand
import jakarta.validation.constraints.NotBlank

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CreateStayImageRequest(
	@field:NotBlank
	val link: String?,
	val stayId: Long?,
) {
	fun toCommand() = CreateStayImageCommand(
		link = link!!,
		stayId = stayId!!,
	)
}
