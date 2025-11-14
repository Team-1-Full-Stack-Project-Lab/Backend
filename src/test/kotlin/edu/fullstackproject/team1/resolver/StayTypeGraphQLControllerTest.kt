package edu.fullstackproject.team1.resolver

import com.ninjasquad.springmockk.MockkBean
import edu.fullstackproject.team1.config.GraphQLScalarConfig
import edu.fullstackproject.team1.dtos.StayTypeResponse
import edu.fullstackproject.team1.services.StayTypeService
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest
import org.springframework.context.annotation.Import
import org.springframework.graphql.test.tester.GraphQlTester
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

@GraphQlTest(StayTypeGraphQLController::class)
@Import(GraphQLScalarConfig::class)
class StayTypeGraphQLControllerTest {
	@Autowired
	private lateinit var graphQlTester: GraphQlTester
	@MockkBean
	private lateinit var stayTypeService: StayTypeService

	/* GET STAY TYPE BY ID TESTS */
	@Test
	fun testGetStayTypeByIdShouldReturnStayTypeById() {
		val stayTypeResponse = StayTypeResponse(
			id = 1L, name = "Hotel")
		every { stayTypeService.getStayTypeById(1) } returns stayTypeResponse
		graphQlTester.document(
			"""
            query {
    			getStayTypeById(id: "1") {
        			id
        			name
    			}
			}
            """)
			.execute()
			.path("getStayTypeById.id").entity(Long::class.java).isEqualTo(1L)
			.path("getStayTypeById.name").entity(String::class.java).isEqualTo("Hotel")
	}
	@Test
	fun testGetStayTypeByIdShouldReturnNullWhenStayTypeNotFound() {
		every { stayTypeService.getStayTypeById(999L) } throws ResponseStatusException(
			HttpStatus.NOT_FOUND,
			"Stay type not found"
		)
		graphQlTester.document(
			"""
            query {
                getStayTypeById(id: 999) {
                    id
                    name
                }
            }
            """)
			.execute()
			.errors()
			.expect { it.message?.contains("Stay type not found") == true }
	}
	@Test
	fun testGetStayTypeByIdShouldReturnOnlyRequestedFields() {
		val stayTypeResponse = StayTypeResponse(
			id = 2L, name = "Hostel")
		every { stayTypeService.getStayTypeById(2L) } returns stayTypeResponse
		graphQlTester.document(
			"""
            query {
                getStayTypeById(id: 2) {
                    name
                }
            }
            """)
			.execute()
			.path("getStayTypeById.name").entity(String::class.java).isEqualTo("Hostel")
	}

	/* GET ALL STAY TYPES TESTS */
	@Test
	fun testGetAllStayTypesShouldReturnAllStayTypes() {
		val stayTypes = listOf(
			StayTypeResponse(id = 1L, name = "Hotel"),
			StayTypeResponse(id = 2L, name = "House")
		)
		every { stayTypeService.getAllStayTypes() } returns stayTypes
		graphQlTester.document(
			"""
            query {
                getAllStayTypes {
        			id
        			name
    			}
            }
            """)
			.execute()
			.path("getAllStayTypes").entityList(StayTypeResponse::class.java).hasSize(2)
	}
	@Test
	fun testGetAllStayTypesShouldReturnEmptyListWhenNoStayTypesExist() {
		every { stayTypeService.getAllStayTypes() } returns emptyList()
		graphQlTester.document(
			"""
            query {
                getAllStayTypes {
                    id
                    name
                }
            }
            """)
			.execute()
			.path("getAllStayTypes").entityList(StayTypeResponse::class.java).hasSize(0)
	}
	@Test
	fun testGetAllStayTypesByNameShouldReturnFilteredStayTypes() {
		val stayTypes = listOf(
			StayTypeResponse(id = 1L, name = "Hotel")
		)
		every { stayTypeService.getStayTypesByName("Hotel") } returns stayTypes
		graphQlTester.document(
			"""
            query {
                getAllStayTypes(name: "Hotel") {
                    id
                    name
                }
            }
            """)
			.execute()
			.path("getAllStayTypes").entityList(StayTypeResponse::class.java).hasSize(1)
			.path("getAllStayTypes[0].name").entity(String::class.java).isEqualTo("Hotel")
	}
	@Test
	fun testGetAllStayTypesByNameShouldReturnEmptyListWhenNoMatch() {
		every { stayTypeService.getStayTypesByName("NonExistentType") } returns emptyList()
		graphQlTester.document(
			"""
            query {
                getAllStayTypes(name: "NonExistentType") {
                    id
                    name
                }
            }
            """)
			.execute()
			.path("getAllStayTypes").entityList(StayTypeResponse::class.java).hasSize(0)
	}

	/* ADDITIONAL TESTS  */
	@Test
	fun testGetStayTypeByIdShouldHandleLargeIds() {
		val stayTypeResponse = StayTypeResponse(
			id = 999999L, name = "Luxury Villa")
		every { stayTypeService.getStayTypeById(999999L) } returns stayTypeResponse
		graphQlTester.document(
			"""
            query {
                getStayTypeById(id: 999999) {
                    id
                    name
                }
            }
            """)
			.execute()
			.path("getStayTypeById.id").entity(Long::class.java).isEqualTo(999999L)
			.path("getStayTypeById.name").entity(String::class.java).isEqualTo("Luxury Villa")
	}
	@Test
	fun testGetAllStayTypesShouldHandleSpecialCharactersInName() {
		val stayTypes = listOf(
			StayTypeResponse(id = 10L, name = "Bed & Breakfast"))
		every { stayTypeService.getStayTypesByName("Bed & Breakfast") } returns stayTypes
		graphQlTester.document(
			"""
            query {
                getAllStayTypes(name: "Bed & Breakfast") {
                    id
                    name
                }
            }
            """)
			.execute()
			.path("getAllStayTypes").entityList(StayTypeResponse::class.java).hasSize(1)
			.path("getAllStayTypes[0].name").entity(String::class.java).isEqualTo("Bed & Breakfast")
	}
}
