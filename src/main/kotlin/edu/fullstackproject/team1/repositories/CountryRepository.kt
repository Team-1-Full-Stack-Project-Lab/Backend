package edu.fullstackproject.team1.repositories

import edu.fullstackproject.team1.models.Country
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CountryRepository : JpaRepository<Country, Long> {
	fun findByIso2Code(iso2Code: String): Country?

	fun findByIso3Code(iso3Code: String): Country?

	fun findByNameContainingIgnoreCase(name: String): List<Country>

	@Query(
		"""
		SELECT DISTINCT c FROM Country c
        LEFT JOIN FETCH c.region r
		LEFT JOIN FETCH c.cities ci
		LEFT JOIN FETCH ci.state s
		WHERE c.id = :id
	""",
	)
	fun findByIdWithRegionCitiesAndStates(
		@Param("id") id: Long,
	): Country?
}
