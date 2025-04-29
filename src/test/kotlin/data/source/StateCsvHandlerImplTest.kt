package data.source

import data.csvDataHelper.createState
import org.junit.jupiter.api.BeforeEach
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StateCsvHandlerImplTest {

    private val testFilePath = "test_assets/states_test.csv"
    private lateinit var handler: StateCsvHandlerImpl

    @BeforeEach
    fun setUp() {
        File(testFilePath).delete()
        handler = StateCsvHandlerImpl(filePath = testFilePath)
    }

    @Test
    fun `should create states_test csv file if not exists`() {
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
        assertEquals("id,name", header)
    }

    @Test
    fun `should read states correctly from file`() {
        // Given
        val file = File(testFilePath)
        val stateLine = "11111111-1111-1111-1111-111111111111,In Progress"
        file.appendText(stateLine + "\n")

        // When
        val result = handler.read(testFilePath)

        // Then
        assertEquals("In Progress", result.first().name)
    }

    @Test
    fun `should write states correctly to file`() {
        // Given
        val state1 = createState(name = "TODO")
        val state2 = createState(name = "DONE")

        // When
        handler.write(testFilePath, listOf(state1, state2))
        val result = File(testFilePath).readLines().drop(1)

        // Then
        assertTrue(result.size == 2)
    }
}

