package edu.fullstackproject.team1.models

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
@Table(name = "stay_types")
data class StayType(
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	val id: Long? = null,

	@Column(nullable = false, length = 100)
	val name: String,

	@CreationTimestamp
	@Column(nullable = false)
	val createdAt: Instant? = null,

	@UpdateTimestamp
	@Column(nullable = false)
	val updatedAt: Instant? = null,

	@OneToMany(mappedBy = "stayType", fetch = FetchType.LAZY)
	val stays: List<Stay> = emptyList(),
)
