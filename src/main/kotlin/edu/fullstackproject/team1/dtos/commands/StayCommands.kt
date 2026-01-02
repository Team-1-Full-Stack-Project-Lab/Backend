package edu.fullstackproject.team1.dtos.commands

data class StayCreateCommand(
	val cityId: Long,
	val stayTypeId: Long,
	val name: String,
	val address: String,
	val latitude: Double,
	val longitude: Double,
	val description: String?,
	val serviceIds: List<Long>?,
	val imageUrls: List<String>?,
)

data class StayUpdateCommand(
	val cityId: Long?,
	val stayTypeId: Long?,
	val name: String?,
	val address: String?,
	val latitude: Double?,
	val longitude: Double?,
	val description: String?,
	val serviceIds: List<Long>?,
	val imageUrls: List<String>?,
)

