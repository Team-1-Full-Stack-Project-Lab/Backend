package edu.fullstackproject.team1.services

import edu.fullstackproject.team1.dtos.CountryResponse
import edu.fullstackproject.team1.dtos.RegionResponse
import edu.fullstackproject.team1.repositories.RegionRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class RegionService(
	private val regionRepository: RegionRepository,
) {
	fun getAllRegions(): List<RegionResponse> {
		val regions = regionRepository.findAll()

		return regions.map {
			RegionResponse(
				id = it.id,
				name = it.name,
				code = it.code,
				countries = null,
			)
		}
	}

	fun getRegionById(id: Long): RegionResponse {
		val region =
			regionRepository
				.findById(id)
				.orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Region not found") }

		return RegionResponse(
			id = region.id,
			name = region.name,
			code = region.code,
			countries = null,
		)
	}

	fun getRegionByIdWithCountries(id: Long): RegionResponse {
		val region =
			regionRepository.findByIdWithCountries(id)
				?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Region not found")

		return RegionResponse(
			id = region.id,
			name = region.name,
			code = region.code,
			countries =
				region.countries.map {
					CountryResponse(
						id = it.id,
						name = it.name,
						iso2Code = it.iso2Code,
						iso3Code = it.iso3Code,
						phoneCode = it.phoneCode,
						currencyCode = it.currencyCode,
						currencySymbol = it.currencySymbol,
						region = null,
						states = null,
						cities = null,
					)
				},
		)
	}

	fun getRegionsByName(name: String): List<RegionResponse> {
		val regions = regionRepository.findByNameContainingIgnoreCase(name)

		return regions.map {
			RegionResponse(
				id = it.id,
				name = it.name,
				code = it.code,
				countries = null,
			)
		}
	}
}
