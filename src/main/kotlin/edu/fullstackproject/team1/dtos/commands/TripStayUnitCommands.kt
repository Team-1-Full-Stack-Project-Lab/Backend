package edu.fullstackproject.team1.dtos.commands

import java.time.LocalDate

data class AddStayUnitToTripCommand(
	val tripId: Long,
	val stayUnitId: Long,
	val startDate: LocalDate,
	val endDate: LocalDate,
)
