package edu.fullstackproject.team1.services

import edu.fullstackproject.team1.dtos.commands.StayUnitCreateCommand
import edu.fullstackproject.team1.dtos.commands.StayUnitUpdateCommand
import edu.fullstackproject.team1.mappers.StayUnitMapper
import edu.fullstackproject.team1.models.*
import edu.fullstackproject.team1.repositories.StayRepository
import edu.fullstackproject.team1.repositories.StayUnitRepository
import edu.fullstackproject.team1.repositories.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.util.*

class StayUnitServiceTest : DescribeSpec({
	val stayUnitRepository = mockk<StayUnitRepository>()
	val stayRepository = mockk<StayRepository>()
	val userRepository = mockk<UserRepository>()
	val stayUnitMapper = mockk<StayUnitMapper>()

	val stayUnitService = StayUnitService(
		stayUnitRepository,
		stayRepository,
		userRepository,
		stayUnitMapper
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
		name = "Hotel Co",
		email = "company@example.com",
		phone = "555-1234",
		description = "A hotel company"
	)

	val stay = Stay(
		id = 1L,
		city = mockk(),
		stayType = mockk(),
		company = company,
		name = "Grand Hotel",
		address = "123 Main St",
		latitude = 37.7749,
		longitude = -122.4194,
		description = "Nice hotel"
	)

	val stayUnit = StayUnit(
		id = 1L,
		stay = stay,
		stayNumber = "101",
		numberOfBeds = 2,
		capacity = 4,
		pricePerNight = 100.0,
		roomType = "Deluxe"
	)

	afterEach {
		clearAllMocks()
	}

	describe("getStayUnitsByStayId") {
		context("when getting units for a stay") {
			it("should return list of stay units") {
				val units = listOf(stayUnit)
				every { stayUnitRepository.findByStayIdWithStay(1L) } returns units

				val result = stayUnitService.getStayUnitsByStayId(1L)

				result.size shouldBe 1
			}
		}
	}

	describe("getStayUnitById") {
		context("when unit exists") {
			it("should return the unit") {
				every { stayUnitRepository.findById(1L) } returns Optional.of(stayUnit)

				val result = stayUnitService.getStayUnitById(1L)

				result shouldBe stayUnit
			}
		}

		context("when unit does not exist") {
			it("should throw ResponseStatusException") {
				every { stayUnitRepository.findById(999L) } returns Optional.empty()

				val exception = shouldThrow<ResponseStatusException> {
					stayUnitService.getStayUnitById(999L)
				}
				exception.statusCode shouldBe HttpStatus.NOT_FOUND
			}
		}
	}

	describe("createStayUnit") {
		val command = StayUnitCreateCommand(
			stayId = 1L,
			stayNumber = "102",
			numberOfBeds = 1,
			capacity = 2,
			pricePerNight = 80.0,
			roomType = "Standard"
		)

		context("when user owns the stay") {
			it("should create and return the unit") {
				val newUnit = stayUnit.copy(id = null, stayNumber = "102")
				val savedUnit = newUnit.copy(id = 2L)

				every { stayRepository.findById(1L) } returns Optional.of(stay)
				every { stayUnitMapper.toEntity(command, stay) } returns newUnit
				every { stayUnitRepository.save(newUnit) } returns savedUnit

				val result = stayUnitService.createStayUnit("host@example.com", command)

				result shouldBe savedUnit
			}
		}

		context("when user does not own the stay") {
			it("should throw ResponseStatusException with FORBIDDEN") {
				val otherUser = user.copy(id = 2L, email = "other@example.com")
				val otherCompany = company.copy(user = otherUser)
				val otherStay = stay.copy(company = otherCompany)

				every { stayRepository.findById(1L) } returns Optional.of(otherStay)

				val exception = shouldThrow<ResponseStatusException> {
					stayUnitService.createStayUnit("host@example.com", command)
				}
				exception.statusCode shouldBe HttpStatus.FORBIDDEN
			}
		}

		context("when stay does not exist") {
			it("should throw ResponseStatusException with NOT_FOUND") {
				every { stayRepository.findById(999L) } returns Optional.empty()

				val exception = shouldThrow<ResponseStatusException> {
					stayUnitService.createStayUnit("host@example.com", command.copy(stayId = 999L))
				}
				exception.statusCode shouldBe HttpStatus.NOT_FOUND
			}
		}
	}

	describe("updateStayUnit") {
		val command = StayUnitUpdateCommand(
			stayNumber = null,
			numberOfBeds = null,
			capacity = null,
			pricePerNight = 120.0,
			roomType = null
		)

		context("when user owns the stay") {
			it("should update and return the unit") {
				val updatedUnit = stayUnit.copy(pricePerNight = 120.0)

				every { stayUnitRepository.findById(1L) } returns Optional.of(stayUnit)
				every { stayUnitRepository.save(any<StayUnit>()) } returns updatedUnit

				val result = stayUnitService.updateStayUnit("host@example.com", 1L, command)

				result.pricePerNight shouldBe 120.0
			}
		}

		context("when user does not own the stay") {
			it("should throw ResponseStatusException with FORBIDDEN") {
				val otherUser = user.copy(id = 2L, email = "other@example.com")
				val otherCompany = company.copy(user = otherUser)
				val otherStay = stay.copy(company = otherCompany)
				val otherUnit = stayUnit.copy(stay = otherStay)

				every { stayUnitRepository.findById(1L) } returns Optional.of(otherUnit)

				val exception = shouldThrow<ResponseStatusException> {
					stayUnitService.updateStayUnit("host@example.com", 1L, command)
				}
				exception.statusCode shouldBe HttpStatus.FORBIDDEN
			}
		}
	}

	describe("deleteStayUnit") {
		context("when user owns the stay") {
			it("should delete the unit") {
				every { stayUnitRepository.findById(1L) } returns Optional.of(stayUnit)
				every { stayUnitRepository.delete(stayUnit) } just Runs

				stayUnitService.deleteStayUnit("host@example.com", 1L)

				verify(exactly = 1) { stayUnitRepository.delete(stayUnit) }
			}
		}

		context("when user does not own the stay") {
			it("should throw ResponseStatusException with FORBIDDEN") {
				val otherUser = user.copy(id = 2L, email = "other@example.com")
				val otherCompany = company.copy(user = otherUser)
				val otherStay = stay.copy(company = otherCompany)
				val otherUnit = stayUnit.copy(stay = otherStay)

				every { stayUnitRepository.findById(1L) } returns Optional.of(otherUnit)

				val exception = shouldThrow<ResponseStatusException> {
					stayUnitService.deleteStayUnit("host@example.com", 1L)
				}
				exception.statusCode shouldBe HttpStatus.FORBIDDEN
			}
		}
	}
})
