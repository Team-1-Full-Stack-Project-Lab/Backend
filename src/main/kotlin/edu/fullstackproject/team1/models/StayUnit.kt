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

@Entity
@Table(name = "stay_units")
data class StayUnit(
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	val id: Long? = null,
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "stay_id", nullable = false)
	val stay: Stay,
	@Column(nullable = false, length = 50)
	val stayNumber: String,
	@Column(nullable = false)
	val numberOfBeds: Int,
	@Column(nullable = false)
	val capacity: Int,
	@Column(nullable = false)
	val pricePerNight: Double,
	@Column(nullable = false, length = 50)
	val roomType: String,
	@CreationTimestamp
	@Column(nullable = false)
	val createdAt: Instant? = null,
	@UpdateTimestamp
	@Column(nullable = false)
	val updatedAt: Instant? = null,
)
