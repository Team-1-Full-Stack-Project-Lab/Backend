package edu.fullstackproject.team1.controllers

import edu.fullstackproject.team1.dtos.AuthResponse
import edu.fullstackproject.team1.dtos.LoginRequest
import edu.fullstackproject.team1.dtos.RegisterRequest
import edu.fullstackproject.team1.services.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "User authentication and registration endpoints")
class AuthController(
	private val authService: AuthService,
) {
	@PostMapping("/login")
	@Operation(
		summary = "User login",
		description = "Authenticate a user with email and password, returns a JWT token",
	)
	@ApiResponses(
		value =
			[
				ApiResponse(
					responseCode = "200",
					description = "Login successful",
					content =
						[
							Content(
								schema =
									Schema(
										implementation =
											AuthResponse::class,
									),
							),
						],
				),
				ApiResponse(
					responseCode = "400",
					description = "Invalid credentials",
					content = [Content()],
				),
			],
	)
	fun login(
		@RequestBody @Valid request: LoginRequest,
	): ResponseEntity<AuthResponse> {
		val response = authService.login(request)
		return ResponseEntity.ok(response)
	}

	@PostMapping("/register")
	@Operation(
		summary = "User registration",
		description = "Register a new user account, returns a JWT token",
	)
	@ApiResponses(
		value =
			[
				ApiResponse(
					responseCode = "201",
					description = "User created successfully",
					content =
						[
							Content(
								schema =
									Schema(
										implementation =
											AuthResponse::class,
									),
							),
						],
				),
				ApiResponse(
					responseCode = "400",
					description = "Invalid input data",
					content = [Content()],
				),
			],
	)
	fun register(
		@RequestBody @Valid request: RegisterRequest,
	): ResponseEntity<AuthResponse> {
		val response = authService.register(request)
		return ResponseEntity.status(HttpStatus.CREATED).body(response)
	}
}
