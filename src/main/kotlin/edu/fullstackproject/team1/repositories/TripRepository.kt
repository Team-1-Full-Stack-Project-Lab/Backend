package edu.fullstackproject.team1.repositories

import edu.fullstackproject.team1.models.Trip
import edu.fullstackproject.team1.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TripRepository : JpaRepository<Trip, Long> {
	fun findByUser(user: User): List<Trip>
}
