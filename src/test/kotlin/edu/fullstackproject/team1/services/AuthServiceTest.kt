package edu.fullstackproject.team1.services

import edu.fullstackproject.team1.dtos.commands.LoginCommand
import edu.fullstackproject.team1.dtos.commands.RegisterCommand
import edu.fullstackproject.team1.mappers.AuthMapper
import edu.fullstackproject.team1.models.User
import edu.fullstackproject.team1.repositories.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.server.ResponseStatusException

class AuthServiceTest : DescribeSpec({
	val userRepository = mockk<UserRepository>()
	val passwordEncoder = mockk<PasswordEncoder>()
	val jwtService = mockk<JwtService>()
	val authenticationManager = mockk<AuthenticationManager>()
	val authMapper = mockk<AuthMapper>()

	val authService = AuthService(
		userRepository,
		passwordEncoder,
		jwtService,
		authenticationManager,
		authMapper
	)

	afterEach {
		clearAllMocks()
	}

	describe("login") {
		val email = "test@example.com"
		val password = "password123"
		val command = LoginCommand(email, password)

		context("when credentials are valid") {
			it("should authenticate and return JWT token") {
				val user = User(
					id = 1L,
					email = email,
					firstName = "John",
					lastName = "Doe",
					password = "encodedPassword"
				)
				val token = "jwt.token.here"

				every {
					authenticationManager.authenticate(
						UsernamePasswordAuthenticationToken(email, password)
					)
				} returns mockk()

				every { userRepository.findByEmail(email) } returns user
				every { jwtService.generateToken(user) } returns token

				val result = authService.login(command)

				result shouldBe token
				verify(exactly = 1) { authenticationManager.authenticate(any()) }
				verify(exactly = 1) { userRepository.findByEmail(email) }
				verify(exactly = 1) { jwtService.generateToken(user) }
			}
		}

		context("when credentials are invalid") {
			it("should throw BadCredentialsException") {
				every {
					authenticationManager.authenticate(any())
				} throws BadCredentialsException("Invalid credentials")

				// When & Then
				shouldThrow<BadCredentialsException> {
					authService.login(command)
				}
			}
		}

		context("when user is not found after authentication") {
			it("should throw BadCredentialsException") {
				every { authenticationManager.authenticate(any()) } returns mockk()
				every { userRepository.findByEmail(email) } returns null

				// When & Then
				shouldThrow<BadCredentialsException> {
					authService.login(command)
				}
			}
		}
	}

	describe("register") {
		val command = RegisterCommand(
			email = "newuser@example.com",
			firstName = "Jane",
			lastName = "Smith",
			password = "password123"
		)

		context("when email is not taken") {
			it("should create user and return JWT token") {
				val encodedPassword = "encodedPassword123"
				val user = User(
					id = null,
					email = command.email,
					firstName = command.firstName,
					lastName = command.lastName,
					password = encodedPassword
				)
				val savedUser = user.copy(id = 1L)
				val token = "jwt.token.here"

				every { userRepository.findByEmail(command.email) } returns null
				every { passwordEncoder.encode(command.password) } returns encodedPassword
				every { authMapper.toEntity(command, encodedPassword) } returns user
				every { userRepository.save(user) } returns savedUser
				every { jwtService.generateToken(savedUser) } returns token

				val result = authService.register(command)

				result shouldBe token
				verify(exactly = 1) { userRepository.findByEmail(command.email) }
				verify(exactly = 1) { passwordEncoder.encode(command.password) }
				verify(exactly = 1) { authMapper.toEntity(command, encodedPassword) }
				verify(exactly = 1) { userRepository.save(user) }
				verify(exactly = 1) { jwtService.generateToken(savedUser) }
			}
		}

		context("when email is already taken") {
			it("should throw ResponseStatusException with CONFLICT status") {
				val existingUser = User(
					id = 1L,
					email = command.email,
					firstName = "Existing",
					lastName = "User",
					password = "hashedPassword"
				)
				every { userRepository.findByEmail(command.email) } returns existingUser

				val exception = shouldThrow<ResponseStatusException> {
					authService.register(command)
				}
				exception.statusCode shouldBe HttpStatus.CONFLICT
				exception.reason shouldBe "A user with that email already exists"

				verify(exactly = 1) { userRepository.findByEmail(command.email) }
				verify(exactly = 0) { passwordEncoder.encode(any()) }
				verify(exactly = 0) { userRepository.save(any()) }
			}
		}
	}
})
