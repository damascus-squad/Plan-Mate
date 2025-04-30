package data.source


import data.csvDataHelper.CreateUserHelper.FILE_PATH
import data.csvDataHelper.CreateUserHelper.buildHandlerUser
import data.csvDataHelper.CreateUserHelper.createUser
import logic.model.Mate
import logic.model.User
import org.damascus.data.csv.CsvParsingException
import org.damascus.data.csv.FileDataParser
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import java.io.File
import java.util.UUID
import kotlin.test.Test

class UserCsvHandlerImplTest {

    private lateinit var handler: CsvHandlerImpl<User>

    @BeforeEach
    fun setUp() {
        File(FILE_PATH).delete()
        handler = buildHandlerUser()
    }

    @Test
    fun `should create file when file does not exist`() {
        // Given
        val file = File(FILE_PATH)

        // When
        val result = file.exists()

        // Then
        assertTrue(result)
    }

    @Test
    fun `should keep existing header when file already exists`() {
        // Given
        val file = File(FILE_PATH)
        file.parentFile.mkdirs()
        file.writeText("id,username,password,role\n")

        // When
        buildHandlerUser()

        // Then
        assertEquals("id,username,password,role", file.readLines().first())
    }

    @Test
    fun `should return valid entries when reading and skipping blank lines`() {
        // Given
        File(FILE_PATH).writeText("id,username,password,role\n${UUID.randomUUID()},Alice,p1,mate\n\n")

        // When
        val result = handler.read()

        // Then
        assertEquals(1, result.size)
        assertEquals("Alice", result.first().username)
    }

    @Test
    fun `should write and return data correctly when reading`() {
        // Given
        val user1 = createUser(UUID.randomUUID(), "Alice", "p1", "mate")
        val user2 = createUser(UUID.randomUUID(), "Bob", "p2", "admin")
        writeUsers(user1, user2)

        // When
        val result = handler.read()

        // Then
        assertEquals(2, result.size)
        assertEquals("Alice", result[0].username)
        assertEquals("Bob", result[1].username)
    }

    @Test
    fun `should return empty list when file only has header`() {
        // Given
        File(FILE_PATH).writeText("id,username,password,role\n")

        // When
        val result = handler.read()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `should skip invalid lines when reading`() {
        // Given
        File(FILE_PATH).writeText("id,username,password,role\n${UUID.randomUUID()}\n${UUID.randomUUID()},Alice,p1,mate\n")

        // When
        val result = handler.read()

        // Then
        assertEquals(1, result.size)
        assertEquals("Alice", result[0].username)
    }

    @Test
    fun `should return empty list when file does not exist`() {
        // Given
        File(FILE_PATH).delete()

        // When
        val result = handler.read()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `should update user when user exists`() {
        // Given
        val id1 = UUID.randomUUID()
        val id2 = UUID.randomUUID()
        writeUsers(
            createUser(id1, "Alice", "p", "mate"),
            createUser(id2, "Bob", "pp", "admin")
        )

        // When
        handler.update(id2.toString(), createUser(id2, "Bobby", "pp", "admin"))
        val result = handler.read()

        // Then
        assertEquals("Bobby", result.find { it.id == id2 }?.username)
    }

    @Test
    fun `should ignore update when user does not exist`() {
        // Given
        val id1 = UUID.randomUUID()
        val fakeId = UUID.randomUUID()
        writeUsers(createUser(id1, "Alice", "123", "mate"))

        // When
        handler.update(fakeId.toString(), createUser(fakeId, "Ghost", "123", "mate"))
        val result = handler.read()

        // Then
        assertEquals(1, result.size)
        assertEquals("Alice", result.first().username)
    }

    @Test
    fun `should delete user when id is found`() {
        // Given
        val id1 = UUID.randomUUID()
        val id2 = UUID.randomUUID()
        writeUsers(
            createUser(id1, "Alice", "pp", "mate"),
            createUser(id2, "Bob", "pp", "admin")
        )

        // When
        handler.delete(id1.toString())
        val result = handler.read()

        // Then
        assertEquals(1, result.size)
        assertEquals("Bob", result.first().username)
    }

    @Test
    fun `should parse valid user`() {
        // Given
        val line = "${UUID.randomUUID()},alice,1234,mate"

        // When
        val result = FileDataParser.parseUser(line)

        // Then
        assertTrue(result is Mate)
        assertEquals("alice", result.username)
    }

    @Test
    fun `should throw when user role is unknown`() {
        // Given
        val line = "${UUID.randomUUID()},bob,pass,manager"

        // When/Then
        val ex = assertThrows(CsvParsingException::class.java) {
            FileDataParser.parseUser(line)
        }
        assertTrue(ex.message!!.contains("Unknown role"))
    }

    @Test
    fun `should throw when user line is invalid`() {
        // Given
        val line = "incomplete,data"

        // When/Then
        assertThrows(CsvParsingException::class.java) {
            FileDataParser.parseUser(line)
        }
    }


    private fun writeUsers(vararg users: User) {
        handler.write(users.toList())
    }
}