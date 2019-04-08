package pl.jsql.api.misc

import org.junit.jupiter.api.TestInstance
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS

@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@SpringJUnitConfig
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(value = PER_CLASS)
abstract class IntegrationTest {
}
