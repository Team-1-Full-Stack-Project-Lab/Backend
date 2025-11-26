package edu.fullstackproject.team1.dtos.requests

import edu.fullstackproject.team1.dtos.commands.UserUpdateCommands

data class UserUpdateRequest(
	val firstName: String?,
	val lastName: String?,
) {
	fun toCommand() = UserUpdateCommands(
		firstName = firstName,
		lastName = lastName,
	)
}
