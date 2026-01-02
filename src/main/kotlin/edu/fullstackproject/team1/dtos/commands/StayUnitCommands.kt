package edu.fullstackproject.team1.dtos.commands

data class StayUnitCreateCommand(
	val stayId: Long,
	val stayNumber: String,
	val numberOfBeds: Int,
	val capacity: Int,
	val pricePerNight: Double,
	val roomType: String,
)

data class StayUnitUpdateCommand(
	val stayNumber: String?,
	val numberOfBeds: Int?,
	val capacity: Int?,
	val pricePerNight: Double?,
	val roomType: String?,
)
