package edu.fullstackproject.team1.repositories

import edu.fullstackproject.team1.models.User
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
	fun findByEmail(email: String): User?

	@EntityGraph(attributePaths = ["company"])
	fun findWithCompanyById(id: Long): User?

	@EntityGraph(attributePaths = ["company"])
	fun findWithCompanyByEmail(email: String): User?
}
