package edu.fullstackproject.team1.models

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.io.Serializable
import java.time.Instant
import java.time.LocalDate

data class TripStayUnitId(
	val trip: Long? = null,
	val stayUnit: Long? = null
) : Serializable

@Entity
@Table(name = "trips_stay_units")
@IdClass(TripStayUnitId::class)
data class TripStayUnit(
	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trip_id", nullable = false)
	val trip: Trip,

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "stay_unit_id", nullable = false)
	val stayUnit: StayUnit,

	@Column(nullable = false)
	val startDate: LocalDate,

	@Column(nullable = false)
	val endDate: LocalDate,

	@CreationTimestamp
	@Column(nullable = false)
	val createdAt: Instant? = null,

	@UpdateTimestamp
	@Column(nullable = false)
	val updatedAt: Instant? = null
)
