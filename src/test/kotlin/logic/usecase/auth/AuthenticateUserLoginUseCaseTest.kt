package logic.usecase.auth

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import logic.model.User
import logic.model.UserRole
import logic.repo.AuthenticationRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID
import kotlin.test.assertEquals

class AuthenticateUserLoginUseCaseTest {

    private lateinit var authRepo: AuthenticationRepository
    private lateinit var authenticateUserLogin: AuthenticateUserLoginUseCase

    @BeforeEach
    fun setUp() {
        authRepo = mockk(relaxed = true)
        authenticateUserLogin = AuthenticateUserLoginUseCase(authRepo)
    }

    @Test
    fun `login should return success result when credentials are valid`() {
        // Given
        val username = "testuser"
        val password = "password123"
        val user = User(UUID.randomUUID(), username, UserRole.MATE)
        every { authRepo.login(username, password) } returns user

        // When
        val result = authenticateUserLogin(username, password)

        // Then
        assertEquals(user, result.getOrNull())
        verify { authRepo.login(username, password) }
    }

    @Test
    fun `login should return failure result when credentials are invalid`() {
        // Given
        val username = "testuser"
        val password = "wrongpassword"
        val exception = RuntimeException("Invalid credentials")
        every { authRepo.login(username, password) } throws exception

        // When & Then
        assertThrows<RuntimeException> {
            authenticateUserLogin(username, password).getOrThrow()
        }
        verify { authRepo.login(username, password) }
    }

    @Test
    fun `login should return failure result when username is empty`() {
        // Given
        val username = ""
        val password = "password123"
        val exception = RuntimeException("Invalid credentials")
        every { authRepo.login(username, password) } throws exception

        // When & Then
        assertThrows<RuntimeException> {
            authenticateUserLogin(username, password).getOrThrow()
        }
        verify { authRepo.login(username, password) }
    }

    @Test
    fun `login should return failure result when password is empty`() {
        // Given
        val username = "testuser"
        val password = ""
        val exception = RuntimeException("Invalid credentials")
        every { authRepo.login(username, password) } throws exception

        // When & Then
        assertThrows<RuntimeException> {
            authenticateUserLogin(username, password).getOrThrow()
        }
        verify { authRepo.login(username, password) }
    }

    @Test
    fun `login should return success result when credentials are valid for admin`() {
        // Given
        val username = "admin"
        val password = "admin123"
        val user = User(UUID.randomUUID(), username, UserRole.ADMIN)
        every { authRepo.login(username, password) } returns user

        // When
        val result = authenticateUserLogin(username, password)

        // Then
        assertEquals(user, result.getOrNull())
        verify { authRepo.login(username, password) }
    }

    @Test
    fun `login should return failure result when both username and password are empty`() {
        // Given
        val username = ""
        val password = ""
        val exception = RuntimeException("Invalid credentials")
        every { authRepo.login(username, password) } throws exception

        // When & Then
        assertThrows<RuntimeException> {
            authenticateUserLogin(username, password).getOrThrow()
        }
        verify { authRepo.login(username, password) }
    }
}