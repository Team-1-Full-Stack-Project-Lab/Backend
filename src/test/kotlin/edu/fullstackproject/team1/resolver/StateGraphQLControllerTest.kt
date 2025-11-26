package edu.fullstackproject.team1.resolver

import com.ninjasquad.springmockk.MockkBean
import edu.fullstackproject.team1.config.GraphQLScalarConfig
import edu.fullstackproject.team1.dtos.responses.CountryResponse
import edu.fullstackproject.team1.dtos.responses.StateResponse
import edu.fullstackproject.team1.services.StateService
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest
import org.springframework.context.annotation.Import
import org.springframework.graphql.test.tester.GraphQlTester
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

@GraphQlTest(StateGraphQLController::class)
@Import(GraphQLScalarConfig::class)
class StateGraphQLControllerTest {
	@Autowired
	private lateinit var graphQlTester: GraphQlTester
	@MockkBean
	private lateinit var stateService: StateService

	/* GET STATE BY ID TESTS */
	@Test
	fun testGetStateByIdShouldReturnStateById() {
		val stateResponse = StateResponse(
			id = 1L, name = "California", code = "CA", country = null, latitude = 36.7783, longitude = -119.4179)
		every { stateService.getStateById(1L) } returns stateResponse
		graphQlTester.document(
			"""
            query {
                getStateById(id: 1) {
                    id
                    name
                    code
                    latitude
                    longitude
                }
            }
            """)
			.execute()
			.path("getStateById.id").entity(Long::class.java).isEqualTo(1L)
			.path("getStateById.name").entity(String::class.java).isEqualTo("California")
			.path("getStateById.code").entity(String::class.java).isEqualTo("CA")
			.path("getStateById.latitude").entity(Double::class.java).isEqualTo(36.7783)
			.path("getStateById.longitude").entity(Double::class.java).isEqualTo(-119.4179)
	}
	@Test
	fun testGetStateByIdShouldReturnStateWithCountry() {
		val stateResponse = StateResponse(
			id = 2L, name = "Texas", code = "TX",
			country = CountryResponse(
				id = 1L,
				name = "United States",
				iso2Code = "US",
				iso3Code = "USA",
				phoneCode = "+1",
				currencyCode = "USD",
				currencySymbol = "$",
				region = null,
				states = null,
				cities = null
			),
			latitude = 31.9686,
			longitude = -99.9018
		)
		every { stateService.getStateById(2L) } returns stateResponse
		graphQlTester.document(
			"""
            query {
                getStateById(id: 2) {
                    id
                    name
                    code
                    country {
                        id
                        name
                        iso2Code
                    }
                }
            }
            """)
			.execute()
			.path("getStateById.id").entity(Long::class.java).isEqualTo(2L)
			.path("getStateById.name").entity(String::class.java).isEqualTo("Texas")
			.path("getStateById.country.name").entity(String::class.java).isEqualTo("United States")
			.path("getStateById.country.iso2Code").entity(String::class.java).isEqualTo("US")
	}
	@Test
	fun testGetStateByIdShouldReturnNullWhenStateNotFound() {
		every { stateService.getStateById(999L) } throws ResponseStatusException(
			HttpStatus.NOT_FOUND,
			"State not found"
		)
		graphQlTester.document(
			"""
            query {
                getStateById(id: 999) {
                    id
                    name
                    code
                }
            }
            """)
			.execute()
			.errors()
			.expect { it.message?.contains("State not found") == true }
	}
	@Test
	fun testGetStateByIdShouldReturnOnlyRequestedFields() {
		val stateResponse = StateResponse(
			id = 3L, name = "Florida", code = "FL", country = null, latitude = 27.6648, longitude = -81.5158)
		every { stateService.getStateById(3L) } returns stateResponse
		graphQlTester.document(
			"""
            query {
                getStateById(id: 3) {
                    name
                }
            }
            """)
			.execute()
			.path("getStateById.name").entity(String::class.java).isEqualTo("Florida")
	}
	@Test
	fun testGetStateByIdWithNullCode() {
		val stateResponse = StateResponse(
			id = 4L, name = "Unknown State", code = null, country = null, latitude = 0.0, longitude = 0.0)
		every { stateService.getStateById(4L) } returns stateResponse
		graphQlTester.document(
			"""
            query {
                getStateById(id: 4) {
                    id
                    name
                    code
                }
            }
            """)
			.execute()
			.path("getStateById.id").entity(Long::class.java).isEqualTo(4L)
			.path("getStateById.name").entity(String::class.java).isEqualTo("Unknown State")
			.path("getStateById.code").valueIsNull()
	}
	@Test
	fun testGetStateByIdWithZeroCoordinates() {
		val stateResponse = StateResponse(
			id = 6L, name = "Test State", code = "TS", country = null, latitude = 0.0, longitude = 0.0)
		every { stateService.getStateById(6L) } returns stateResponse
		graphQlTester.document(
			"""
            query {
                getStateById(id: 6) {
                    latitude
                    longitude
                }
            }
            """)
			.execute()
			.path("getStateById.latitude").entity(Double::class.java).isEqualTo(0.0)
			.path("getStateById.longitude").entity(Double::class.java).isEqualTo(0.0)
	}

	/* GET STATES TESTS */
	@Test
	fun testGetStatesShouldReturnAllStates() {
		val states = listOf(
			StateResponse(
				id = 1L, name = "California", code = "CA", country = null, latitude = 36.7783, longitude = -119.4179),
			StateResponse(
				id = 2L, name = "Texas", code = "TX", country = null, latitude = 31.9686, longitude = -99.9018)
		)
		every { stateService.getAllStates() } returns states
		graphQlTester.document(
			"""
            query {
                getStates {
                    id
                    name
                    code
                }
            }
            """)
			.execute()
			.path("getStates").entityList(StateResponse::class.java).hasSize(2)
	}
	@Test
	fun testGetStatesShouldReturnEmptyListWhenNoStatesExist() {
		every { stateService.getAllStates() } returns emptyList()
		graphQlTester.document(
			"""
            query {
                getStates {
                    id
                    name
                }
            }
            """)
			.execute()
			.path("getStates").entityList(StateResponse::class.java).hasSize(0)
	}
	@Test
	fun testGetStatesWithNestedCountryData() {
		val country = CountryResponse(
			id = 1L,
			name = "United States",
			iso2Code = "US",
			iso3Code = "USA",
			phoneCode = "+1",
			currencyCode = "USD",
			currencySymbol = "$",
			region = null,
			states = null,
			cities = null
		)
		val states = listOf(
			StateResponse(
				id = 1L, name = "California", code = "CA", country = country, latitude = 36.7783, longitude = -119.4179)
		)
		every { stateService.getAllStates() } returns states
		graphQlTester.document(
			"""
            query {
                getStates {
                    id
                    name
                    country {
                        name
                        iso2Code
                    }
                }
            }
            """)
			.execute()
			.path("getStates[0].country.name").entity(String::class.java).isEqualTo("United States")
	}

	/* GET STATES TESTS FILTER BY NAME */
	@Test
	fun testGetStatesByNameShouldReturnFilteredStates() {
		val states = listOf(
			StateResponse(
				id = 1L, name = "California", code = "CA", country = null, latitude = 36.7783, longitude = -119.4179)
		)
		every { stateService.getStatesByName("California") } returns states
		graphQlTester.document(
			"""
            query {
                getStates(name: "California") {
                    id
                    name
                    code
                }
            }
            """)
			.execute()
			.path("getStates").entityList(StateResponse::class.java).hasSize(1)
			.path("getStates[0].name").entity(String::class.java).isEqualTo("California")
	}
	@Test
	fun testGetStatesByNameShouldReturnEmptyListWhenNoMatch() {
		every { stateService.getStatesByName("NonExistentState") } returns emptyList()
		graphQlTester.document(
			"""
            query {
                getStates(name: "NonExistentState") {
                    id
                    name
                }
            }
            """)
			.execute()
			.path("getStates").entityList(StateResponse::class.java).hasSize(0)
	}
	@Test
	fun testGetStatesByNameShouldBeCaseInsensitive() {
		val states = listOf(
			StateResponse(
				id = 2L, name = "Texas", code = "TX", country = null, latitude = 31.9686, longitude = -99.9018)
		)
		every { stateService.getStatesByName("texas") } returns states
		graphQlTester.document(
			"""
            query {
                getStates(name: "texas") {
                    id
                    name
                    code
                }
            }
            """)
			.execute()
			.path("getStates").entityList(StateResponse::class.java).hasSize(1)
			.path("getStates[0].name").entity(String::class.java).isEqualTo("Texas")
	}

	/* ADDITIONAL TESTS */
	@Test
	fun testGetStateByIdShouldHandleLargeIds() {
		val stateResponse = StateResponse(
			id = 999999L, name = "Test State", code = "TS", country = null, latitude = 0.0, longitude = 0.0)
		every { stateService.getStateById(999999L) } returns stateResponse
		graphQlTester.document(
			"""
            query {
                getStateById(id: 999999) {
                    id
                    name
                }
            }
            """)
			.execute()
			.path("getStateById.id").entity(Long::class.java).isEqualTo(999999L)
	}
	@Test
	fun testGetStatesShouldHandleSpecialCharactersInName() {
		val states = listOf(
			StateResponse(
				id = 10L, name = "Hawai'i", code = "HI", country = null, latitude = 19.8968, longitude = -155.5828)
		)
		every { stateService.getStatesByName("Hawai'i") } returns states
		graphQlTester.document(
			"""
            query {
                getStates(name: "Hawai'i") {
                    id
                    name
                    code
                }
            }
            """)
			.execute()
			.path("getStates").entityList(StateResponse::class.java).hasSize(1)
			.path("getStates[0].name").entity(String::class.java).isEqualTo("Hawai'i")
	}
	@Test
	fun testMultipleQueriesInSingleDocument() {
		val stateResponse = StateResponse(id = 1L, name = "California", code = "CA", country = null, latitude = 0.0, longitude = 0.0)
		val allStates = listOf(
			StateResponse(id = 1L, name = "California", code = "CA", country = null, latitude = 0.0, longitude = 0.0),
			StateResponse(id = 2L, name = "Texas", code = "TX", country = null, latitude = 0.0, longitude = 0.0)
		)
		every { stateService.getStateById(1L) } returns stateResponse
		every { stateService.getAllStates() } returns allStates
		graphQlTester.document(
			"""
            query {
                single: getStateById(id: 1) {
                    id
                    name
                }
                all: getStates {
                    id
                    name
                }
            }
            """)
			.execute()
			.path("single.name").entity(String::class.java).isEqualTo("California")
			.path("all").entityList(StateResponse::class.java).hasSize(2)
	}
	@Test
	fun testGetStateByIdWithNegativeCoordinates() {
		val stateResponse = StateResponse(
			id = 11L, name = "Alaska", code = "AK", country = null, latitude = 64.2008, longitude = -149.4937)
		every { stateService.getStateById(11L) } returns stateResponse
		graphQlTester.document(
			"""
            query {
                getStateById(id: 11) {
                    name
                    latitude
                    longitude
                }
            }
            """)
			.execute()
			.path("getStateById.name").entity(String::class.java).isEqualTo("Alaska")
			.path("getStateById.latitude").entity(Double::class.java).isEqualTo(64.2008)
			.path("getStateById.longitude").entity(Double::class.java).isEqualTo(-149.4937)
	}
}
