package edu.fullstackproject.team1.resolver

import com.ninjasquad.springmockk.MockkBean
import edu.fullstackproject.team1.config.GraphQLScalarConfig
import edu.fullstackproject.team1.dtos.TripResponse
import edu.fullstackproject.team1.dtos.TripsListResponse
import edu.fullstackproject.team1.services.TripService
import io.mockk.every
import io.mockk.justRun
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest
import org.springframework.context.annotation.Import
import org.springframework.graphql.test.tester.GraphQlTester
import org.springframework.http.HttpStatus
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate

@GraphQlTest(TripGraphQLController::class)
@Import(GraphQLScalarConfig::class)
class TripGraphQLControllerTest {
	@Autowired
	private lateinit var graphQlTester: GraphQlTester
	@MockkBean
	private lateinit var tripService: TripService

	/* CREATE ITINERARY MUTATION TESTS*/
	@Test
	@WithMockUser(username = "user@example.com")
	fun testCreateItineraryShouldCreateTripSuccessfully() {
		val tripResponse = TripResponse(
			id = 1L,
			name = "Paris Vacation",
			cityId = 100L,
			cityName = "Paris",
			countryName = "France",
			startDate = LocalDate.of(2025, 12, 1),
			finishDate = LocalDate.of(2025, 12, 10))
		every {
			tripService.createTrip(
				email = "user@example.com",
				request = match {
					it.cityId == 100L &&
						it.name == "Paris Vacation"
				}
			)
		} returns tripResponse
		graphQlTester.document(
			"""
            mutation {
                createItinerary(request: {
                    cityId: 100
                    name: "Paris Vacation"
                    startDate: "2025-12-01"
                    endDate: "2025-12-10"
                }) {
                    id
                    name
                    cityName
                    countryName
                    startDate
                    finishDate
                }
            }
            """)
			.execute()
			.path("createItinerary.id").entity(Long::class.java).isEqualTo(1L)
			.path("createItinerary.name").entity(String::class.java).isEqualTo("Paris Vacation")
			.path("createItinerary.cityName").entity(String::class.java).isEqualTo("Paris")
			.path("createItinerary.countryName").entity(String::class.java).isEqualTo("France")
	}
	@Test
	@WithMockUser(username = "user@example.com")
	fun testCreateItineraryShouldCreateTripWithoutName() {
		val tripResponse = TripResponse(
			id = 2L,
			name = "Trip to Tokyo",
			cityId = 200L,
			cityName = "Tokyo",
			countryName = "Japan",
			startDate = LocalDate.of(2025, 11, 15),
			finishDate = LocalDate.of(2025, 11, 20)
		)
		every {
			tripService.createTrip(
				email = "user@example.com",
				request = match { it.cityId == 200L && it.name == null }
			)
		} returns tripResponse
		graphQlTester.document(
			"""
            mutation {
                createItinerary(request: {
                    cityId: 200
                    startDate: "2025-11-15"
                    endDate: "2025-11-20"
                }) {
                    id
                    cityName
                    countryName
                }
            }
            """)
			.execute()
			.path("createItinerary.id").entity(Long::class.java).isEqualTo(2L)
			.path("createItinerary.cityName").entity(String::class.java).isEqualTo("Tokyo")
	}
	@Test
	@WithMockUser(username = "user@example.com")
	fun testCreateItineraryShouldFailWithMissingCityId() {
		graphQlTester.document(
			"""
            mutation {
                createItinerary(request: {
                    startDate: "2025-12-01"
                    endDate: "2025-12-10"
                }) {
                    id
                }
            }
            """)
			.execute()
			.errors()
			.expect { error ->
				error.message?.contains("cityId") == true ||
					error.message?.contains("required") == true
			}
	}
	@Test
	@WithMockUser(username = "user@example.com")
	fun testCreateItineraryShouldFailWithMissingStartDate() {
		graphQlTester.document(
			"""
            mutation {
                createItinerary(request: {
                    cityId: 100
                    endDate: "2025-12-10"
                }) {
                    id
                }
            }
            """)
			.execute()
			.errors()
			.expect { error ->
				error.message?.contains("startDate") == true ||
					error.message?.contains("required") == true }
	}
	@Test
	@WithMockUser(username = "user@example.com")
	fun testCreateItineraryShouldFailWithMissingEndDate() {
		graphQlTester.document(
			"""
            mutation {
                createItinerary(request: {
                    cityId: 100
                    startDate: "2025-12-01"
                }) {
                    id
                }
            }
            """)
			.execute()
			.errors()
			.expect { error ->
				error.message?.contains("endDate") == true ||
					error.message?.contains("required") == true }
	}
	@Test
	@WithMockUser(username = "user@example.com")
	fun testCreateItineraryShouldFailWhenCityNotFound() {
		every {
			tripService.createTrip(
				email = "user@example.com",
				request = match { it.cityId == 999L }
			)
		} throws ResponseStatusException(HttpStatus.NOT_FOUND, "City not found")
		graphQlTester.document(
			"""
            mutation {
                createItinerary(request: {
                    cityId: 999
                    startDate: "2025-12-01"
                    endDate: "2025-12-10"
                }) {
                    id
                }
            }
            """)
			.execute()
			.errors()
			.expect { it.message?.contains("City not found") == true }
	}

	/* GET USER ITINERARIES TESTS */
	@Test
	@WithMockUser(username = "user@example.com")
	fun testGetUserItinerariesShouldReturnUserTrips() {
		val trips = listOf(
			TripResponse(
				id = 1L,
				name = "Paris Vacation",
				cityId = 100L,
				cityName = "Paris",
				countryName = "France",
				startDate = LocalDate.of(2025, 12, 1),
				finishDate = LocalDate.of(2025, 12, 10)
			),
			TripResponse(
				id = 2L,
				name = "Tokyo Adventure",
				cityId = 200L,
				cityName = "Tokyo",
				countryName = "Japan",
				startDate = LocalDate.of(2026, 1, 15),
				finishDate = LocalDate.of(2026, 1, 25)
			)
		)
		every { tripService.getUserTrips("user@example.com") } returns TripsListResponse(trips)
		graphQlTester.document(
			"""
            query {
                getUserItineraries {
                    trips {
                        id
                        name
                        cityName
                        countryName
                        startDate
                        finishDate
                    }
                }
            }
            """)
			.execute()
			.path("getUserItineraries.trips[0].name").entity(String::class.java).isEqualTo("Paris Vacation")
			.path("getUserItineraries.trips[0].cityName").entity(String::class.java).isEqualTo("Paris")
			.path("getUserItineraries.trips[1].name").entity(String::class.java).isEqualTo("Tokyo Adventure")
			.path("getUserItineraries.trips[1].cityName").entity(String::class.java).isEqualTo("Tokyo")
	}
	@Test
	@WithMockUser(username = "user@example.com")
	fun testGetUserItinerariesShouldReturnEmptyListWhenNoTrips() {
		every { tripService.getUserTrips("user@example.com") } returns TripsListResponse(emptyList())
		graphQlTester.document(
			"""
            query {
                getUserItineraries {
                    trips {
                        id
                        name
                    }
                }
            }
            """)
			.execute()
			.path("getUserItineraries.trips").entityList(TripResponse::class.java).hasSize(0)
	}
	@Test
	@WithMockUser(username = "user@example.com")
	fun testGetUserItinerariesShouldReturnOnlyRequestedFields() {
		val trips = listOf(
			TripResponse(
				id = 1L,
				name = "Paris Vacation",
				cityId = 100L,
				cityName = "Paris",
				countryName = "France",
				startDate = LocalDate.of(2025, 12, 1),
				finishDate = LocalDate.of(2025, 12, 10)
			)
		)
		every { tripService.getUserTrips("user@example.com") } returns TripsListResponse(trips)
		graphQlTester.document(
			"""
            query {
                getUserItineraries {
                    trips {
                        id
                        name
                    }
                }
            }
            """)
			.execute()
			.path("getUserItineraries.trips[0].id").entity(Long::class.java).isEqualTo(1L)
			.path("getUserItineraries.trips[0].name").entity(String::class.java).isEqualTo("Paris Vacation")
	}
	@Test
	@WithMockUser(username = "different@example.com")
	fun testGetUserItinerariesShouldReturnTripsForCorrectUser() {
		val trips = listOf(
			TripResponse(
				id = 10L,
				name = "User Specific Trip",
				cityId = 500L,
				cityName = "Berlin",
				countryName = "Germany",
				startDate = LocalDate.of(2025, 12, 1),
				finishDate = LocalDate.of(2025, 12, 5)
			)
		)
		every { tripService.getUserTrips("different@example.com") } returns TripsListResponse(trips)
		graphQlTester.document(
			"""
            query {
                getUserItineraries {
                    trips {
                        id
                        name
                    }
                }
            }
            """)
			.execute()
			.path("getUserItineraries.trips[0].id").entity(Long::class.java).isEqualTo(10L)
		verify(exactly = 1) { tripService.getUserTrips("different@example.com") }
	}

	/* DELETE ITINERARY MUTATION TESTS */
	@Test
	@WithMockUser(username = "user@example.com")
	fun testDeleteItineraryShouldDeleteTripSuccessfully() {
		justRun { tripService.deleteTrip("user@example.com", 1L) }
		graphQlTester.document(
			"""
        mutation {
            deleteItinerary(id: 1) {
                success
                message
            }
        }
        """)
			.execute()
			.path("deleteItinerary.success").entity(Boolean::class.java).isEqualTo(true)
			.path("deleteItinerary.message").entity(String::class.java).isEqualTo("Itineraries deleted successfully")
		verify(exactly = 1) { tripService.deleteTrip("user@example.com", 1L) }
	}
	@Test
	@WithMockUser(username = "user@example.com")
	fun testDeleteItineraryShouldCallServiceWithCorrectParameters() {
		justRun { tripService.deleteTrip("user@example.com", 5L) }
		graphQlTester.document(
			"""
            mutation {
                deleteItinerary(id: 5) {
                    success
                }
            }
            """)
			.execute()
			.path("deleteItinerary.success").entity(Boolean::class.java).isEqualTo(true)
		verify(exactly = 1) { tripService.deleteTrip("user@example.com", 5L) }
	}
	@Test
	@WithMockUser(username = "user@example.com")
	fun testDeleteItineraryShouldFailWhenTripNotFound() {
		every {
			tripService.deleteTrip("user@example.com", 999L)
		} throws ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found")
		graphQlTester.document(
			"""
            mutation {
                deleteItinerary(id: 999) {
                    success
                    message
                }
            }
            """)
			.execute()
			.errors()
			.expect { it.message?.contains("Trip not found") == true }
	}
	@Test
	@WithMockUser(username = "user@example.com")
	fun testDeleteItineraryShouldFailWhenUserDoesNotOwnTrip() {
		every {
			tripService.deleteTrip("user@example.com", 10L)
		} throws ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to delete this trip")
		graphQlTester.document(
			"""
            mutation {
                deleteItinerary(id: 10) {
                    success
                    message
                }
            }
            """)
			.execute()
			.errors()
			.expect { it.message?.contains("permission") == true }
	}

	/* UPDATE ITINERARY MUTATION TESTS */
	@Test
	@WithMockUser(username = "user@example.com")
	fun testUpdateItineraryShouldUpdateTripSuccessfully() {
		val updatedTrip = TripResponse(
			id = 1L,
			name = "Updated Paris Trip",
			cityId = 100L,
			cityName = "Paris",
			countryName = "France",
			startDate = LocalDate.of(2025, 12, 5),
			finishDate = LocalDate.of(2025, 12, 15)
		)
		every {
			tripService.updateTrip(
				userEmail = "user@example.com",
				tripID = 1L,
				request = match { it.name == "Updated Paris Trip" }
			)
		} returns updatedTrip
		graphQlTester.document(
			"""
            mutation {
                updateItinerary(id: 1, request: {
                    name: "Updated Paris Trip"
                    startDate: "2025-12-05"
                    endDate: "2025-12-15"
                }) {
                    id
                    name
                    startDate
                    finishDate
                }
            }
            """)
			.execute()
			.path("updateItinerary.id").entity(Long::class.java).isEqualTo(1L)
			.path("updateItinerary.name").entity(String::class.java).isEqualTo("Updated Paris Trip")
	}
	@Test
	@WithMockUser(username = "user@example.com")
	fun testUpdateItineraryShouldUpdateOnlyName() {
		val updatedTrip = TripResponse(
			id = 2L,
			name = "New Trip Name",
			cityId = 200L,
			cityName = "Tokyo",
			countryName = "Japan",
			startDate = LocalDate.of(2025, 11, 15),
			finishDate = LocalDate.of(2025, 11, 20)
		)
		every {
			tripService.updateTrip(
				userEmail = "user@example.com",
				tripID = 2L,
				request = match { it.name == "New Trip Name" && it.cityId == null }
			)
		} returns updatedTrip
		graphQlTester.document(
			"""
            mutation {
                updateItinerary(id: 2, request: {
                    name: "New Trip Name"
                }) {
                    id
                    name
                }
            }
            """)
			.execute()
			.path("updateItinerary.name").entity(String::class.java).isEqualTo("New Trip Name")
	}
	@Test
	@WithMockUser(username = "user@example.com")
	fun testUpdateItineraryShouldUpdateOnlyCity() {
		val updatedTrip = TripResponse(
			id = 3L,
			name = "Trip",
			cityId = 300L,
			cityName = "London",
			countryName = "United Kingdom",
			startDate = LocalDate.of(2025, 12, 1),
			finishDate = LocalDate.of(2025, 12, 10)
		)
		every {
			tripService.updateTrip(
				userEmail = "user@example.com",
				tripID = 3L,
				request = match { it.cityId == 300L && it.name == null }
			)
		} returns updatedTrip
		graphQlTester.document(
			"""
            mutation {
                updateItinerary(id: 3, request: {
                    cityId: 300
                }) {
                    cityName
                }
            }
            """)
			.execute()
			.path("updateItinerary.cityName").entity(String::class.java).isEqualTo("London")
	}
	@Test
	@WithMockUser(username = "user@example.com")
	fun testUpdateItineraryShouldUpdateOnlyDates() {
		val updatedTrip = TripResponse(
			id = 4L,
			name = "Trip",
			cityId = 100L,
			cityName = "Paris",
			countryName = "France",
			startDate = LocalDate.of(2026, 1, 1),
			finishDate = LocalDate.of(2026, 1, 10)
		)
		every {
			tripService.updateTrip(
				userEmail = "user@example.com",
				tripID = 4L,
				request = any()
			)
		} returns updatedTrip
		graphQlTester.document(
			"""
            mutation {
                updateItinerary(id: 4, request: {
                    startDate: "2026-01-01"
                    endDate: "2026-01-10"
                }) {
                    startDate
                    finishDate
                }
            }
            """)
			.execute()
			.path("updateItinerary.startDate").entity(String::class.java).isEqualTo("2026-01-01")
			.path("updateItinerary.finishDate").entity(String::class.java).isEqualTo("2026-01-10")
	}
	@Test
	@WithMockUser(username = "user@example.com")
	fun testUpdateItineraryShouldFailWhenTripNotFound() {
		every {
			tripService.updateTrip(
				userEmail = "user@example.com",
				tripID = 999L,
				request = any()
			)
		} throws ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found")
		graphQlTester.document(
			"""
            mutation {
                updateItinerary(id: 999, request: {
                    name: "Updated"
                }) {
                    id
                }
            }
            """)
			.execute()
			.errors()
			.expect { it.message?.contains("Trip not found") == true }
	}
	@Test
	@WithMockUser(username = "user@example.com")
	fun testUpdateItineraryShouldFailWhenUserDoesNotOwnTrip() {
		every {
			tripService.updateTrip(
				userEmail = "user@example.com",
				tripID = 10L,
				request = any()
			)
		} throws ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to update this trip")
		graphQlTester.document(
			"""
            mutation {
                updateItinerary(id: 10, request: {
                    name: "Updated"
                }) {
                    id
                }
            }
            """)
			.execute()
			.errors()
			.expect { it.message?.contains("permission") == true }
	}
	@Test
	@WithMockUser(username = "user@example.com")
	fun testUpdateItineraryShouldUpdateAllFields() {
		val updatedTrip = TripResponse(
			id = 6L,
			name = "Complete Update",
			cityId = 400L,
			cityName = "Rome",
			countryName = "Italy",
			startDate = LocalDate.of(2026, 3, 1),
			finishDate = LocalDate.of(2026, 3, 15)
		)
		every {
			tripService.updateTrip(
				userEmail = "user@example.com",
				tripID = 6L,
				request = match {
					it.name == "Complete Update" &&
						it.cityId == 400L
				}
			)
		} returns updatedTrip
		graphQlTester.document(
			"""
            mutation {
                updateItinerary(id: 6, request: {
                    name: "Complete Update"
                    cityId: 400
                    startDate: "2026-03-01"
                    endDate: "2026-03-15"
                }) {
                    id
                    name
                    cityName
                    countryName
                    startDate
                    finishDate
                }
            }
            """)
			.execute()
			.path("updateItinerary.name").entity(String::class.java).isEqualTo("Complete Update")
			.path("updateItinerary.cityName").entity(String::class.java).isEqualTo("Rome")
			.path("updateItinerary.countryName").entity(String::class.java).isEqualTo("Italy")
	}

	/* ADDITIONAL TESTS */
	@Test
	@WithMockUser(username = "user@example.com")
	fun testCreateItineraryServiceIsCalledWithCorrectParameters() {
		val tripResponse = TripResponse(
			id = 1L,
			name = "Test",
			cityId = 100L,
			cityName = "Paris",
			countryName = "France",
			startDate = LocalDate.of(2025, 12, 1),
			finishDate = LocalDate.of(2025, 12, 10)
		)
		every {
			tripService.createTrip(
				email = "user@example.com",
				request = any()
			)
		} returns tripResponse
		graphQlTester.document(
			"""
            mutation {
                createItinerary(request: {
                    cityId: 100
                    name: "Test"
                    startDate: "2025-12-01"
                    endDate: "2025-12-10"
                }) {
                    id
                }
            }
            """
		)
			.execute()
		verify(exactly = 1) {
			tripService.createTrip(
				email = "user@example.com",
				request = any()
			)
		}
	}
}
