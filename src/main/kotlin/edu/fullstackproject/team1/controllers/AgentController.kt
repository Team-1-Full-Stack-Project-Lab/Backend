package edu.fullstackproject.team1.controllers

import edu.fullstackproject.team1.dtos.requests.ChatRequest
import edu.fullstackproject.team1.dtos.responses.ChatResponse
import edu.fullstackproject.team1.services.AgentService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/agent/chat")
class AgentController(
	private val agentService: AgentService
) {

	@PostMapping
	suspend fun chatWithAgent(@RequestBody request: ChatRequest): ResponseEntity<ChatResponse> {
		return try {
			val response = agentService.processMessage(request.message)
			ResponseEntity.ok(ChatResponse(response))
		} catch (e: Exception) {
			ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ChatResponse("Error processing request: ${e.message}"))
		}
	}
}
