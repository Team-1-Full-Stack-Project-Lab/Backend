package edu.fullstackproject.team1.resolver

import com.ninjasquad.springmockk.MockkBean
import edu.fullstackproject.team1.config.GraphQLScalarConfig
import edu.fullstackproject.team1.dtos.CityResponse
import edu.fullstackproject.team1.dtos.CountryResponse
import edu.fullstackproject.team1.dtos.StateResponse
import edu.fullstackproject.team1.services.CityService
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.graphql.test.tester.GraphQlTester

@GraphQlTest(CityGraphQLController::class)
@Import(GraphQLScalarConfig::class)
class CityGraphQLControllerTest {
	@Autowired
	private lateinit var graphQlTester: GraphQlTester
	@MockkBean
	private lateinit var cityService: CityService

	/* GET CITY BY ID TESTS */
	@Test
	fun testGetCityByIdShouldReturnCityById() {
		val cityResponse = CityResponse(
			id = 1L,
			name = "Santiago",
			nameAscii = "Santiago",
			country = CountryResponse(
				id = 1L,
				name = "Chile",
				iso2Code = "CL",
				iso3Code = "CHL",
				phoneCode = "+56",
				currencyCode = "CLP",
				currencySymbol = "$",
				region = null,
				states = null,
				cities = null),
			state = null,
			latitude = -33.4489,
			longitude = -70.6693,
			timezone = "America/Santiago",
			googlePlaceId = "ChIJL68lBEHFYpYR4eFNav87s90",
			population = 6000000,
			isCapital = true,
			isFeatured = true)
		every { cityService.getCityById(1L) } returns cityResponse
		graphQlTester.document(
			"""
            query {
                getCityById(id: 1) {
                    id
                    name
                    nameAscii
                    latitude
                    longitude
                    isCapital
                    isFeatured
                }
            }
            """)
			.execute()
			.path("getCityById.id").entity(Long::class.java).isEqualTo(1L)
			.path("getCityById.name").entity(String::class.java).isEqualTo("Santiago")
			.path("getCityById.isCapital").entity(Boolean::class.java).isEqualTo(true)
			.path("getCityById.isFeatured").entity(Boolean::class.java).isEqualTo(true)
	}
	@Test
	fun testGetCityByIdShouldReturnCityWithNestedCountry() {
		val cityResponse = CityResponse(
			id = 2L,
			name = "New York",
			nameAscii = "New York",
			country = CountryResponse(
				id = 2L,
				name = "United States",
				iso2Code = "US",
				iso3Code = "USA",
				phoneCode = "+1",
				currencyCode = "USD",
				currencySymbol = "$",
				region = null,
				states = null,
				cities = null),
			state = StateResponse(
				id = 1L,
				name = "New York",
				code = "NY",
				country = null,
				latitude = 40.7128,
				longitude = -74.0060),
			latitude = 40.7128,
			longitude = -74.0060,
			timezone = "America/New_York",
			googlePlaceId = null,
			population = 8000000,
			isCapital = false,
			isFeatured = true)
		every { cityService.getCityById(2L) } returns cityResponse
		graphQlTester.document(
			"""
            query {
                getCityById(id: 2) {
                    id
                    name
                    country {
                        id
                        name
                        iso2Code
                    }
                    state {
                        id
                        name
                        code
                    }
                }
            }
            """)
			.execute()
			.path("getCityById.name").entity(String::class.java).isEqualTo("New York")
			.path("getCityById.country.name").entity(String::class.java).isEqualTo("United States")
			.path("getCityById.state.name").entity(String::class.java).isEqualTo("New York")
	}
	@Test
	fun testGetCityByIdShouldReturnNullWhenCityNotFound() {
		every { cityService.getCityById(999L) } throws org.springframework.web.server.ResponseStatusException(
			org.springframework.http.HttpStatus.NOT_FOUND,
			"City not found")
		graphQlTester.document(
			"""
            query {
                getCityById(id: 999) {
                    id
                    name
                }
            }
            """)
			.execute()
			.errors()
			.expect { it.message?.contains("City not found") == true }
	}

	/* GET ALL CITIES TESTS */
	@Test
	fun testGetAllCitiesShouldReturnPaginatedCities() {
		val cities = listOf(
			CityResponse(
				id = 1L, name = "Santiago", nameAscii = "Santiago", country = null, state = null,
				latitude = -33.4489, longitude = -70.6693, timezone = null, googlePlaceId = null,
				population = null, isCapital = true, isFeatured = true),
			CityResponse(
				id = 2L, name = "Valparaiso", nameAscii = "Valparaiso", country = null, state = null,
				latitude = -33.0472, longitude = -71.6127, timezone = null, googlePlaceId = null,
				population = null, isCapital = false, isFeatured = true)
		)
		val page: Page<CityResponse> = PageImpl(cities, PageRequest.of(0, 20), 2)
		every { cityService.getAllCities(PageRequest.of(0, 20)) } returns page
		graphQlTester.document(
			"""
            query {
                getAllCities {
                    content {
                        id
                        name
                        isCapital
                    }
                    totalElements
                    totalPages
                    number
                    size
                }
            }
            """)
			.execute()
			.path("getAllCities.content").entityList(CityResponse::class.java).hasSize(2)
			.path("getAllCities.totalElements").entity(Long::class.java).isEqualTo(2L)
			.path("getAllCities.content[0].name").entity(String::class.java).isEqualTo("Santiago")
	}
	@Test
	fun testGetAllCitiesWithCustomPagination() {
		val cities = listOf(
			CityResponse(
				id = 1L, name = "City1", nameAscii = "City1", country = null, state = null,
				latitude = 0.0, longitude = 0.0, timezone = null, googlePlaceId = null,
				population = null, isCapital = false, isFeatured = false)
		)
		val page: Page<CityResponse> = PageImpl(cities, PageRequest.of(2, 5), 11)
		every { cityService.getAllCities(PageRequest.of(2, 5)) } returns page
		graphQlTester.document(
			"""
            query {
                getAllCities(page: 2, size: 5) {
                    content {
                        id
                        name
                    }
                    number
                    size
                    totalElements
                }
            }
            """)
			.execute()
			.path("getAllCities.number").entity(Int::class.java).isEqualTo(2)
			.path("getAllCities.size").entity(Int::class.java).isEqualTo(5)
			.path("getAllCities.totalElements").entity(Long::class.java).isEqualTo(11L)
	}
	@Test
	fun testGetAllCitiesShouldReturnEmptyPage() {
		val page: Page<CityResponse> = PageImpl(emptyList(), PageRequest.of(0, 20), 0)
		every { cityService.getAllCities(PageRequest.of(0, 20)) } returns page
		graphQlTester.document(
			"""
            query {
                getAllCities {
                    content {
                        id
                        name
                    }
                    totalElements
                }
            }
            """)
			.execute()
			.path("getAllCities.totalElements").entity(Long::class.java).isEqualTo(0L)
	}

	/* GET ALL CITIES TESTS WITH FILTERS */
	@Test
	fun testGetAllCitiesFilteredByName() {
		val cities = listOf(
			CityResponse(
				id = 1L, name = "Santiago", nameAscii = "Santiago", country = null, state = null,
				latitude = -33.4489, longitude = -70.6693, timezone = null, googlePlaceId = null,
				population = null, isCapital = true, isFeatured = true)
		)
		val page: Page<CityResponse> = PageImpl(cities, PageRequest.of(0, 20), 1)
		every { cityService.searchCities(null, null, "Santiago", null, PageRequest.of(0, 20)) } returns page
		graphQlTester.document(
			"""
            query {
                getAllCities(name: "Santiago") {
                    content {
                        id
                        name
                    }
                    totalElements
                }
            }
            """)
			.execute()
			.path("getAllCities.content").entityList(CityResponse::class.java).hasSize(1)
			.path("getAllCities.content[0].name").entity(String::class.java).isEqualTo("Santiago")
	}

	/* GET ALL CITIES TESTS FILTERED BY COUNTRY */
	@Test
	fun testGetAllCitiesFilteredByCountry() {
		val cities = listOf(
			CityResponse(
				id = 1L, name = "Santiago", nameAscii = "Santiago", country = null, state = null,
				latitude = -33.4489, longitude = -70.6693, timezone = null, googlePlaceId = null,
				population = null, isCapital = true, isFeatured = true),
			CityResponse(
				id = 2L, name = "Valparaiso", nameAscii = "Valparaiso", country = null, state = null,
				latitude = -33.0472, longitude = -71.6127, timezone = null, googlePlaceId = null,
				population = null, isCapital = false, isFeatured = true)
		)
		val page: Page<CityResponse> = PageImpl(cities, PageRequest.of(0, 20), 2)
		every { cityService.searchCities(1L, null, null, null, PageRequest.of(0, 20)) } returns page
		graphQlTester.document(
			"""
            query {
                getAllCities(country: 1) {
                    content {
                        id
                        name
                    }
                    totalElements
                }
            }
            """)
			.execute()
			.path("getAllCities.content").entityList(CityResponse::class.java).hasSize(2)
			.path("getAllCities.totalElements").entity(Long::class.java).isEqualTo(2L)
	}

	/* GET ALL CITIES TEST WITH MULTIPLE FILTERS */
	@Test
	fun testGetAllCitiesWithMultipleFilters() {
		val cities = listOf(
			CityResponse(
				id = 1L, name = "Santiago", nameAscii = "Santiago", country = null, state = null,
				latitude = -33.4489, longitude = -70.6693, timezone = null, googlePlaceId = null,
				population = null, isCapital = true, isFeatured = true)
		)
		val page: Page<CityResponse> = PageImpl(cities, PageRequest.of(0, 20), 1)
		every { cityService.searchCities(1L, null, "Santiago", true, PageRequest.of(0, 20)) } returns page
		graphQlTester.document(
			"""
            query {
                getAllCities(name: "Santiago", country: 1, featured: true) {
                    content {
                        id
                        name
                        isCapital
                        isFeatured
                    }
                }
            }
            """)
			.execute()
			.path("getAllCities.content").entityList(CityResponse::class.java).hasSize(1)
			.path("getAllCities.content[0].name").entity(String::class.java).isEqualTo("Santiago")
			.path("getAllCities.content[0].isFeatured").entity(Boolean::class.java).isEqualTo(true)
	}
	@Test
	fun testGetAllCitiesWithAllFiltersAndPagination() {
		val cities = listOf(
			CityResponse(
				id = 1L, name = "Berlin", nameAscii = "Berlin", country = null, state = null,
				latitude = 52.5200, longitude = 13.4050, timezone = null, googlePlaceId = null,
				population = null, isCapital = true, isFeatured = true
			)
		)
		val page: Page<CityResponse> = PageImpl(cities, PageRequest.of(1, 10), 1)
		every { cityService.searchCities(3L, 2L, "Berlin", true, PageRequest.of(1, 10)) } returns page
		graphQlTester.document(
			"""
            query {
                getAllCities(name: "Berlin", country: 3, state: 2, featured: true, page: 1, size: 10) {
                    content {
                        id
                        name
                    }
                    number
                    size
                }
            }
            """)
			.execute()
			.path("getAllCities.content[0].name").entity(String::class.java).isEqualTo("Berlin")
			.path("getAllCities.number").entity(Int::class.java).isEqualTo(1)
			.path("getAllCities.size").entity(Int::class.java).isEqualTo(10)
	}

	/* ADDITIONAL TESTS */
	@Test
	fun testGetAllCitiesWithNoResultsFromFilter() {
		val page: Page<CityResponse> = PageImpl(emptyList(), PageRequest.of(0, 20), 0)
		every { cityService.searchCities(null, null, "NonExistentCity", null, PageRequest.of(0, 20)) } returns page
		graphQlTester.document(
			"""
            query {
                getAllCities(name: "NonExistentCity") {
                    content {
                        id
                        name
                    }
                    totalElements
                }
            }
            """)
			.execute()
			.path("getAllCities.content").entityList(CityResponse::class.java).hasSize(0)
			.path("getAllCities.totalElements").entity(Long::class.java).isEqualTo(0L)
	}
	@Test
	fun testGetCityByIdWithNullOptionalFields() {
		val cityResponse = CityResponse(
			id = 100L,
			name = "SmallCity",
			nameAscii = null,
			country = null,
			state = null,
			latitude = 0.0,
			longitude = 0.0,
			timezone = null,
			googlePlaceId = null,
			population = null,
			isCapital = false,
			isFeatured = false
		)
		every { cityService.getCityById(100L) } returns cityResponse
		graphQlTester.document(
			"""
            query {
                getCityById(id: 100) {
                    id
                    name
                    nameAscii
                    timezone
                    population
                }
            }
            """)
			.execute()
			.path("getCityById.name").entity(String::class.java).isEqualTo("SmallCity")
			.path("getCityById.nameAscii").valueIsNull()
			.path("getCityById.timezone").valueIsNull()
			.path("getCityById.population").valueIsNull()
	}
	@Test
	fun testGetAllCitiesDefaultPaginationValues() {
		val cities = listOf(
			CityResponse(
				id = 1L, name = "City1", nameAscii = "City1", country = null, state = null,
				latitude = 0.0, longitude = 0.0, timezone = null, googlePlaceId = null,
				population = null, isCapital = false, isFeatured = false)
		)
		val page: Page<CityResponse> = PageImpl(cities, PageRequest.of(0, 20), 1)
		every { cityService.getAllCities(PageRequest.of(0, 20)) } returns page
		graphQlTester.document(
			"""
            query {
                getAllCities {
                    content {
                        id
                    }
                    number
                    size
                }
            }
            """)
			.execute()
			.path("getAllCities.number").entity(Int::class.java).isEqualTo(0)
			.path("getAllCities.size").entity(Int::class.java).isEqualTo(20)
	}
}
