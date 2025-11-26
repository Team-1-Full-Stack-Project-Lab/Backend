package edu.fullstackproject.team1.dtos.responses

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDate

@JsonInclude(JsonInclude.Include.NON_NULL)
data class TripStayUnitResponse(
	val trip: TripResponse?,
	val stayUnit: StayUnitResponse?,
	val startDate: LocalDate,
	val endDate: LocalDate,
)
