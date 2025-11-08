package edu.fullstackproject.team1.repositories

import edu.fullstackproject.team1.models.StayUnit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.util.Optional

@Repository
interface StayUnitRepository : JpaRepository<StayUnit, Long> {
	@Query(
		"""
        SELECT su FROM StayUnit su
        WHERE su.stay.id = :stayId
        ORDER BY su.pricePerNight
    """,
	)
	fun findByStayId(
		@Param("stayId") stayId: Long,
	): List<StayUnit>

	@Query(
		"""
    SELECT su FROM StayUnit su
    JOIN FETCH su.stay s
    JOIN FETCH s.city c
    JOIN FETCH s.stayType st
    WHERE su.stay.id = :stayId
    ORDER BY su.pricePerNight
    """,
	)
	fun findByStayIdWithStay(
		@Param("stayId") stayId: Long,
	): List<StayUnit>

	@Query(
		"""
        SELECT su FROM StayUnit su
        WHERE su.stay.id = :stayId
        AND su.capacity >= :minCapacity
        AND su.pricePerNight <= :maxPrice
        ORDER BY su.pricePerNight
    """,
	)
	fun findAvailableUnits(
		@Param("stayId") stayId: Long,
		@Param("minCapacity") minCapacity: Int,
		@Param("maxPrice") maxPrice: BigDecimal,
	): List<StayUnit>
}
