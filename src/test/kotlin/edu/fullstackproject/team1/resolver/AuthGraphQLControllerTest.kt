package edu.fullstackproject.team1.resolver

import com.ninjasquad.springmockk.MockkBean
import edu.fullstackproject.team1.config.GraphQLScalarConfig
import edu.fullstackproject.team1.dtos.AuthResponse
import edu.fullstackproject.team1.services.AuthService
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest
import org.springframework.context.annotation.Import
import org.springframework.graphql.test.tester.GraphQlTester
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.server.ResponseStatusException

@GraphQlTest(AuthGraphQLController::class)
@Import(GraphQLScalarConfig::class)
class AuthGraphQLControllerTest {
	@Autowired
	private lateinit var graphQlTester: GraphQlTester
	@MockkBean
	private lateinit var authService: AuthService

	/* LOGIN MUTATION TESTS */
	@Test
	fun testLoginShouldReturnTokenWhenCredentialsAreValid() {
		val authResponse = AuthResponse(token = "valid.jwt.token")
		every {
			authService.login(match {
				it.email == "test@example.com" && it.password == "password123" })
		} returns authResponse
		graphQlTester.document(
			"""
            mutation {
                login(request: { email: "test@example.com", password: "password123" }) {
                    token
                }
            }
            """)
			.execute()
			.path("login.token").entity(String::class.java).isEqualTo("valid.jwt.token")
	}
	@Test
	fun testLoginShouldFailWithInvalidCredentials() {
		every {
			authService.login(match {
				it.email == "wrong@example.com" && it.password == "wrongpassword" })
		} throws BadCredentialsException("Invalid credentials")
		graphQlTester.document(
			"""
            mutation {
                login(request: { email: "wrong@example.com", password: "wrongpassword" }) {
                    token
                }
            }
            """)
			.execute()
			.errors()
			.expect { it.message?.contains("Invalid credentials") == true }
	}
	@Test
	fun testLoginShouldFailWithInvalidEmailFormat() {
		graphQlTester.document(
			"""
            mutation {
                login(request: { email: "not-an-email", password: "password123" }) {
                    token
                }
            }
            """)
			.execute()
			.errors()
			.expect { error ->
				error.message?.contains("email") == true ||
					error.message?.contains("Email") == true ||
					error.message?.contains("valid") == true }
	}
	@Test
	fun testLoginShouldFailWithEmptyEmail() {
		graphQlTester.document(
			"""
            mutation {
                login(request: { email: "", password: "password123" }) {
                    token
                }
            }
            """)
			.execute()
			.errors()
			.expect { it.message?.contains("The provided data is invalid") == true }
	}
	@Test
	fun testLoginShouldFailWithEmptyPassword() {
		every {
			authService.login(match { it.password.isEmpty() })
		} throws ResponseStatusException(
			HttpStatus.BAD_REQUEST,
			"Password must not be blank")
		graphQlTester.document(
			"""
        mutation {
            login(request: { email: "test@example.com", password: "" }) {
                token
            }
        }
        """)
			.execute()
			.errors()
			.expect { error ->
				error.message?.contains("The provided data is invalid") == true }
	}
	@Test
	fun testLoginShouldFailWithShortPassword() {
		graphQlTester.document(
			"""
            mutation {
                login(request: { email: "test@example.com", password: "12345" }) {
                    token
                }
            }
            """)
			.execute()
			.errors()
			.expect { error ->
				error.message?.contains("The provided data is invalid") == true }
	}
	@Test
	fun testLoginWithDifferentValidEmails() {
		val authResponse = AuthResponse(token = "valid.token.123")
		every {
			authService.login(match { it.email == "john.doe+test@example.co.uk" })
		} returns authResponse
		graphQlTester.document(
			"""
            mutation {
                login(request: { email: "john.doe+test@example.co.uk", password: "password123" }) {
                    token
                }
            }
            """)
			.execute()
			.path("login.token").entity(String::class.java).isEqualTo("valid.token.123")
	}
	@Test
	fun testLoginServiceIsCalledWithCorrectParameters() {
		val authResponse = AuthResponse(token = "test.token")
		every {
			authService.login(match {
				it.email == "verify@example.com" && it.password == "verify123" })
		} returns authResponse
		graphQlTester.document(
			"""
            mutation {
                login(request: { email: "verify@example.com", password: "verify123" }) {
                    token
                }
            }
            """)
			.execute()
			.path("login.token").entity(String::class.java).isEqualTo("test.token")
		verify(exactly = 1) {
			authService.login(match {
				it.email == "verify@example.com" && it.password == "verify123" })
		}
	}
	@Test
	fun testLoginWithLongPassword() {
		val longPassword = "a".repeat(100)
		val authResponse = AuthResponse(token = "long.password.token")
		every {
			authService.login(match { it.password == longPassword })
		} returns authResponse
		graphQlTester.document(
			"""
            mutation {
                login(request: { email: "test@example.com", password: "$longPassword" }) {
                    token
                }
            }
            """)
			.execute()
			.path("login.token").entity(String::class.java).isEqualTo("long.password.token")
	}

	/* REGISTER MUTATION TESTS */
	@Test
	fun testRegisterShouldCreateUserAndReturnToken() {
		val authResponse = AuthResponse(token = "new.user.token")
		every {
			authService.register(match {
				it.email == "newuser@example.com" &&
					it.firstName == "John" &&
					it.lastName == "Doe" &&
					it.password == "password123" })
		} returns authResponse
		graphQlTester.document(
			"""
            mutation {
                register(request: {
                    email: "newuser@example.com",
                    firstName: "John",
                    lastName: "Doe",
                    password: "password123"
                }) {
                    token
                }
            }
            """)
			.execute()
			.path("register.token").entity(String::class.java).isEqualTo("new.user.token")
	}
	@Test
	fun testRegisterShouldFailWhenUserAlreadyExists() {
		every {
			authService.register(match { it.email == "existing@example.com" })
		} throws ResponseStatusException(
			HttpStatus.CONFLICT,
			"A user with that email already exists")
		graphQlTester.document(
			"""
            mutation {
                register(request: {
                    email: "existing@example.com",
                    firstName: "Test",
                    lastName: "User",
                    password: "password123"
                }) {
                    token
                }
            }
            """)
			.execute()
			.errors()
			.expect { it.message?.contains("already exists") == true }
	}
	@Test
	fun testRegisterShouldFailWithInvalidEmailFormat() {
		graphQlTester.document(
			"""
            mutation {
                register(request: {
                    email: "invalid-email",
                    firstName: "Test",
                    lastName: "User",
                    password: "password123"
                }) {
                    token
                }
            }
            """)
			.execute()
			.errors()
			.expect { error ->
				error.message?.contains("email") == true ||
					error.message?.contains("Email") == true ||
					error.message?.contains("valid") == true }
	}
	@Test
	fun testRegisterShouldFailWithEmptyEmail() {
		graphQlTester.document(
			"""
            mutation {
                register(request: {
                    email: "",
                    firstName: "Test",
                    lastName: "User",
                    password: "password123"
                }) {
                    token
                }
            }
            """)
			.execute()
			.errors()
			.expect { error ->
				error.message?.contains("The provided data is invalid") == true }
	}
	@Test
	fun testRegisterShouldFailWithEmptyFirstName() {
		graphQlTester.document(
			"""
            mutation {
                register(request: {
                    email: "test@example.com",
                    firstName: "",
                    lastName: "User",
                    password: "password123"
                }) {
                    token
                }
            }
            """)
			.execute()
			.errors()
			.expect { error ->
				error.message?.contains("The provided data is invalid") == true }
	}
	@Test
	fun testRegisterShouldFailWithEmptyLastName() {
		graphQlTester.document(
			"""
            mutation {
                register(request: {
                    email: "test@example.com",
                    firstName: "Test",
                    lastName: "",
                    password: "password123"
                }) {
                    token
                }
            }
            """)
			.execute()
			.errors()
			.expect { error ->
				error.message?.contains("The provided data is invalid") == true }
	}
	@Test
	fun testRegisterShouldFailWithEmptyPassword() {
		graphQlTester.document(
			"""
            mutation {
                register(request: {
                    email: "test@example.com",
                    firstName: "Test",
                    lastName: "User",
                    password: ""
                }) {
                    token
                }
            }
            """)
			.execute()
			.errors()
			.expect { error ->
				error.message?.contains("The provided data is invalid") == true }
	}
	@Test
	fun testRegisterShouldFailWithShortPassword() {
		graphQlTester.document(
			"""
            mutation {
                register(request: {
                    email: "test@example.com",
                    firstName: "Test",
                    lastName: "User",
                    password: "12345"
                }) {
                    token
                }
            }
            """)
			.execute()
			.errors()
			.expect { error ->
				error.message?.contains("The provided data is invalid") == true }
	}
	@Test
	fun testRegisterWithSpecialCharactersInName() {
		val authResponse = AuthResponse(token = "special.char.token")
		every {
			authService.register(match {
				it.firstName == "José-María" && it.lastName == "O'Connor" })
		} returns authResponse
		graphQlTester.document(
			"""
            mutation {
                register(request: {
                    email: "special@example.com",
                    firstName: "José-María",
                    lastName: "O'Connor",
                    password: "password123"
                }) {
                    token
                }
            }
            """)
			.execute()
			.path("register.token").entity(String::class.java).isEqualTo("special.char.token")
	}
	@Test
	fun testRegisterWithLongNames() {
		val authResponse = AuthResponse(token = "long.name.token")
		every {
			authService.register(match {
				it.firstName == "Alexander Christopher Benjamin" &&
					it.lastName == "Van Der Berg-Williams III" })
		} returns authResponse
		graphQlTester.document(
			"""
            mutation {
                register(request: {
                    email: "longname@example.com",
                    firstName: "Alexander Christopher Benjamin",
                    lastName: "Van Der Berg-Williams III",
                    password: "password123"
                }) {
                    token
                }
            }
            """)
			.execute()
			.path("register.token").entity(String::class.java).isEqualTo("long.name.token")
	}
	@Test
	fun testRegisterServiceIsCalledWithCorrectParameters() {
		val authResponse = AuthResponse(token = "verify.register.token")
		every {
			authService.register(match {
				it.email == "verify@example.com" &&
					it.firstName == "Verify" &&
					it.lastName == "Test" &&
					it.password == "verify123" })
		} returns authResponse
		graphQlTester.document(
			"""
            mutation {
                register(request: {
                    email: "verify@example.com",
                    firstName: "Verify",
                    lastName: "Test",
                    password: "verify123"
                }) {
                    token
                }
            }
            """)
			.execute()
			.path("register.token").entity(String::class.java).isEqualTo("verify.register.token")
		verify(exactly = 1) {
			authService.register(match {
				it.email == "verify@example.com" &&
					it.firstName == "Verify" &&
					it.lastName == "Test" &&
					it.password == "verify123" })
		}
	}
	@Test
	fun testRegisterWithDifferentValidEmails() {
		val authResponse = AuthResponse(token = "complex.email.token")
		every {
			authService.register(match { it.email == "user.name+tag@subdomain.example.co.uk" })
		} returns authResponse
		graphQlTester.document(
			"""
            mutation {
                register(request: {
                    email: "user.name+tag@subdomain.example.co.uk",
                    firstName: "Complex",
                    lastName: "Email",
                    password: "password123"
                }) {
                    token
                }
            }
            """)
			.execute()
			.path("register.token").entity(String::class.java).isEqualTo("complex.email.token")
	}

	/* ADDITIONAL TESTS */
	@Test
	fun testMultipleMutationsInSingleDocument() {
		val loginResponse = AuthResponse(token = "login.token")
		val registerResponse = AuthResponse(token = "register.token")
		every {
			authService.login(match { it.email == "login@example.com" })
		} returns loginResponse
		every {
			authService.register(match { it.email == "register@example.com" })
		} returns registerResponse
		graphQlTester.document(
			"""
            mutation {
                login(request: { email: "login@example.com", password: "password123" }) {
                    token
                }
            }
            """)
			.execute()
			.path("login.token").entity(String::class.java).isEqualTo("login.token")
	}
	@Test
	fun testLoginWithMinimumValidPassword() {
		val authResponse = AuthResponse(token = "min.pass.token")
		every {
			authService.login(match { it.password == "123456" })
		} returns authResponse
		graphQlTester.document(
			"""
            mutation {
                login(request: { email: "test@example.com", password: "123456" }) {
                    token
                }
            }
            """)
			.execute()
			.path("login.token").entity(String::class.java).isEqualTo("min.pass.token")
	}

	@Test
	fun testRegisterWithMinimumValidPassword() {
		val authResponse = AuthResponse(token = "min.reg.token")
		every {
			authService.register(match { it.password == "abcdef" })
		} returns authResponse
		graphQlTester.document(
			"""
            mutation {
                register(request: {
                    email: "test@example.com",
                    firstName: "Test",
                    lastName: "User",
                    password: "abcdef"
                }) {
                    token
                }
            }
            """)
			.execute()
			.path("register.token").entity(String::class.java).isEqualTo("min.reg.token")
	}
	@Test
	fun testRegisterWithSingleCharacterNames() {
		val authResponse = AuthResponse(token = "single.char.token")
		every {
			authService.register(match {
				it.firstName == "A" && it.lastName == "B" })
		} returns authResponse
		graphQlTester.document(
			"""
            mutation {
                register(request: {
                    email: "test@example.com",
                    firstName: "A",
                    lastName: "B",
                    password: "password123"
                }) {
                    token
                }
            }
            """)
			.execute()
			.path("register.token").entity(String::class.java).isEqualTo("single.char.token")
	}
}
