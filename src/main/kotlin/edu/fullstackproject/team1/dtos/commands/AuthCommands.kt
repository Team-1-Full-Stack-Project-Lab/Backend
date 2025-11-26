package edu.fullstackproject.team1.dtos.commands

data class LoginCommand(
	val email: String,
	val password: String,
)

data class RegisterCommand(
	val email: String,
	val firstName: String,
	val lastName: String,
	val password: String,
)
