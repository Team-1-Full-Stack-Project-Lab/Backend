package edu.fullstackproject.team1.services

import edu.fullstackproject.team1.models.User
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldNotBeEmpty
import org.springframework.test.util.ReflectionTestUtils

class JwtServiceTest : DescribeSpec({
	lateinit var jwtService: JwtService

	beforeEach {
		jwtService = JwtService()
		// Set test values using reflection
		ReflectionTestUtils.setField(jwtService, "secret", "test-secret-key-12345678901234567890123456789012")
		ReflectionTestUtils.setField(jwtService, "expiration", 3600000L) // 1 hour
	}

	describe("generateToken") {
		context("when generating a token for a user") {
			it("should return a valid JWT token string") {
				val user = User(
					id = 1L,
					email = "test@example.com",
					firstName = "John",
					lastName = "Doe",
					password = "hashedPassword"
				)

				val token = jwtService.generateToken(user)

				token.shouldNotBeEmpty()
				token.split(".").size shouldBe 3 // JWT has 3 parts: header.payload.signature
			}
		}
	}

	describe("extractUsername") {
		context("when extracting username from a valid token") {
			it("should return the correct username (email)") {
				val user = User(
					id = 1L,
					email = "test@example.com",
					firstName = "John",
					lastName = "Doe",
					password = "hashedPassword"
				)
				val token = jwtService.generateToken(user)

				val extractedUsername = jwtService.extractUsername(token)

				extractedUsername shouldBe "test@example.com"
			}
		}
	}

	describe("validateToken") {
		context("when token is valid and belongs to the user") {
			it("should return true") {
				val user = User(
					id = 1L,
					email = "test@example.com",
					firstName = "John",
					lastName = "Doe",
					password = "hashedPassword"
				)
				val token = jwtService.generateToken(user)

				val isValid = jwtService.validateToken(token, user)

				isValid shouldBe true
			}
		}

		context("when token belongs to a different user") {
			it("should return false") {
				val user1 = User(
					id = 1L,
					email = "user1@example.com",
					firstName = "User",
					lastName = "One",
					password = "hashedPassword"
				)
				val user2 = User(
					id = 2L,
					email = "user2@example.com",
					firstName = "User",
					lastName = "Two",
					password = "hashedPassword"
				)
				val token = jwtService.generateToken(user1)

				val isValid = jwtService.validateToken(token, user2)

				isValid shouldBe false
			}
		}

		context("when token is expired") {
			it("should throw ExpiredJwtException") {
				val expiredJwtService = JwtService()
				ReflectionTestUtils.setField(
					expiredJwtService,
					"secret",
					"test-secret-key-12345678901234567890123456789012"
				)
				ReflectionTestUtils.setField(expiredJwtService, "expiration", -1000L) // Negative = expired

				val user = User(
					id = 1L,
					email = "test@example.com",
					firstName = "John",
					lastName = "Doe",
					password = "hashedPassword"
				)

				// Wait a moment to ensure token is expired
				val token = expiredJwtService.generateToken(user)
				Thread.sleep(100)

				shouldThrow<io.jsonwebtoken.ExpiredJwtException> {
					expiredJwtService.validateToken(token, user)
				}
			}
		}
	}

	describe("token lifecycle") {
		context("when creating and validating multiple tokens") {
			it("should handle multiple users correctly") {
				val user1 = User(
					id = 1L,
					email = "user1@example.com",
					firstName = "User",
					lastName = "One",
					password = "hashedPassword"
				)
				val user2 = User(
					id = 2L,
					email = "user2@example.com",
					firstName = "User",
					lastName = "Two",
					password = "hashedPassword"
				)

				val token1 = jwtService.generateToken(user1)
				val token2 = jwtService.generateToken(user2)

				token1 shouldNotBe token2
				jwtService.validateToken(token1, user1) shouldBe true
				jwtService.validateToken(token2, user2) shouldBe true
				jwtService.validateToken(token1, user2) shouldBe false
				jwtService.validateToken(token2, user1) shouldBe false
				jwtService.extractUsername(token1) shouldBe "user1@example.com"
				jwtService.extractUsername(token2) shouldBe "user2@example.com"
			}
		}
	}
})
