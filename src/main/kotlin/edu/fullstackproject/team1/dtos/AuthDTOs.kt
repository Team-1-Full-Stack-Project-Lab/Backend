package edu.fullstackproject.team1.dtos

data class LoginRequest(
	val email: String,
	val password: String
)

data class RegisterRequest(
	val email: String,
	val firstName: String,
	val lastName: String,
	val password: String
)

data class AuthResponse(
	val token: String
)
