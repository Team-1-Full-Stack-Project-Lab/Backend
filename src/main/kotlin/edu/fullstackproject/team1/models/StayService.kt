package edu.fullstackproject.team1.models

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
@Table(name = "stay_services")
data class StayService(
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	val id: Long? = null,
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "stay_id", nullable = false)
	val stay: Stay,
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "service_id", nullable = false)
	val service: Service,
	@CreationTimestamp
	val createdAt: Instant? = null,
	@UpdateTimestamp
	val updatedAt: Instant? = null,
)
