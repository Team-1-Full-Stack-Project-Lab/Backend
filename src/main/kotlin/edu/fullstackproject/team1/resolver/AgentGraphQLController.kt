package edu.fullstackproject.team1.resolver

import edu.fullstackproject.team1.dtos.responses.ChatResponse
import edu.fullstackproject.team1.dtos.responses.ConversationMessage
import edu.fullstackproject.team1.services.AgentService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class AgentGraphQLController(
	private val agentService: AgentService
) {
	@MutationMapping
	suspend fun chatWithAgent(
		@Argument message: String,
		@Argument sessionId: String?
	): ChatResponse{
		return agentService.processMessage(
			message = message,
			sessionId = sessionId
		)
	}

	@MutationMapping
	fun clearSession(@Argument sessionId: String): Boolean {
		return try {
			agentService.clearSession(sessionId)
			true
		} catch (e: Exception) {
			false
		}
	}

	@QueryMapping
	fun getSessionHistory(@Argument sessionId: String): List<ConversationMessage>{
		return agentService.getHistory(sessionId)
	}
}
