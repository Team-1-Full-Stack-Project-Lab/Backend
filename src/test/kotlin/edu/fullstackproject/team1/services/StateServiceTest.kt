package edu.fullstackproject.team1.services

import edu.fullstackproject.team1.models.Country
import edu.fullstackproject.team1.models.State
import edu.fullstackproject.team1.repositories.StateRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class StateServiceTest : DescribeSpec({
	val stateRepository = mockk<StateRepository>()
	val stateService = StateService(stateRepository)

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

	afterEach {
		clearAllMocks()
	}

	describe("getAllStates") {
		context("when getting all states") {
			it("should return list of states") {
				val states = listOf(state)
				every { stateRepository.findAll() } returns states

				val result = stateService.getAllStates()

				result.size shouldBe 1
			}
		}
	}

	describe("getStateById") {
		context("when state exists") {
			it("should return the state") {
				every { stateRepository.findByIdWithCountry(1L) } returns state

				val result = stateService.getStateById(1L)

				result shouldBe state
			}
		}

		context("when state does not exist") {
			it("should throw ResponseStatusException") {
				every { stateRepository.findByIdWithCountry(999L) } returns null

				val exception = shouldThrow<ResponseStatusException> {
					stateService.getStateById(999L)
				}
				exception.statusCode shouldBe HttpStatus.NOT_FOUND
			}
		}
	}

	describe("getStatesByName") {
		context("when searching states by name") {
			it("should return matching states") {
				val states = listOf(state)
				every { stateRepository.findByNameContainingIgnoreCase("Calif") } returns states

				val result = stateService.getStatesByName("Calif")

				result.size shouldBe 1
				result[0].name shouldBe "California"
			}
		}
	}
})
