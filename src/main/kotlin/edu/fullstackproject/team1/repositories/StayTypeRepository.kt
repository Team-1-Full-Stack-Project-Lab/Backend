package edu.fullstackproject.team1.repositories

import edu.fullstackproject.team1.models.StayType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StayTypeRepository : JpaRepository<StayType, Long> {
	fun findByName(name: String): List<StayType>
}
