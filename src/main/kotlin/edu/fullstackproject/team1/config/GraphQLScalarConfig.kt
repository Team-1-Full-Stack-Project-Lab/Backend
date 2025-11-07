package edu.fullstackproject.team1.config

import graphql.language.StringValue
import graphql.schema.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.execution.RuntimeWiringConfigurer
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Configuration
class GraphQLScalarConfig {

	@Bean
	fun runtimeWiringConfigurer(): RuntimeWiringConfigurer {
		return RuntimeWiringConfigurer { wiringBuilder ->
			wiringBuilder
				.scalar(dateScalar())
				.scalar(dateTimeScalar())
		}
	}

	private fun dateScalar(): GraphQLScalarType {
		return GraphQLScalarType.newScalar()
			.name("Date")
			.description("Java LocalDate as scalar")
			.coercing(object : Coercing<LocalDate, String> {
				override fun serialize(dataFetcherResult: Any): String {
					return when (dataFetcherResult) {
						is LocalDate -> dataFetcherResult.format(DateTimeFormatter.ISO_DATE)
						else -> throw CoercingSerializeException("Expected a LocalDate object.")
					}
				}

				override fun parseValue(input: Any): LocalDate {
					return try {
						if (input is String) {
							LocalDate.parse(input, DateTimeFormatter.ISO_DATE)
						} else {
							throw CoercingParseValueException("Expected a String")
						}
					} catch (e: Exception) {
						throw CoercingParseValueException("Unable to parse variable value $input as LocalDate", e)
					}
				}

				override fun parseLiteral(input: Any): LocalDate {
					return if (input is StringValue) {
						try {
							LocalDate.parse(input.value, DateTimeFormatter.ISO_DATE)
						} catch (e: Exception) {
							throw CoercingParseLiteralException("Unable to parse literal value $input as LocalDate", e)
						}
					} else {
						throw CoercingParseLiteralException("Expected AST type 'StringValue' but was '${input.javaClass.simpleName}'.")
					}
				}
			})
			.build()
	}

	private fun dateTimeScalar(): GraphQLScalarType {
		return GraphQLScalarType.newScalar()
			.name("DateTime")
			.description("Java Instant/DateTime as scalar")
			.coercing(object : Coercing<String, String> {
				override fun serialize(dataFetcherResult: Any): String {
					return dataFetcherResult.toString()
				}

				override fun parseValue(input: Any): String {
					return input.toString()
				}

				override fun parseLiteral(input: Any): String {
					if (input is StringValue) {
						return input.value
					}
					throw CoercingParseLiteralException("Expected AST type 'StringValue' but was '${input.javaClass.simpleName}'.")
				}
			})
			.build()
	}
}
