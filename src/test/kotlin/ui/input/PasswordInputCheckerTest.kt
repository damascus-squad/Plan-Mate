package ui.input

import org.damascus.ui.input.passwordInputChecker
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class PasswordInputCheckerTest {

    @Test
    fun `should return false when first letter is not digit or letter`() {
        // Given
        val input = "_harith"

        // When
        val result = passwordInputChecker(input)

        // Then
        assertEquals(false, result)
    }

    @Test
    fun `should return false when password is empty`() {
        // Given
        val input = ""

        // When
        val result = passwordInputChecker(input)

        // Then
        assertEquals(false, result)
    }

    @Test
    fun `should return false when password is filed with spaces`() {
        // Given
        val input = "      "

        // When
        val result = passwordInputChecker(input)

        // Then
        assertEquals(false, result)
    }

    @Test
    fun `should return false when password ends with space`() {
        // Given
        val input = "H@rIth "

        // When
        val result = passwordInputChecker(input)

        // Then
        assertEquals(false, result)
    }

    @Test
    fun `should return false when password length is less than 6 characters`() {
        // Given
        val input = "H@ri"

        // When
        val result = passwordInputChecker(input)

        // Then
        assertEquals(false, result)
    }

    @Test
    fun `should return false when password length is more than 20 characters`() {
        // Given
        val input = "harithAbdulrahmanYosufAhmed"

        // When
        val result = passwordInputChecker(input)

        // Then
        assertEquals(false, result)
    }
}