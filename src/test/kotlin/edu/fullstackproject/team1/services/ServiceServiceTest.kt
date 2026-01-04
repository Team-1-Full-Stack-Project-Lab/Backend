package edu.fullstackproject.team1.services

import edu.fullstackproject.team1.models.Service
import edu.fullstackproject.team1.repositories.ServiceRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.util.*

class ServiceServiceTest : DescribeSpec({
	val serviceRepository = mockk<ServiceRepository>()
	val serviceService = ServiceService(serviceRepository)

	val service = Service(
		id = 1L,
		name = "WiFi"
	)

	afterEach {
		clearAllMocks()
	}

	describe("getAllServices") {
		context("when getting all services") {
			it("should return list of services") {
				val services = listOf(
					service,
					Service(id = 2L, name = "Pool"),
					Service(id = 3L, name = "Breakfast")
				)
				every { serviceRepository.findAll() } returns services

				val result = serviceService.getAllServices()

				result.size shouldBe 3
			}
		}
	}

	describe("getServiceById") {
		context("when service exists") {
			it("should return the service") {
				every { serviceRepository.findById(1L) } returns Optional.of(service)

				val result = serviceService.getServiceById(1L)

				result shouldBe service
			}
		}

		context("when service does not exist") {
			it("should throw ResponseStatusException") {
				every { serviceRepository.findById(999L) } returns Optional.empty()

				val exception = shouldThrow<ResponseStatusException> {
					serviceService.getServiceById(999L)
				}
				exception.statusCode shouldBe HttpStatus.NOT_FOUND
			}
		}
	}

	describe("getServicesByName") {
		context("when searching services by name") {
			it("should return matching services") {
				val services = listOf(service)
				every { serviceRepository.findByName("WiFi") } returns services

				val result = serviceService.getServicesByName("WiFi")

				result.size shouldBe 1
				result[0].name shouldBe "WiFi"
			}
		}
	}
})
