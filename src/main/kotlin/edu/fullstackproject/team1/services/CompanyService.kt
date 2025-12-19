package edu.fullstackproject.team1.services

import edu.fullstackproject.team1.dtos.commands.CompanyCreateCommand
import edu.fullstackproject.team1.dtos.commands.CompanyUpdateCommand
import edu.fullstackproject.team1.models.Company
import edu.fullstackproject.team1.repositories.CompanyRepository
import edu.fullstackproject.team1.repositories.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
@Transactional
class CompanyService(
	private val companyRepository: CompanyRepository,
	private val userRepository: UserRepository,
) {
	fun createCompany(email: String, command: CompanyCreateCommand): Company {
		val user = userRepository.findByEmail(email)
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")

		if (companyRepository.existsByUserId(user.id!!)) {
			throw ResponseStatusException(HttpStatus.BAD_REQUEST, "User already has a company")
		}

		val company = Company(
			user = user,
			name = command.name,
			email = command.email,
			phone = command.phone,
			description = command.description,
		)

		return companyRepository.save(company)
	}

	fun getCompanyByUser(email: String): Company {
		val user = userRepository.findByEmail(email)
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")

		return companyRepository.findByUserId(user.id!!)
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found for this user")
	}

	fun getCompanyById(id: Long): Company {
		return companyRepository.findById(id)
			.orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found") }
	}

	fun getAllCompanies(): List<Company> {
		return companyRepository.findAll()
	}

	fun updateCompany(email: String, id: Long, command: CompanyUpdateCommand): Company {
		val company = companyRepository.findById(id)
			.orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found") }

		if (company.user.email != email) {
			throw ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to update this company")
		}

		command.name?.let { company.name = it }
		command.email?.let { company.email = it }
		command.phone?.let { company.phone = it }
		command.description?.let { company.description = it }

		return companyRepository.save(company)
	}

	fun deleteCompany(email: String, id: Long) {
		val company = companyRepository.findById(id)
			.orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found") }

		if (company.user.email != email) {
			throw ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to delete this company")
		}

		companyRepository.delete(company)
	}
}
