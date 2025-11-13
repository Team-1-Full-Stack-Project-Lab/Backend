package edu.fullstackproject.team1.resolver

import com.ninjasquad.springmockk.MockkBean
import edu.fullstackproject.team1.config.GraphQLScalarConfig
import edu.fullstackproject.team1.dtos.UserResponse
import edu.fullstackproject.team1.services.UserService
import io.mockk.every
import io.mockk.justRun
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest
import org.springframework.context.annotation.Import
import org.springframework.graphql.test.tester.GraphQlTester
import org.springframework.http.HttpStatus
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.web.server.ResponseStatusException

@GraphQlTest(UserGraphQLController::class)
@Import(GraphQLScalarConfig::class)
class UserGraphQLControllerTest {
	@Autowired
	private lateinit var graphQlTester: GraphQlTester
	@MockkBean
	private lateinit var userService: UserService

	/* GET USER TESTS */
	@Test
	@WithMockUser(username = "test@example.com")
	fun testGetUserShouldGetUserWhenIsAuthenticated() {
		val userResponse = UserResponse(
			email = "test@example.com", firstName = "Test", lastName = "Example")
		every { userService.getUserByEmail("test@example.com") } returns userResponse
		graphQlTester.document(
			"""
            query {
    			getUser {
       	 			email
        			firstName
        			lastName
    			}
			}
            """)
			.execute()
			.path("getUser.email").entity(String::class.java).isEqualTo("test@example.com")
			.path("getUser.firstName").entity(String::class.java).isEqualTo("Test")
			.path("getUser.lastName").entity(String::class.java).isEqualTo("Example")
	}
	@Test
	@WithMockUser(username = "john.doe@example.com")
	fun testGetUserShouldReturnDifferentUserBasedOnAuthentication() {
		val userResponse = UserResponse(
			email = "john.doe@example.com", firstName = "John", lastName = "Doe")
		every { userService.getUserByEmail("john.doe@example.com") } returns userResponse
		graphQlTester.document(
			"""
            query {
                getUser {
                    email
                    firstName
                    lastName
                }
            }
            """)
			.execute()
			.path("getUser.email").entity(String::class.java).isEqualTo("john.doe@example.com")
			.path("getUser.firstName").entity(String::class.java).isEqualTo("John")
			.path("getUser.lastName").entity(String::class.java).isEqualTo("Doe")
	}
	@Test
	fun testGetUserShouldFailWhenNotAuthenticated() {
		graphQlTester.document(
			"""
            query {
                getUser {
                    email
                    firstName
                    lastName
                }
            }
            """)
			.execute()
			.errors()
			.expect { error ->
				error.message?.contains("Authentication required") == true }
	}
	@Test
	@WithMockUser(username = "notfound@example.com")
	fun testGetUserShouldHandleUserNotFound() {
		every { userService.getUserByEmail("notfound@example.com") } throws ResponseStatusException(
			HttpStatus.NOT_FOUND,
			"User not found")
		graphQlTester.document(
			"""
            query {
                getUser {
                    email
                    firstName
                    lastName
                }
            }
            """)
			.execute()
			.errors()
			.expect { it.message?.contains("User not found") == true }
	}
	@Test
	@WithMockUser(username = "user@example.com")
	fun testGetUserShouldReturnOnlyRequestedFields() {
		val userResponse = UserResponse(
			email = "user@example.com", firstName = "Test", lastName = "User")
		every { userService.getUserByEmail("user@example.com") } returns userResponse
		graphQlTester.document(
			"""
            query {
                getUser {
                    email
                }
            }
            """)
			.execute()
			.path("getUser.email").entity(String::class.java).isEqualTo("user@example.com")
	}
	@Test
	@WithMockUser(username = "jane@example.com")
	fun testGetUserWithLongNames() {
		val userResponse = UserResponse(
			email = "jane@example.com", firstName = "Jane Elizabeth Katherine", lastName = "Van Der Berg-Williams")
		every { userService.getUserByEmail("jane@example.com") } returns userResponse
		graphQlTester.document(
			"""
            query {
                getUser {
                    email
                    firstName
                    lastName
                }
            }
            """)
			.execute()
			.path("getUser.firstName").entity(String::class.java).isEqualTo("Jane Elizabeth Katherine")
			.path("getUser.lastName").entity(String::class.java).isEqualTo("Van Der Berg-Williams")
	}
	@Test
	@WithMockUser(username = "special@example.com")
	fun testGetUserWithSpecialCharactersInName() {
		val userResponse = UserResponse(
			email = "special@example.com", firstName = "José-María", lastName = "O'Brien")
		every { userService.getUserByEmail("special@example.com") } returns userResponse
		graphQlTester.document(
			"""
            query {
                getUser {
                    firstName
                    lastName
                }
            }
            """)
			.execute()
			.path("getUser.firstName").entity(String::class.java).isEqualTo("José-María")
			.path("getUser.lastName").entity(String::class.java).isEqualTo("O'Brien")
	}

	/* UPDATE USER MUTATION TESTS */
	@Test
	@WithMockUser(username = "update@example.com")
	fun testUpdateUserShouldUpdateUserSuccessfully() {
		val updatedUser = UserResponse(
			email = "update@example.com", firstName = "Updated", lastName = "Name")
		every {
			userService.updateUser(
				email = "update@example.com", firstName = "Updated", lastName = "Name")
		} returns updatedUser
		graphQlTester.document(
			"""
            mutation {
                updateUser(request: { firstName: "Updated", lastName: "Name" }) {
                    email
                    firstName
                    lastName
                }
            }
            """)
			.execute()
			.path("updateUser.email").entity(String::class.java).isEqualTo("update@example.com")
			.path("updateUser.firstName").entity(String::class.java).isEqualTo("Updated")
			.path("updateUser.lastName").entity(String::class.java).isEqualTo("Name")
	}
	@Test
	@WithMockUser(username = "update@example.com")
	fun testUpdateUserShouldUpdateOnlyFirstName() {
		val updatedUser = UserResponse(
			email = "update@example.com", firstName = "NewFirst", lastName = "OldLast")
		every {
			userService.updateUser(
				email = "update@example.com", firstName = "NewFirst", lastName = "OldLast")
		} returns updatedUser
		graphQlTester.document(
			"""
            mutation {
                updateUser(request: { firstName: "NewFirst", lastName: "OldLast" }) {
                    firstName
                    lastName
                }
            }
            """)
			.execute()
			.path("updateUser.firstName").entity(String::class.java).isEqualTo("NewFirst")
			.path("updateUser.lastName").entity(String::class.java).isEqualTo("OldLast")
	}
	@Test
	fun testUpdateUserShouldFailWhenNotAuthenticated() {
		graphQlTester.document(
			"""
            mutation {
                updateUser(request: { firstName: "Test", lastName: "User" }) {
                    email
                    firstName
                    lastName
                }
            }
            """)
			.execute()
			.errors()
			.expect { error ->
				error.message?.contains("Authentication required") == true }
	}
	@Test
	@WithMockUser(username = "notfound@example.com")
	fun testUpdateUserShouldHandleUserNotFound() {
		every {
			userService.updateUser(
				email = "notfound@example.com", firstName = any(), lastName = any())
		} throws ResponseStatusException(
			HttpStatus.NOT_FOUND,
			"User not found")
		graphQlTester.document(
			"""
            mutation {
                updateUser(request: { firstName: "Test", lastName: "User" }) {
                    email
                }
            }
            """)
			.execute()
			.errors()
			.expect { it.message?.contains("User not found") == true }
	}
	@Test
	@WithMockUser(username = "update@example.com")
	fun testUpdateUserWithSpecialCharacters() {
		val updatedUser = UserResponse(
			email = "update@example.com", firstName = "María-José", lastName = "O'Connor")
		every {
			userService.updateUser(
				email = "update@example.com", firstName = "María-José", lastName = "O'Connor")
		} returns updatedUser
		graphQlTester.document(
			"""
            mutation {
                updateUser(request: { firstName: "María-José", lastName: "O'Connor" }) {
                    firstName
                    lastName
                }
            }
            """)
			.execute()
			.path("updateUser.firstName").entity(String::class.java).isEqualTo("María-José")
			.path("updateUser.lastName").entity(String::class.java).isEqualTo("O'Connor")
	}

	/* DELETE USER MUTATION TESTS */
	@Test
	@WithMockUser(username = "delete@example.com")
	fun testDeleteUserShouldDeleteUserSuccessfully() {
		justRun { userService.deleteUser("delete@example.com") }
		graphQlTester.document(
			"""
            mutation {
                deleteUser {
                    success
                    message
                }
            }
            """)
			.execute()
			.path("deleteUser.success").entity(Boolean::class.java).isEqualTo(true)
			.path("deleteUser.message").entity(String::class.java).isEqualTo("User deleted successfully")
		verify(exactly = 1) { userService.deleteUser("delete@example.com") }
	}
	@Test
	@WithMockUser(username = "another@example.com")
	fun testDeleteUserShouldCallServiceWithCorrectEmail() {
		justRun { userService.deleteUser("another@example.com") }
		graphQlTester.document(
			"""
            mutation {
                deleteUser {
                    success
                }
            }
            """)
			.execute()
			.path("deleteUser.success").entity(Boolean::class.java).isEqualTo(true)
		verify(exactly = 1) { userService.deleteUser("another@example.com") }
	}
	@Test
	fun testDeleteUserShouldFailWhenNotAuthenticated() {
		graphQlTester.document(
			"""
            mutation {
                deleteUser {
                    success
                    message
                }
            }
            """)
			.execute()
			.errors()
			.expect { error ->
				error.message?.contains("Authentication required") == true }
	}
	@Test
	@WithMockUser(username = "notfound@example.com")
	fun testDeleteUserShouldHandleUserNotFound() {
		every { userService.deleteUser("notfound@example.com") } throws ResponseStatusException(
			HttpStatus.NOT_FOUND,
			"User not found")
		graphQlTester.document(
			"""
            mutation {
                deleteUser {
                    success
                    message
                }
            }
            """)
			.execute()
			.errors()
			.expect { it.message?.contains("User not found") == true }
	}
	@Test
	@WithMockUser(username = "delete@example.com")
	fun testDeleteUserShouldReturnOnlySuccessField() {
		justRun { userService.deleteUser("delete@example.com") }
		graphQlTester.document(
			"""
            mutation {
                deleteUser {
                    success
                }
            }
            """)
			.execute()
			.path("deleteUser.success").entity(Boolean::class.java).isEqualTo(true)
	}
	@Test
	@WithMockUser(username = "delete@example.com")
	fun testDeleteUserShouldReturnOnlyMessageField() {
		justRun { userService.deleteUser("delete@example.com") }
		graphQlTester.document(
			"""
            mutation {
                deleteUser {
                    message
                }
            }
            """)
			.execute()
			.path("deleteUser.message").entity(String::class.java).isEqualTo("User deleted successfully")
	}

	/* ADDITIONAL TESTS */
	@Test
	@WithMockUser(username = "multi@example.com")
	fun testMultipleOperationsInSingleDocument() {
		val userResponse = UserResponse(
			email = "multi@example.com", firstName = "Multi", lastName = "Test")
		val updatedUser = UserResponse(
			email = "multi@example.com", firstName = "Updated", lastName = "Multi")
		every { userService.getUserByEmail("multi@example.com") } returns userResponse
		every {
			userService.updateUser(
				email = "multi@example.com", firstName = "Updated", lastName = "Multi")
		} returns updatedUser
		graphQlTester.document(
			"""
            query {
                current: getUser {
                    firstName
                    lastName
                }
            }
            """)
			.execute()
			.path("current.firstName").entity(String::class.java).isEqualTo("Multi")
	}
}
