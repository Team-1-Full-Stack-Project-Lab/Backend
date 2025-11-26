package edu.fullstackproject.team1.services

import edu.fullstackproject.team1.dtos.commands.UserUpdateCommands
import edu.fullstackproject.team1.models.User
import edu.fullstackproject.team1.repositories.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class UserService(
	private val userRepository: UserRepository,
) {
	fun getUserByEmail(email: String): User {
		return userRepository.findByEmail(email)
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
	}

	fun updateUser(email: String, command: UserUpdateCommands): User {
		val existingUser = userRepository.findByEmail(email)
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
		val user = existingUser.copy(
			firstName = command.firstName ?: existingUser.firstName,
			lastName = command.lastName ?: existingUser.lastName,
		)
		return userRepository.save(user)
	}

	fun deleteUser(email: String) {
		val user = userRepository.findByEmail(email)
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
		userRepository.delete(user)
	}
}
