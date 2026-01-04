package edu.fullstackproject.team1.services

import edu.fullstackproject.team1.models.StayType
import edu.fullstackproject.team1.repositories.StayTypeRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.util.*

class StayTypeServiceTest : DescribeSpec({
	val stayTypeRepository = mockk<StayTypeRepository>()
	val stayTypeService = StayTypeService(stayTypeRepository)

	val stayType = StayType(
		id = 1L,
		name = "Hotel"
	)

	afterEach {
		clearAllMocks()
	}

	describe("getAllStayTypes") {
		context("when getting all stay types") {
			it("should return list of stay types") {
				val stayTypes = listOf(
					stayType,
					StayType(id = 2L, name = "Hostel"),
					StayType(id = 3L, name = "Apartment")
				)
				every { stayTypeRepository.findAll() } returns stayTypes

				val result = stayTypeService.getAllStayTypes()

				result.size shouldBe 3
			}
		}
	}

	describe("getStayTypeById") {
		context("when stay type exists") {
			it("should return the stay type") {
				every { stayTypeRepository.findById(1L) } returns Optional.of(stayType)

				val result = stayTypeService.getStayTypeById(1L)

				result shouldBe stayType
			}
		}

		context("when stay type does not exist") {
			it("should throw ResponseStatusException") {
				every { stayTypeRepository.findById(999L) } returns Optional.empty()

				val exception = shouldThrow<ResponseStatusException> {
					stayTypeService.getStayTypeById(999L)
				}
				exception.statusCode shouldBe HttpStatus.NOT_FOUND
			}
		}
	}

	describe("getStayTypesByName") {
		context("when searching stay types by name") {
			it("should return matching stay types") {
				val stayTypes = listOf(stayType)
				every { stayTypeRepository.findByName("Hotel") } returns stayTypes

				val result = stayTypeService.getStayTypesByName("Hotel")

				result.size shouldBe 1
				result[0].name shouldBe "Hotel"
			}
		}
	}
})
