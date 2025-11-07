package edu.fullstackproject.team1.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
@Table(name = "countries")
data class Country(
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	val id: Long? = null,
	@Column(nullable = false, length = 100)
	val name: String,
	@Column(name = "iso2_code", nullable = false, unique = true, length = 2)
	val iso2Code: String,
	@Column(name = "iso3_code", nullable = false, unique = true, length = 3)
	val iso3Code: String,
	@Column(length = 10)
	val phoneCode: String? = null,
	@Column(length = 3)
	val currencyCode: String? = null,
	@Column(length = 5)
	val currencySymbol: String? = null,
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "region_id")
	val region: Region? = null,
	@CreationTimestamp
	@Column(nullable = false)
	val createdAt: Instant? = null,
	@UpdateTimestamp
	@Column(nullable = false)
	val updatedAt: Instant? = null,
	@OneToMany(mappedBy = "country", fetch = FetchType.LAZY)
	val states: List<State> = emptyList(),
	@OneToMany(mappedBy = "country", fetch = FetchType.LAZY)
	val cities: List<City> = emptyList(),
)
