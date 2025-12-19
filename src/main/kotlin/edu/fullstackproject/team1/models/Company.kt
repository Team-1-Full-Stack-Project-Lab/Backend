package edu.fullstackproject.team1.models

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
@Table(name = "companies")
data class Company(
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	val id: Long? = null,

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	val user: User,

	@Column(nullable = false, length = 255)
	var name: String,

	@Column(nullable = false, length = 255)
	var email: String,

	@Column(length = 50)
	var phone: String? = null,

	@Column(columnDefinition = "TEXT")
	var description: String? = null,

	@CreationTimestamp
	@Column(nullable = false)
	val createdAt: Instant? = null,

	@UpdateTimestamp
	@Column(nullable = false)
	val updatedAt: Instant? = null,

	@OneToMany(mappedBy = "company", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
	val stays: List<Stay> = emptyList(),
)

