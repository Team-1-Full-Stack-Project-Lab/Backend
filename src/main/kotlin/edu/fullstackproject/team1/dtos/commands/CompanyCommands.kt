package edu.fullstackproject.team1.dtos.commands

data class CompanyCreateCommand(
	val name: String,
	val email: String,
	val phone: String?,
	val description: String?,
)

data class CompanyUpdateCommand(
	val name: String?,
	val email: String?,
	val phone: String?,
	val description: String?,
)
