package data.source

import data.csvDataHelper.createUser
import data.model.Mate
import data.model.User
import org.damascus.data.csv.CsvParsingException
import org.damascus.data.csv.FileDataParser
import org.damascus.data.csv.FileDataSerializer
import org.junit.jupiter.api.Assertions.*
import java.io.File
import java.util.UUID
import kotlin.test.BeforeTest
import kotlin.test.Test

class UserCsvHandlerImplTest {

    private val filePath = "test_assets/users.csv"
    private lateinit var handler: GenericCsvHandlerImpl<User>

    @BeforeTest
    fun setUp() {
        File(filePath).delete()
        handler = buildHandler()
    }

    @Test
    fun `should create file when file does not exist`() {
        // Given
        val file = File(filePath)

        // When
        val result = file.exists()

        // Then
        assertTrue(result)
    }

    @Test
    fun `should keep existing header when file already exists`() {
        // Given
        val file = File(filePath)
        file.parentFile.mkdirs()
        file.writeText("id,username,password,role\n")

        // When
        buildHandler()

        // Then
        assertEquals("id,username,password,role", file.readLines().first())
    }

    @Test
    fun `should return valid entries when reading and skipping blank lines`() {
        // Given
        File(filePath).writeText("id,username,password,role\n11111111-1111-1111-1111-111111111111,Alice,p1,mate\n\n")

        // When
        val result = handler.read(filePath)

        // Then
        assertEquals(1, result.size)
        assertEquals("Alice", result.first().username)
    }

    @Test
    fun `should write and return data correctly when reading`() {
        // Given
        val user1 = createUser(UUID.fromString("11111111-1111-1111-1111-111111111111"), "Alice", "p1", "mate")
        val user2 = createUser(UUID.fromString("22222222-2222-2222-2222-222222222222"), "Bob", "p2", "admin")
        writeUsers(user1, user2)

        // When
        val result = handler.read(filePath)

        // Then
        assertEquals(2, result.size)
        assertEquals("Alice", result[0].username)
        assertEquals("Bob", result[1].username)
    }

    @Test
    fun `should update user when user exists`() {
        // Given
        val id1 = UUID.fromString("11111111-1111-1111-1111-111111111111")
        val id2 = UUID.fromString("22222222-2222-2222-2222-222222222222")
        writeUsers(
            createUser(id1, "Alice", "p", "mate"),
            createUser(id2, "Bob", "pp", "admin")
        )

        // When
        handler.update(filePath, id2.toString(), createUser(id2, "Bobby", "pp", "admin"))
        val result = handler.read(filePath)

        // Then
        assertEquals("Bobby", result.find { it.id == id2 }?.username)
    }

    @Test
    fun `should ignore update when user does not exist`() {
        // Given
        val id1 = UUID.fromString("11111111-1111-1111-1111-111111111111")
        val fakeId = UUID.fromString("99999999-9999-9999-9999-999999999999")
        writeUsers(createUser(id1, "Alice", "x", "mate"))

        // When
        handler.update(filePath, fakeId.toString(), createUser(fakeId, "Ghost", "x", "mate"))
        val result = handler.read(filePath)

        // Then
        assertEquals(1, result.size)
        assertEquals("Alice", result.first().username)
    }

    @Test
    fun `should delete user when id is found`() {
        // Given
        val id1 = UUID.fromString("11111111-1111-1111-1111-111111111111")
        val id2 = UUID.fromString("22222222-2222-2222-2222-222222222222")
        writeUsers(
            createUser(id1, "Alice", "pp", "mate"),
            createUser(id2, "Bob", "pp", "admin")
        )

        // When
        handler.delete(filePath, id1.toString())
        val result = handler.read(filePath)

        // Then
        assertEquals(1, result.size)
        assertEquals("Bob", result.first().username)
    }

    @Test
    fun `should return empty list when file only has header`() {
        // Given
        File(filePath).writeText("id,username,password,role\n")

        // When
        val result = handler.read(filePath)

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `should skip invalid lines when reading`() {
        // Given
        File(filePath).writeText("id,username,password,role\n\n11111111-1111-1111-1111-111111111111,Alice,p1,mate\n")

        // When
        val result = handler.read(filePath)

        // Then
        assertEquals(1, result.size)
        assertEquals("Alice", result[0].username)
    }

    @Test
    fun `should return empty list when file does not exist`() {
        // Given
        File(filePath).delete()

        // When
        val result = handler.read(filePath)

        // Then
        assertTrue(result.isEmpty())
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

    private fun buildHandler(): GenericCsvHandlerImpl<User> {
        return GenericCsvHandlerImpl(
            filePath = filePath,
            header = "id,username,password,role",
            idSelector = { it.id.toString() },
            parser = FileDataParser::parseUser,
            serializer = FileDataSerializer::serializeUser
        )
    }

    private fun writeUsers(vararg users: User) {
        handler.write(filePath, users.toList())
    }
}