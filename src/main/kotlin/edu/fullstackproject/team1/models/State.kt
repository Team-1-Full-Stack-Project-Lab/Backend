package edu.fullstackproject.team1.models

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
@Table(
	name = "states",
	uniqueConstraints = [UniqueConstraint(columnNames = ["country_id", "code"])],
)
data class State(
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	val id: Long? = null,

	@Column(nullable = false, length = 100)
	val name: String,

	@Column(length = 10)
	val code: String? = null,

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "country_id", nullable = false)
	val country: Country,

	@Column
	val latitude: Double? = null,

	@Column
	val longitude: Double? = null,

	@CreationTimestamp
	@Column(nullable = false)
	val createdAt: Instant? = null,

	@UpdateTimestamp
	@Column(nullable = false)
	val updatedAt: Instant? = null,

	@OneToMany(mappedBy = "state", fetch = FetchType.LAZY)
	val cities: List<City> = emptyList(),
)
