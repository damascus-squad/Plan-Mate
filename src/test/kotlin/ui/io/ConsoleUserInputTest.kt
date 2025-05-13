package ui.io

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import ui.exception.InputException
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class ConsoleUserInputTest {

    private fun provideInput(input: String) {
        System.setIn(ByteArrayInputStream(input.toByteArray()))
    }

    private fun captureOutput(block: () -> Unit): String {
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))
        block()
        return outputStream.toString()
    }

    @Test
    fun `should return string when input is not empty`() {
        // Given
        provideInput("hello\n")
        // When
        val result = ConsoleUserInput().readString("Enter:")
        // Then
        assertEquals("hello", result)
    }

    @Test
    fun `should retry when input is empty then valid string`() {
        // Given
        provideInput("\nworld\n")
        // When
        val output = captureOutput {
            val result = ConsoleUserInput().readString("Enter:")
            assertEquals("world", result)
        }
        // Then
        assertTrue(output.contains(ERROR_INPUT_EMPTY))
    }

    @ParameterizedTest
    @CsvSource(
        "'5\n', 1, 10, true, 5, ''",
        "'0\n5\n', 1, 10, false, 5,$ERROR_INVALID_INPUT",
        "'100\n8\n', 1, 10, false, 8, $ERROR_INVALID_INPUT",
        "'20\n5\n', null, 10, false, 5, $ERROR_INVALID_INPUT",
        "'0\n5\n', 1, null, false, 5,$ERROR_INVALID_INPUT",
        "'5\n', null, null, true, 5, ''"
    )
    fun `should validate int input when given various ranges`(
        input: String,
        minRaw: String?,
        maxRaw: String?,
        isValidInput: Boolean,
        expected: Int,
        expectedMsg: String
    ) {
        val min = if (minRaw == "null") null else minRaw?.toInt()
        val max = if (maxRaw == "null") null else maxRaw?.toInt()
        provideInput(input)
        val output = captureOutput {
            val result = ConsoleUserInput().readInt("Enter number:", min, max)
            assertEquals(expected, result)
        }
        if (!isValidInput) assertTrue(output.contains(expectedMsg))
    }

    @Test
    fun `should throw exception when input is not a number`() {
        // Given
        provideInput("abc\n")
        // When + Then
        assertThrows(InputException::class.java) {
            ConsoleUserInput().readInt("Enter:", 1, 10)
        }
    }

    @ParameterizedTest
    @CsvSource("yes,true", "no,false")
    fun `should return boolean when valid input is provided`(input: String, expected: Boolean) {
        // Given
        provideInput("$input\n")
        // When
        val result = ConsoleUserInput().readBoolean()
        // Then
        assertEquals(expected, result)
    }

    @Test
    fun `should retry when invalid boolean input then valid`() {
        // Given
        provideInput("maybe\nyes\n")
        // When
        val output = captureOutput {
            val result = ConsoleUserInput().readBoolean(any())
            assertTrue(result)
        }
        // Then
        assertTrue(output.contains(ERROR_INVALID_INPUT))
    }

    @ParameterizedTest
    @CsvSource(
        "'3.14\n',3.14,false",
        "'abc\n2.71\n',2.71,true"
    )
    fun `should return double when input is valid or retries once`(
        input: String,
        expected: Double,
        expectError: Boolean
    ) {
        // Given
        provideInput(input)

        // When
        val output = captureOutput {
            val result = ConsoleUserInput().readDouble("Enter:")
            assertEquals(expected, result, 0.0001)
        }

        // Then
        assertEquals(expectError, output.contains(ERROR_INVALID_NUMBER))
    }

    @Test
    fun `should retry when input is null in read double`() {
        // Given
        System.setIn(ByteArrayInputStream(ByteArray(0)))
        // When
        val output = captureOutput {
            val thread = Thread {
                ConsoleUserInput().readDouble("Enter:")
            }
            thread.start()
            Thread.sleep(200)
            thread.stop()// used for coverage only not recommended in production
        }
        // Then
        assertTrue(output.contains("❌ Invalid number"))
    }

    @Test
    fun `should retry when input is null in read boolean`() {
        // Given
        System.setIn(ByteArrayInputStream(ByteArray(0)))
        // When
        val output = captureOutput {
            val thread = Thread {
                ConsoleUserInput().readBoolean("❌ Invalid input. Please enter 'yes' or 'no'.")
            }
            thread.start()
            Thread.sleep(200)
            thread.stop()// used for coverage only not recommended in production
        }
        // Then
        assertTrue(output.contains("❌ Invalid input"))
    }

    private companion object {
        const val ERROR_INPUT_EMPTY = "❌ Input cannot be empty"
        const val ERROR_INVALID_INPUT = "❌ Invalid input"
        const val ERROR_INVALID_NUMBER = "❌ Invalid number"
    }

}