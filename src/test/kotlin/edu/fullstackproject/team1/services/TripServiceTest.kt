package edu.fullstackproject.team1.services

import edu.fullstackproject.team1.dtos.CreateTripRequest
import edu.fullstackproject.team1.dtos.UpdateTripRequest
import edu.fullstackproject.team1.models.City
import edu.fullstackproject.team1.models.Country
import edu.fullstackproject.team1.models.Trip
import edu.fullstackproject.team1.models.User
import edu.fullstackproject.team1.repositories.CityRepository
import edu.fullstackproject.team1.repositories.TripRepository
import edu.fullstackproject.team1.repositories.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.time.LocalDate
import java.util.Optional
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class TripServiceTest :
        FunSpec({
          val tripRepository = mockk<TripRepository>()
          val userRepository = mockk<UserRepository>()
          val cityRepository = mockk<CityRepository>()
          val tripService = TripService(tripRepository, userRepository, cityRepository)

          beforeEach { clearMocks(tripRepository, userRepository, cityRepository) }

          // Mock data
          val mockUser =
                  User(
                          id = 1L,
                          email = "test@example.com",
                          firstName = "John",
                          lastName = "Doe",
                          password = "hashedPassword"
                  )

          val mockCountry =
                  Country(id = 1L, name = "United States", iso2Code = "US", iso3Code = "USA")

          val mockCity =
                  City(
                          id = 1L,
                          name = "New York",
                          country = mockCountry,
                          latitude = 40.7128,
                          longitude = -74.0060,
                          isCapital = false,
                          isFeatured = true
                  )

          val mockTrip =
                  Trip(
                          id = 1L,
                          user = mockUser,
                          city = mockCity,
                          name = "New York Adventure",
                          startDate = LocalDate.now().plusDays(10),
                          finishDate = LocalDate.now().plusDays(15)
                  )

          context("createTrip") {
            test("should create trip successfully with custom name") {
              val request =
                      CreateTripRequest(
                              cityId = 1L,
                              startDate = LocalDate.now().plusDays(10),
                              endDate = LocalDate.now().plusDays(15),
                              name = "My Custom Trip"
                      )

              every { userRepository.findByEmail("test@example.com") } returns mockUser
              every { cityRepository.findByIdWithCountryAndState(1L) } returns mockCity

              val savedTripSlot = slot<Trip>()
              every { tripRepository.save(capture(savedTripSlot)) } answers
                      {
                        savedTripSlot.captured.copy(id = 1L)
                      }

              val result = tripService.createTrip("test@example.com", request)

              result shouldNotBe null
              result.name shouldBe "My Custom Trip"
              result.cityId shouldBe 1L
              result.cityName shouldBe "New York"
              result.countryName shouldBe "United States"

              verify(exactly = 1) { tripRepository.save(any()) }
            }

            test("should create trip with city name when name is not provided") {
              val request =
                      CreateTripRequest(
                              cityId = 1L,
                              startDate = LocalDate.now().plusDays(10),
                              endDate = LocalDate.now().plusDays(15),
                              name = null
                      )

              every { userRepository.findByEmail("test@example.com") } returns mockUser
              every { cityRepository.findByIdWithCountryAndState(1L) } returns mockCity

              val savedTripSlot = slot<Trip>()
              every { tripRepository.save(capture(savedTripSlot)) } answers
                      {
                        savedTripSlot.captured.copy(id = 1L)
                      }

              val result = tripService.createTrip("test@example.com", request)

              result.name shouldBe "New York"
              verify(exactly = 1) { tripRepository.save(any()) }
            }

            test("should create trip with city name when name is blank") {
              val request =
                      CreateTripRequest(
                              cityId = 1L,
                              startDate = LocalDate.now().plusDays(10),
                              endDate = LocalDate.now().plusDays(15),
                              name = "   "
                      )

              every { userRepository.findByEmail("test@example.com") } returns mockUser
              every { cityRepository.findByIdWithCountryAndState(1L) } returns mockCity

              val savedTripSlot = slot<Trip>()
              every { tripRepository.save(capture(savedTripSlot)) } answers
                      {
                        savedTripSlot.captured.copy(id = 1L)
                      }

              val result = tripService.createTrip("test@example.com", request)

              result.name shouldBe "New York"
            }

            test("should throw exception when user not found") {
              val request =
                      CreateTripRequest(
                              cityId = 1L,
                              startDate = LocalDate.now().plusDays(10),
                              endDate = LocalDate.now().plusDays(15)
                      )

              every { userRepository.findByEmail("nonexistent@example.com") } returns null

              val exception =
                      shouldThrow<ResponseStatusException> {
                        tripService.createTrip("nonexistent@example.com", request)
                      }

              exception.statusCode shouldBe HttpStatus.NOT_FOUND
              exception.reason shouldContain "User not found"
            }

            test("should throw exception when city not found") {
              val request =
                      CreateTripRequest(
                              cityId = 999L,
                              startDate = LocalDate.now().plusDays(10),
                              endDate = LocalDate.now().plusDays(15)
                      )

              every { userRepository.findByEmail("test@example.com") } returns mockUser
              every { cityRepository.findByIdWithCountryAndState(999L) } returns null

              val exception =
                      shouldThrow<ResponseStatusException> {
                        tripService.createTrip("test@example.com", request)
                      }

              exception.statusCode shouldBe HttpStatus.NOT_FOUND
              exception.reason shouldContain "City not found"
            }

            test("should throw exception when end date is before start date") {
              val request =
                      CreateTripRequest(
                              cityId = 1L,
                              startDate = LocalDate.now().plusDays(15),
                              endDate = LocalDate.now().plusDays(10)
                      )

              every { userRepository.findByEmail("test@example.com") } returns mockUser
              every { cityRepository.findByIdWithCountryAndState(1L) } returns mockCity

              val exception =
                      shouldThrow<ResponseStatusException> {
                        tripService.createTrip("test@example.com", request)
                      }

              exception.statusCode shouldBe HttpStatus.BAD_REQUEST
              exception.reason shouldContain "End date must be before start date"
            }

            test("should throw exception when start date is in the past") {
              val request =
                      CreateTripRequest(
                              cityId = 1L,
                              startDate = LocalDate.now().minusDays(1),
                              endDate = LocalDate.now().plusDays(5)
                      )

              every { userRepository.findByEmail("test@example.com") } returns mockUser
              every { cityRepository.findByIdWithCountryAndState(1L) } returns mockCity

              val exception =
                      shouldThrow<ResponseStatusException> {
                        tripService.createTrip("test@example.com", request)
                      }

              exception.statusCode shouldBe HttpStatus.BAD_REQUEST
              exception.reason shouldContain "Start date cannot be in the past"
            }
          }

          context("getUserTrips") {
            test("should return all trips for a user") {
              val mockTrips = listOf(mockTrip, mockTrip.copy(id = 2L, name = "Trip 2"))

              every { userRepository.findByEmail("test@example.com") } returns mockUser
              every { tripRepository.findByUser(mockUser) } returns mockTrips

              val result = tripService.getUserTrips("test@example.com")

              result.trips.size shouldBe 2
              result.trips[0].name shouldBe "New York Adventure"
              result.trips[1].name shouldBe "Trip 2"
            }

            test("should return empty list when user has no trips") {
              every { userRepository.findByEmail("test@example.com") } returns mockUser
              every { tripRepository.findByUser(mockUser) } returns emptyList()

              val result = tripService.getUserTrips("test@example.com")

              result.trips.size shouldBe 0
            }

            test("should throw exception when user not found") {
              every { userRepository.findByEmail("nonexistent@example.com") } returns null

              val exception =
                      shouldThrow<ResponseStatusException> {
                        tripService.getUserTrips("nonexistent@example.com")
                      }

              exception.statusCode shouldBe HttpStatus.NOT_FOUND
              exception.reason shouldContain "User not found"
            }
          }

          context("deleteTrip") {
            test("should delete trip successfully when user is owner") {
              every { tripRepository.findById(1L) } returns Optional.of(mockTrip)
              every { tripRepository.delete(mockTrip) } returns Unit

              tripService.deleteTrip("test@example.com", 1L)

              verify(exactly = 1) { tripRepository.delete(mockTrip) }
            }

            test("should throw exception when trip not found") {
              every { tripRepository.findById(999L) } returns Optional.empty()

              val exception =
                      shouldThrow<ResponseStatusException> {
                        tripService.deleteTrip("test@example.com", 999L)
                      }

              exception.statusCode shouldBe HttpStatus.NOT_FOUND
            }

            test("should throw exception when user is not the owner") {
              every { tripRepository.findById(1L) } returns Optional.of(mockTrip)

              val exception =
                      shouldThrow<ResponseStatusException> {
                        tripService.deleteTrip("other@example.com", 1L)
                      }

              exception.statusCode shouldBe HttpStatus.FORBIDDEN
              verify(exactly = 0) { tripRepository.delete(any()) }
            }
          }

          context("updateTrip") {
            test("should update trip name successfully") {
              val updateRequest = UpdateTripRequest(name = "Updated Trip Name")

              every { tripRepository.findById(1L) } returns Optional.of(mockTrip)
              every { tripRepository.save(any()) } answers { firstArg() }

              val result = tripService.updateTrip("test@example.com", 1L, updateRequest)

              result.name shouldBe "Updated Trip Name"
              result.cityName shouldBe "New York"
            }

            test("should update trip city successfully") {
              val newCity =
                      City(
                              id = 2L,
                              name = "Los Angeles",
                              country = mockCountry,
                              latitude = 34.0522,
                              longitude = -118.2437,
                              isCapital = false,
                              isFeatured = true
                      )

              val updateRequest = UpdateTripRequest(cityId = 2L)

              every { tripRepository.findById(1L) } returns Optional.of(mockTrip)
              every { cityRepository.findById(2L) } returns Optional.of(newCity)
              every { tripRepository.save(any()) } answers { firstArg() }

              val result = tripService.updateTrip("test@example.com", 1L, updateRequest)

              result.cityName shouldBe "Los Angeles"
            }

            test("should update trip dates successfully") {
              val updateRequest =
                      UpdateTripRequest(
                              startDate = LocalDate.now().plusDays(20),
                              endDate = LocalDate.now().plusDays(25)
                      )

              every { tripRepository.findById(1L) } returns Optional.of(mockTrip)
              every { tripRepository.save(any()) } answers { firstArg() }

              val result = tripService.updateTrip("test@example.com", 1L, updateRequest)

              result.startDate shouldBe LocalDate.now().plusDays(20)
              result.finishDate shouldBe LocalDate.now().plusDays(25)
            }

            test("should auto-update name when city changes and name was auto-generated") {
              val tripWithAutoName = mockTrip.copy(name = "New York")
              val newCity =
                      City(
                              id = 2L,
                              name = "Los Angeles",
                              country = mockCountry,
                              latitude = 34.0522,
                              longitude = -118.2437,
                              isCapital = false,
                              isFeatured = true
                      )

              val updateRequest = UpdateTripRequest(cityId = 2L)

              every { tripRepository.findById(1L) } returns Optional.of(tripWithAutoName)
              every { cityRepository.findById(2L) } returns Optional.of(newCity)
              every { tripRepository.save(any()) } answers { firstArg() }

              val result = tripService.updateTrip("test@example.com", 1L, updateRequest)

              result.name shouldBe "Los Angeles"
              result.cityName shouldBe "Los Angeles"
            }

            test("should not auto-update name when city changes but name was custom") {
              val updateRequest = UpdateTripRequest(cityId = 2L)

              val newCity =
                      City(
                              id = 2L,
                              name = "Los Angeles",
                              country = mockCountry,
                              latitude = 34.0522,
                              longitude = -118.2437,
                              isCapital = false,
                              isFeatured = true
                      )

              every { tripRepository.findById(1L) } returns Optional.of(mockTrip)
              every { cityRepository.findById(2L) } returns Optional.of(newCity)
              every { tripRepository.save(any()) } answers { firstArg() }

              val result = tripService.updateTrip("test@example.com", 1L, updateRequest)

              result.name shouldBe "New York Adventure"
              result.cityName shouldBe "Los Angeles"
            }

            test("should throw exception when trip not found") {
              val updateRequest = UpdateTripRequest(name = "New Name")

              every { tripRepository.findById(999L) } returns Optional.empty()

              val exception =
                      shouldThrow<ResponseStatusException> {
                        tripService.updateTrip("test@example.com", 999L, updateRequest)
                      }

              exception.statusCode shouldBe HttpStatus.NOT_FOUND
            }

            test("should throw exception when user is not owner") {
              val updateRequest = UpdateTripRequest(name = "New Name")

              every { tripRepository.findById(1L) } returns Optional.of(mockTrip)

              val exception =
                      shouldThrow<ResponseStatusException> {
                        tripService.updateTrip("other@example.com", 1L, updateRequest)
                      }

              exception.statusCode shouldBe HttpStatus.FORBIDDEN
            }

            test("should throw exception when new city not found") {
              val updateRequest = UpdateTripRequest(cityId = 999L)

              every { tripRepository.findById(1L) } returns Optional.of(mockTrip)
              every { cityRepository.findById(999L) } returns Optional.empty()

              val exception =
                      shouldThrow<ResponseStatusException> {
                        tripService.updateTrip("test@example.com", 1L, updateRequest)
                      }

              exception.statusCode shouldBe HttpStatus.NOT_FOUND
              exception.reason shouldContain "City not found"
            }

            test("should throw exception when updated dates are invalid") {
              val updateRequest =
                      UpdateTripRequest(
                              startDate = LocalDate.now().plusDays(25),
                              endDate = LocalDate.now().plusDays(20)
                      )

              every { tripRepository.findById(1L) } returns Optional.of(mockTrip)

              val exception =
                      shouldThrow<ResponseStatusException> {
                        tripService.updateTrip("test@example.com", 1L, updateRequest)
                      }

              exception.statusCode shouldBe HttpStatus.BAD_REQUEST
            }

            test("should throw exception when updated start date is in the past") {
              val updateRequest = UpdateTripRequest(startDate = LocalDate.now().minusDays(1))

              every { tripRepository.findById(1L) } returns Optional.of(mockTrip)

              val exception =
                      shouldThrow<ResponseStatusException> {
                        tripService.updateTrip("test@example.com", 1L, updateRequest)
                      }

              exception.statusCode shouldBe HttpStatus.BAD_REQUEST
            }
          }
        })
