package edu.fullstackproject.team1.dtos.commands

import java.time.LocalDate

data class CreateTripCommand(
	val name: String?,
	val cityId: Long,
	val startDate: LocalDate,
	val endDate: LocalDate,
)

data class UpdateTripCommand(
	val name: String?,
	val cityId: Long?,
	val startDate: LocalDate?,
	val endDate: LocalDate?,
)
