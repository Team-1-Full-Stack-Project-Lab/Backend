package edu.fullstackproject.team1.services

import edu.fullstackproject.team1.models.Country
import edu.fullstackproject.team1.repositories.CountryRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class CountryServiceTest : DescribeSpec({
	val countryRepository = mockk<CountryRepository>()
	val countryService = CountryService(countryRepository)

	val country = Country(
		id = 1L,
		name = "United States",
		iso2Code = "US",
		iso3Code = "USA",
		phoneCode = "+1",
		currencyCode = "USD",
		currencySymbol = "$",
		region = null
	)

	afterEach {
		clearAllMocks()
	}

	describe("getAllCountries") {
		context("when getting all countries") {
			it("should return list of countries") {
				val countries = listOf(country)
				every { countryRepository.findAll() } returns countries

				val result = countryService.getAllCountries()

				result.size shouldBe 1
			}
		}
	}

	describe("getCountryById") {
		context("when country exists") {
			it("should return the country") {
				every { countryRepository.findByIdWithRegionCitiesAndStates(1L) } returns country

				val result = countryService.getCountryById(1L)

				result shouldBe country
			}
		}

		context("when country does not exist") {
			it("should throw ResponseStatusException") {
				every { countryRepository.findByIdWithRegionCitiesAndStates(999L) } returns null

				val exception = shouldThrow<ResponseStatusException> {
					countryService.getCountryById(999L)
				}
				exception.statusCode shouldBe HttpStatus.NOT_FOUND
			}
		}
	}

	describe("getCountryByIso2Code") {
		context("when country exists") {
			it("should return the country") {
				every { countryRepository.findByIso2Code("US") } returns country

				val result = countryService.getCountryByIso2Code("US")

				result shouldBe country
			}
		}

		context("when country does not exist") {
			it("should throw ResponseStatusException") {
				every { countryRepository.findByIso2Code("XX") } returns null

				val exception = shouldThrow<ResponseStatusException> {
					countryService.getCountryByIso2Code("XX")
				}
				exception.statusCode shouldBe HttpStatus.NOT_FOUND
			}
		}
	}

	describe("getCountryByIso3Code") {
		context("when country exists") {
			it("should return the country") {
				every { countryRepository.findByIso3Code("USA") } returns country

				val result = countryService.getCountryByIso3Code("USA")

				result shouldBe country
			}
		}
	}

	describe("getCountriesByName") {
		context("when searching countries by name") {
			it("should return matching countries") {
				val countries = listOf(country)
				every { countryRepository.findByNameContainingIgnoreCase("United") } returns countries

				val result = countryService.getCountriesByName("United")

				result.size shouldBe 1
			}
		}
	}
})
