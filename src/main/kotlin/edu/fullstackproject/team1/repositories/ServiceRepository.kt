package edu.fullstackproject.team1.repositories

import edu.fullstackproject.team1.models.Service
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ServiceRepository : JpaRepository<Service, Long> {
	fun findByName(name: String): List<Service>
}
