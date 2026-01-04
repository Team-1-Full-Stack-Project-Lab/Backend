package edu.fullstackproject.team1.services

import edu.fullstackproject.team1.dtos.commands.UserUpdateCommands
import edu.fullstackproject.team1.models.User
import edu.fullstackproject.team1.repositories.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class UserServiceTest : DescribeSpec({
	val userRepository = mockk<UserRepository>()
	val userService = UserService(userRepository)

	afterEach {
		clearAllMocks()
	}

	describe("getUserByEmail") {
		val email = "test@example.com"

		context("when user exists") {
			it("should return the user with company") {
				val user = User(
					id = 1L,
					email = email,
					firstName = "John",
					lastName = "Doe",
					password = "hashedPassword"
				)
				every { userRepository.findWithCompanyByEmail(email) } returns user

				val result = userService.getUserByEmail(email)

				result shouldBe user
				verify(exactly = 1) { userRepository.findWithCompanyByEmail(email) }
			}
		}

		context("when user does not exist") {
			it("should throw ResponseStatusException with NOT_FOUND status") {
				every { userRepository.findWithCompanyByEmail(email) } returns null

				val exception = shouldThrow<ResponseStatusException> {
					userService.getUserByEmail(email)
				}
				exception.statusCode shouldBe HttpStatus.NOT_FOUND
				exception.reason shouldBe "User not found"
			}
		}
	}

	describe("updateUser") {
		val email = "test@example.com"
		val command = UserUpdateCommands(
			firstName = "Jane",
			lastName = "Smith"
		)

		context("when user exists") {
			it("should update and return the user") {
				val existingUser = User(
					id = 1L,
					email = email,
					firstName = "John",
					lastName = "Doe",
					password = "hashedPassword"
				)
				val updatedUser = existingUser.copy(
					firstName = command.firstName!!,
					lastName = command.lastName!!
				)

				every { userRepository.findWithCompanyByEmail(email) } returns existingUser
				every { userRepository.save(any<User>()) } returns updatedUser

				val result = userService.updateUser(email, command)

				result.firstName shouldBe "Jane"
				result.lastName shouldBe "Smith"
				verify(exactly = 1) { userRepository.findWithCompanyByEmail(email) }
				verify(exactly = 1) { userRepository.save(any<User>()) }
			}
		}

		context("when only firstName is provided") {
			it("should update only firstName") {
				val partialCommand = UserUpdateCommands(firstName = "Jane", lastName = null)
				val existingUser = User(
					id = 1L,
					email = email,
					firstName = "John",
					lastName = "Doe",
					password = "hashedPassword"
				)
				val updatedUser = existingUser.copy(firstName = "Jane")

				every { userRepository.findWithCompanyByEmail(email) } returns existingUser
				every { userRepository.save(any<User>()) } returns updatedUser

				val result = userService.updateUser(email, partialCommand)

				result.firstName shouldBe "Jane"
				result.lastName shouldBe "Doe"
			}
		}

		context("when user does not exist") {
			it("should throw ResponseStatusException with NOT_FOUND status") {
				every { userRepository.findWithCompanyByEmail(email) } returns null

				val exception = shouldThrow<ResponseStatusException> {
					userService.updateUser(email, command)
				}
				exception.statusCode shouldBe HttpStatus.NOT_FOUND
				exception.reason shouldBe "User not found"
			}
		}
	}

	describe("deleteUser") {
		val email = "test@example.com"

		context("when user exists") {
			it("should delete the user") {
				val user = User(
					id = 1L,
					email = email,
					firstName = "John",
					lastName = "Doe",
					password = "hashedPassword"
				)
				every { userRepository.findByEmail(email) } returns user
				every { userRepository.delete(user) } just Runs

				userService.deleteUser(email)

				verify(exactly = 1) { userRepository.findByEmail(email) }
				verify(exactly = 1) { userRepository.delete(user) }
			}
		}

		context("when user does not exist") {
			it("should throw ResponseStatusException with NOT_FOUND status") {
				every { userRepository.findByEmail(email) } returns null

				val exception = shouldThrow<ResponseStatusException> {
					userService.deleteUser(email)
				}
				exception.statusCode shouldBe HttpStatus.NOT_FOUND
				exception.reason shouldBe "User not found"
			}
		}
	}
})
