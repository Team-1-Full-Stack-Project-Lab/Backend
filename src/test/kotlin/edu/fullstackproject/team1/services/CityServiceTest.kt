package edu.fullstackproject.team1.services

import edu.fullstackproject.team1.models.City
import edu.fullstackproject.team1.models.Country
import edu.fullstackproject.team1.models.State
import edu.fullstackproject.team1.repositories.CityRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class CityServiceTest : DescribeSpec({
	val cityRepository = mockk<CityRepository>()
	val cityService = CityService(cityRepository)

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

	val state = State(
		id = 1L,
		name = "California",
		code = "CA",
		latitude = 36.7783,
		longitude = -119.4179,
		country = country
	)

	val city = City(
		id = 1L,
		name = "San Francisco",
		nameAscii = "San Francisco",
		latitude = 37.7749,
		longitude = -122.4194,
		timezone = "America/Los_Angeles",
		googlePlaceId = "ChIJIQBpAG2ahYAR_6128GcTUEo",
		population = 873965,
		isCapital = false,
		isFeatured = true,
		country = country,
		state = state
	)

	afterEach {
		clearAllMocks()
	}

	describe("getAllCities") {
		context("when requesting paginated cities") {
			it("should return page of cities") {
				val pageable = PageRequest.of(0, 20)
				val cities = listOf(city)
				val page = PageImpl(cities, pageable, 1)

				every { cityRepository.findAllWithCountryAndState(pageable) } returns page

				val result = cityService.getAllCities(pageable)

				result.content.size shouldBe 1
				verify(exactly = 1) { cityRepository.findAllWithCountryAndState(pageable) }
			}
		}
	}

	describe("getCityById") {
		context("when city exists") {
			it("should return the city") {
				every { cityRepository.findByIdWithCountryAndState(1L) } returns city

				val result = cityService.getCityById(1L)

				result shouldBe city
			}
		}

		context("when city does not exist") {
			it("should throw ResponseStatusException") {
				every { cityRepository.findByIdWithCountryAndState(999L) } returns null

				val exception = shouldThrow<ResponseStatusException> {
					cityService.getCityById(999L)
				}
				exception.statusCode shouldBe HttpStatus.NOT_FOUND
			}
		}
	}

	describe("getFeaturedCities") {
		context("when requesting featured cities") {
			it("should return list of featured cities") {
				val featuredCities = listOf(city)
				every { cityRepository.findFeaturedWithCountryAndState() } returns featuredCities

				val result = cityService.getFeaturedCities()

				result.size shouldBe 1
				result[0].isFeatured shouldBe true
			}
		}
	}

	describe("getCapitalCities") {
		context("when requesting capital cities") {
			it("should return list of capital cities") {
				val capitalCity = city.copy(isCapital = true)
				val capitalCities = listOf(capitalCity)
				every { cityRepository.findCapitalsWithCountryAndState() } returns capitalCities

				val result = cityService.getCapitalCities()

				result.size shouldBe 1
				result[0].isCapital shouldBe true
			}
		}
	}

	describe("searchCities") {
		context("when searching cities with filters") {
			it("should return filtered cities") {
				val pageable = PageRequest.of(0, 20)
				val cities = listOf(city)
				val page = PageImpl(cities, pageable, 1)

				every {
					cityRepository.searchCitiesWithCountryAndState(
						countryId = 1L,
						stateId = 1L,
						search = "San",
						featured = true,
						pageable = pageable
					)
				} returns page

				val result = cityService.searchCities(
					countryId = 1L,
					stateId = 1L,
					search = "San",
					featured = true,
					pageable = pageable
				)

				result.content.size shouldBe 1
			}
		}
	}

	describe("findCitiesNearby") {
		context("when searching cities by coordinates") {
			it("should return nearby cities") {
				val cities = listOf(city)
				every {
					cityRepository.findCitiesNearby(37.7749, -122.4194, 50.0)
				} returns cities

				val result = cityService.findCitiesNearby(37.7749, -122.4194, 50.0)

				result.size shouldBe 1
			}
		}
	}
})
