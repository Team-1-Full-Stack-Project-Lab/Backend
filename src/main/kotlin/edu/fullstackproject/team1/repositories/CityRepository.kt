package edu.fullstackproject.team1.repositories

import edu.fullstackproject.team1.models.City
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CityRepository : JpaRepository<City, Long> {
	@Query("""
        SELECT c FROM City c
        JOIN FETCH c.country co
        LEFT JOIN FETCH c.state s
        ORDER BY c.isFeatured DESC, c.name
    """, countQuery = "SELECT COUNT(c) FROM City c")
	fun findAllWithCountryAndState(pageable: Pageable): Page<City>

	@Query("""
        SELECT c FROM City c
        LEFT JOIN FETCH c.state s
        WHERE c.country.id = :countryId
        ORDER BY c.isFeatured DESC, c.name
    """)
	fun findByCountryIdWithState(@Param("countryId") countryId: Long, pageable: Pageable): Page<City>

	@Query("""
        SELECT c FROM City c
        JOIN FETCH c.country co
        LEFT JOIN FETCH c.state s
        WHERE c.isFeatured = true
        ORDER BY c.population DESC NULLS LAST
    """)
	fun findFeaturedWithCountryAndState(): List<City>

	@Query("""
		SELECT c FROM City c
		JOIN FETCH c.country co
		LEFT JOIN FETCH c.state s
		WHERE c.isCapital = true
        ORDER BY c.population DESC NULLS LAST
	""")
	fun findCapitalsWithCountryAndState(): List<City>

	@Query("""
        SELECT c FROM City c
        JOIN FETCH c.country co
        LEFT JOIN FETCH c.state s
        WHERE (:countryId IS NULL OR c.country.id = :countryId)
        AND (:stateId IS NULL OR c.state.id = :stateId)
        AND (:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')))
        AND (:featured IS NULL OR c.isFeatured = :featured)
        ORDER BY c.isFeatured DESC, c.name
    """, countQuery = """
		SELECT COUNT(c) FROM City c
		WHERE (:countryId IS NULL OR c.country.id = :countryId)
		AND (:stateId IS NULL OR c.state.id = :stateId)
		AND (:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')))
		AND (:featured IS NULL OR c.isFeatured = :featured)
	""")
	fun searchCitiesWithCountryAndState(
		@Param("countryId") countryId: Long?,
		@Param("stateId") stateId: Long?,
		@Param("search") search: String?,
		@Param("featured") featured: Boolean?,
		pageable: Pageable
	): Page<City>

	@Query("""
        SELECT c FROM City c
        JOIN FETCH c.country co
        LEFT JOIN FETCH c.state s
        WHERE (
            6371 * acos(
                cos(radians(:latitude)) *
                cos(radians(c.latitude)) *
                cos(radians(c.longitude) - radians(:longitude)) +
                sin(radians(:latitude)) *
                sin(radians(c.latitude))
            )
        ) <= :radiusKm
        ORDER BY (
            6371 * acos(
                cos(radians(:latitude)) *
                cos(radians(c.latitude)) *
                cos(radians(c.longitude) - radians(:longitude)) +
                sin(radians(:latitude)) *
                sin(radians(c.latitude))
            )
        )
    """)
	fun findCitiesNearby(
		@Param("latitude") latitude: Double,
		@Param("longitude") longitude: Double,
		@Param("radiusKm") radiusKm: Double
	): List<City>

	@Query("""
		SELECT c FROM City c
		JOIN FETCH c.country co
		LEFT JOIN FETCH c.state s
		WHERE c.id = :id
	""")
	fun findByIdWithCountryAndState(id: Long): City?
}
