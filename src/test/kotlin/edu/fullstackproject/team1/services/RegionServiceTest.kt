package edu.fullstackproject.team1.services

import edu.fullstackproject.team1.models.Region
import edu.fullstackproject.team1.repositories.RegionRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.util.*

class RegionServiceTest : DescribeSpec({
	val regionRepository = mockk<RegionRepository>()
	val regionService = RegionService(regionRepository)

	val region = Region(
		id = 1L,
		name = "Americas",
		code = "AM"
	)

	afterEach {
		clearAllMocks()
	}

	describe("getAllRegions") {
		context("when getting all regions") {
			it("should return list of regions") {
				val regions = listOf(region)
				every { regionRepository.findAll() } returns regions

				val result = regionService.getAllRegions()

				result.size shouldBe 1
			}
		}
	}

	describe("getRegionById") {
		context("when region exists") {
			it("should return the region") {
				every { regionRepository.findById(1L) } returns Optional.of(region)

				val result = regionService.getRegionById(1L)

				result shouldBe region
			}
		}

		context("when region does not exist") {
			it("should throw ResponseStatusException") {
				every { regionRepository.findById(999L) } returns Optional.empty()

				val exception = shouldThrow<ResponseStatusException> {
					regionService.getRegionById(999L)
				}
				exception.statusCode shouldBe HttpStatus.NOT_FOUND
			}
		}
	}

	describe("getRegionByIdWithCountries") {
		context("when region exists") {
			it("should return the region with countries") {
				every { regionRepository.findByIdWithCountries(1L) } returns region

				val result = regionService.getRegionByIdWithCountries(1L)

				result shouldBe region
			}
		}

		context("when region does not exist") {
			it("should throw ResponseStatusException") {
				every { regionRepository.findByIdWithCountries(999L) } returns null

				val exception = shouldThrow<ResponseStatusException> {
					regionService.getRegionByIdWithCountries(999L)
				}
				exception.statusCode shouldBe HttpStatus.NOT_FOUND
			}
		}
	}

	describe("getRegionsByName") {
		context("when searching regions by name") {
			it("should return matching regions") {
				val regions = listOf(region)
				every { regionRepository.findByNameContainingIgnoreCase("Americ") } returns regions

				val result = regionService.getRegionsByName("Americ")

				result.size shouldBe 1
				result[0].name shouldBe "Americas"
			}
		}
	}
})
