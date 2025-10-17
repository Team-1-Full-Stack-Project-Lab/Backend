package edu.fullstackproject.team1.repositories

import edu.fullstackproject.team1.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
	fun findByEmail(email: String): User?
}
