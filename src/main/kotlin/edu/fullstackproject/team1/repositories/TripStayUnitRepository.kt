package edu.fullstackproject.team1.repositories

import edu.fullstackproject.team1.models.TripStayUnit
import edu.fullstackproject.team1.models.TripStayUnitId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface TripStayUnitRepository : JpaRepository<TripStayUnit, TripStayUnitId> {
	fun findByTripId(tripId: Long): List<TripStayUnit>

	fun findByStayUnitId(stayUnitId: Long): List<TripStayUnit>

	@Query(
		"""
        SELECT CASE WHEN COUNT(tsu) > 0 THEN true ELSE false END
        FROM TripStayUnit tsu
        WHERE tsu.stayUnit.id = :stayUnitId
        AND tsu.trip.id != :excludeTripId
        AND (
            (tsu.startDate <= :endDate AND tsu.endDate >= :startDate)
        )
    """
	)
	fun isStayUnitReserved(
		stayUnitId: Long,
		startDate: LocalDate,
		endDate: LocalDate,
		excludeTripId: Long = 0
	): Boolean

	fun deleteByTripId(tripId: Long)
}
