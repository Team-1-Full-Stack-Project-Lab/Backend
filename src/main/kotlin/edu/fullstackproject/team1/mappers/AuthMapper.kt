package edu.fullstackproject.team1.mappers

import edu.fullstackproject.team1.dtos.commands.RegisterCommand
import edu.fullstackproject.team1.dtos.responses.AuthResponse
import edu.fullstackproject.team1.models.User
import org.springframework.stereotype.Component

@Component
class AuthMapper {
	fun toEntity(command: RegisterCommand, encodedPassword: String): User {
		return User(
			email = command.email,
			firstName = command.firstName,
			lastName = command.lastName,
			password = encodedPassword,
		)
	}

	fun toResponse(token: String): AuthResponse = AuthResponse(token = token)
}
