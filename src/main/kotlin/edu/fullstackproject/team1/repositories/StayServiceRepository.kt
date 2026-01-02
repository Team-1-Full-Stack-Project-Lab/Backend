package edu.fullstackproject.team1.repositories

import edu.fullstackproject.team1.models.StayService
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface StayServiceRepository : JpaRepository<StayService, Long> {
	@Query(
		"""
        SELECT ss FROM StayService ss
        JOIN FETCH ss.service s
        WHERE ss.stay.id = :stayId
    """,
	)
	fun findByStayIdWithService(
		@Param("stayId") stayId: Long,
	): List<StayService>

	fun deleteByStayId(stayId: Long)
}
