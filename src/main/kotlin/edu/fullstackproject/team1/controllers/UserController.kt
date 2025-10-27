package edu.fullstackproject.team1.controllers

import edu.fullstackproject.team1.dtos.UserResponse
import edu.fullstackproject.team1.dtos.UserUpdateRequest
import edu.fullstackproject.team1.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
class UserController(
	private val userService: UserService
) {

	@GetMapping("/profile")
	fun getUser(@AuthenticationPrincipal user: UserDetails): ResponseEntity<UserResponse> {
		val response = userService.getUserByEmail(user.username)

		return ResponseEntity.ok(response)
	}

	@PutMapping("/profile")
	fun updateUser(@AuthenticationPrincipal user: UserDetails, @RequestBody request: UserUpdateRequest): ResponseEntity<UserResponse> {
		val response = userService.updateUser(user.username, request.firstName, request.lastName)

		return ResponseEntity.ok(response)
	}

	@DeleteMapping("/profile")
	fun deleteUser(@AuthenticationPrincipal user: UserDetails): ResponseEntity<Void> {
		userService.deleteUser(user.username)

		return ResponseEntity.noContent().build()
	}
}
