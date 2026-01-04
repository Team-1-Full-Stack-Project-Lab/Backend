package edu.fullstackproject.team1.services

import edu.fullstackproject.team1.dtos.commands.CreateTripCommand
import edu.fullstackproject.team1.dtos.commands.UpdateTripCommand
import edu.fullstackproject.team1.mappers.TripMapper
import edu.fullstackproject.team1.models.City
import edu.fullstackproject.team1.models.Country
import edu.fullstackproject.team1.models.State
import edu.fullstackproject.team1.models.Trip
import edu.fullstackproject.team1.models.User
import edu.fullstackproject.team1.repositories.CityRepository
import edu.fullstackproject.team1.repositories.TripRepository
import edu.fullstackproject.team1.repositories.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate
import java.util.*

class TripServiceTest : DescribeSpec({
	val tripRepository = mockk<TripRepository>()
	val userRepository = mockk<UserRepository>()
	val cityRepository = mockk<CityRepository>()
	val tripMapper = mockk<TripMapper>()

	val tripService = TripService(
		tripRepository,
		userRepository,
		cityRepository,
		tripMapper
	)

	val user = User(
		id = 1L,
		email = "test@example.com",
		firstName = "John",
		lastName = "Doe",
		password = "hashedPassword"
	)

	val country = Country(
		id = 1L,
		name = "United States",
		iso2Code = "US",
		iso3Code = "USA",
		phoneCode = "+1",
		currencyCode = "USD",
		currencySymbol = "$",
		region = null
	)

	val state = State(
		id = 1L,
		name = "California",
		code = "CA",
		latitude = 36.7783,
		longitude = -119.4179,
		country = country
	)

	val city = City(
		id = 1L,
		name = "San Francisco",
		nameAscii = "San Francisco",
		latitude = 37.7749,
		longitude = -122.4194,
		timezone = "America/Los_Angeles",
		googlePlaceId = "ChIJIQBpAG2ahYAR_6128GcTUEo",
		population = 873965,
		isCapital = false,
		isFeatured = true,
		country = country,
		state = state
	)

	afterEach {
		clearAllMocks()
	}

	describe("createTrip") {
		val email = "test@example.com"
		val command = CreateTripCommand(
			name = "Trip to SF",
			cityId = 1L,
			startDate = LocalDate.of(2026, 6, 1),
			endDate = LocalDate.of(2026, 6, 7)
		)

		context("when user and city exist") {
			it("should create and return the trip") {
				val trip = Trip(
					id = null,
					user = user,
					city = city,
					name = command.name ?: "Trip to SF",
					startDate = command.startDate!!,
					finishDate = command.endDate!!
				)
				val savedTrip = trip.copy(id = 1L)

				every { userRepository.findByEmail(email) } returns user
				every { cityRepository.findByIdWithCountryAndState(command.cityId) } returns city
				every { tripMapper.toEntity(command, user, city) } returns trip
				every { tripRepository.save(trip) } returns savedTrip

				val result = tripService.createTrip(email, command)

				result shouldBe savedTrip
				verify(exactly = 1) { userRepository.findByEmail(email) }
				verify(exactly = 1) { cityRepository.findByIdWithCountryAndState(command.cityId) }
				verify(exactly = 1) { tripMapper.toEntity(command, user, city) }
				verify(exactly = 1) { tripRepository.save(trip) }
			}
		}

		context("when user does not exist") {
			it("should throw ResponseStatusException with NOT_FOUND status") {
				every { userRepository.findByEmail(email) } returns null

				val exception = shouldThrow<ResponseStatusException> {
					tripService.createTrip(email, command)
				}
				exception.statusCode shouldBe HttpStatus.NOT_FOUND
				exception.reason shouldBe "User not found"
			}
		}

		context("when city does not exist") {
			it("should throw ResponseStatusException with NOT_FOUND status") {
				every { userRepository.findByEmail(email) } returns user
				every { cityRepository.findByIdWithCountryAndState(command.cityId) } returns null

				val exception = shouldThrow<ResponseStatusException> {
					tripService.createTrip(email, command)
				}
				exception.statusCode shouldBe HttpStatus.NOT_FOUND
				exception.reason shouldBe "City not found"
			}
		}
	}

	describe("getUserTrips") {
		val email = "test@example.com"

		context("when user exists") {
			it("should return list of user trips") {
				val trips = listOf(
					Trip(
						id = 1L,
						user = user,
						city = city,
						name = "Trip 1",
						startDate = LocalDate.of(2026, 6, 1),
						finishDate = LocalDate.of(2026, 6, 7)
					),
					Trip(
						id = 2L,
						user = user,
						city = city,
						name = "Trip 2",
						startDate = LocalDate.of(2026, 7, 1),
						finishDate = LocalDate.of(2026, 7, 10)
					)
				)

				every { userRepository.findByEmail(email) } returns user
				every { tripRepository.findByUserWithCityAndCountry(user) } returns trips

				val result = tripService.getUserTrips(email)

				result shouldBe trips
				result.size shouldBe 2
				verify(exactly = 1) { userRepository.findByEmail(email) }
				verify(exactly = 1) { tripRepository.findByUserWithCityAndCountry(user) }
			}
		}

		context("when user does not exist") {
			it("should throw ResponseStatusException with NOT_FOUND status") {
				every { userRepository.findByEmail(email) } returns null

				val exception = shouldThrow<ResponseStatusException> {
					tripService.getUserTrips(email)
				}
				exception.statusCode shouldBe HttpStatus.NOT_FOUND
				exception.reason shouldBe "User not found"
			}
		}
	}

	describe("deleteTrip") {
		val email = "test@example.com"
		val tripId = 1L

		context("when trip exists and user owns it") {
			it("should delete the trip") {
				val trip = Trip(
					id = tripId,
					user = user,
					city = city,
					name = "Trip to delete",
					startDate = LocalDate.of(2026, 6, 1),
					finishDate = LocalDate.of(2026, 6, 7)
				)

				every { tripRepository.findById(tripId) } returns Optional.of(trip)
				every { tripRepository.delete(trip) } just Runs

				tripService.deleteTrip(email, tripId)

				verify(exactly = 1) { tripRepository.findById(tripId) }
				verify(exactly = 1) { tripRepository.delete(trip) }
			}
		}

		context("when trip does not exist") {
			it("should throw ResponseStatusException with NOT_FOUND status") {
				every { tripRepository.findById(tripId) } returns Optional.empty()

				val exception = shouldThrow<ResponseStatusException> {
					tripService.deleteTrip(email, tripId)
				}
				exception.statusCode shouldBe HttpStatus.NOT_FOUND
				exception.reason shouldBe "Trip not found"
			}
		}

		context("when user does not own the trip") {
			it("should throw ResponseStatusException with FORBIDDEN status") {
				val otherUser = user.copy(id = 2L, email = "other@example.com")
				val trip = Trip(
					id = tripId,
					user = otherUser,
					city = city,
					name = "Someone else's trip",
					startDate = LocalDate.of(2026, 6, 1),
					finishDate = LocalDate.of(2026, 6, 7)
				)

				every { tripRepository.findById(tripId) } returns Optional.of(trip)

				val exception = shouldThrow<ResponseStatusException> {
					tripService.deleteTrip(email, tripId)
				}
				exception.statusCode shouldBe HttpStatus.FORBIDDEN
				exception.reason shouldBe "Not allowed to delete this trip"
			}
		}
	}

	describe("updateTrip") {
		val email = "test@example.com"
		val tripId = 1L

		context("when updating trip name only") {
			it("should update and return the trip") {
				val existingTrip = Trip(
					id = tripId,
					user = user,
					city = city,
					name = "Old Name",
					startDate = LocalDate.of(2026, 6, 1),
					finishDate = LocalDate.of(2026, 6, 7)
				)
				val command = UpdateTripCommand(
					name = "New Name",
					cityId = null,
					startDate = null,
					endDate = null
				)
				val updatedTrip = existingTrip.copy(name = "New Name")

				every { tripRepository.findById(tripId) } returns Optional.of(existingTrip)
				every { tripRepository.save(any<Trip>()) } returns updatedTrip

				val result = tripService.updateTrip(email, tripId, command)

				result.name shouldBe "New Name"
				verify(exactly = 1) { tripRepository.findById(tripId) }
				verify(exactly = 1) { tripRepository.save(any<Trip>()) }
			}
		}

		context("when updating city and name was auto-generated") {
			it("should update city and auto-update name") {
				val newCity = city.copy(id = 2L, name = "Los Angeles")
				val existingTrip = Trip(
					id = tripId,
					user = user,
					city = city,
					name = city.name, // Auto-generated
					startDate = LocalDate.of(2026, 6, 1),
					finishDate = LocalDate.of(2026, 6, 7)
				)
				val command = UpdateTripCommand(
					name = null,
					cityId = 2L,
					startDate = null,
					endDate = null
				)
				val updatedTrip = existingTrip.copy(city = newCity, name = "Los Angeles")

				every { tripRepository.findById(tripId) } returns Optional.of(existingTrip)
				every { cityRepository.findById(2L) } returns Optional.of(newCity)
				every { tripRepository.save(any<Trip>()) } returns updatedTrip

				val result = tripService.updateTrip(email, tripId, command)

				result.name shouldBe "Los Angeles"
				result.city shouldBe newCity
			}
		}

		context("when trip does not exist") {
			it("should throw ResponseStatusException with NOT_FOUND status") {
				val command = UpdateTripCommand(
					name = "New Name",
					cityId = null,
					startDate = null,
					endDate = null
				)
				every { tripRepository.findById(tripId) } returns Optional.empty()

				val exception = shouldThrow<ResponseStatusException> {
					tripService.updateTrip(email, tripId, command)
				}
				exception.statusCode shouldBe HttpStatus.NOT_FOUND
				exception.reason shouldBe "Trip not found"
			}
		}

		context("when user does not own the trip") {
			it("should throw ResponseStatusException with FORBIDDEN status") {
				val otherUser = user.copy(id = 2L, email = "other@example.com")
				val existingTrip = Trip(
					id = tripId,
					user = otherUser,
					city = city,
					name = "Someone else's trip",
					startDate = LocalDate.of(2026, 6, 1),
					finishDate = LocalDate.of(2026, 6, 7)
				)
				val command = UpdateTripCommand(
					name = "New Name",
					cityId = null,
					startDate = null,
					endDate = null
				)

				every { tripRepository.findById(tripId) } returns Optional.of(existingTrip)

				val exception = shouldThrow<ResponseStatusException> {
					tripService.updateTrip(email, tripId, command)
				}
				exception.statusCode shouldBe HttpStatus.FORBIDDEN
				exception.reason shouldBe "Not allowed to delete this trip"
			}
		}
	}
})
