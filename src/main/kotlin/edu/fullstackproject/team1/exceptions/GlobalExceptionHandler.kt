package edu.fullstackproject.team1.exceptions

import com.fasterxml.jackson.annotation.JsonInclude
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.Instant

@RestControllerAdvice
class GlobalExceptionHandler {
	@ExceptionHandler(MethodArgumentNotValidException::class)
	fun handleValidationErrors(
		ex: MethodArgumentNotValidException,
		request: HttpServletRequest,
	): ResponseEntity<ErrorResponse> {
		val errors =
			ex.bindingResult.allErrors
				.groupBy { (it as? FieldError)?.field ?: "unknown" }
				.mapValues { (_, fieldErrors) ->
					fieldErrors.map { it.defaultMessage ?: "Validation error" }
				}

		val errorResponse =
			ErrorResponse(
				timestamp = Instant.now(),
				status = HttpStatus.BAD_REQUEST.value(),
				error = "Validation error",
				message = "The provided data is invalid",
				errors = errors,
				path = request.requestURI,
			)

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
	}

	@ExceptionHandler(Exception::class)
	fun handleGenericError(
		ex: Exception,
		request: HttpServletRequest,
	): ResponseEntity<ErrorResponse> {
		val errorResponse =
			ErrorResponse(
				timestamp = Instant.now(),
				status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
				error = "Internal server error",
				message = ex.message ?: "An unexpected error occurred",
				path = request.requestURI,
			)

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
	}
}

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorResponse(
	val timestamp: Instant,
	val status: Int,
	val error: String,
	val message: String,
	val errors: Map<String, List<String>>? = null,
	val path: String,
)
