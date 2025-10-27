package edu.fullstackproject.team1.repositories

import edu.fullstackproject.team1.models.State
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface StateRepository : JpaRepository<State, Long> {
	fun findByNameContainingIgnoreCase(name: String): List<State>

	@Query("""
        SELECT s FROM State s
        JOIN FETCH s.country c
        WHERE s.id = :id
    """)
	fun findByIdWithCountry(@Param("id") id: Long): State?
}
