package edu.fullstackproject.team1.resolver

import com.ninjasquad.springmockk.MockkBean
import edu.fullstackproject.team1.config.GraphQLScalarConfig
import edu.fullstackproject.team1.dtos.responses.CityResponse
import edu.fullstackproject.team1.dtos.ServiceResponse
import edu.fullstackproject.team1.dtos.StayResponse
import edu.fullstackproject.team1.dtos.StayTypeResponse
import edu.fullstackproject.team1.dtos.StayUnitResponse
import edu.fullstackproject.team1.services.StayUnitService
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest
import org.springframework.context.annotation.Import
import org.springframework.graphql.test.tester.GraphQlTester
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal

@GraphQlTest(StayUnitGraphQLController::class)
@Import(GraphQLScalarConfig::class)
class StayUnitGraphQLControllerTest {
	@Autowired
	private lateinit var graphQlTester: GraphQlTester
	@MockkBean
	private lateinit var stayUnitService: StayUnitService

	/* GET STAY UNIT BY ID TESTS */
	@Test
	fun testGetStayUnitByIdShouldReturnStayUnitById() {
		val stayUnitResponse = StayUnitResponse(
			id = 1L,
			stayNumber = "101",
			numberOfBeds = 2,
			capacity = 4,
			pricePerNight = 150.0,
			roomType = "Deluxe",
			stay = null)
		every { stayUnitService.getStayUnitById(1L) } returns stayUnitResponse
		graphQlTester.document(
			"""
            query {
                getStayUnitById(id: 1) {
                    id
                    stayNumber
                    numberOfBeds
                    capacity
                    pricePerNight
                    roomType
                }
            }
            """)
			.execute()
			.path("getStayUnitById.id").entity(Long::class.java).isEqualTo(1L)
			.path("getStayUnitById.stayNumber").entity(String::class.java).isEqualTo("101")
			.path("getStayUnitById.numberOfBeds").entity(Int::class.java).isEqualTo(2)
			.path("getStayUnitById.capacity").entity(Int::class.java).isEqualTo(4)
			.path("getStayUnitById.pricePerNight").entity(Double::class.java).isEqualTo(150.0)
			.path("getStayUnitById.roomType").entity(String::class.java).isEqualTo("Deluxe")
	}
	@Test
	fun testGetStayUnitByIdShouldReturnStayUnitWithNestedStay() {
		val stayUnitResponse = StayUnitResponse(
			id = 2L,
			stayNumber = "202",
			numberOfBeds = 1,
			capacity = 2,
			pricePerNight = 100.0,
			roomType = "Standard",
			stay = StayResponse(
				id = 1L,
				name = "Grand Hotel",
				address = "123 Main St",
				latitude = 40.7128,
				longitude = -74.0060,
				city = CityResponse(
					id = 1L,
					name = "New York",
					nameAscii = "New York",
					country = null,
					state = null,
					latitude = 40.7128,
					longitude = -74.0060,
					timezone = "America/New_York",
					googlePlaceId = null,
					population = 8000000,
					isCapital = false,
					isFeatured = true),
				stayType = StayTypeResponse(
					id = 1L,
					name = "Hotel"),
				services = listOf(
					ServiceResponse(id = 1L, name = "Wifi", icon = "wifi-icon")
				),
				units = null)
		)
		every { stayUnitService.getStayUnitById(2L) } returns stayUnitResponse
		graphQlTester.document(
			"""
            query {
                getStayUnitById(id: 2) {
                    id
                    stayNumber
                    roomType
                    stay {
                        id
                        name
                        address
                        city {
                            name
                        }
                        stayType {
                            name
                        }
                    }
                }
            }
            """)
			.execute()
			.path("getStayUnitById.id").entity(Long::class.java).isEqualTo(2L)
			.path("getStayUnitById.stay.name").entity(String::class.java).isEqualTo("Grand Hotel")
			.path("getStayUnitById.stay.city.name").entity(String::class.java).isEqualTo("New York")
			.path("getStayUnitById.stay.stayType.name").entity(String::class.java).isEqualTo("Hotel")
	}
	@Test
	fun testGetStayUnitByIdShouldReturnNullWhenStayUnitNotFound() {
		every { stayUnitService.getStayUnitById(999L) } throws ResponseStatusException(
			HttpStatus.NOT_FOUND,
			"Stay unit not found")
		graphQlTester.document(
			"""
            query {
                getStayUnitById(id: 999) {
                    id
                    stayNumber
                }
            }
            """)
			.execute()
			.errors()
			.expect { it.message?.contains("Stay unit not found") == true }
	}

	/* GET STAY UNIT BY STAY ID TESTS */
	@Test
	fun testGetStayUnitsByStayIdShouldReturnUnitsForStay() {
		val stayUnits = listOf(
			StayUnitResponse(
				id = 1L,
				stayNumber = "101",
				numberOfBeds = 2,
				capacity = 4,
				pricePerNight = 150.0,
				roomType = "Deluxe",
				stay = null),
			StayUnitResponse(
				id = 2L,
				stayNumber = "102",
				numberOfBeds = 1,
				capacity = 2,
				pricePerNight = 100.0,
				roomType = "Standard",
				stay = null)
		)
		every { stayUnitService.getStayUnitsByStayId(1L) } returns stayUnits
		graphQlTester.document(
			"""
            query {
                getStayUnitsByStayId(stayId: 1) {
                    id
                    stayNumber
                    numberOfBeds
                    capacity
                    pricePerNight
                    roomType
                }
            }
            """)
			.execute()
			.path("getStayUnitsByStayId[0].id").entity(Long::class.java).isEqualTo(1L)
			.path("getStayUnitsByStayId[0].stayNumber").entity(String::class.java).isEqualTo("101")
			.path("getStayUnitsByStayId[1].id").entity(Long::class.java).isEqualTo(2L)
			.path("getStayUnitsByStayId[1].stayNumber").entity(String::class.java).isEqualTo("102")
	}
	@Test
	fun testGetStayUnitsByStayIdShouldReturnEmptyListWhenNoUnits() {
		every { stayUnitService.getStayUnitsByStayId(999L) } returns emptyList()
		graphQlTester.document(
			"""
            query {
                getStayUnitsByStayId(stayId: 999) {
                    id
                    stayNumber
                }
            }
            """)
			.execute()
			.path("getStayUnitsByStayId[0]").pathDoesNotExist()
	}

	/* SEARCH AVAILABLE UNITS TESTS */
	@Test
	fun testSearchAvailableUnitsShouldReturnFilteredUnits() {
		val availableUnits = listOf(
			StayUnitResponse(
				id = 1L,
				stayNumber = "101",
				numberOfBeds = 2,
				capacity = 4,
				pricePerNight = 120.0,
				roomType = "Deluxe",
				stay = null),
			StayUnitResponse(
				id = 2L,
				stayNumber = "102",
				numberOfBeds = 3,
				capacity = 6,
				pricePerNight = 150.0,
				roomType = "Suite",
				stay = null)
		)
		every { stayUnitService.searchAvailableUnits(1L, 4, BigDecimal.valueOf(200.00)) } returns availableUnits
		graphQlTester.document(
			"""
        query {
            searchAvailableUnits(stayId: 1, minCapacity: 4, maxPrice: 200.00) {
                id
                stayNumber
                capacity
                pricePerNight
                roomType
            }
        }
        """)
			.execute()
			.path("searchAvailableUnits[0].capacity").entity(Int::class.java).isEqualTo(4)
			.path("searchAvailableUnits[0].pricePerNight").entity(Double::class.java).isEqualTo(120.0)
			.path("searchAvailableUnits[1].capacity").entity(Int::class.java).isEqualTo(6)
			.path("searchAvailableUnits[1].pricePerNight").entity(Double::class.java).isEqualTo(150.0)
	}
	@Test
	fun testSearchAvailableUnitsShouldReturnEmptyListWhenNoMatch() {
		every { stayUnitService.searchAvailableUnits(1L, 10, BigDecimal.valueOf(50.00)) } returns emptyList()
		graphQlTester.document(
			"""
            query {
                searchAvailableUnits(stayId: 1, minCapacity: 10, maxPrice: 50.00) {
                    id
                    stayNumber
                }
            }
            """)
			.execute()
			.path("searchAvailableUnits[0]").pathDoesNotExist()
	}
	@Test
	fun testSearchAvailableUnitsWithMinimumCapacity() {
		val availableUnits = listOf(
			StayUnitResponse(
				id = 1L,
				stayNumber = "201",
				numberOfBeds = 1,
				capacity = 2,
				pricePerNight = 100.0,
				roomType = "Standard",
				stay = null),
			StayUnitResponse(
				id = 2L,
				stayNumber = "202",
				numberOfBeds = 2,
				capacity = 4,
				pricePerNight = 150.0,
				roomType = "Deluxe",
				stay = null)
		)
		every { stayUnitService.searchAvailableUnits(2L, 2, BigDecimal.valueOf(200.00)) } returns availableUnits
		graphQlTester.document(
			"""
            query {
                searchAvailableUnits(stayId: 2, minCapacity: 2, maxPrice: 200.00) {
                    id
                    capacity
                    pricePerNight
                }
            }
            """
		)
			.execute()
			.path("searchAvailableUnits[0].capacity").entity(Int::class.java).isEqualTo(2)
			.path("searchAvailableUnits[1].capacity").entity(Int::class.java).isEqualTo(4)
	}
	@Test
	fun testSearchAvailableUnitsWithMaxPrice() {
		val availableUnits = listOf(
			StayUnitResponse(
				id = 1L,
				stayNumber = "301",
				numberOfBeds = 1,
				capacity = 2,
				pricePerNight = 75.0,
				roomType = "Economy",
				stay = null)
		)
		every { stayUnitService.searchAvailableUnits(3L, 1, BigDecimal.valueOf(100.00)) } returns availableUnits
		graphQlTester.document(
			"""
            query {
                searchAvailableUnits(stayId: 3, minCapacity: 1, maxPrice: 100.00) {
                    id
                    pricePerNight
                    roomType
                }
            }
            """)
			.execute()
			.path("searchAvailableUnits[0].pricePerNight").entity(Double::class.java).isEqualTo(75.0)
			.path("searchAvailableUnits[0].roomType").entity(String::class.java).isEqualTo("Economy")
	}
	@Test
	fun testSearchAvailableUnitsWithHighCapacityRequirement() {
		val availableUnits = listOf(
			StayUnitResponse(
				id = 1L,
				stayNumber = "501",
				numberOfBeds = 4,
				capacity = 8,
				pricePerNight = 300.0,
				roomType = "Family Suite",
				stay = null)
		)
		every { stayUnitService.searchAvailableUnits(5L, 8, BigDecimal.valueOf(500.00)) } returns availableUnits
		graphQlTester.document(
			"""
            query {
                searchAvailableUnits(stayId: 5, minCapacity: 8, maxPrice: 500.00) {
                    id
                    capacity
                    roomType
                }
            }
            """)
			.execute()
			.path("searchAvailableUnits[0].capacity").entity(Int::class.java).isEqualTo(8)
			.path("searchAvailableUnits[0].roomType").entity(String::class.java).isEqualTo("Family Suite")
	}

	/* ADDITIONAL TESTS */
	@Test
	fun testGetStayUnitByIdShouldHandleLargeIds() {
		val stayUnitResponse = StayUnitResponse(
			id = 999999L,
			stayNumber = "PENT-999",
			numberOfBeds = 5,
			capacity = 10,
			pricePerNight = 1000.0,
			roomType = "Penthouse",
			stay = null)
		every { stayUnitService.getStayUnitById(999999L) } returns stayUnitResponse
		graphQlTester.document(
			"""
            query {
                getStayUnitById(id: 999999) {
                    id
                    stayNumber
                }
            }
            """)
			.execute()
			.path("getStayUnitById.id").entity(Long::class.java).isEqualTo(999999L)
	}
	@Test
	fun testSearchAvailableUnitsWithExactPriceMatch() {
		val availableUnits = listOf(
			StayUnitResponse(
				id = 1L,
				stayNumber = "601",
				numberOfBeds = 2,
				capacity = 4,
				pricePerNight = 150.0,
				roomType = "Standard",
				stay = null)
		)
		every { stayUnitService.searchAvailableUnits(6L, 4, BigDecimal.valueOf(150.00)) } returns availableUnits
		graphQlTester.document(
			"""
            query {
                searchAvailableUnits(stayId: 6, minCapacity: 4, maxPrice: 150.00) {
                    id
                    pricePerNight
                }
            }
            """)
			.execute()
			.path("searchAvailableUnits[0].pricePerNight").entity(Double::class.java).isEqualTo(150.0)
	}
	@Test
	fun testMultipleQueriesInSingleDocument() {
		val stayUnitResponse = StayUnitResponse(id = 1L, stayNumber = "101", numberOfBeds = 2, capacity = 4, pricePerNight = 150.0, roomType = "Deluxe", stay = null)
		val stayUnits = listOf(
			StayUnitResponse(id = 1L, stayNumber = "101", numberOfBeds = 2, capacity = 4, pricePerNight = 150.0, roomType = "Deluxe", stay = null),
			StayUnitResponse(id = 2L, stayNumber = "102", numberOfBeds = 1, capacity = 2, pricePerNight = 100.0, roomType = "Standard", stay = null)
		)
		every { stayUnitService.getStayUnitById(1L) } returns stayUnitResponse
		every { stayUnitService.getStayUnitsByStayId(1L) } returns stayUnits
		graphQlTester.document(
			"""
            query {
                single: getStayUnitById(id: 1) {
                    id
                    stayNumber
                }
                all: getStayUnitsByStayId(stayId: 1) {
                    id
                    stayNumber
                }
            }
            """)
			.execute()
			.path("single.stayNumber").entity(String::class.java).isEqualTo("101")
			.path("all[0].stayNumber").entity(String::class.java).isEqualTo("101")
			.path("all[1].stayNumber").entity(String::class.java).isEqualTo("102")
	}
	@Test
	fun testGetStayUnitsByStayIdWithSpecialRoomNumbers() {
		val stayUnits = listOf(
			StayUnitResponse(id = 1L, stayNumber = "A-101", numberOfBeds = 1, capacity = 2, pricePerNight = 100.0, roomType = "Standard", stay = null),
			StayUnitResponse(id = 2L, stayNumber = "B-202", numberOfBeds = 2, capacity = 4, pricePerNight = 150.0, roomType = "Deluxe", stay = null),
			StayUnitResponse(id = 3L, stayNumber = "C-303", numberOfBeds = 3, capacity = 6, pricePerNight = 200.0, roomType = "Suite", stay = null)
		)
		every { stayUnitService.getStayUnitsByStayId(7L) } returns stayUnits
		graphQlTester.document(
			"""
            query {
                getStayUnitsByStayId(stayId: 7) {
                    stayNumber
                }
            }
            """)
			.execute()
			.path("getStayUnitsByStayId[0].stayNumber").entity(String::class.java).isEqualTo("A-101")
			.path("getStayUnitsByStayId[1].stayNumber").entity(String::class.java).isEqualTo("B-202")
			.path("getStayUnitsByStayId[2].stayNumber").entity(String::class.java).isEqualTo("C-303")
	}
}
