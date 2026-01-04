package edu.fullstackproject.team1.services

import edu.fullstackproject.team1.dtos.commands.CompanyCreateCommand
import edu.fullstackproject.team1.dtos.commands.CompanyUpdateCommand
import edu.fullstackproject.team1.models.Company
import edu.fullstackproject.team1.models.User
import edu.fullstackproject.team1.repositories.CompanyRepository
import edu.fullstackproject.team1.repositories.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.util.*

class CompanyServiceTest : DescribeSpec({
	val companyRepository = mockk<CompanyRepository>()
	val userRepository = mockk<UserRepository>()

	val companyService = CompanyService(
		companyRepository,
		userRepository
	)

	val user = User(
		id = 1L,
		email = "host@example.com",
		firstName = "John",
		lastName = "Host",
		password = "hashedPassword"
	)

	val company = Company(
		id = 1L,
		user = user,
		name = "My Hotel Company",
		email = "company@example.com",
		phone = "555-1234",
		description = "A hotel company"
	)

	afterEach {
		clearAllMocks()
	}

	describe("createCompany") {
		val email = "host@example.com"
		val command = CompanyCreateCommand(
			name = "New Hotel Company",
			email = "newcompany@example.com",
			phone = "555-5678",
			description = "A new hotel company"
		)

		context("when user exists and doesn't have a company") {
			it("should create and return the company") {
				val newCompany = Company(
					id = null,
					user = user,
					name = command.name,
					email = command.email,
					phone = command.phone,
					description = command.description
				)
				val savedCompany = newCompany.copy(id = 1L)

				every { userRepository.findByEmail(email) } returns user
				every { companyRepository.existsByUserId(user.id!!) } returns false
				every { companyRepository.save(any<Company>()) } returns savedCompany

				val result = companyService.createCompany(email, command)

				result shouldBe savedCompany
				result.name shouldBe command.name
				result.email shouldBe command.email
				verify(exactly = 1) { userRepository.findByEmail(email) }
				verify(exactly = 1) { companyRepository.existsByUserId(user.id!!) }
				verify(exactly = 1) { companyRepository.save(any<Company>()) }
			}
		}

		context("when user does not exist") {
			it("should throw ResponseStatusException with NOT_FOUND status") {
				every { userRepository.findByEmail(email) } returns null

				val exception = shouldThrow<ResponseStatusException> {
					companyService.createCompany(email, command)
				}
				exception.statusCode shouldBe HttpStatus.NOT_FOUND
				exception.reason shouldBe "User not found"
			}
		}

		context("when user already has a company") {
			it("should throw ResponseStatusException with BAD_REQUEST status") {
				every { userRepository.findByEmail(email) } returns user
				every { companyRepository.existsByUserId(user.id!!) } returns true

				val exception = shouldThrow<ResponseStatusException> {
					companyService.createCompany(email, command)
				}
				exception.statusCode shouldBe HttpStatus.BAD_REQUEST
				exception.reason shouldBe "User already has a company"
			}
		}
	}

	describe("getCompanyByUser") {
		val email = "host@example.com"

		context("when user and company exist") {
			it("should return the company") {
				every { userRepository.findByEmail(email) } returns user
				every { companyRepository.findByUserId(user.id!!) } returns company

				val result = companyService.getCompanyByUser(email)

				result shouldBe company
				verify(exactly = 1) { userRepository.findByEmail(email) }
				verify(exactly = 1) { companyRepository.findByUserId(user.id!!) }
			}
		}

		context("when user does not exist") {
			it("should throw ResponseStatusException with NOT_FOUND status") {
				every { userRepository.findByEmail(email) } returns null

				val exception = shouldThrow<ResponseStatusException> {
					companyService.getCompanyByUser(email)
				}
				exception.statusCode shouldBe HttpStatus.NOT_FOUND
				exception.reason shouldBe "User not found"
			}
		}

		context("when company does not exist for user") {
			it("should throw ResponseStatusException with NOT_FOUND status") {
				every { userRepository.findByEmail(email) } returns user
				every { companyRepository.findByUserId(user.id!!) } returns null

				val exception = shouldThrow<ResponseStatusException> {
					companyService.getCompanyByUser(email)
				}
				exception.statusCode shouldBe HttpStatus.NOT_FOUND
				exception.reason shouldBe "Company not found for this user"
			}
		}
	}

	describe("getCompanyById") {
		val companyId = 1L

		context("when company exists") {
			it("should return the company") {
				every { companyRepository.findById(companyId) } returns Optional.of(company)

				val result = companyService.getCompanyById(companyId)

				result shouldBe company
				verify(exactly = 1) { companyRepository.findById(companyId) }
			}
		}

		context("when company does not exist") {
			it("should throw ResponseStatusException with NOT_FOUND status") {
				every { companyRepository.findById(companyId) } returns Optional.empty()

				val exception = shouldThrow<ResponseStatusException> {
					companyService.getCompanyById(companyId)
				}
				exception.statusCode shouldBe HttpStatus.NOT_FOUND
				exception.reason shouldBe "Company not found"
			}
		}
	}

	describe("getAllCompanies") {
		context("when companies exist") {
			it("should return list of all companies") {
				val companies = listOf(
					company,
					company.copy(id = 2L, name = "Another Company")
				)
				every { companyRepository.findAll() } returns companies

				val result = companyService.getAllCompanies()

				result.size shouldBe 2
				verify(exactly = 1) { companyRepository.findAll() }
			}
		}
	}

	describe("updateCompany") {
		val email = "host@example.com"
		val companyId = 1L
		val command = CompanyUpdateCommand(
			name = "Updated Company Name",
			email = "updated@example.com",
			phone = "555-9999",
			description = "Updated description"
		)

		context("when user owns the company") {
			it("should update and return the company") {
				val mutableCompany = company.copy()
				every { companyRepository.findById(companyId) } returns Optional.of(mutableCompany)
				every { companyRepository.save(any<Company>()) } answers {
					val savedCompany = firstArg<Company>()
					savedCompany
				}

				val result = companyService.updateCompany(email, companyId, command)

				result.name shouldBe command.name
				result.email shouldBe command.email
				result.phone shouldBe command.phone
				result.description shouldBe command.description
				verify(exactly = 1) { companyRepository.findById(companyId) }
				verify(exactly = 1) { companyRepository.save(any<Company>()) }
			}
		}

		context("when updating only some fields") {
			it("should update only provided fields") {
				val partialCommand = CompanyUpdateCommand(
					name = "New Name Only",
					email = null,
					phone = null,
					description = null
				)
				val mutableCompany = company.copy()

				every { companyRepository.findById(companyId) } returns Optional.of(mutableCompany)
				every { companyRepository.save(any<Company>()) } answers {
					firstArg<Company>()
				}

				val result = companyService.updateCompany(email, companyId, partialCommand)

				result.name shouldBe "New Name Only"
				result.email shouldBe company.email // Unchanged
			}
		}

		context("when company does not exist") {
			it("should throw ResponseStatusException with NOT_FOUND status") {
				every { companyRepository.findById(companyId) } returns Optional.empty()

				val exception = shouldThrow<ResponseStatusException> {
					companyService.updateCompany(email, companyId, command)
				}
				exception.statusCode shouldBe HttpStatus.NOT_FOUND
				exception.reason shouldBe "Company not found"
			}
		}

		context("when user does not own the company") {
			it("should throw ResponseStatusException with FORBIDDEN status") {
				val otherUser = user.copy(id = 2L, email = "other@example.com")
				val companyWithOtherUser = company.copy(user = otherUser)

				every { companyRepository.findById(companyId) } returns Optional.of(companyWithOtherUser)

				val exception = shouldThrow<ResponseStatusException> {
					companyService.updateCompany(email, companyId, command)
				}
				exception.statusCode shouldBe HttpStatus.FORBIDDEN
				exception.reason shouldBe "Not allowed to update this company"
			}
		}
	}

	describe("deleteCompany") {
		val email = "host@example.com"
		val companyId = 1L

		context("when user owns the company") {
			it("should delete the company") {
				every { companyRepository.findById(companyId) } returns Optional.of(company)
				every { companyRepository.delete(company) } just Runs

				companyService.deleteCompany(email, companyId)

				verify(exactly = 1) { companyRepository.findById(companyId) }
				verify(exactly = 1) { companyRepository.delete(company) }
			}
		}

		context("when company does not exist") {
			it("should throw ResponseStatusException with NOT_FOUND status") {
				every { companyRepository.findById(companyId) } returns Optional.empty()

				val exception = shouldThrow<ResponseStatusException> {
					companyService.deleteCompany(email, companyId)
				}
				exception.statusCode shouldBe HttpStatus.NOT_FOUND
				exception.reason shouldBe "Company not found"
			}
		}

		context("when user does not own the company") {
			it("should throw ResponseStatusException with FORBIDDEN status") {
				val otherUser = user.copy(id = 2L, email = "other@example.com")
				val companyWithOtherUser = company.copy(user = otherUser)

				every { companyRepository.findById(companyId) } returns Optional.of(companyWithOtherUser)

				val exception = shouldThrow<ResponseStatusException> {
					companyService.deleteCompany(email, companyId)
				}
				exception.statusCode shouldBe HttpStatus.FORBIDDEN
				exception.reason shouldBe "Not allowed to delete this company"
			}
		}
	}
})
