package org.damascus.ui.input

import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class PasswordInputCheckerTest {

    @Test
    fun `checkPasswordInput should return false when first letter is not digit or letter`() {
        // Given
        val input = "_harith"

        // When
        val result = checkPasswordInput(input)

        // Then
        assertEquals(false, result)
    }

    @Test
    fun `checkPasswordInput should return false when password is empty`() {
        // Given
        val input = ""

        // When
        val result = checkPasswordInput(input)

        // Then
        assertEquals(false, result)
    }

    @Test
    fun `checkPasswordInput should return false when password is filed with spaces`() {
        // Given
        val input = "      "

        // When
        val result = checkPasswordInput(input)

        // Then
        assertEquals(false, result)
    }

    @Test
    fun `checkPasswordInput should return false when password ends with space`() {
        // Given
        val input = "H@rIth "

        // When
        val result = checkPasswordInput(input)

        // Then
        assertEquals(false, result)
    }

    @Test
    fun `checkPasswordInput should return false when password length is less than 6 characters`() {
        // Given
        val input = "H@ri"

        // When
        val result = checkPasswordInput(input)

        // Then
        assertEquals(false, result)
    }

    @Test
    fun `checkPasswordInput should return false when password length is more than 20 characters`() {
        // Given
        val input = "harithAbdulrahmanYosufAhmed"

        // When
        val result = checkPasswordInput(input)

        // Then
        assertEquals(false, result)
    }
}