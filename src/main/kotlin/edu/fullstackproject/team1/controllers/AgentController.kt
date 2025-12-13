package edu.fullstackproject.team1.controllers

import edu.fullstackproject.team1.dtos.requests.ChatRequest
import edu.fullstackproject.team1.dtos.responses.ChatResponse
import edu.fullstackproject.team1.dtos.responses.ConversationMessage
import edu.fullstackproject.team1.services.AgentService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/agent")
class AgentController(
	private val agentService: AgentService
) {

	@PostMapping("/chat")
	suspend fun chatWithAgent(@RequestBody request: ChatRequest): ResponseEntity<ChatResponse> {
		return try {
			val chatResponse = agentService.processMessage(
				message = request.message,
				sessionId = request.sessionId
			)
			ResponseEntity. ok(chatResponse)
		} catch (e: Exception) {
			ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ChatResponse(
					"Error processing request: ${e.message}",
					"", null))
		}
	}

	@DeleteMapping("/session/{sessionId}")
	fun clearSession(@PathVariable sessionId: String): ResponseEntity<Unit> {
		return try {
			agentService.clearSession(sessionId)
			ResponseEntity.ok().build()
		} catch (e: Exception) {
			ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
		}
	}

	@GetMapping("/session/{sessionId}/history")
	fun getSessionHistory(@PathVariable sessionId: String): ResponseEntity<List<ConversationMessage>> {
		return try {
			val history = agentService.getHistory(sessionId)
			ResponseEntity.ok(history)
		} catch (e: Exception) {
			ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
		}
	}
}
