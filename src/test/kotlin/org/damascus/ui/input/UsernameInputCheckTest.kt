package org.damascus.ui.input

import junit.framework.TestCase.assertEquals
import kotlin.test.Test

class UsernameInputCheckTest {

    @Test
    fun `checkUsernameInput should return false when leading is not letter`() {
        // Given
        val input = "1harith"

        // When
        val result = checkUsernameInput(input)

        // Then
        assertEquals(false, result)
    }

    @Test
    fun `checkUsernameInput should return false when username has a character other than letter, digit or underscore`() {
        // Given
        val input = "harith.12*"

        // When
        val result = checkUsernameInput(input)

        // Then
        assertEquals(false, result)
    }

    @Test
    fun `checkUsernameInput should return false when username is empty`() {
        // Given
        val input = ""

        // When
        val result = checkUsernameInput(input)

        // Then
        assertEquals(false, result)
    }

    @Test
    fun `checkUsernameInput should return false when username is only spaces`() {
        // Given
        val input = "      "

        // When
        val result = checkUsernameInput(input)

        // Then
        assertEquals(false, result)
    }

    @Test
    fun `checkUsernameInput should return false when username is less than 4 characters`() {
        // Given
        val input = "har"

        // When
        val result = checkUsernameInput(input)

        // Then
        assertEquals(false, result)
    }

    @Test
    fun `checkUsernameInput should return false when username is more than 20 character`() {
        // Given
        val input = "Harith_Yosuf_abdulrahman"

        // When
        val result = checkUsernameInput(input)

        // Then
        assertEquals(false, result)
    }

    @Test
    fun `checkUsernameInput should return false when username already exist`() {
        // Given
        val input = "harith"

        // When
        val result = checkUsernameInput(input)

        // Then
        assertEquals(false, result)
    }

    @Test
    fun `checkUsernameInput should return true when valid username`() {
        // Given
        val input = "harith_123"

        // When
        val result = checkUsernameInput(input)

        // Then
        assertEquals(true, result)
    }

}