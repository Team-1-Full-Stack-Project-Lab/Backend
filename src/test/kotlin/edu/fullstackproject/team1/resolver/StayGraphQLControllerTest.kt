package edu.fullstackproject.team1.resolver

import com.ninjasquad.springmockk.MockkBean
import edu.fullstackproject.team1.config.GraphQLScalarConfig
import edu.fullstackproject.team1.dtos.CityResponse
import edu.fullstackproject.team1.dtos.ServiceResponse
import edu.fullstackproject.team1.dtos.StayResponse
import edu.fullstackproject.team1.dtos.StayTypeResponse
import edu.fullstackproject.team1.dtos.StayUnitResponse
import edu.fullstackproject.team1.services.StayService
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.graphql.test.tester.GraphQlTester
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

@GraphQlTest(StayGraphQLController::class)
@Import(GraphQLScalarConfig::class)
class StayGraphQLControllerTest {
	@Autowired
	private lateinit var graphQlTester: GraphQlTester
	@MockkBean
	private lateinit var stayService: StayService

	/* GET STAY BY ID TESTS */
	@Test
	fun testGetStayByIdShouldReturnStayById() {
		val stayResponse = StayResponse(
			id = 1L,
			name = "Grand Hotel",
			address = "123 Main Street",
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
				ServiceResponse(id = 1L, name = "Wifi", icon = "Wifi"),
				ServiceResponse(id = 2L, name = "Parking", icon = "Car")
			),
			units = null)
		every { stayService.getStayById(1L) } returns stayResponse
		graphQlTester.document(
			"""
            query {
                getStayById(id: 1) {
                    id
                    name
                    address
                    latitude
                    longitude
                    city {
                        name
                    }
                    stayType {
                        name
                    }
                    services {
                        name
                    }
                }
            }
            """)
			.execute()
			.path("getStayById.id").entity(Long::class.java).isEqualTo(1L)
			.path("getStayById.name").entity(String::class.java).isEqualTo("Grand Hotel")
			.path("getStayById.address").entity(String::class.java).isEqualTo("123 Main Street")
			.path("getStayById.city.name").entity(String::class.java).isEqualTo("New York")
			.path("getStayById.stayType.name").entity(String::class.java).isEqualTo("Hotel")
	}
	@Test
	fun testGetStayByIdShouldReturnStayWithUnits() {
		val stayResponse = StayResponse(
			id = 2L,
			name = "Comfort Inn",
			address = "456 Park Avenue",
			latitude = 40.7589,
			longitude = -73.9851,
			city = null,
			stayType = StayTypeResponse(id = 2L, name = "Hostel"),
			services = null,
			units = listOf(
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
		)
		every { stayService.getStayById(2L) } returns stayResponse
		graphQlTester.document(
			"""
            query {
                getStayById(id: 2) {
                    id
                    name
                    units {
                        id
                        stayNumber
                        pricePerNight
                        roomType
                    }
                }
            }
            """)
			.execute()
			.path("getStayById.name").entity(String::class.java).isEqualTo("Comfort Inn")
			.path("getStayById.units[0].stayNumber").entity(String::class.java).isEqualTo("101")
			.path("getStayById.units[1].stayNumber").entity(String::class.java).isEqualTo("102")
	}
	@Test
	fun testGetStayByIdShouldReturnNullWhenStayNotFound() {
		every { stayService.getStayById(999L) } throws ResponseStatusException(
			HttpStatus.NOT_FOUND,
			"Stay not found")
		graphQlTester.document(
			"""
            query {
                getStayById(id: 999) {
                    id
                    name
                }
            }
            """)
			.execute()
			.errors()
			.expect { it.message?.contains("Stay not found") == true }
	}
	@Test
	fun testGetStayByIdWithAllNestedData() {
		val stayResponse = StayResponse(
			id = 5L,
			name = "City Center Hotel",
			address = "555 Downtown Blvd",
			latitude = 34.0522,
			longitude = -118.2437,
			city = CityResponse(
				id = 2L,
				name = "Los Angeles",
				nameAscii = "Los Angeles",
				country = null,
				state = null,
				latitude = 34.0522,
				longitude = -118.2437,
				timezone = "America/Los_Angeles",
				googlePlaceId = null,
				population = 4000000,
				isCapital = false,
				isFeatured = true),
			stayType = StayTypeResponse(id = 1L, name = "Hotel"),
			services = listOf(
				ServiceResponse(id = 1L, name = "Wifi", icon = "wifi"),
				ServiceResponse(id = 2L, name = "Pool", icon = "pool"),
				ServiceResponse(id = 3L, name = "Gym", icon = "gym")
			),
			units = listOf(
				StayUnitResponse(id = 1L, stayNumber = "201", numberOfBeds = 2, capacity = 4, pricePerNight = 180.0, roomType = "Deluxe", stay = null)
			)
		)
		every { stayService.getStayById(5L) } returns stayResponse
		graphQlTester.document(
			"""
            query {
                getStayById(id: 5) {
                    id
                    name
                    city {
                        name
                        population
                    }
                    stayType {
                        name
                    }
                    services {
                        name
                        icon
                    }
                    units {
                        stayNumber
                        pricePerNight
                    }
                }
            }
            """)
			.execute()
			.path("getStayById.name").entity(String::class.java).isEqualTo("City Center Hotel")
			.path("getStayById.city.name").entity(String::class.java).isEqualTo("Los Angeles")
			.path("getStayById.city.population").entity(Int::class.java).isEqualTo(4000000)
	}

	/* GET ALL STAYS TESTS */
	@Test
	fun testGetAllStaysShouldReturnPaginatedStays() {
		val stays = listOf(
			StayResponse(
				id = 1L,
				name = "Grand Hotel",
				address = "123 Main St",
				latitude = 40.7128,
				longitude = -74.0060,
				city = null,
				stayType = null,
				services = null,
				units = null),
			StayResponse(
				id = 2L,
				name = "Comfort Inn",
				address = "456 Park Ave",
				latitude = 40.7589,
				longitude = -73.9851,
				city = null,
				stayType = null,
				services = null,
				units = null)
		)
		val page: Page<StayResponse> = PageImpl(stays, PageRequest.of(0, 20), 2)
		every { stayService.getAllStays(PageRequest.of(0, 20)) } returns page
		graphQlTester.document(
			"""
            query {
                getAllStays {
                    content {
                        id
                        name
                        address
                    }
                    totalElements
                    number
                    size
                }
            }
            """)
			.execute()
			.path("getAllStays.totalElements").entity(Long::class.java).isEqualTo(2L)
			.path("getAllStays.content[0].name").entity(String::class.java).isEqualTo("Grand Hotel")
			.path("getAllStays.content[1].name").entity(String::class.java).isEqualTo("Comfort Inn")
	}
	@Test
	fun testGetAllStaysShouldReturnEmptyPage() {
		val page: Page<StayResponse> = PageImpl(emptyList(), PageRequest.of(0, 20), 0)
		every { stayService.getAllStays(PageRequest.of(0, 20)) } returns page
		graphQlTester.document(
			"""
            query {
                getAllStays {
                    content {
                        id
                        name
                    }
                    totalElements
                }
            }
            """)
			.execute()
			.path("getAllStays.totalElements").entity(Long::class.java).isEqualTo(0L)
	}

	/* GET STAYS BY CITY TESTS */
	@Test
	fun testGetStaysByCityShouldReturnStaysForCity() {
		val stays = listOf(
			StayResponse(
				id = 1L,
				name = "NYC Hotel 1",
				address = "123 Broadway",
				latitude = 40.7128,
				longitude = -74.0060,
				city = null,
				stayType = null,
				services = null,
				units = null),
			StayResponse(
				id = 2L,
				name = "NYC Hotel 2",
				address = "456 5th Ave",
				latitude = 40.7614,
				longitude = -73.9776,
				city = null,
				stayType = null,
				services = null,
				units = null)
		)
		val page: Page<StayResponse> = PageImpl(stays, PageRequest.of(0, 20), 2)
		every { stayService.getStaysByCity(1L, PageRequest.of(0, 20)) } returns page
		graphQlTester.document(
			"""
            query {
                getStaysByCity(cityId: 1) {
                    content {
                        id
                        name
                        address
                    }
                    totalElements
                }
            }
            """)
			.execute()
			.path("getStaysByCity.totalElements").entity(Long::class.java).isEqualTo(2L)
			.path("getStaysByCity.content[0].name").entity(String::class.java).isEqualTo("NYC Hotel 1")
			.path("getStaysByCity.content[1].name").entity(String::class.java).isEqualTo("NYC Hotel 2")
	}
	@Test
	fun testGetStaysByCityShouldReturnEmptyWhenNoCityStays() {
		val page: Page<StayResponse> = PageImpl(emptyList(), PageRequest.of(0, 20), 0)
		every { stayService.getStaysByCity(999L, PageRequest.of(0, 20)) } returns page
		graphQlTester.document(
			"""
            query {
                getStaysByCity(cityId: 999) {
                    content {
                        id
                        name
                    }
                    totalElements
                }
            }
            """)
			.execute()
			.path("getStaysByCity.totalElements").entity(Long::class.java).isEqualTo(0L)
	}
	@Test
	fun testGetStaysByCityWithCustomPagination() {
		val stays = listOf(
			StayResponse(id = 1L, name = "Stay 1", address = "Addr 1", latitude = 0.0, longitude = 0.0, city = null, stayType = null, services = null, units = null)
		)
		val page: Page<StayResponse> = PageImpl(stays, PageRequest.of(2, 5), 12)
		every { stayService.getStaysByCity(2L, PageRequest.of(2, 5)) } returns page
		graphQlTester.document(
			"""
            query {
                getStaysByCity(cityId: 2, page: 2, size: 5) {
                    content {
                        name
                    }
                    number
                    size
                }
            }
            """)
			.execute()
			.path("getStaysByCity.number").entity(Int::class.java).isEqualTo(2)
			.path("getStaysByCity.size").entity(Int::class.java).isEqualTo(5)
	}

	/* SEARCH STAYS NEARBY TESTS */
	@Test
	fun testSearchStaysNearbyShouldReturnStaysWithinRadius() {
		val stays = listOf(
			StayResponse(
				id = 1L,
				name = "Nearby Hotel 1",
				address = "Close St",
				latitude = 40.7128,
				longitude = -74.0060,
				city = null,
				stayType = null,
				services = null,
				units = null),
			StayResponse(
				id = 2L,
				name = "Nearby Hotel 2",
				address = "Near Ave",
				latitude = 40.7150,
				longitude = -74.0080,
				city = null,
				stayType = null,
				services = null,
				units = null)
		)
		val page: Page<StayResponse> = PageImpl(stays, PageRequest.of(0, 20), 2)
		every { stayService.searchStaysNearby(40.7128, -74.0060, 5.0, PageRequest.of(0, 20)) } returns page
		graphQlTester.document(
			"""
            query {
                searchStaysNearby(latitude: 40.7128, longitude: -74.0060, radiusKm: 5.0) {
                    content {
                        id
                        name
                        latitude
                        longitude
                    }
                    totalElements
                }
            }
            """)
			.execute()
			.path("searchStaysNearby.totalElements").entity(Long::class.java).isEqualTo(2L)
			.path("searchStaysNearby.content[0].name").entity(String::class.java).isEqualTo("Nearby Hotel 1")
			.path("searchStaysNearby.content[1].name").entity(String::class.java).isEqualTo("Nearby Hotel 2")
	}
	@Test
	fun testSearchStaysNearbyShouldReturnEmptyWhenNoStaysNearby() {
		val page: Page<StayResponse> = PageImpl(emptyList(), PageRequest.of(0, 20), 0)
		every { stayService.searchStaysNearby(0.0, 0.0, 1.0, PageRequest.of(0, 20)) } returns page
		graphQlTester.document(
			"""
            query {
                searchStaysNearby(latitude: 0.0, longitude: 0.0, radiusKm: 1.0) {
                    content {
                        id
                        name
                    }
                    totalElements
                }
            }
            """)
			.execute()
			.path("searchStaysNearby.totalElements").entity(Long::class.java).isEqualTo(0)
	}
	@Test
	fun testSearchStaysNearbyWithLargeRadius() {
		val stays = listOf(
			StayResponse(id = 1L, name = "Far Hotel", address = "Distant Rd", latitude = 50.0, longitude = -100.0, city = null, stayType = null, services = null, units = null)
		)
		val page: Page<StayResponse> = PageImpl(stays, PageRequest.of(0, 20), 1)
		every { stayService.searchStaysNearby(40.0, -90.0, 50.0, PageRequest.of(0, 20)) } returns page
		graphQlTester.document(
			"""
            query {
                searchStaysNearby(latitude: 40.0, longitude: -90.0, radiusKm: 50.0) {
                    content {
                        name
                    }
                }
            }
            """)
			.execute()
			.path("searchStaysNearby.content[0].name").entity(String::class.java).isEqualTo("Far Hotel")
	}
	@Test
	fun testSearchStaysNearbyWithCustomPagination() {
		val stays = listOf(
			StayResponse(id = 1L, name = "Hotel 1", address = "Addr 1", latitude = 0.0, longitude = 0.0, city = null, stayType = null, services = null, units = null)
		)
		val page: Page<StayResponse> = PageImpl(stays, PageRequest.of(1, 5), 8)
		every { stayService.searchStaysNearby(35.0, -120.0, 10.0, PageRequest.of(1, 5)) } returns page
		graphQlTester.document(
			"""
            query {
                searchStaysNearby(latitude: 35.0, longitude: -120.0, radiusKm: 10.0, page: 1, size: 5) {
                    content {
                        name
                    }
                    number
                    size
                }
            }
            """)
			.execute()
			.path("searchStaysNearby.number").entity(Int::class.java).isEqualTo(1)
			.path("searchStaysNearby.size").entity(Int::class.java).isEqualTo(5)
	}
	@Test
	fun testSearchStaysNearbyWithNegativeCoordinates() {
		val stays = listOf(
			StayResponse(id = 1L, name = "Southern Hotel", address = "South Rd", latitude = -33.8688, longitude = 151.2093, city = null, stayType = null, services = null, units = null)
		)
		val page: Page<StayResponse> = PageImpl(stays, PageRequest.of(0, 20), 1)
		every { stayService.searchStaysNearby(-33.8688, 151.2093, 3.0, PageRequest.of(0, 20)) } returns page
		graphQlTester.document(
			"""
            query {
                searchStaysNearby(latitude: -33.8688, longitude: 151.2093, radiusKm: 3.0) {
                    content {
                        name
                        latitude
                        longitude
                    }
                }
            }
            """)
			.execute()
			.path("searchStaysNearby.content[0].name").entity(String::class.java).isEqualTo("Southern Hotel")
			.path("searchStaysNearby.content[0].latitude").entity(Double::class.java).isEqualTo(-33.8688)
	}

	/* ADDITIONAL TESTS */
	@Test
	fun testGetStayByIdShouldHandleLargeIds() {
		val stayResponse = StayResponse(
			id = 999999L,
			name = "Luxury Resort",
			address = "Paradise Island",
			latitude = 0.0,
			longitude = 0.0,
			city = null,
			stayType = null,
			services = null,
			units = null)
		every { stayService.getStayById(999999L) } returns stayResponse
		graphQlTester.document(
			"""
            query {
                getStayById(id: 999999) {
                    id
                    name
                }
            }
            """)
			.execute()
			.path("getStayById.id").entity(Long::class.java).isEqualTo(999999L)
	}
	@Test
	fun testMultipleQueriesInSingleDocument() {
		val stayResponse = StayResponse(id = 1L, name = "Hotel A", address = "Addr A", latitude = 0.0, longitude = 0.0, city = null, stayType = null, services = null, units = null)
		val stays = listOf(stayResponse)
		val page: Page<StayResponse> = PageImpl(stays, PageRequest.of(0, 20), 1)
		every { stayService.getStayById(1L) } returns stayResponse
		every { stayService.getAllStays(PageRequest.of(0, 20)) } returns page
		graphQlTester.document(
			"""
            query {
                single: getStayById(id: 1) {
                    id
                    name
                }
                all: getAllStays {
                    content {
                        name
                    }
                    totalElements
                }
            }
            """)
			.execute()
			.path("single.name").entity(String::class.java).isEqualTo("Hotel A")
			.path("all.totalElements").entity(Long::class.java).isEqualTo(1L)
	}
	@Test
	fun testGetAllStaysDefaultPaginationValues() {
		val stays = listOf(
			StayResponse(id = 1L, name = "Default Hotel", address = "Default St", latitude = 0.0, longitude = 0.0, city = null, stayType = null, services = null, units = null)
		)
		val page: Page<StayResponse> = PageImpl(stays, PageRequest.of(0, 20), 1)
		every { stayService.getAllStays(PageRequest.of(0, 20)) } returns page
		graphQlTester.document(
			"""
            query {
                getAllStays {
                    content {
                        id
                    }
                    number
                    size
                }
            }
            """)
			.execute()
			.path("getAllStays.number").entity(Int::class.java).isEqualTo(0)
			.path("getAllStays.size").entity(Int::class.java).isEqualTo(20)
	}
}
