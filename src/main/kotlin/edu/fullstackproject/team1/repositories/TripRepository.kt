package edu.fullstackproject.team1.repositories

import edu.fullstackproject.team1.models.Trip
import edu.fullstackproject.team1.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface TripRepository : JpaRepository<Trip, Long> {
	fun findByUser(user: User): List<Trip>

	@Query(
		"""
		SELECT t FROM Trip t
		JOIN FETCH t.user u
		WHERE t.id = :id
	"""
	)
	fun findByIdWithUser(@Param("id") id: Long): Trip?

	@Query(
		"""
		SELECT t FROM Trip t
		JOIN FETCH t.city c
		JOIN FETCH c.country co
		WHERE t.id = :id
	"""
	)
	fun findByIdWithCityAndCountry(@Param("id") id: Long): Trip?

	@Query(
		"""
		SELECT t FROM Trip t
		JOIN FETCH t.city c
		JOIN FETCH c.country co
		WHERE t.user = :user
	"""
	)
	fun findByUserWithCityAndCountry(@Param("user") user: User): List<Trip>
}
