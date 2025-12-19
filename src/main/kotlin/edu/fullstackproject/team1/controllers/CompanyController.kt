package edu.fullstackproject.team1.controllers

import edu.fullstackproject.team1.dtos.requests.CompanyCreateRequest
import edu.fullstackproject.team1.dtos.requests.CompanyUpdateRequest
import edu.fullstackproject.team1.dtos.responses.CompanyResponse
import edu.fullstackproject.team1.mappers.CompanyMapper
import edu.fullstackproject.team1.services.CompanyService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/companies")
@Tag(name = "Companies", description = "Company management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
class CompanyController(
	private val companyService: CompanyService,
	private val companyMapper: CompanyMapper,
) {
	@PostMapping
	@Operation(
		summary = "Create company",
		description = "Register a new company for the authenticated user",
	)
	@ApiResponses(
		value =
			[
				ApiResponse(
					responseCode = "201",
					description = "Company created successfully",
					content =
						[
							Content(
								schema =
									Schema(
										implementation =
											CompanyResponse::class,
									),
							),
						],
				),
				ApiResponse(
					responseCode = "400",
					description = "Invalid input data or user already has a company",
					content = [Content()],
				),
				ApiResponse(
					responseCode = "401",
					description = "Unauthorized",
					content = [Content()],
				),
			],
	)
	fun createCompany(
		@AuthenticationPrincipal user: UserDetails,
		@RequestBody @Valid request: CompanyCreateRequest,
	): ResponseEntity<CompanyResponse> {
		val company = companyService.createCompany(user.username, request.toCommand())
		return ResponseEntity.status(HttpStatus.CREATED).body(companyMapper.toResponse(company, true))
	}


	@GetMapping("/{id}")
	@Operation(
		summary = "Get company by ID",
		description = "Retrieve a specific company by its ID",
	)
	@ApiResponses(
		value =
			[
				ApiResponse(
					responseCode = "200",
					description = "Company retrieved successfully",
					content =
						[
							Content(
								schema =
									Schema(
										implementation =
											CompanyResponse::class,
									),
							),
						],
				),
				ApiResponse(
					responseCode = "401",
					description = "Unauthorized",
					content = [Content()],
				),
				ApiResponse(
					responseCode = "404",
					description = "Company not found",
					content = [Content()],
				),
			],
	)
	fun getCompanyById(
		@Parameter(description = "Company ID") @PathVariable id: Long,
	): ResponseEntity<CompanyResponse> {
		val company = companyService.getCompanyById(id)
		return ResponseEntity.ok(companyMapper.toResponse(company, true))
	}

	@GetMapping
	@Operation(
		summary = "Get all companies",
		description = "Retrieve all registered companies",
	)
	@ApiResponses(
		value =
			[
				ApiResponse(
					responseCode = "200",
					description = "Companies retrieved successfully",
					content = [Content(array = ArraySchema(schema = Schema(implementation = CompanyResponse::class)))],
				),
				ApiResponse(
					responseCode = "401",
					description = "Unauthorized",
					content = [Content()],
				),
			],
	)
	fun getAllCompanies(): ResponseEntity<List<CompanyResponse>> {
		val companies = companyService.getAllCompanies()
		return ResponseEntity.ok(companyMapper.toResponseList(companies, false))
	}

	@PutMapping("/{id}")
	@Operation(
		summary = "Update company",
		description = "Update an existing company owned by the authenticated user",
	)
	@ApiResponses(
		value =
			[
				ApiResponse(
					responseCode = "200",
					description = "Company updated successfully",
					content =
						[
							Content(
								schema =
									Schema(
										implementation =
											CompanyResponse::class,
									),
							),
						],
				),
				ApiResponse(
					responseCode = "400",
					description = "Invalid input data",
					content = [Content()],
				),
				ApiResponse(
					responseCode = "401",
					description = "Unauthorized",
					content = [Content()],
				),
				ApiResponse(
					responseCode = "403",
					description = "Forbidden - not the owner",
					content = [Content()],
				),
				ApiResponse(
					responseCode = "404",
					description = "Company not found",
					content = [Content()],
				),
			],
	)
	fun updateCompany(
		@AuthenticationPrincipal user: UserDetails,
		@Parameter(description = "Company ID") @PathVariable id: Long,
		@RequestBody @Valid request: CompanyUpdateRequest,
	): ResponseEntity<CompanyResponse> {
		val company = companyService.updateCompany(user.username, id, request.toCommand())
		return ResponseEntity.ok(companyMapper.toResponse(company, true))
	}

	@DeleteMapping("/{id}")
	@Operation(
		summary = "Delete company",
		description = "Delete a specific company owned by the authenticated user",
	)
	@ApiResponses(
		value =
			[
				ApiResponse(
					responseCode = "204",
					description = "Company deleted successfully",
				),
				ApiResponse(
					responseCode = "401",
					description = "Unauthorized",
					content = [Content()],
				),
				ApiResponse(
					responseCode = "403",
					description = "Forbidden - not the owner",
					content = [Content()],
				),
				ApiResponse(
					responseCode = "404",
					description = "Company not found",
					content = [Content()],
				),
			],
	)
	fun deleteCompany(
		@AuthenticationPrincipal user: UserDetails,
		@Parameter(description = "Company ID") @PathVariable id: Long,
	): ResponseEntity<Void> {
		companyService.deleteCompany(user.username, id)
		return ResponseEntity.noContent().build()
	}
}
