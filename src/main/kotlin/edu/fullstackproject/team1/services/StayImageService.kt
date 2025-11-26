package edu.fullstackproject.team1.services

import edu.fullstackproject.team1.dtos.commands.CreateStayImageCommand
import edu.fullstackproject.team1.mappers.StayImageMapper
import edu.fullstackproject.team1.models.StayImage
import edu.fullstackproject.team1.repositories.StayImageRepository
import edu.fullstackproject.team1.repositories.StayRepository
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class StayImageService(
	private val stayImageRepository: StayImageRepository,
	private val stayRepository: StayRepository,
	private val stayImageMapper: StayImageMapper,
) {
	fun getAllStayImages(): List<StayImage> {
		return stayImageRepository.findAll()
	}

	fun getStayImageById(id: Long): StayImage {
		return stayImageRepository.findById(id)
			.orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Stay image not found") }
	}

	fun createStayImage(command: CreateStayImageCommand): StayImage {
		val stay = stayRepository.findById(command.stayId)
			.orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Stay not found") }

		val stayImage = stayImageMapper.toEntity(command, stay)
		return stayImageRepository.save(stayImage)
	}

	@Transactional
	fun deleteStayImage(id: Long) {
		if (!stayImageRepository.existsById(id)) {
			throw ResponseStatusException(HttpStatus.NOT_FOUND, "Stay image not found")
		}

		stayImageRepository.deleteById(id)
	}
}
