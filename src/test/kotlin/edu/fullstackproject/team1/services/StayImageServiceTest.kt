package edu.fullstackproject.team1.services

import edu.fullstackproject.team1.dtos.commands.CreateStayImageCommand
import edu.fullstackproject.team1.mappers.StayImageMapper
import edu.fullstackproject.team1.models.Stay
import edu.fullstackproject.team1.models.StayImage
import edu.fullstackproject.team1.repositories.StayImageRepository
import edu.fullstackproject.team1.repositories.StayRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.util.*

class StayImageServiceTest : DescribeSpec({
	val stayImageRepository = mockk<StayImageRepository>()
	val stayRepository = mockk<StayRepository>()
	val stayImageMapper = mockk<StayImageMapper>()

	val stayImageService = StayImageService(
		stayImageRepository,
		stayRepository,
		stayImageMapper
	)

	val stay = mockk<Stay>()
	val stayImage = StayImage(
		id = 1L,
		link = "https://example.com/image1.jpg",
		stay = stay
	)

	afterEach {
		clearAllMocks()
	}

	describe("getAllStayImages") {
		context("when getting all images") {
			it("should return list of images") {
				val images = listOf(stayImage)
				every { stayImageRepository.findAll() } returns images

				val result = stayImageService.getAllStayImages()

				result.size shouldBe 1
			}
		}
	}

	describe("getStayImageById") {
		context("when image exists") {
			it("should return the image") {
				every { stayImageRepository.findById(1L) } returns Optional.of(stayImage)

				val result = stayImageService.getStayImageById(1L)

				result shouldBe stayImage
			}
		}

		context("when image does not exist") {
			it("should throw ResponseStatusException") {
				every { stayImageRepository.findById(999L) } returns Optional.empty()

				val exception = shouldThrow<ResponseStatusException> {
					stayImageService.getStayImageById(999L)
				}
				exception.statusCode shouldBe HttpStatus.NOT_FOUND
			}
		}
	}

	describe("createStayImage") {
		val command = CreateStayImageCommand(
			stayId = 1L,
			link = "https://example.com/new-image.jpg"
		)

		context("when stay exists") {
			it("should create and return the image") {
				val newImage = StayImage(id = null, link = command.link, stay = stay)
				val savedImage = newImage.copy(id = 2L)

				every { stayRepository.findById(1L) } returns Optional.of(stay)
				every { stayImageMapper.toEntity(command, stay) } returns newImage
				every { stayImageRepository.save(newImage) } returns savedImage

				val result = stayImageService.createStayImage(command)

				result shouldBe savedImage
			}
		}

		context("when stay does not exist") {
			it("should throw ResponseStatusException") {
				every { stayRepository.findById(999L) } returns Optional.empty()

				val exception = shouldThrow<ResponseStatusException> {
					stayImageService.createStayImage(command.copy(stayId = 999L))
				}
				exception.statusCode shouldBe HttpStatus.NOT_FOUND
			}
		}
	}

	describe("deleteStayImage") {
		context("when image exists") {
			it("should delete the image") {
				every { stayImageRepository.existsById(1L) } returns true
				every { stayImageRepository.deleteById(1L) } just Runs

				stayImageService.deleteStayImage(1L)

				verify(exactly = 1) { stayImageRepository.deleteById(1L) }
			}
		}

		context("when image does not exist") {
			it("should throw ResponseStatusException") {
				every { stayImageRepository.existsById(999L) } returns false

				val exception = shouldThrow<ResponseStatusException> {
					stayImageService.deleteStayImage(999L)
				}
				exception.statusCode shouldBe HttpStatus.NOT_FOUND
			}
		}
	}
})
