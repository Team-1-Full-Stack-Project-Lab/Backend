package edu.fullstackproject.team1.services

import edu.fullstackproject.team1.dtos.commands.StayCreateCommand
import edu.fullstackproject.team1.dtos.commands.StayUpdateCommand
import edu.fullstackproject.team1.mappers.StayMapper
import edu.fullstackproject.team1.models.*
import edu.fullstackproject.team1.repositories.*
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.util.*

class StayServiceTest : DescribeSpec({
	val stayRepository = mockk<StayRepository>()
	val stayImageRepository = mockk<StayImageRepository>()
	val cityRepository = mockk<CityRepository>()
	val stayTypeRepository = mockk<StayTypeRepository>()
	val userRepository = mockk<UserRepository>()
	val serviceRepository = mockk<ServiceRepository>()
	val stayServiceRepository = mockk<StayServiceRepository>()
	val stayMapper = mockk<StayMapper>()

	val stayService = StayService(
		stayRepository,
		stayImageRepository,
		cityRepository,
		stayTypeRepository,
		userRepository,
		serviceRepository,
		stayServiceRepository,
		stayMapper
	)

	val user = User(
		id = 1L,
		email = "host@example.com",
		firstName = "John",
		lastName = "Host",
		password = "hashedPassword"
	)

	val company = Company(
		id = 1L,
		user = user,
		name = "My Hotel Company",
		email = "company@example.com",
		phone = "555-1234",
		description = "A hotel company"
	)

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

	val stayType = StayType(
		id = 1L,
		name = "Hotel"
	)

	val stay = Stay(
		id = 1L,
		city = city,
		stayType = stayType,
		company = company,
		name = "Grand Hotel",
		address = "123 Main St",
		latitude = 37.7749,
		longitude = -122.4194,
		description = "A nice hotel"
	)

	afterEach {
		clearAllMocks()
	}

	describe("getAllStays") {
		val pageable = PageRequest.of(0, 20)

		context("when no filters are applied") {
			it("should return paginated stays") {
				val stays = listOf(stay)
				val page: Page<Stay> = PageImpl(stays, pageable, 1)

				every {
					stayRepository.findAllWithFilters(
						companyId = null,
						cityId = null,
						serviceIds = null,
						serviceCount = null,
						minPrice = null,
						maxPrice = null,
						pageable = pageable
					)
				} returns page

				val result = stayService.getAllStays(
					companyId = null,
					cityId = null,
					serviceIds = null,
					minPrice = null,
					maxPrice = null,
					pageable = pageable
				)

				result shouldBe page
				result.content.size shouldBe 1
				verify(exactly = 1) { stayRepository.findAllWithFilters(any(), any(), any(), any(), any(), any(), any()) }
			}
		}

		context("when filtered by city") {
			it("should return stays in that city") {
				val cityId = 1L
				val stays = listOf(stay)
				val page: Page<Stay> = PageImpl(stays, pageable, 1)

				every {
					stayRepository.findAllWithFilters(
						companyId = null,
						cityId = cityId,
						serviceIds = null,
						serviceCount = null,
						minPrice = null,
						maxPrice = null,
						pageable = pageable
					)
				} returns page

				val result = stayService.getAllStays(
					companyId = null,
					cityId = cityId,
					serviceIds = null,
					minPrice = null,
					maxPrice = null,
					pageable = pageable
				)

				result.content.size shouldBe 1
			}
		}

		context("when filtered by services") {
			it("should return stays with those services") {
				val serviceIds = listOf(1L, 2L)
				val stays = listOf(stay)
				val page: Page<Stay> = PageImpl(stays, pageable, 1)

				every {
					stayRepository.findAllWithFilters(
						companyId = null,
						cityId = null,
						serviceIds = serviceIds,
						serviceCount = 2L,
						minPrice = null,
						maxPrice = null,
						pageable = pageable
					)
				} returns page

				val result = stayService.getAllStays(
					companyId = null,
					cityId = null,
					serviceIds = serviceIds,
					minPrice = null,
					maxPrice = null,
					pageable = pageable
				)

				result.content.size shouldBe 1
			}
		}
	}

	describe("getStayById") {
		context("when stay exists") {
			it("should return the stay") {
				val stayId = 1L
				every { stayRepository.findById(stayId) } returns Optional.of(stay)

				val result = stayService.getStayById(stayId)

				result shouldBe stay
				verify(exactly = 1) { stayRepository.findById(stayId) }
			}
		}

		context("when stay does not exist") {
			it("should throw ResponseStatusException with NOT_FOUND status") {
				val stayId = 999L
				every { stayRepository.findById(stayId) } returns Optional.empty()

				val exception = shouldThrow<ResponseStatusException> {
					stayService.getStayById(stayId)
				}
				exception.statusCode shouldBe HttpStatus.NOT_FOUND
				exception.reason shouldBe "Stay not found"
			}
		}
	}

	describe("searchStaysNearby") {
		val pageable = PageRequest.of(0, 20)

		context("when searching by coordinates") {
			it("should return nearby stays") {
				val latitude = 37.7749
				val longitude = -122.4194
				val radiusKm = 10.0
				val stays = listOf(stay)
				val page: Page<Stay> = PageImpl(stays, pageable, 1)

				every {
					stayRepository.findStaysNearby(latitude, longitude, radiusKm, pageable)
				} returns page

				val result = stayService.searchStaysNearby(latitude, longitude, radiusKm, pageable)

				result shouldBe page
				result.content.size shouldBe 1
				verify(exactly = 1) { stayRepository.findStaysNearby(latitude, longitude, radiusKm, pageable) }
			}
		}
	}

	describe("createStay") {
		val email = "host@example.com"
		val command = StayCreateCommand(
			cityId = 1L,
			stayTypeId = 1L,
			name = "New Hotel",
			address = "456 Market St",
			latitude = 37.7750,
			longitude = -122.4195,
			description = "A brand new hotel",
			serviceIds = listOf(1L, 2L),
			imageUrls = listOf("http://example.com/image1.jpg")
		)

		context("when user has a company") {
			it("should create and return the stay") {
				val userWithCompany = user.copy(company = company)
				val services = listOf(
					Service(id = 1L, name = "WiFi"),
					Service(id = 2L, name = "Pool")
				)
				val newStay = stay.copy(id = null, name = command.name)
				val savedStay = newStay.copy(id = 2L)

				every { userRepository.findWithCompanyByEmail(email) } returns userWithCompany
				every { cityRepository.findById(command.cityId) } returns Optional.of(city)
				every { stayTypeRepository.findById(command.stayTypeId) } returns Optional.of(stayType)
				every { stayMapper.toEntity(command, city, stayType, company) } returns newStay
				every { serviceRepository.findAllById(command.serviceIds!!) } returns services
				every { stayRepository.save(any<Stay>()) } returns savedStay

				val result = stayService.createStay(email, command)

				result shouldBe savedStay
				verify(exactly = 1) { userRepository.findWithCompanyByEmail(email) }
				verify(exactly = 1) { cityRepository.findById(command.cityId) }
				verify(exactly = 1) { stayTypeRepository.findById(command.stayTypeId) }
				verify(exactly = 1) { stayRepository.save(any<Stay>()) }
			}
		}

		context("when user does not have a company") {
			it("should throw ResponseStatusException with BAD_REQUEST status") {
				val userWithoutCompany = user.copy(company = null)
				every { userRepository.findWithCompanyByEmail(email) } returns userWithoutCompany

				val exception = shouldThrow<ResponseStatusException> {
					stayService.createStay(email, command)
				}
				exception.statusCode shouldBe HttpStatus.BAD_REQUEST
				exception.reason shouldBe "User doesn't have a company. Please create a company first."
			}
		}

		context("when user does not exist") {
			it("should throw ResponseStatusException with NOT_FOUND status") {
				every { userRepository.findWithCompanyByEmail(email) } returns null

				val exception = shouldThrow<ResponseStatusException> {
					stayService.createStay(email, command)
				}
				exception.statusCode shouldBe HttpStatus.NOT_FOUND
				exception.reason shouldBe "User not found"
			}
		}

		context("when city does not exist") {
			it("should throw ResponseStatusException with NOT_FOUND status") {
				val userWithCompany = user.copy(company = company)
				every { userRepository.findWithCompanyByEmail(email) } returns userWithCompany
				every { cityRepository.findById(command.cityId) } returns Optional.empty()

				val exception = shouldThrow<ResponseStatusException> {
					stayService.createStay(email, command)
				}
				exception.statusCode shouldBe HttpStatus.NOT_FOUND
				exception.reason shouldBe "City not found"
			}
		}

		context("when service IDs are invalid") {
			it("should throw ResponseStatusException with BAD_REQUEST status") {
				val userWithCompany = user.copy(company = company)
				val services = listOf(Service(id = 1L, name = "WiFi")) // Only 1 service found instead of 2

				every { userRepository.findWithCompanyByEmail(email) } returns userWithCompany
				every { cityRepository.findById(command.cityId) } returns Optional.of(city)
				every { stayTypeRepository.findById(command.stayTypeId) } returns Optional.of(stayType)
				every { stayMapper.toEntity(command, city, stayType, company) } returns stay
				every { serviceRepository.findAllById(command.serviceIds!!) } returns services

				val exception = shouldThrow<ResponseStatusException> {
					stayService.createStay(email, command)
				}
				exception.statusCode shouldBe HttpStatus.BAD_REQUEST
				exception.reason shouldBe "One or more service IDs are invalid"
			}
		}
	}

	describe("updateStay") {
		val email = "host@example.com"
		val stayId = 1L
		val command = StayUpdateCommand(
				cityId = null,
				stayTypeId = null,
				name = "Updated Hotel Name",
				address = null,
				latitude = null,
				longitude = null,
				description = "Updated description",
				serviceIds = null,
				imageUrls = null
		)

		context("when user owns the stay") {
			it("should update the stay successfully") {
				val stayWithCompany = stay.copy(company = company)
				val updatedStay = stayWithCompany.copy(
					name = command.name!!,
					description = command.description
				)

				every { stayRepository.findById(stayId) } returns Optional.of(stayWithCompany)
				every { stayRepository.save(any<Stay>()) } returns updatedStay

				val result = stayService.updateStay(email, stayId, command)

				result.name shouldBe "Updated Hotel Name"
				result.description shouldBe "Updated description"
				verify(exactly = 1) { stayRepository.findById(stayId) }
				verify(exactly = 1) { stayRepository.save(any<Stay>()) }
			}
		}

		context("when user does not own the stay") {
			it("should throw ResponseStatusException with FORBIDDEN status") {
				val otherUser = user.copy(id = 2L, email = "other@example.com")
				val otherCompany = company.copy(id = 2L, user = otherUser)
				val stayWithOtherCompany = stay.copy(company = otherCompany)

				every { stayRepository.findById(stayId) } returns Optional.of(stayWithOtherCompany)

				val exception = shouldThrow<ResponseStatusException> {
					stayService.updateStay(email, stayId, command)
				}
				exception.statusCode shouldBe HttpStatus.FORBIDDEN
				exception.reason shouldBe "You don't own this stay"
			}
		}

		context("when stay does not exist") {
			it("should throw ResponseStatusException with NOT_FOUND status") {
				every { stayRepository.findById(stayId) } returns Optional.empty()

				val exception = shouldThrow<ResponseStatusException> {
					stayService.updateStay(email, stayId, command)
				}
				exception.statusCode shouldBe HttpStatus.NOT_FOUND
				exception.reason shouldBe "Stay not found"
			}
		}
	}

	describe("getImagesForStay") {
		val stayId = 1L

		context("when stay exists") {
			it("should return list of images") {
				val images = listOf(
					StayImage(id = 1L, link = "http://example.com/image1.jpg", stay = stay),
					StayImage(id = 2L, link = "http://example.com/image2.jpg", stay = stay)
				)

				every { stayRepository.existsById(stayId) } returns true
				every { stayImageRepository.findAllByStayId(stayId) } returns images

				val result = stayService.getImagesForStay(stayId)

				result.size shouldBe 2
				verify(exactly = 1) { stayRepository.existsById(stayId) }
				verify(exactly = 1) { stayImageRepository.findAllByStayId(stayId) }
			}
		}

		context("when stay does not exist") {
			it("should throw ResponseStatusException with NOT_FOUND status") {
				every { stayRepository.existsById(stayId) } returns false

				val exception = shouldThrow<ResponseStatusException> {
					stayService.getImagesForStay(stayId)
				}
				exception.statusCode shouldBe HttpStatus.NOT_FOUND
				exception.reason shouldBe "Stay not found"
			}
		}
	}
})
