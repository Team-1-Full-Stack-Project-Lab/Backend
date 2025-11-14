package edu.fullstackproject.team1

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@Disabled("Integration test - requires database. Use unit tests for CI/CD")
class ApplicationTests {
  @Test fun contextLoads() {}
}
