package edu.fullstackproject.team1.services

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.reflect.tool
import ai.koog.prompt.executor.clients.google.GoogleLLMClient
import ai.koog.prompt.executor.llms.SingleLLMPromptExecutor
import edu.fullstackproject.team1.config.AgentConfig
import edu.fullstackproject.team1.dtos.responses.ChatResponse
import edu.fullstackproject.team1.dtos.responses.ConversationMessage
import edu.fullstackproject.team1.dtos.responses.HotelData
import edu.fullstackproject.team1.tool.AgentTools
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Service
class AgentService(
	private val agentConfig: AgentConfig,
	private val agentTools: AgentTools,
	@Value("\${google.api.key:}") private val googleApiKey: String = "",
) {
	private val sessionHistories = ConcurrentHashMap<String, MutableList<ConversationMessage>>()
	private val sessionHotels = ConcurrentHashMap<String, List<HotelData>>()

	fun createAgentWithHistory(sessionId: String): AIAgent<String, String> {
		val history = getHistory(sessionId)
		val hotels = sessionHotels[sessionId]
		val contextHistory = buildContextFromHistory(history,hotels)
		val systemPromptWithContext = if (contextHistory.isNotEmpty()) {
			"""
            ${agentConfig.systemPrompt}

            <conversation_history>
            $contextHistory
            </conversation_history>

            IMPORTANT INSTRUCTIONS FOR CONTEXT:
            - You MUST use the conversation history above to maintain context
            - When the user says "the first hotel", "that hotel", or similar references, look in the conversation history
            - DO NOT say you don't have information if it's in the conversation history
            - Be coherent with your previous responses
			- You can reference specific hotels by their name, id, or position in the list
            """.trimIndent()
		} else {
			agentConfig.systemPrompt
		}
		val client = GoogleLLMClient(
			apiKey = googleApiKey
		)
		val executor = SingleLLMPromptExecutor(client)
		val agent = createAgent(executor, systemPromptWithContext)
		return agent
	}

	private val json = Json{
		ignoreUnknownKeys = true
		isLenient = true
		prettyPrint = true
	}

	private fun createAgent(executor: SingleLLMPromptExecutor, systemPromptWithContext: Any): AIAgent<String, String> {
		return AIAgent(
			promptExecutor = executor,
			llmModel = agentConfig.model,
			temperature = agentConfig.temperature,
			toolRegistry = createToolRegistry(),
			systemPrompt = systemPromptWithContext as String?,
			maxIterations = agentConfig.maxIterations
		)
	}

	private fun createToolRegistry(): ToolRegistry {
		return ToolRegistry {
			tool( agentTools::getCities)
			tool(agentTools::getHotelsByCity)
			tool(agentTools::getAllHotels)
			tool(agentTools::getCurrentDate)
			tool(agentTools::searchHotelsWithFilters)
			tool(agentTools::getAvailableServices)
		}
	}

	fun getHistory(sessionId: String): List<ConversationMessage> {
		return sessionHistories.getOrDefault(sessionId,mutableListOf())
	}
	fun addMessage(sessionId: String,role: String, content: String) {
		val history = sessionHistories.getOrPut(sessionId, { mutableListOf() })
		history.add(ConversationMessage(role, content))
		if(history.size >20) {
			history.removeAt(0)
		}
	}
	private fun buildContextFromHistory(
		history: List<ConversationMessage>,
		hotels: List<HotelData>?
	): String {
		if(history.isEmpty()) return ""
		val contextMessages = history.takeLast(10).joinToString("\n\n"){msg->
			when(msg.role){
				"user" -> "User asked: ${msg.content}"
				"assistant" -> "You (Assistant) responded: ${msg.content}"
				else -> ""
			}
		}
		val hotelsContext = if (! hotels.isNullOrEmpty()) {
			"""
            <hotels_data>
            Previously retrieved hotels (use this data to answer questions):
            ${json.encodeToString(hotels)}
            </hotels_data>
            """.trimIndent()
		} else {
			""
		}
		return """
            |Previous conversation:
            |
            |$contextMessages
			|$hotelsContext
            |
            |---
            |Now respond to the user's new message, using the context above.
            """.trimMargin()
	}

	fun clearSession(sessionId: String) {
		sessionHistories.remove(sessionId)
		sessionHotels.remove(sessionId)
	}

	suspend fun processMessage(message: String, sessionId: String?): ChatResponse {
		val actualSessionId = sessionId ?: UUID.randomUUID().toString()
		val agent = createAgentWithHistory(actualSessionId)
		val response = try {
			val agentResponse = agent.run(message)
			parseAgentResponse(agentResponse, actualSessionId)
		} catch (e: Exception) {
			e.printStackTrace()
			ChatResponse(
				response = "Error processing your request: ${e.message}",
				sessionId = actualSessionId,
				hotels = null
			)
		}
		addMessage(actualSessionId,"user", message)
		addMessage(actualSessionId,"assistant", response.response)
		if (!response.hotels.isNullOrEmpty()) {
			sessionHotels[actualSessionId] = response.hotels
		}
		return response
	}
	private fun parseAgentResponse(response: String, actualSessionId: String): ChatResponse {
		val marker = "###HOTELS_DATA###"

		return if (response.contains(marker)) {
			val parts = response.split(marker, limit = 2)

			if (parts.size == 2) {
				val message = parts[0].trim()
				val jsonPart = parts[1].trim()

				val hotels = try {
					val cleanJson = jsonPart
						.removePrefix("```json")
						. removePrefix("```")
						.removeSuffix("```")
						.trim()
					json.decodeFromString<List<HotelData>>(cleanJson)
				} catch (e: Exception) {
					println("Error parsing JSON: ${e.message}")
					e.printStackTrace()
					null
				}
				ChatResponse(
					response = message.ifEmpty { "Here are the hotels that were found:" },
					sessionId = actualSessionId,
					hotels = hotels
				)
			} else {
				ChatResponse(
					response = response.replace(marker, "").trim(),
					sessionId = actualSessionId,
					hotels = null
				)
			}
		} else {
			ChatResponse(
				response = response.trim(),
				sessionId = actualSessionId,
				hotels = null
			)
		}
	}
}

