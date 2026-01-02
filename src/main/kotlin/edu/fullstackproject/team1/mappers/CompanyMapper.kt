package edu.fullstackproject.team1.mappers

import edu.fullstackproject.team1.dtos.responses.CompanyResponse
import edu.fullstackproject.team1.models.Company
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

@Component
class CompanyMapper(
	@Lazy private val userMapper: UserMapper,
	@Lazy private val stayMapper: StayMapper,
) {
	fun toResponse(
		company: Company,
		includeRelations: Boolean = false,
	): CompanyResponse {
		val userResp = if (includeRelations) userMapper.toResponse(company.user) else null
		val staysResp = if (includeRelations) {
			company.stays.map { stayMapper.toResponse(it, includeRelations = false) }
		} else {
			null
		}

		return CompanyResponse(
			id = company.id!!,
			userId = company.user.id!!,
			name = company.name,
			email = company.email,
			phone = company.phone,
			description = company.description,
			createdAt = company.createdAt!!,
			updatedAt = company.updatedAt!!,
			user = userResp,
			stays = staysResp,
		)
	}

	fun toResponseList(companies: List<Company>, includeRelations: Boolean = false): List<CompanyResponse> {
		return companies.map { toResponse(it, includeRelations) }
	}
}

