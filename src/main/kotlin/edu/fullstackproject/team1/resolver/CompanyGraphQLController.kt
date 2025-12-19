package edu.fullstackproject.team1.resolver

import edu.fullstackproject.team1.dtos.requests.CompanyCreateRequest
import edu.fullstackproject.team1.dtos.requests.CompanyUpdateRequest
import edu.fullstackproject.team1.dtos.responses.CompanyResponse
import edu.fullstackproject.team1.mappers.CompanyMapper
import edu.fullstackproject.team1.services.CompanyService
import jakarta.validation.Valid
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller

@Controller
class CompanyGraphQLController(
	private val companyService: CompanyService,
	private val companyMapper: CompanyMapper,
) {
	@MutationMapping
	@PreAuthorize("isAuthenticated()")
	fun createCompany(
		@AuthenticationPrincipal user: UserDetails,
		@Argument @Valid request: CompanyCreateRequest,
	): CompanyResponse {
		val company = companyService.createCompany(user.username, request.toCommand())
		return companyMapper.toResponse(company, true)
	}

	@QueryMapping
	@PreAuthorize("isAuthenticated()")
	fun getCompanyById(
		@Argument id: Long,
	): CompanyResponse {
		val company = companyService.getCompanyById(id)
		return companyMapper.toResponse(company, true)
	}

	@QueryMapping
	@PreAuthorize("isAuthenticated()")
	fun getAllCompanies(): List<CompanyResponse> {
		val companies = companyService.getAllCompanies()
		return companyMapper.toResponseList(companies, false)
	}

	@MutationMapping
	@PreAuthorize("isAuthenticated()")
	fun updateCompany(
		@AuthenticationPrincipal user: UserDetails,
		@Argument id: Long,
		@Argument @Valid request: CompanyUpdateRequest,
	): CompanyResponse {
		val company = companyService.updateCompany(user.username, id, request.toCommand())
		return companyMapper.toResponse(company, true)
	}

	@MutationMapping
	@PreAuthorize("isAuthenticated()")
	fun deleteCompany(
		@AuthenticationPrincipal user: UserDetails,
		@Argument id: Long,
	) {
		companyService.deleteCompany(user.username, id)
	}
}
