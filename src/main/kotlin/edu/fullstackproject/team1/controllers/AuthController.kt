package edu.fullstackproject.team1.controllers

import edu.fullstackproject.team1.dtos.AuthResponse
import edu.fullstackproject.team1.dtos.LoginRequest
import edu.fullstackproject.team1.dtos.RegisterRequest
import edu.fullstackproject.team1.services.AuthService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
	private val authService: AuthService,
) {
	@PostMapping("/login")
	fun login(
		@RequestBody request: LoginRequest,
	): ResponseEntity<AuthResponse> {
		val response = authService.login(request)
		return ResponseEntity.ok(response)
	}

	@PostMapping("/register")
	fun register(
		@RequestBody request: RegisterRequest,
	): ResponseEntity<AuthResponse> {
		val response = authService.register(request)
		return ResponseEntity.status(HttpStatus.CREATED).body(response)
	}
}
