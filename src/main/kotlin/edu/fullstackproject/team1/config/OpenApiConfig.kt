package edu.fullstackproject.team1.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {
	@Bean
	fun customOpenAPI(): OpenAPI =
		OpenAPI()
			.info(
				Info()
					.title("Team 1 Full Stack Project API")
					.version("1.0.0")
					.description(
						"""
						REST API documentation for the Full Stack Project Lab.

						**Features:**
						- Authentication & Authorization (JWT)
						- User Profile Management
						- Trip & Itinerary Management
						- Stay & Accommodation Search
						- Geographic Location Services

						**Authentication:**
						Use the `/auth/login` endpoint to obtain a JWT token, then include it in the `Authorization` header as `Bearer <token>`.
						""".trimIndent(),
					).contact(
						Contact()
							.name("Team 1 Full Stack Project Lab")
							.url(
								"https://github.com/Team-1-Full-Stack-Project-Lab",
							),
					),
			).servers(
				listOf(
					Server()
						.url("http://localhost:8080")
						.description("Local Development Server"),
					Server()
						.url("http://localhost:5433")
						.description("Docker Container"),
				),
			).addSecurityItem(
				SecurityRequirement().addList("Bearer Authentication"),
			).components(
				Components()
					.addSecuritySchemes(
						"Bearer Authentication",
						SecurityScheme()
							.type(SecurityScheme.Type.HTTP)
							.scheme("bearer")
							.bearerFormat("JWT")
							.description(
								"Enter JWT token obtained from /auth/login",
							),
					),
			)
}
