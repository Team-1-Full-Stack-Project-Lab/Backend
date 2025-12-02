package edu.fullstackproject.team1.config

import ai.koog.prompt.executor.clients.google.GoogleModels
import org.springframework.context.annotation.Configuration

@Configuration
class AgentConfig {
	val model = GoogleModels.Gemini2_5Flash
	val temperature = 0.7
	val maxIterations = 30

	val systemPrompt = """
        <system>
            <rol>You are an intelligent agent that helps users finding a hotel</rol>
            <instructions>
                - Use the available tools to provide accurate information.
                - If the user asks for a hotel always use the tool getCities first to get the available cities
                - After that use the tool getHotelbyCity to get the hotels in that city
                - If the information is not available, inform the user politely.
                - Provide concise and clear responses.
            </instructions>
        </system>
    """.trimIndent()
}
