package edu.fullstackproject.team1.models

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
@Table(name = "cities")
data class City(
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	val id: Long? = null,

	@Column(nullable = false, length = 100)
	val name: String,

	@Column(length = 100)
	val nameAscii: String? = null,

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "country_id", nullable = false)
	val country: Country,

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "state_id")
	val state: State? = null,

	@Column(nullable = false)
	val latitude: Double,

	@Column(nullable = false)
	val longitude: Double,

	@Column(length = 50)
	val timezone: String? = null,

	@Column(length = 255)
	val googlePlaceId: String? = null,

	@Column
	val population: Int? = null,

	@Column(nullable = false)
	val isCapital: Boolean = false,

	@Column(nullable = false)
	val isFeatured: Boolean = false,

	@CreationTimestamp
	@Column(nullable = false)
	val createdAt: Instant? = null,

	@UpdateTimestamp
	@Column(nullable = false)
	val updatedAt: Instant? = null,
)
