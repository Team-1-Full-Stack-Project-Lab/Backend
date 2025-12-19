package edu.fullstackproject.team1.mappers

import edu.fullstackproject.team1.dtos.responses.UserResponse
import edu.fullstackproject.team1.models.User
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

@Component
class UserMapper(
	@Lazy private val companyMapper: CompanyMapper,
) {
	fun toResponse(user: User, includeRelations: Boolean = false): UserResponse {
		val companyResp = if (includeRelations && user.company != null) {
			companyMapper.toResponse(user.company!!, includeRelations = false)
		} else {
			null
		}

		return UserResponse(
			email = user.email,
			firstName = user.firstName,
			lastName = user.lastName,
			company = companyResp,
		)
	}
}
