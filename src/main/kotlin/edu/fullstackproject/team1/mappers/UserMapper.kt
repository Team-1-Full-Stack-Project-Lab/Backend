package edu.fullstackproject.team1.mappers

import edu.fullstackproject.team1.dtos.responses.UserResponse
import edu.fullstackproject.team1.models.User
import org.springframework.stereotype.Component

@Component
class UserMapper {
	fun toResponse(user: User): UserResponse {
		return UserResponse(
			email = user.email,
			firstName = user.firstName,
			lastName = user.lastName,
		)
	}
}
