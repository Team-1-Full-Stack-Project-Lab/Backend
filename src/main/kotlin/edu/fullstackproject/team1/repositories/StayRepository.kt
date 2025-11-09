package edu.fullstackproject.team1.repositories

import edu.fullstackproject.team1.models.Stay
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface StayRepository : JpaRepository<Stay, Long> {
	@Query(
		"""
        SELECT s FROM Stay s
        JOIN FETCH s.city c
        JOIN FETCH s.stayType st
        WHERE s.city.id = :cityId
    """,
	)
	fun findByCityIdWithCityAndType(
		@Param("cityId") cityId: Long,
		pageable: Pageable,
	): Page<Stay>

	@Query(
		"""
        SELECT s FROM Stay s
        JOIN FETCH s.city c
        JOIN FETCH s.stayType st
        WHERE (
            6371 * acos(
                cos(radians(:latitude)) *
                cos(radians(s.latitude)) *
                cos(radians(s.longitude) - radians(:longitude)) +
                sin(radians(:latitude)) *
                sin(radians(s.latitude))
            )
        ) <= :radiusKm
        ORDER BY (
            6371 * acos(
                cos(radians(:latitude)) *
                cos(radians(s.latitude)) *
                cos(radians(s.longitude) - radians(:longitude)) +
                sin(radians(:latitude)) *
                sin(radians(s.latitude))
            )
        )
    """,
	)
	fun findStaysNearby(
		@Param("latitude") latitude: Double,
		@Param("longitude") longitude: Double,
		@Param("radiusKm") radiusKm: Double,
		pageable: Pageable,
	): Page<Stay>

	@Query(
		"""
	SELECT DISTINCT s FROM Stay s
	JOIN FETCH s.city c
	JOIN FETCH s.stayType st
	""",
		countQuery = "SELECT COUNT(DISTINCT s) FROM Stay s"
	)
	fun findAllWithCityAndType(pageable: Pageable): Page<Stay>
}
