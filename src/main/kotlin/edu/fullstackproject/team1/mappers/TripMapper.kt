package edu.fullstackproject.team1.mappers

import edu.fullstackproject.team1.dtos.commands.CreateTripCommand
import edu.fullstackproject.team1.dtos.responses.TripResponse
import edu.fullstackproject.team1.models.City
import edu.fullstackproject.team1.models.Trip
import edu.fullstackproject.team1.models.User
import org.springframework.stereotype.Component

@Component
class TripMapper(
	private val cityMapper: CityMapper,
	private val countryMapper: CountryMapper,
) {
	fun toEntity(command: CreateTripCommand, user: User, city: City): Trip {
		val resolvedName = command.name?.takeIf { it.isNotBlank() } ?: city.name

		return Trip(
			user = user,
			city = city,
			name = resolvedName,
			startDate = command.startDate,
			finishDate = command.endDate,
		)
	}

	fun toResponse(trip: Trip, includeRelations: Boolean = false): TripResponse {
		val city = if (includeRelations) cityMapper.toResponse(trip.city, includeRelations = false) else null
		val country =
			if (includeRelations) countryMapper.toResponse(trip.city.country, includeRelations = false) else null

		return TripResponse(
			id = trip.id!!,
			name = trip.name,
			city = city,
			country = country,
			startDate = trip.startDate,
			endDate = trip.finishDate,
		)
	}

	fun toResponseList(trips: List<Trip>, includeRelations: Boolean = false): List<TripResponse> =
		trips.map { toResponse(it, includeRelations) }
}
