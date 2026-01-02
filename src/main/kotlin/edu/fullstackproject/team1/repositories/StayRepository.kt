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
        LEFT JOIN FETCH s.company comp
        WHERE s.city.id = :cityId
        ORDER BY s.createdAt DESC
    """,
	)
	fun findByCityIdWithCityAndTypeAndCompany(
		@Param("cityId") cityId: Long,
		pageable: Pageable,
	): Page<Stay>

	@Query(
		"""
        SELECT s FROM Stay s
        JOIN FETCH s.city c
        JOIN FETCH s.stayType st
        LEFT JOIN FETCH s.company comp
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
	LEFT JOIN FETCH s.company comp
	ORDER BY s.createdAt DESC
	""",
		countQuery = "SELECT COUNT(DISTINCT s) FROM Stay s"
	)
	fun findAllWithCityAndType(pageable: Pageable): Page<Stay>

	@Query(
		"""
        SELECT DISTINCT s FROM Stay s
        LEFT JOIN FETCH s.city c
        LEFT JOIN FETCH s.stayType st
        LEFT JOIN FETCH s.company comp
        LEFT JOIN FETCH s.stayServices ss
        LEFT JOIN FETCH ss.service
        WHERE s.id = :id
    """,
	)
	fun findByIdWithRelations(@Param("id") id: Long): Stay?

	@Query(
		"""
        SELECT DISTINCT s FROM Stay s
        LEFT JOIN FETCH s.city c
        LEFT JOIN FETCH s.stayType st
        LEFT JOIN FETCH s.company comp
        LEFT JOIN FETCH s.stayServices ss
        LEFT JOIN FETCH ss.service
        WHERE s.id IN (
            SELECT s2.id FROM Stay s2
        	LEFT JOIN s2.city c2
			LEFT JOIN s2.company comp2
            LEFT JOIN s2.stayServices ss2
            WHERE (:cityId IS NULL OR c2.id = :cityId)
			AND (:companyId IS NULL OR comp2.id = :companyId)
            AND (:serviceIds IS NULL OR ss2.service.id IN :serviceIds)
            AND (:minPrice IS NULL AND :maxPrice IS NULL OR EXISTS (
                SELECT 1 FROM StayUnit su2
                WHERE su2.stay.id = s2.id
                AND (:minPrice IS NULL OR su2.pricePerNight >= :minPrice)
                AND (:maxPrice IS NULL or su2.pricePerNight <= :maxPrice)
            ))
            GROUP BY s2.id
            HAVING :serviceCount IS NULL OR COUNT(DISTINCT ss2.service.id) = :serviceCount
        )
		ORDER BY s.createdAt DESC
    """,
		countQuery = """
        SELECT COUNT(DISTINCT s.id) FROM Stay s
        LEFT JOIN s.city c
        LEFT JOIN s.company comp
        LEFT JOIN s.stayServices ss
        WHERE (:cityId IS NULL OR c.id = :cityId)
		AND (:companyId IS NULL OR comp.id = :companyId)
        AND (:serviceIds IS NULL OR ss.service.id IN :serviceIds)
        AND (:minPrice IS NULL AND :maxPrice IS NULL OR EXISTS (
            SELECT 1 FROM StayUnit su
            WHERE su.stay.id = s.id
            AND (:minPrice IS NULL OR su.pricePerNight >= :minPrice)
            AND (:maxPrice IS NULL OR su.pricePerNight <= :maxPrice)
        ))
        GROUP BY s.id
        HAVING :serviceCount IS NULL OR COUNT(DISTINCT ss.service.id) = :serviceCount
    """
	)
	fun findAllWithFilters(
		@Param("companyId") companyId: Long?,
		@Param("cityId") cityId: Long?,
		@Param("serviceIds") serviceIds: List<Long>?,
		@Param("serviceCount") serviceCount: Long?,
		@Param("minPrice") minPrice: Double?,
		@Param("maxPrice") maxPrice: Double?,
		pageable: Pageable
	): Page<Stay>
}
