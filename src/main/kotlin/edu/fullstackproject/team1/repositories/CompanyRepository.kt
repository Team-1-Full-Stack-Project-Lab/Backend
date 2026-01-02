package edu.fullstackproject.team1.repositories

import edu.fullstackproject.team1.models.Company
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CompanyRepository : JpaRepository<Company, Long> {
	fun findByUserId(userId: Long): Company?
	fun existsByUserId(userId: Long): Boolean
}
