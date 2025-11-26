package edu.fullstackproject.team1.mappers

import edu.fullstackproject.team1.dtos.commands.AddStayUnitToTripCommand
import edu.fullstackproject.team1.dtos.responses.TripStayUnitResponse
import edu.fullstackproject.team1.models.StayUnit
import edu.fullstackproject.team1.models.Trip
import edu.fullstackproject.team1.models.TripStayUnit
import org.springframework.stereotype.Component

@Component
class TripStayUnitMapper(
	private val tripMapper: TripMapper,
	private val stayUnitMapper: StayUnitMapper,
) {
	fun toEntity(command: AddStayUnitToTripCommand, trip: Trip, stayUnit: StayUnit): TripStayUnit {
		return TripStayUnit(
			trip = trip,
			stayUnit = stayUnit,
			startDate = command.startDate,
			endDate = command.endDate,
		)
	}

	fun toResponse(tripStayUnit: TripStayUnit, includeRelations: Boolean = false): TripStayUnitResponse {
		val tripResp =
			if (includeRelations) tripMapper.toResponse(tripStayUnit.trip, includeRelations = false) else null
		val stayUnitResp =
			if (includeRelations) stayUnitMapper.toResponse(tripStayUnit.stayUnit, includeRelations = false) else null

		return TripStayUnitResponse(
			trip = tripResp,
			stayUnit = stayUnitResp,
			startDate = tripStayUnit.startDate,
			endDate = tripStayUnit.endDate,
		)
	}

	fun toResponseList(
		tripStayUnits: List<TripStayUnit>,
		includeRelations: Boolean = false,
	): List<TripStayUnitResponse> = tripStayUnits.map { toResponse(it, includeRelations) }
}
