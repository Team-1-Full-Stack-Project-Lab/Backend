package edu.fullstackproject.team1.dtos.requests

data class ChatRequest(
	val message: String,
	val sessionId: String? = null
)
