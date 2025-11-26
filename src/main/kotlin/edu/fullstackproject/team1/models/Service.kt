package edu.fullstackproject.team1.models

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
@Table(name = "services")
data class Service(
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	val id: Long? = null,

	@Column(nullable = false, length = 100)
	val name: String,

	@Column(length = 255)
	val icon: String? = null,

	@CreationTimestamp
	@Column(nullable = false)
	val createdAt: Instant? = null,

	@UpdateTimestamp
	@Column(nullable = false)
	val updatedAt: Instant? = null,

	@OneToMany(mappedBy = "service", fetch = FetchType.LAZY)
	val stayServices: List<StayService> = emptyList(),
)
