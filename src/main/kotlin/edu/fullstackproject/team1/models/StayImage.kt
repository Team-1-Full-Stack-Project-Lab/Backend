package edu.fullstackproject.team1.models

import jakarta.persistence.*

@Entity
@Table(name = "stay_images")
data class StayImage(
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	val id: Long? = null,

	@Column(name = "link", nullable = false, length = 2048)
	var link: String,

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "stay_id", nullable = false)
	var stay: Stay
)
