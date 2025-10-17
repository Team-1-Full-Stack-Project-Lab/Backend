package edu.fullstackproject.team1.services

import edu.fullstackproject.team1.dtos.UserResponse
import edu.fullstackproject.team1.repositories.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class UserService(
	private val userRepository: UserRepository
) {
	fun getUserByEmail(email: String): UserResponse {
		val user = userRepository.findByEmail(email)
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
		return UserResponse(
			email = user.email,
			firstName = user.firstName,
			lastName = user.lastName
		)
	}

	fun updateUser(email: String, firstName: String, lastName: String): UserResponse {
		val existingUser = userRepository.findByEmail(email)
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
		val user = existingUser.copy(
			firstName = firstName,
			lastName = lastName
		)
		val updatedUser = userRepository.save(user)
		return UserResponse(
			email = updatedUser.email,
			firstName = updatedUser.firstName,
			lastName = updatedUser.lastName
		)
	}

	fun deleteUser(email: String) {
		val user = userRepository.findByEmail(email)
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
		userRepository.delete(user)
	}
}
