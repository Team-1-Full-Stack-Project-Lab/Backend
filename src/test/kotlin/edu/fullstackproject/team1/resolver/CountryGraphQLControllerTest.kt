package edu.fullstackproject.team1.resolver

import com.ninjasquad.springmockk.MockkBean
import edu.fullstackproject.team1.config.GraphQLScalarConfig
import edu.fullstackproject.team1.dtos.CityResponse
import edu.fullstackproject.team1.dtos.CountryResponse
import edu.fullstackproject.team1.dtos.RegionResponse
import edu.fullstackproject.team1.dtos.StateResponse
import edu.fullstackproject.team1.services.CountryService
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest
import org.springframework.context.annotation.Import
import org.springframework.graphql.test.tester.GraphQlTester
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

@GraphQlTest(CountryGraphQLController::class)
@Import(GraphQLScalarConfig::class)
class CountryGraphQLControllerTest {
	@Autowired
	private lateinit var graphQlTester: GraphQlTester
	@MockkBean
	private lateinit var countryService: CountryService

	/* GET COUNTRY BY ID TESTS */
	@Test
	fun testGetCountryByIdShouldReturnCountryById() {
		val countryResponse = CountryResponse(
			id = 1L,
			name = "Argentina",
			iso2Code = "AR",
			iso3Code = "ARG",
			phoneCode = "+54",
			currencyCode = "ARS",
			currencySymbol = "$",
			region = null,
			states = null,
			cities = null)
		every { countryService.getCountryById(1L) } returns countryResponse
		graphQlTester.document(
			"""
            query {
                getCountryById(id: 1) {
                    id
                    name
                    iso2Code
                    iso3Code
                    phoneCode
                    currencyCode
                    currencySymbol
                }
            }
            """)
			.execute()
			.path("getCountryById.id").entity(Long::class.java).isEqualTo(1L)
			.path("getCountryById.name").entity(String::class.java).isEqualTo("Argentina")
			.path("getCountryById.iso2Code").entity(String::class.java).isEqualTo("AR")
			.path("getCountryById.iso3Code").entity(String::class.java).isEqualTo("ARG")
			.path("getCountryById.phoneCode").entity(String::class.java).isEqualTo("+54")
			.path("getCountryById.currencyCode").entity(String::class.java).isEqualTo("ARS")
			.path("getCountryById.currencySymbol").entity(String::class.java).isEqualTo("$")
	}
	@Test
	fun testGetCountryByIdShouldReturnCountryWithNestedRegion() {
		val countryResponse = CountryResponse(
			id = 2L,
			name = "Brazil",
			iso2Code = "BR",
			iso3Code = "BRA",
			phoneCode = "+55",
			currencyCode = "BRL",
			currencySymbol = "R$",
			region = RegionResponse(
				id = 1L,
				name = "South America",
				code = "SA",
				countries = null),
			states = null,
			cities = null)
		every { countryService.getCountryById(2L) } returns countryResponse
		graphQlTester.document(
			"""
            query {
                getCountryById(id: 2) {
                    id
                    name
                    iso2Code
                    region {
                        id
                        name
                        code
                    }
                }
            }
            """)
			.execute()
			.path("getCountryById.id").entity(Long::class.java).isEqualTo(2L)
			.path("getCountryById.name").entity(String::class.java).isEqualTo("Brazil")
			.path("getCountryById.region.name").entity(String::class.java).isEqualTo("South America")
			.path("getCountryById.region.code").entity(String::class.java).isEqualTo("SA")
	}
	@Test
	fun testGetCountryByIdShouldReturnCountryWithStates() {
		val countryResponse = CountryResponse(
			id = 3L,
			name = "United States",
			iso2Code = "US",
			iso3Code = "USA",
			phoneCode = "+1",
			currencyCode = "USD",
			currencySymbol = "$",
			region = null,
			states = listOf(
				StateResponse(
					id = 1L,
					name = "California",
					code = "CA",
					country = null,
					latitude = 36.7783,
					longitude = -119.4179
				),
				StateResponse(
					id = 2L,
					name = "Texas",
					code = "TX",
					country = null,
					latitude = 31.9686,
					longitude = -99.9018)
			),
			cities = null)
		every { countryService.getCountryById(3L) } returns countryResponse
		graphQlTester.document(
			"""
            query {
                getCountryById(id: 3) {
                    id
                    name
                    states {
                        id
                        name
                        code
                    }
                }
            }
            """)
			.execute()
			.path("getCountryById.name").entity(String::class.java).isEqualTo("United States")
			.path("getCountryById.states").entityList(StateResponse::class.java).hasSize(2)
			.path("getCountryById.states[0].name").entity(String::class.java).isEqualTo("California")
			.path("getCountryById.states[1].name").entity(String::class.java).isEqualTo("Texas")
	}
	@Test
	fun testGetCountryByIdShouldReturnCountryWithCities() {
		val countryResponse = CountryResponse(
			id = 4L,
			name = "Chile",
			iso2Code = "CL",
			iso3Code = "CHL",
			phoneCode = "+56",
			currencyCode = "CLP",
			currencySymbol = "$",
			region = null,
			states = null,
			cities = listOf(
				CityResponse(
					id = 1L,
					name = "Santiago",
					nameAscii = "Santiago",
					country = null,
					state = null,
					latitude = -33.4489,
					longitude = -70.6693,
					timezone = "America/Santiago",
					googlePlaceId = null,
					population = 6000000,
					isCapital = true,
					isFeatured = true),
				CityResponse(
					id = 2L,
					name = "Valparaíso",
					nameAscii = "Valparaiso",
					country = null,
					state = null,
					latitude = -33.0472,
					longitude = -71.6127,
					timezone = "America/Santiago",
					googlePlaceId = null,
					population = 300000,
					isCapital = false,
					isFeatured = true)
			)
		)
		every { countryService.getCountryById(4L) } returns countryResponse
		graphQlTester.document(
			"""
            query {
                getCountryById(id: 4) {
                    id
                    name
                    cities {
                        id
                        name
                        isCapital
                    }
                }
            }
            """)
			.execute()
			.path("getCountryById.name").entity(String::class.java).isEqualTo("Chile")
			.path("getCountryById.cities").entityList(CityResponse::class.java).hasSize(2)
			.path("getCountryById.cities[0].name").entity(String::class.java).isEqualTo("Santiago")
			.path("getCountryById.cities[0].isCapital").entity(Boolean::class.java).isEqualTo(true)
	}
	@Test
	fun testGetCountryByIdShouldReturnCountryWithAllNestedData() {
		val countryResponse = CountryResponse(
			id = 5L,
			name = "Mexico",
			iso2Code = "MX",
			iso3Code = "MEX",
			phoneCode = "+52",
			currencyCode = "MXN",
			currencySymbol = "$",
			region = RegionResponse(
				id = 2L,
				name = "North America",
				code = "NA",
				countries = null
			),
			states = listOf(
				StateResponse(
					id = 10L,
					name = "Jalisco",
					code = "JAL",
					country = null,
					latitude = 20.6595,
					longitude = -103.3494
				)
			),
			cities = listOf(
				CityResponse(
					id = 20L,
					name = "Mexico City",
					nameAscii = "Mexico City",
					country = null,
					state = null,
					latitude = 19.4326,
					longitude = -99.1332,
					timezone = "America/Mexico_City",
					googlePlaceId = null,
					population = 9000000,
					isCapital = true,
					isFeatured = true)
			)
		)
		every { countryService.getCountryById(5L) } returns countryResponse
		graphQlTester.document(
			"""
            query {
                getCountryById(id: 5) {
                    id
                    name
                    region {
                        name
                    }
                    states {
                        name
                    }
                    cities {
                        name
                    }
                }
            }
            """)
			.execute()
			.path("getCountryById.name").entity(String::class.java).isEqualTo("Mexico")
			.path("getCountryById.region.name").entity(String::class.java).isEqualTo("North America")
			.path("getCountryById.states").entityList(StateResponse::class.java).hasSize(1)
			.path("getCountryById.cities").entityList(CityResponse::class.java).hasSize(1)
	}
	@Test
	fun testGetCountryByIdShouldReturnNullWhenCountryNotFound() {
		every { countryService.getCountryById(999L) } throws ResponseStatusException(
			HttpStatus.NOT_FOUND,
			"Country not found"
		)
		graphQlTester.document(
			"""
            query {
                getCountryById(id: 999) {
                    id
                    name
                }
            }
            """)
			.execute()
			.errors()
			.expect { it.message?.contains("Country not found") == true }
	}

	/* GET COUNTRIES TESTS */
	@Test
	fun testGetCountriesShouldReturnAllCountries() {
		val countries = listOf(
			CountryResponse(
				id = 1L,
				name = "Argentina",
				iso2Code = "AR",
				iso3Code = "ARG",
				phoneCode = "+54",
				currencyCode = "ARS",
				currencySymbol = "$",
				region = null,
				states = null,
				cities = null),
			CountryResponse(
				id = 2L,
				name = "Brazil",
				iso2Code = "BR",
				iso3Code = "BRA",
				phoneCode = "+55",
				currencyCode = "BRL",
				currencySymbol = "R$",
				region = null,
				states = null,
				cities = null)
		)
		every { countryService.getAllCountries() } returns countries
		graphQlTester.document(
			"""
            query {
                getCountries {
                    id
                    name
                    iso2Code
                    iso3Code
                }
            }
            """)
			.execute()
			.path("getCountries").entityList(CountryResponse::class.java).hasSize(2)
			.path("getCountries[0].name").entity(String::class.java).isEqualTo("Argentina")
			.path("getCountries[1].name").entity(String::class.java).isEqualTo("Brazil")
	}
	@Test
	fun testGetCountriesShouldReturnEmptyListWhenNoCountriesExist() {
		every { countryService.getAllCountries() } returns emptyList()
		graphQlTester.document(
			"""
            query {
                getCountries {
                    id
                    name
                }
            }
            """)
			.execute()
			.path("getCountries").entityList(CountryResponse::class.java).hasSize(0)
	}


	/* GET COUNTRIES TESTS FILTER BY NAME */
	@Test
	fun testGetCountriesByNameShouldReturnFilteredCountries() {
		val countries = listOf(
			CountryResponse(
				id = 1L,
				name = "France",
				iso2Code = "FR",
				iso3Code = "FRA",
				phoneCode = "+33",
				currencyCode = "EUR",
				currencySymbol = "€",
				region = null,
				states = null,
				cities = null)
		)
		every { countryService.getCountriesByName("France") } returns countries
		graphQlTester.document(
			"""
            query {
                getCountries(name: "France") {
                    id
                    name
                    iso2Code
                    iso3Code
                    phoneCode
                    currencyCode
                    currencySymbol
                }
            }
            """)
			.execute()
			.path("getCountries").entityList(CountryResponse::class.java).hasSize(1)
			.path("getCountries[0].id").entity(Long::class.java).isEqualTo(1L)
			.path("getCountries[0].name").entity(String::class.java).isEqualTo("France")
			.path("getCountries[0].iso2Code").entity(String::class.java).isEqualTo("FR")
	}
	@Test
	fun testGetCountriesByNameShouldReturnEmptyListWhenNoMatch() {
		every { countryService.getCountriesByName("NonExistentCountry") } returns emptyList()
		graphQlTester.document(
			"""
            query {
                getCountries(name: "NonExistentCountry") {
                    id
                    name
                }
            }
            """)
			.execute()
			.path("getCountries").entityList(CountryResponse::class.java).hasSize(0)
	}
	@Test
	fun testGetCountriesByNameShouldBeCaseInsensitive() {
		val countries = listOf(
			CountryResponse(
				id = 1L,
				name = "Germany",
				iso2Code = "DE",
				iso3Code = "DEU",
				phoneCode = "+49",
				currencyCode = "EUR",
				currencySymbol = "€",
				region = null,
				states = null,
				cities = null)
		)
		every { countryService.getCountriesByName("germany") } returns countries
		graphQlTester.document(
			"""
            query {
                getCountries(name: "germany") {
					name
					currencySymbol
				}
            }
            """)
			.execute()
			.path("getCountries[0].name").entity(String::class.java).isEqualTo("Germany")
			.path("getCountries[0].currencySymbol").entity(String::class.java).isEqualTo("€")
	}
	@Test
	fun testGetCountriesByPartialNameMatch() {
		val countries = listOf(
			CountryResponse(
				id = 1L,
				name = "South Africa",
				iso2Code = "ZA",
				iso3Code = "ZAF",
				phoneCode = "+27",
				currencyCode = "ZAR",
				currencySymbol = "R",
				region = null,
				states = null,
				cities = null),
			CountryResponse(
				id = 2L,
				name = "South Korea",
				iso2Code = "KR",
				iso3Code = "KOR",
				phoneCode = "+82",
				currencyCode = "KRW",
				currencySymbol = "₩",
				region = null,
				states = null,
				cities = null)
		)
		every { countryService.getCountriesByName("South") } returns countries
		graphQlTester.document(
			"""
            query {
                getCountries(name: "South") {
                    id
                    name
                    iso2Code
                }
            }
            """)
			.execute()
			.path("getCountries").entityList(CountryResponse::class.java).hasSize(2)
			.path("getCountries[0].name").entity(String::class.java).isEqualTo("South Africa")
			.path("getCountries[1].name").entity(String::class.java).isEqualTo("South Korea")
	}

	/* ADDITIONAL TESTS */
	@Test
	fun testGetCountriesShouldHandleSpecialCurrencySymbols() {
		val countries = listOf(
			CountryResponse(
				id = 1L,
				name = "Japan",
				iso2Code = "JP",
				iso3Code = "JPN",
				phoneCode = "+81",
				currencyCode = "JPY",
				currencySymbol = "¥",
				region = null,
				states = null,
				cities = null
			),
			CountryResponse(
				id = 2L,
				name = "United Kingdom",
				iso2Code = "GB",
				iso3Code = "GBR",
				phoneCode = "+44",
				currencyCode = "GBP",
				currencySymbol = "£",
				region = null,
				states = null,
				cities = null
			),
			CountryResponse(
				id = 3L,
				name = "India",
				iso2Code = "IN",
				iso3Code = "IND",
				phoneCode = "+91",
				currencyCode = "INR",
				currencySymbol = "₹",
				region = null,
				states = null,
				cities = null
			)
		)
		every { countryService.getAllCountries() } returns countries
		graphQlTester.document(
			"""
            query {
				getCountries {
					currencySymbol
				}
        	}
            """)
			.execute()
			.path("getCountries[0].currencySymbol").entity(String::class.java).isEqualTo("¥")
			.path("getCountries[1].currencySymbol").entity(String::class.java).isEqualTo("£")
			.path("getCountries[2].currencySymbol").entity(String::class.java).isEqualTo("₹")
	}
	@Test
	fun testMultipleQueriesInSingleDocument() {
		val countryResponse = CountryResponse(id = 1L, name = "Argentina", iso2Code = "AR", iso3Code = "ARG", phoneCode = "+54", currencyCode = "ARS", currencySymbol = "$", region = null, states = null, cities = null)
		val allCountries = listOf(
			CountryResponse(id = 1L, name = "Argentina", iso2Code = "AR", iso3Code = "ARG", phoneCode = "+54", currencyCode = "ARS", currencySymbol = "$", region = null, states = null, cities = null),
			CountryResponse(id = 2L, name = "Brazil", iso2Code = "BR", iso3Code = "BRA", phoneCode = "+55", currencyCode = "BRL", currencySymbol = "R$", region = null, states = null, cities = null)
		)
		every { countryService.getCountryById(1L) } returns countryResponse
		every { countryService.getAllCountries() } returns allCountries
		graphQlTester.document(
			"""
            query {
                single: getCountryById(id: 1) {
                    id
                    name
                }
                all: getCountries {
                    id
                    name
                }
            }
            """)
			.execute()
			.path("single.name").entity(String::class.java).isEqualTo("Argentina")
			.path("all").entityList(StateResponse::class.java).hasSize(2)
	}
}
