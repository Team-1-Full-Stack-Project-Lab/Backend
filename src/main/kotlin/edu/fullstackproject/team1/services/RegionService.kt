package edu.fullstackproject.team1.services

import edu.fullstackproject.team1.models.Region
import edu.fullstackproject.team1.repositories.RegionRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class RegionService(
	private val regionRepository: RegionRepository,
) {
	fun getAllRegions(): List<Region> {
		return regionRepository.findAll()
	}

	fun getRegionById(id: Long): Region {
		return regionRepository
			.findById(id)
			.orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Region not found") }
	}

	fun getRegionByIdWithCountries(id: Long): Region {
		return regionRepository.findByIdWithCountries(id)
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Region not found")
	}

	fun getRegionsByName(name: String): List<Region> {
		return regionRepository.findByNameContainingIgnoreCase(name)
	}
}
