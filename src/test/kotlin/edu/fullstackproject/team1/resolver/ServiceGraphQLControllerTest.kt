package edu.fullstackproject.team1.resolver

import com.ninjasquad.springmockk.MockkBean
import edu.fullstackproject.team1.config.GraphQLScalarConfig
import edu.fullstackproject.team1.dtos.ServiceResponse
import edu.fullstackproject.team1.services.ServiceService
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest
import org.springframework.context.annotation.Import
import org.springframework.graphql.test.tester.GraphQlTester
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

@GraphQlTest(ServiceGraphQLController::class)
@Import(GraphQLScalarConfig::class)
class ServiceGraphQLControllerTest {
	@Autowired
	private lateinit var graphQlTester: GraphQlTester
	@MockkBean
	private lateinit var serviceService: ServiceService

	/* GET SERVICES BY ID TESTS */
	@Test
	fun testGetServiceByIdShouldReturnServiceById() {
		val serviceResponse = ServiceResponse(id = 1L, name = "Wifi", icon = "Wifi")
		every { serviceService.getServiceById(1L) } returns serviceResponse
		graphQlTester.document(
			"""
            query {
                getServiceById(id: 1) {
                    id
                    name
                    icon
                }
            }
            """)
			.execute()
			.path("getServiceById.id").entity(Long::class.java).isEqualTo(1L)
			.path("getServiceById.name").entity(String::class.java).isEqualTo("Wifi")
			.path("getServiceById.icon").entity(String::class.java).isEqualTo("Wifi")
	}
	@Test
	fun testGetServiceByIdShouldReturnServiceWithNullIcon() {
		val serviceResponse = ServiceResponse(id = 2L, name = "Parking", icon = null)
		every { serviceService.getServiceById(2L) } returns serviceResponse
		graphQlTester.document(
			"""
            query {
                getServiceById(id: 2) {
                    id
                    name
                    icon
                }
            }
            """)
			.execute()
			.path("getServiceById.id").entity(Long::class.java).isEqualTo(2L)
			.path("getServiceById.name").entity(String::class.java).isEqualTo("Parking")
			.path("getServiceById.icon").valueIsNull()
	}
	@Test
	fun testGetServiceByIdShouldReturnNullWhenServiceNotFound() {
		every { serviceService.getServiceById(999L) } throws ResponseStatusException(
			HttpStatus.NOT_FOUND,
			"Service not found")
		graphQlTester.document(
			"""
            query {
                getServiceById(id: 999) {
                    id
                    name
                    icon
                }
            }
            """)
			.execute()
			.errors()
			.expect { it.message?.contains("Service not found") == true }
	}
	@Test
	fun testGetServiceByIdShouldReturnOnlyRequestedFields() {
		val serviceResponse = ServiceResponse(id = 3L, name = "Pool", icon = "Waves")
		every { serviceService.getServiceById(3L) } returns serviceResponse
		graphQlTester.document(
			"""
            query {
                getServiceById(id: 3) {
                    name
                }
            }
            """)
			.execute()
			.path("getServiceById.name").entity(String::class.java).isEqualTo("Pool")
	}

	/* GET ALL SERVICES TESTS */
	@Test
	fun testGetAllServicesShouldReturnAllServices() {
		val services = listOf(
			ServiceResponse(id = 1L, name = "Wifi", icon = "Wifi"),
			ServiceResponse(id = 2L, name = "Parking", icon = "Car")
		)
		every { serviceService.getAllServices() } returns services
		graphQlTester.document(
			"""
            query {
                getAllServices {
                    id
                    name
                    icon
                }
            }
            """)
			.execute()
			.path("getAllServices").entityList(ServiceResponse::class.java).hasSize(2)
			.path("getAllServices[0].name").entity(String::class.java).isEqualTo("Wifi")
			.path("getAllServices[1].name").entity(String::class.java).isEqualTo("Parking")
	}
	@Test
	fun testGetAllServicesShouldReturnEmptyListWhenNoServicesExist() {
		every { serviceService.getAllServices() } returns emptyList()
		graphQlTester.document(
			"""
            query {
                getAllServices {
                    id
                    name
                }
            }
            """)
			.execute()
			.path("getAllServices").entityList(ServiceResponse::class.java).hasSize(0)
	}
	@Test
	fun testGetAllServicesShouldReturnOnlyIdField() {
		val services = listOf(
			ServiceResponse(id = 1L, name = "Wifi", icon = "Wifi"),
			ServiceResponse(id = 2L, name = "Parking", icon = "Car")
		)
		every { serviceService.getAllServices() } returns services
		graphQlTester.document(
			"""
            query {
                getAllServices {
                    id
                }
            }
            """)
			.execute()
			.path("getAllServices[0].id").entity(Long::class.java).isEqualTo(1L)
			.path("getAllServices[1].id").entity(Long::class.java).isEqualTo(2L)
	}
	@Test
	fun testGetAllServicesShouldHandleServicesWithNullIcons() {
		val services = listOf(
			ServiceResponse(id = 1L, name = "Wifi", icon = "Wifi"),
			ServiceResponse(id = 2L, name = "Parking", icon = null),
			ServiceResponse(id = 3L, name = "Pool", icon = "Waves")
		)
		every { serviceService.getAllServices() } returns services
		graphQlTester.document(
			"""
            query {
                getAllServices {
                    id
                    name
                    icon
                }
            }
            """)
			.execute()
			.path("getAllServices").entityList(ServiceResponse::class.java).hasSize(3)
			.path("getAllServices[0].icon").entity(String::class.java).isEqualTo("Wifi")
			.path("getAllServices[1].icon").valueIsNull()
			.path("getAllServices[2].icon").entity(String::class.java).isEqualTo("Waves")
	}

	/* GET ALL SERVICES TEST FILTER BY NAME */
	@Test
	fun testGetAllServicesByNameShouldReturnFilteredServices() {
		val services = listOf(
			ServiceResponse(id = 1L, name = "Wifi", icon = "Wifi")
		)
		every { serviceService.getServicesByName("Wifi") } returns services
		graphQlTester.document(
			"""
            query {
                getAllServices(name: "Wifi") {
                    id
                    name
                    icon
                }
            }
            """)
			.execute()
			.path("getAllServices").entityList(ServiceResponse::class.java).hasSize(1)
			.path("getAllServices[0].name").entity(String::class.java).isEqualTo("Wifi")
	}
	@Test
	fun testGetAllServicesByNameShouldReturnEmptyListWhenNoMatch() {
		every { serviceService.getServicesByName("NonExistentService") } returns emptyList()
		graphQlTester.document(
			"""
            query {
                getAllServices(name: "NonExistentService") {
                    id
                    name
                }
            }
            """)
			.execute()
			.path("getAllServices").entityList(ServiceResponse::class.java).hasSize(0)
	}

	/* ADDITIONAL TESTS */
	@Test
	fun testGetAllServicesWithNullNameParameterShouldReturnAllServices() {
		val services = listOf(
			ServiceResponse(id = 1L, name = "Wifi", icon = "wifi-icon"),
			ServiceResponse(id = 2L, name = "Parking", icon = "parking-icon")
		)
		every { serviceService.getAllServices() } returns services
		graphQlTester.document(
			"""
            query {
                getAllServices {
                    id
                    name
                }
            }
            """)
			.execute()
			.path("getAllServices").entityList(ServiceResponse::class.java).hasSize(2)
	}
	@Test
	fun testGetAllServicesWithEmptyNameShouldCallNameFilter() {
		val services = emptyList<ServiceResponse>()
		every { serviceService.getServicesByName("") } returns services
		graphQlTester.document(
			"""
            query {
                getAllServices(name: "") {
                    id
                    name
                }
            }
            """)
			.execute()
			.path("getAllServices").entityList(ServiceResponse::class.java).hasSize(0)
	}
	@Test
	fun testGetServiceByIdShouldHandleLargeIds() {
		val serviceResponse = ServiceResponse(id = 999999L, name = "Wifi", icon = "Wifi")
		every { serviceService.getServiceById(999999L) } returns serviceResponse
		graphQlTester.document(
			"""
            query {
                getServiceById(id: 999999) {
                    id
                    name
                }
            }
            """)
			.execute()
			.path("getServiceById.id").entity(Long::class.java).isEqualTo(999999L)
	}
	@Test
	fun testMultipleQueriesInSingleDocument() {
		val serviceResponse = ServiceResponse(id = 1L, name = "Wifi", icon = "Wifi")
		val allServices = listOf(
			ServiceResponse(id = 1L, name = "Wifi", icon = "Wifi"),
			ServiceResponse(id = 2L, name = "Parking", icon = "Car")
		)
		every { serviceService.getServiceById(1L) } returns serviceResponse
		every { serviceService.getAllServices() } returns allServices
		graphQlTester.document(
			"""
            query {
                single: getServiceById(id: 1) {
                    id
                    name
                }
                all: getAllServices {
                    id
                    name
                }
            }
            """)
			.execute()
			.path("single.name").entity(String::class.java).isEqualTo("Wifi")
			.path("all").entityList(ServiceResponse::class.java).hasSize(2)
	}
}
