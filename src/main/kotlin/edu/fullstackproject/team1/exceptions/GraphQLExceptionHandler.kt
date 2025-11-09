package edu.fullstackproject.team1.exceptions

import graphql.GraphQLError
import graphql.GraphqlErrorBuilder
import graphql.schema.DataFetchingEnvironment
import jakarta.validation.ConstraintViolationException
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter
import org.springframework.graphql.execution.ErrorType
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@Component
class GraphQLExceptionHandler : DataFetcherExceptionResolverAdapter() {
	override fun resolveToSingleError(
		ex: Throwable,
		env: DataFetchingEnvironment
	): GraphQLError? {
		return when (ex) {
			// Handle validation errors (400 Bad Request)
			is MethodArgumentNotValidException -> {
				val errors = ex.bindingResult.allErrors
					.groupBy { (it as? FieldError)?.field ?: "unknown" }
					.mapValues { (_, fieldErrors) ->
						fieldErrors.map { it.defaultMessage ?: "Validation error" }
					}
				buildGraphQLError(
					env = env,
					status = HttpStatus.BAD_REQUEST.value(),
					error = "Validation error",
					message = "The provided data is invalid",
					errors = errors,
					errorType = ErrorType.BAD_REQUEST
				)
			}
			// Handle constraint violations (400 Bad Request)
			is ConstraintViolationException -> {
				val errors = ex.constraintViolations
					.groupBy { it.propertyPath.toString() }
					.mapValues { (_, violations) ->
						violations.map { it.message }
					}
				buildGraphQLError(
					env = env,
					status = HttpStatus.BAD_REQUEST.value(),
					error = "Validation error",
					message = "The provided data is invalid",
					errors = errors,
					errorType = ErrorType.BAD_REQUEST
				)
			}
			// Handle ResponseStatusException (custom exceptions from services)
			is ResponseStatusException -> {
				val status = ex.statusCode.value()
				val errorType = when (status) {
					400 -> ErrorType.BAD_REQUEST
					401 -> ErrorType.UNAUTHORIZED
					403 -> ErrorType.FORBIDDEN
					404 -> ErrorType.NOT_FOUND
					else -> ErrorType.INTERNAL_ERROR
				}
				val errorName = when (status) {
					400 -> "Bad request"
					401 -> "Unauthorized"
					403 -> "Forbidden"
					404 -> "Not found"
					else -> "Internal server error"
				}
				buildGraphQLError(
					env = env,
					status = status,
					error = errorName,
					message = ex.reason ?: "An error occurred",
					errorType = errorType
				)
			}
			// Handle IllegalArgumentException (400 Bad Request)
			is IllegalArgumentException -> {
				buildGraphQLError(
					env = env,
					status = HttpStatus.BAD_REQUEST.value(),
					error = "Bad request",
					message = ex.message ?: "Invalid argument",
					errorType = ErrorType.BAD_REQUEST
				)
			}
			// Handle any other generic exceptions (500 Internal Server Error)
			is Exception -> {
				buildGraphQLError(
					env = env,
					status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
					error = "Internal server error",
					message = ex.message ?: "An unexpected error occurred",
					errorType = ErrorType.INTERNAL_ERROR
				)
			}
			else -> null
		}
	}
	//GraphQL error builder
	private fun buildGraphQLError(
		env: DataFetchingEnvironment,
		status: Int,
		error: String,
		message: String,
		errors: Map<String, List<String>>? = null,
		errorType: ErrorType
	): GraphQLError {
		val errorDetails = mutableMapOf<String, Any>(
			"timestamp" to Instant.now().toString(),
			"status" to status,
			"error" to error,
			"message" to message,
			"path" to env.executionStepInfo.path.toString()
		)
		// Only include "errors" field if there are validation errors
		if (errors != null) {
			errorDetails["errors"] = errors
		}
		return GraphqlErrorBuilder.newError()
			.errorType(errorType)
			.message(message)
			.path(env.executionStepInfo.path)
			.location(env.field.sourceLocation)
			.extensions(errorDetails)
			.build()
	}
}
