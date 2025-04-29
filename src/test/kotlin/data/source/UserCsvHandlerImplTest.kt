package data.source

import data.model.Admin
import data.model.Mate
import org.junit.jupiter.api.BeforeEach
import java.io.File
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import com.google.common.truth.Truth.assertThat

class UserCsvHandlerImplTest {

    private val testFilePath = "test_assets/users_test.csv"
    private lateinit var handler: UserCsvHandlerImpl

    @BeforeEach
    fun setUp() {
        File(testFilePath).delete()
        handler = UserCsvHandlerImpl(filePath = testFilePath)
    }

    @Test
    fun `should create users csv file if not exists`() {
        // Given
        val file = File(testFilePath)

        // When/Then
        assertTrue(file.exists(), "File should be created at $testFilePath")
    }

    @Test
    fun `should contain correct header in the file`() {
        // Given/When
        val header = File(testFilePath).readLines().firstOrNull()

        // Then
        assertEquals("id,username,password,role", header)
    }

    @Test
    fun `should read users correctly from file`() {
        // Given
        val file = File(testFilePath)
        val userLine = "11111111-1111-1111-1111-111111111111,adminUser,hash1,admin"
        file.appendText(userLine + "\n")

        // When
        val result = handler.read(testFilePath)

        // Then
        assertEquals(1, result.size)
        assertEquals("adminUser", result.first().username)
    }

    @Test
    fun `should write users correctly to file`() {
        // Given
        val user1 = Admin(UUID.fromString("11111111-1111-1111-1111-111111111111"), "adminUser", "hash1", "admin")
        val user2 = Mate(UUID.fromString("22222222-2222-2222-2222-222222222222"), "mateUser", "hash2", "mate")

        // When
        handler.write(testFilePath, listOf(user1, user2))
        val result = File(testFilePath).readLines().drop(1).map { it.split(",")[1] }

        // Then
        assertThat(result).containsExactly("adminUser", "mateUser")
    }

}