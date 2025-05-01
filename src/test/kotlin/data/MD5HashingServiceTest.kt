package data

import org.damascus.data.MD5HashingService
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class MD5HashingServiceTest {

    private val hashingService = MD5HashingService()

    @Test
    fun `test hashData returns consistent hash`() {
        // Given
        val input = "password123"

        // When
        val hash1 = hashingService.hashData(input)
        val hash2 = hashingService.hashData(input)

        // Then
        assertEquals(hash1, hash2)
    }

    @Test
    fun `test verifyData returns true for correct input`() {
        // Given
        val input = "password123"
        val hash = hashingService.hashData(input)

        // When
        val isVerified = hashingService.verifyData(input, hash)

        // Then
        assertTrue(isVerified)
    }

    @Test
    fun `test verifyData returns false for incorrect input`() {
        // Given
        val input = "password123"
        val wrongInput = "wrongpassword"
        val hash = hashingService.hashData(input)

        // When
        val isVerified = hashingService.verifyData(wrongInput, hash)

        // Then
        assertFalse(isVerified)
    }

}