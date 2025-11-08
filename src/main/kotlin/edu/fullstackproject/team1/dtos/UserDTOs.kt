package edu.fullstackproject.team1.dtos

data class UserUpdateRequest(
	val email: String,
	val firstName: String,
	val lastName: String,
)

data class UserResponse(
	val email: String,
	val firstName: String,
	val lastName: String,
)
