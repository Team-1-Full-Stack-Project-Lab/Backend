package edu.fullstackproject.team1.services

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.Date
import javax.crypto.SecretKey

@Service
class JwtService {
	@Value("\${jwt.secret}")
	private lateinit var secret: String

	@Value("\${jwt.expiration}")
	private var expiration: Long = 0

	fun generateToken(user: UserDetails): String {
		val claims = mutableMapOf<String, Any>()
		val subject = user.username
		val now = Date()
		val expirationDate = Date(now.time + expiration)

		return Jwts
			.builder()
			.claims(claims)
			.subject(subject)
			.issuedAt(now)
			.expiration(expirationDate)
			.signWith(getSigningKey())
			.compact()
	}

	fun validateToken(
		token: String,
		user: UserDetails,
	): Boolean = (extractUsername(token) == user.username && !isTokenExpired(token))

	fun extractUsername(token: String): String = extractClaims(token).subject

	private fun getSigningKey(): SecretKey = Keys.hmacShaKeyFor(secret.toByteArray())

	private fun extractClaims(token: String): Claims =
		Jwts
			.parser()
			.verifyWith(getSigningKey())
			.build()
			.parseSignedClaims(token)
			.payload

	private fun isTokenExpired(token: String): Boolean {
		val claims = extractClaims(token)
		return claims.expiration.before(Date())
	}
}
