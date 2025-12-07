package edu.fullstackproject.team1.services

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.reflect.tool
import ai.koog.prompt.executor.clients.google.GoogleLLMClient
import ai.koog.prompt.executor.llms.SingleLLMPromptExecutor
import edu.fullstackproject.team1.config.AgentConfig
import edu.fullstackproject.team1.dtos.responses.ChatResponse
import edu.fullstackproject.team1.dtos.responses.HotelData
import edu.fullstackproject.team1.tool.AgentTools
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Service

@Service
class AgentService(
	private val agentConfig: AgentConfig,
	private val agentTools: AgentTools
) {
	private val client = GoogleLLMClient(
		apiKey = System.getenv("GOOGLE_API_KEY")
	)

	private val executor = SingleLLMPromptExecutor(client)

	private val json = Json{
		ignoreUnknownKeys = true
		isLenient = true
	}

	private fun createAgent(): AIAgent<String, String> {
		return AIAgent(
			promptExecutor = executor,
			llmModel = agentConfig.model,
			temperature = agentConfig.temperature,
			toolRegistry = createToolRegistry(),
			systemPrompt = agentConfig.systemPrompt,
			maxIterations = agentConfig.maxIterations
		)
	}

	private fun createToolRegistry(): ToolRegistry {
		return ToolRegistry {
			tool( agentTools::getCities)
			tool(agentTools::getHotelsByCity)
			tool(agentTools::getAllHotels)
			tool(agentTools::getCurrentDate)
		}
	}

	suspend fun processMessage(message: String): ChatResponse {
		val agent = createAgent()
		return try {
			val agentResponse = agent.run(message)
			parseAgentResponse(agentResponse)
		} catch (e: Exception) {
			e.printStackTrace()
			ChatResponse(
				response = "Error procesando tu solicitud: ${e.message}",
				hotels = null
			)
		}
	}
	private fun parseAgentResponse(response: String): ChatResponse {
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
					response = message. ifEmpty { "Aquí están los hoteles encontrados:" },
					hotels = hotels
				)
			} else {
				ChatResponse(
					response = response. replace(marker, ""). trim(),
					hotels = null
				)
			}
		} else {
			ChatResponse(
				response = response.trim(),
				hotels = null
			)
		}
	}
}

