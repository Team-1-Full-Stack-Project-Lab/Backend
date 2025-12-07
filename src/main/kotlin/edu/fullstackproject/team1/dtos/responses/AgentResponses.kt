package edu.fullstackproject.team1.dtos.responses

import kotlinx.serialization.Serializable

@Serializable
data class ChatResponse(
	val response: String,
	val hotels: List<HotelData>? = null
)

@Serializable
data class HotelData(
	val id: Int,
	val name: String,
	val address: String,
	val latitude: Double,
	val longitude: Double,
	val imageUrl: String? = null
)
