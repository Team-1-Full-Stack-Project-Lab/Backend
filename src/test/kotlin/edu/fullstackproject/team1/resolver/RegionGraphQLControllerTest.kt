package edu.fullstackproject.team1.resolver

import com.ninjasquad.springmockk.MockkBean
import edu.fullstackproject.team1.config.GraphQLScalarConfig
import edu.fullstackproject.team1.dtos.RegionResponse
import edu.fullstackproject.team1.services.RegionService
import io.mockk.every
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest
import org.springframework.context.annotation.Import
import org.springframework.graphql.test.tester.GraphQlTester
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import kotlin.test.Test

@GraphQlTest(RegionGraphQLController::class)
@Import(GraphQLScalarConfig::class)
class RegionGraphQLControllerTest {
	@Autowired
	private lateinit var graphQlTester: GraphQlTester
	@MockkBean
	private lateinit var regionService: RegionService

	/* GET REGIONS BY ID TESTS */
	@Test
	fun testGetRegionByIdShouldReturnRegionById() {
		val regionResponse = RegionResponse(
			id = 1L, name = "Africa", code = "AF", countries = null)
		every { regionService.getRegionById(1) } returns regionResponse
		graphQlTester.document(
			"""
			query {
				getRegionById(id: 1) {
					id
					name
					code
				}
			}
		""")
			.execute()
			.path("getRegionById.id").entity(Long::class.java).isEqualTo(1L)
			.path("getRegionById.name").entity(String::class.java).isEqualTo("Africa")
			.path("getRegionById.code").entity(String::class.java).isEqualTo("AF")
	}
	@Test
	fun testGetRegionByIdShouldReturnNullWhenRegionNotFound() {
		every { regionService.getRegionById(999L) } throws ResponseStatusException(
			HttpStatus.NOT_FOUND,
			"Region not found")
		graphQlTester.document(
			"""
            query {
                getRegionById(id: 999) {
                    id
                    name
                    code
                }
            }
            """)
			.execute()
			.errors()
			.expect { it.message?.contains("Region not found") == true }
	}
	@Test
	fun testGetRegionByIdShouldReturnOnlyRequestedFields() {
		val regionResponse = RegionResponse(
			id = 2L,
			name = "Asia",
			code = "AS",
			countries = null
		)
		every { regionService.getRegionById(2L) } returns regionResponse
		graphQlTester.document(
			"""
            query {
                getRegionById(id: 2) {
                    name
                }
            }
            """)
			.execute()
			.path("getRegionById.name").entity(String::class.java).isEqualTo("Asia")
	}

	/* GET ALL REGIONS TESTS */
	@Test
	fun testGetRegionsShouldReturnRegions() {
		val regions = listOf(
			RegionResponse(
				id = 1L, name = "Africa", code = "AF", countries = null),
			RegionResponse(
				id = 2L, name = "Asia", code = "AS", countries = null)
		)
		every { regionService.getAllRegions() } returns regions
		graphQlTester.document(
			"""
			query {
				getRegions {
        			id
        			name
        			code
    			}
			}
		""")
			.execute()
			.path("getRegions").entityList(RegionResponse::class.java).hasSize(2)
	}
	@Test
	fun testGetRegionsShouldReturnEmptyListWhenNoRegionsExist() {
		every { regionService.getAllRegions() } returns emptyList()
		graphQlTester.document(
			"""
            query {
                getRegions {
                    id
                    name
                }
            }
            """)
			.execute()
			.path("getRegions").entityList(RegionResponse::class.java).hasSize(0)
	}

	/* GET REGIONS TEST FILTER BY NAME */
	@Test
	fun testGetRegionsByNameShouldReturnFilteredRegions() {
		val regions = listOf(
			RegionResponse(id = 1L, name = "North America", code = "NA", countries = null),
			RegionResponse(id = 2L, name = "South America", code = "SA", countries = null)
		)
		every { regionService.getRegionsByName("America") } returns regions
		graphQlTester.document(
			"""
            query {
                getRegions(name: "America") {
                    id
                    name
                    code
                }
            }
            """)
			.execute()
			.path("getRegions").entityList(RegionResponse::class.java).hasSize(2)
			.path("getRegions[0].name").entity(String::class.java).isEqualTo("North America")
			.path("getRegions[1].name").entity(String::class.java).isEqualTo("South America")
	}
	@Test
	fun testGetRegionsByNameShouldReturnEmptyListWhenNoMatch() {
		every { regionService.getRegionsByName("NonExistentRegion") } returns emptyList()
		graphQlTester.document(
			"""
            query {
                getRegions(name: "NonExistentRegion") {
                    id
                    name
					code
                }
            }
            """)
			.execute()
			.path("getRegions").entityList(RegionResponse::class.java).hasSize(0)
	}
	@Test
	fun testGetRegionsByNameShouldBeCaseInsensitive() {
		val regions = listOf(
			RegionResponse(id = 2L, name = "Asia", code = "AS", countries = null)
		)
		every { regionService.getRegionsByName("asia") } returns regions
		graphQlTester.document(
			"""
            query {
                getRegions(name: "asia") {
                    id
                    name
                    code
                }
            }
            """)
			.execute()
			.path("getRegions").entityList(RegionResponse::class.java).hasSize(1)
			.path("getRegions[0].name").entity(String::class.java).isEqualTo("Asia")
	}
}
