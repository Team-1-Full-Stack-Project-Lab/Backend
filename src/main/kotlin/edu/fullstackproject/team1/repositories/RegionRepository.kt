package edu.fullstackproject.team1.repositories

import edu.fullstackproject.team1.models.Region
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface RegionRepository : JpaRepository<Region, Long> {
	fun findByNameContainingIgnoreCase(name: String): List<Region>

	@Query("""
		SELECT r FROM Region r
		LEFT JOIN FETCH r.countries c
		WHERE r.id = :id
	""")
	fun findByIdWithCountries(id: Long): Region?
}
