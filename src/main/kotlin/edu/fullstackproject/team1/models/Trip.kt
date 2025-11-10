package edu.fullstackproject.team1.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.time.LocalDate

@Entity
@Table(name = "trips")
data class Trip(
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	val user: User,
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "city_id", nullable = false)
	val city: City,
	@Column(name = "name", nullable = false) val name: String,
	@Column(nullable = false) val startDate: LocalDate,
	@Column(nullable = false) val finishDate: LocalDate,
	@CreationTimestamp @Column(nullable = false) val createdAt: Instant? = null,
	@UpdateTimestamp @Column(nullable = false) val updatedAt: Instant? = null,
)
