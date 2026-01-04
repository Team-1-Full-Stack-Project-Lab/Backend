package edu.fullstackproject.team1.services

import edu.fullstackproject.team1.dtos.commands.AddStayUnitToTripCommand
import edu.fullstackproject.team1.mappers.TripStayUnitMapper
import edu.fullstackproject.team1.models.*
import edu.fullstackproject.team1.repositories.StayUnitRepository
import edu.fullstackproject.team1.repositories.TripRepository
import edu.fullstackproject.team1.repositories.TripStayUnitRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate
import java.util.*

class TripStayUnitServiceTest : DescribeSpec({
	val tripStayUnitRepository = mockk<TripStayUnitRepository>()
	val tripRepository = mockk<TripRepository>()
	val stayUnitRepository = mockk<StayUnitRepository>()
	val tripStayUnitMapper = mockk<TripStayUnitMapper>()

	val tripStayUnitService = TripStayUnitService(
		tripStayUnitRepository,
		tripRepository,
		stayUnitRepository,
		tripStayUnitMapper
	)

	val user = User(
		id = 1L,
		email = "test@example.com",
		firstName = "John",
		lastName = "Doe",
		password = "hashedPassword"
	)

	val trip = Trip(
		id = 1L,
		user = user,
		city = mockk(),
		name = "Summer Vacation",
		startDate = LocalDate.of(2026, 6, 1),
		finishDate = LocalDate.of(2026, 6, 7)
	)

	val stayUnit = StayUnit(
		id = 1L,
		stay = mockk(),
		stayNumber = "101",
		numberOfBeds = 2,
		capacity = 4,
		pricePerNight = 100.0,
		roomType = "Deluxe"
	)

	val tripStayUnit = TripStayUnit(
		trip = trip,
		stayUnit = stayUnit,
		startDate = LocalDate.of(2026, 6, 2),
		endDate = LocalDate.of(2026, 6, 5)
	)

	afterEach {
		clearAllMocks()
	}

	describe("addStayUnitToTrip") {
		val command = AddStayUnitToTripCommand(
			tripId = 1L,
			stayUnitId = 1L,
			startDate = LocalDate.of(2026, 6, 2),
			endDate = LocalDate.of(2026, 6, 5)
		)

		context("when user owns trip and unit is available") {
			it("should add unit to trip") {
				every { tripRepository.findByIdWithCityAndCountry(1L) } returns trip
				every { stayUnitRepository.findById(1L) } returns Optional.of(stayUnit)
				every {
					tripStayUnitRepository.isStayUnitReserved(
						1L,
						command.startDate,
						command.endDate
					)
				} returns false
				every { tripStayUnitMapper.toEntity(command, trip, stayUnit) } returns tripStayUnit
				every { tripStayUnitRepository.save(tripStayUnit) } returns tripStayUnit

				val result = tripStayUnitService.addStayUnitToTrip("test@example.com", command)

				result shouldBe tripStayUnit
			}
		}

		context("when trip does not exist") {
			it("should throw ResponseStatusException with NOT_FOUND") {
				every { tripRepository.findByIdWithCityAndCountry(999L) } returns null

				val exception = shouldThrow<ResponseStatusException> {
					tripStayUnitService.addStayUnitToTrip("test@example.com", command.copy(tripId = 999L))
				}
				exception.statusCode shouldBe HttpStatus.NOT_FOUND
			}
		}

		context("when user does not own the trip") {
			it("should throw ResponseStatusException with FORBIDDEN") {
				val otherUser = user.copy(id = 2L, email = "other@example.com")
				val otherTrip = trip.copy(user = otherUser)
				every { tripRepository.findByIdWithCityAndCountry(1L) } returns otherTrip

				val exception = shouldThrow<ResponseStatusException> {
					tripStayUnitService.addStayUnitToTrip("test@example.com", command)
				}
				exception.statusCode shouldBe HttpStatus.FORBIDDEN
			}
		}

		context("when stay unit does not exist") {
			it("should throw ResponseStatusException with NOT_FOUND") {
				every { tripRepository.findByIdWithCityAndCountry(1L) } returns trip
				every { stayUnitRepository.findById(999L) } returns Optional.empty()

				val exception = shouldThrow<ResponseStatusException> {
					tripStayUnitService.addStayUnitToTrip("test@example.com", command.copy(stayUnitId = 999L))
				}
				exception.statusCode shouldBe HttpStatus.NOT_FOUND
			}
		}

		context("when stay unit is already reserved") {
			it("should throw ResponseStatusException with BAD_REQUEST") {
				every { tripRepository.findByIdWithCityAndCountry(1L) } returns trip
				every { stayUnitRepository.findById(1L) } returns Optional.of(stayUnit)
				every {
					tripStayUnitRepository.isStayUnitReserved(1L, command.startDate, command.endDate)
				} returns true

				val exception = shouldThrow<ResponseStatusException> {
					tripStayUnitService.addStayUnitToTrip("test@example.com", command)
				}
				exception.statusCode shouldBe HttpStatus.BAD_REQUEST
				exception.reason shouldBe "StayUnit is not available for these dates"
			}
		}
	}

	describe("getStayUnitsForTrip") {
		context("when user owns the trip") {
			it("should return list of stay units") {
				val units = listOf(tripStayUnit)
				every { tripRepository.findByIdWithUser(1L) } returns trip
				every { tripStayUnitRepository.findByTripIdWithRelations(1L) } returns units

				val result = tripStayUnitService.getStayUnitsForTrip("test@example.com", 1L)

				result.size shouldBe 1
			}
		}

		context("when trip does not exist") {
			it("should throw ResponseStatusException with NOT_FOUND") {
				every { tripRepository.findByIdWithUser(999L) } returns null

				val exception = shouldThrow<ResponseStatusException> {
					tripStayUnitService.getStayUnitsForTrip("test@example.com", 999L)
				}
				exception.statusCode shouldBe HttpStatus.NOT_FOUND
			}
		}

		context("when user does not own the trip") {
			it("should throw ResponseStatusException with FORBIDDEN") {
				val otherUser = user.copy(id = 2L, email = "other@example.com")
				val otherTrip = trip.copy(user = otherUser)
				every { tripRepository.findByIdWithUser(1L) } returns otherTrip

				val exception = shouldThrow<ResponseStatusException> {
					tripStayUnitService.getStayUnitsForTrip("test@example.com", 1L)
				}
				exception.statusCode shouldBe HttpStatus.FORBIDDEN
			}
		}
	}
})
