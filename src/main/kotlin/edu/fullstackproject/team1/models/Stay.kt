package edu.fullstackproject.team1.models

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
@Table(name = "stays")
data class Stay(
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	val id: Long? = null,

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "city_id", nullable = false)
	val city: City,

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "stay_type_id", nullable = false)
	val stayType: StayType,

	@Column(nullable = false, length = 255)
	val name: String,

	@Column(nullable = false, length = 500)
	val address: String,

	@Column(nullable = false)
	val latitude: Double,

	@Column(nullable = false)
	val longitude: Double,

	@CreationTimestamp
	@Column(nullable = false)
	val createdAt: Instant? = null,

	@UpdateTimestamp
	@Column(nullable = false)
	val updatedAt: Instant? = null,

	@OneToMany(mappedBy = "stay", fetch = FetchType.LAZY)
	val stayUnits: List<StayUnit> = emptyList(),

	@OneToMany(mappedBy = "stay", fetch = FetchType.LAZY)
	val stayServices: List<StayService> = emptyList(),

	@Column(name = "description")
	var description: String? = null,

	@OneToMany(mappedBy = "stay", fetch = FetchType.LAZY)
	val images: List<StayImage> = emptyList(),
)
