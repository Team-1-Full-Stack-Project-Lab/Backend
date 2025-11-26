package edu.fullstackproject.team1.models

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant

@Entity
@Table(name = "regions")
data class Region(
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	val id: Long? = null,

	@Column(nullable = false, unique = true, length = 50)
	val name: String,

	@Column(unique = true, length = 10)
	val code: String? = null,

	@CreationTimestamp
	@Column(nullable = false)
	val createdAt: Instant? = null,
	
	@OneToMany(mappedBy = "region", fetch = FetchType.LAZY)
	val countries: List<Country> = emptyList(),
)
