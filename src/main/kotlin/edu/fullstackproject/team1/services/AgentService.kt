package edu.fullstackproject.team1.services

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.reflect.tool
import ai.koog.prompt.executor.clients.google.GoogleLLMClient
import ai.koog.prompt.executor.llms.SingleLLMPromptExecutor
import edu.fullstackproject.team1.config.AgentConfig
import edu.fullstackproject.team1.tool.AgentTools
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

	suspend fun processMessage(message: String): String {
		val agent = createAgent()
		return try {
			agent.run(message)
		} catch (e: Exception) {
			"Error: ${e.message}"
		}
	}
}
