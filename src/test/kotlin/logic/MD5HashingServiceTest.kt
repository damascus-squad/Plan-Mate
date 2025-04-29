package logic


import org.damascus.logic.MD5HashingService
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class MD5HashingServiceTest {

    private val hashingService = MD5HashingService()

    @Test
    fun `test hashData returns consistent hash`() {
        val input = "password123"
        val hash1 = hashingService.hashData(input)
        val hash2 = hashingService.hashData(input)

        assertEquals(hash1, hash2)
    }

    @Test
    fun `test verifyData returns true for correct input`() {
        val input = "password123"
        val hash = hashingService.hashData(input)

        val isVerified = hashingService.verifyData(input, hash)

        assertTrue(isVerified)
    }

    @Test
    fun `test verifyData returns false for incorrect input`() {
        val input = "password123"
        val wrongInput = "wrongpassword"
        val hash = hashingService.hashData(input)

        val isVerified = hashingService.verifyData(wrongInput, hash)

        assertFalse(isVerified)
    }

}