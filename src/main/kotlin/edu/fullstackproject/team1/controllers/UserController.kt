package edu.fullstackproject.team1.controllers

import edu.fullstackproject.team1.dtos.UserProfileDto
import edu.fullstackproject.team1.dtos.UserUpdateRequestDto
import edu.fullstackproject.team1.repositories.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = ["https://frontend-sable-three-71.vercel.app"])
class UserController(
	private val userRepository: UserRepository
) {

	@GetMapping("/profile/{id}")
	fun getUserProfileById(@PathVariable id: Long): ResponseEntity<UserProfileDto> {
		val user = userRepository.findById(id)
			.orElseThrow { RuntimeException("User not found with id: $id") }

		val userProfileDto = UserProfileDto(
            firstName = user.firstName,
            lastName = user.lastName,
            email = user.email
        )
		return ResponseEntity.ok(userProfileDto)
	}

	@DeleteMapping("/profile/{id}")
	fun deleteUserById(@PathVariable id: Long): ResponseEntity<Void> {
		userRepository.deleteById(id)
		return ResponseEntity.noContent().build()
	}

	@PutMapping("/profile/edit/{id}")
	fun updateUserById(@PathVariable id: Long, @RequestBody request: UserUpdateRequestDto): ResponseEntity<Void> {
		val existingUser = userRepository.findById(id)
			.orElseThrow { RuntimeException("User not found with id: $id") }
		val updatedUser = existingUser.copy(
			firstName = request.firstName,
			lastName = request.lastName
		)
		userRepository.save(updatedUser)
		return ResponseEntity.status(HttpStatus.ACCEPTED).build()
	}
}
