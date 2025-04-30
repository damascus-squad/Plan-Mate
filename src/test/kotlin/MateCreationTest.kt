import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.security.MessageDigest
import java.util.*

class MateCreationTest {

    class Mate private constructor(
        val id: UUID,
        val username: String,
        val password: String,
    ) {
        companion object {
            private val registeredUsernames = mutableSetOf<String>()

            fun create(
                id: UUID,
                username: String,
                rawPassword: String,
            ): Mate {
                validateUsername(username)

                validatePassword(rawPassword)

                if (registeredUsernames.contains(username)) {
                    throw IllegalArgumentException("Username '$username' already exists")
                }

                val encryptedPassword = md5(rawPassword)

                registeredUsernames.add(username)
                return Mate(id, username, encryptedPassword)
            }

            private fun validateUsername(username: String) {
                require(username.isNotBlank()) { "Username cannot be blank" }
                require(username.matches(Regex("^[a-zA-Z0-9]+$"))) {
                    "Invalid username format. Only alphanumeric characters allowed."
                }
            }

            private fun validatePassword(password: String) {
                require(password.isNotBlank()) { "Password cannot be blank" }
                require(password.length >= 6) { "Password must be at least 6 characters" }
            }

            fun md5(input: String): String {
                val bytes = MessageDigest.getInstance("MD5").digest(input.toByteArray())
                return bytes.joinToString("") { "%02x".format(it) }
            }
        }
    }

    interface UsernameValidator {
        fun isUsernameTaken(username: String): Boolean
    }

    @Test
    fun `should encrypt password using MD5`() {

        val rawPassword = "pass123"
        val mate = Mate.create(
            id = UUID.randomUUID(),
            username = "user1",
            rawPassword = rawPassword
        )

        assertThat(mate.password).isNotEqualTo(rawPassword)
    }

    @Test
    fun `should throw exception when password is too short`() {

        val exception = assertThrows<IllegalArgumentException> {
            Mate.create(
                id = UUID.randomUUID(),
                username = "user1",
                rawPassword = "123"
            )
        }

        assertThat(exception.message).contains("Password must be at least 6 characters")
    }

    @Test
    fun `should throw exception when username contains special characters`() {

        val exception = assertThrows<IllegalArgumentException> {
            Mate.create(
                id = UUID.randomUUID(),
                username = "user@123",
                rawPassword = "pass123"
            )
        }

        assertThat(exception.message).contains("Invalid username format")
    }

    @Test
    fun `should create mate successfully when all data is valid`() {

        val id = UUID.randomUUID()
        val username = "validUser"
        val rawPassword = "secure123"

        val mate = Mate.create(
            id = id,
            username = username,
            rawPassword = rawPassword
        )

        assertThat(mate.id).isEqualTo(id)
        assertThat(mate.username).isEqualTo(username)
        assertThat(mate.password).isEqualTo(Mate.md5(rawPassword))
    }

    @Test
    fun `should throw exception when creating mate with blank username`() {

        val id = UUID.randomUUID()
        val rawPassword = "secure123"

        val exception = assertThrows<IllegalArgumentException> {
            Mate.create(
                id = id,
                username = "   ",
                rawPassword = rawPassword
            )
        }

        assertThat(exception.message).isEqualTo("Username cannot be blank")
    }

    @Test
    fun `should throw exception when creating mate with empty password`() {

        val id = UUID.randomUUID()
        val username = "newUser"

        val exception = assertThrows<IllegalArgumentException> {
            Mate.create(
                id = id,
                username = username,
                rawPassword = ""
            )
        }

        assertThat(exception.message).isEqualTo("Password cannot be blank")
    }

    @Test
    fun `should return true when checking if username is taken`() {
        val username = "existingUser"
        val validator = mockk<UsernameValidator>()

        every { validator.isUsernameTaken(username) } returns true

        val isTaken = validator.isUsernameTaken(username)

        assertThat(isTaken).isTrue()
    }

    @Test
    fun `should return false when checking if username is available`() {
        val username = "uniqueUser"
        val validator = mockk<UsernameValidator>()

        every { validator.isUsernameTaken(username) } returns false

        val isTaken = validator.isUsernameTaken(username)

        assertThat(isTaken).isFalse()
    }
}