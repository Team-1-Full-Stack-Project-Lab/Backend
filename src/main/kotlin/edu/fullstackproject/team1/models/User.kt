package edu.fullstackproject.team1.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.security.core.userdetails.UserDetails
import java.time.Instant

@Entity
@Table(name = "users")
data class User(
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	val id: Long? = null,
	@Column(unique = true, nullable = false)
	val email: String,
	@Column(nullable = false)
	var firstName: String,
	@Column(nullable = false)
	var lastName: String,
	@Column(nullable = false)
	private val password: String,
	@CreationTimestamp
	@Column(nullable = false)
	val createdAt: Instant? = null,
	@UpdateTimestamp
	@Column(nullable = false)
	val updatedAt: Instant? = null,
) : UserDetails {
	override fun getAuthorities() = emptyList<Nothing>()

	override fun getPassword() = password

	override fun getUsername() = email

	override fun isAccountNonExpired() = true

	override fun isAccountNonLocked() = true

	override fun isCredentialsNonExpired() = true

	override fun isEnabled() = true
}
