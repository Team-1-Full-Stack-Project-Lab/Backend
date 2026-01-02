plugins {
	kotlin("jvm") version "2.2.10"
	kotlin("plugin.spring") version "2.2.10"
	kotlin("plugin.serialization") version "2.2.0"
	id("org.springframework.boot") version "3.5.6"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.jlleitschuh.gradle.ktlint") version "13.1.0"
	id("org.flywaydb.flyway") version "11.15.0"
	kotlin("plugin.jpa") version "2.2.10"
}

group = "edu.fullstackproject"
version = "0.0.1-SNAPSHOT"
description = "Full stack web application developed by Team 1"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

configurations.all {
	resolutionStrategy {
		eachDependency {
			if (requested.group == "org.jetbrains.kotlinx" && requested.name.startsWith("kotlinx-serialization")) {
				useVersion("1.8.1")
			}
			if (requested.group == "org.jetbrains.kotlinx" && requested.name.startsWith("kotlinx-coroutines")) {
				useVersion("1.10.2")
			}
		}
	}
}

repositories {
	mavenCentral()
}

buildscript {
	dependencies {
		classpath("org.flywaydb:flyway-database-postgresql:11.15.0")
		classpath("org.postgresql:postgresql:42.7.4")
	}
}

dependencies {
	// Spring Boot
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")

	//Koog
	implementation("ai.koog:koog-spring-boot-starter:0.5.3")

	//Coroutines
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

	//Serialization
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.8.1")

	// Kotlin
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	// Database
	implementation("org.flywaydb:flyway-core")
	implementation("org.flywaydb:flyway-database-postgresql")
	runtimeOnly("org.postgresql:postgresql")

	// Development Tools
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	// JWT
	implementation("io.jsonwebtoken:jjwt-api:0.13.0")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.13.0")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.13.0")

	// OpenAPI/Swagger Documentation
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")

	// Testing
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	// Kotest
	testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
	testImplementation("io.kotest:kotest-assertions-core:5.8.0")
	testImplementation("io.kotest:kotest-property:5.8.0")
	testImplementation("io.mockk:mockk:1.13.9")
	testImplementation("com.ninja-squad:springmockk:4.0.2")
	testImplementation("org.springframework.graphql:spring-graphql-test")

	// GraphQL
	implementation("org.springframework.boot:spring-boot-starter-graphql")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

flyway {
	driver = "org.postgresql.Driver"
	url = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:5432/fullstack_project"
	user = System.getenv("DB_USERNAME") ?: "postgres"
	password = System.getenv("DB_PASSWORD") ?: ""
	schemas = arrayOf("public")
	locations = arrayOf("filesystem:src/main/resources/db/migration")
	cleanDisabled = System.getenv("ENVIRONMENT") == "production"
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
