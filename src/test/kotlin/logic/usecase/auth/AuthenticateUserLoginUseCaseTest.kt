package logic.usecase.auth

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import logic.model.User
import logic.model.UserRole
import logic.repo.AuthenticationRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals

class AuthenticateUserLoginUseCaseTest {

    private lateinit var authRepo: AuthenticationRepository
    private lateinit var authenticateUserLogin: AuthenticateUserLoginUseCase

    @BeforeEach
    fun setUp() {
        authRepo = mockk()
        authenticateUserLogin = AuthenticateUserLoginUseCase(authRepo)
    }

    @Test
    fun `login should return success result when credentials are valid`() {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val user = User(UUID.randomUUID(), email, UserRole.MATE)
        every { authRepo.login(email, password) } returns user

        // When
        val result = authenticateUserLogin(email, password)

        // Then
        assertEquals(user, result.getOrNull())
        verify { authRepo.login(email, password) }
    }

    @Test
    fun `login should return failure result when credentials are invalid`() {
        // Given
        val email = "test@example.com"
        val password = "wrongpassword"
        val exception = RuntimeException("Invalid credentials")
        every { authRepo.login(email, password) } throws exception

        // When
        val result = authenticateUserLogin(email, password)

        // Then
        assertEquals(exception, result.exceptionOrNull())
        verify { authRepo.login(email, password) }
    }

    @Test
    fun `login should return failure result when email is empty`() {
        // Given
        val email = ""
        val password = "password123"

        // When
        val result = authenticateUserLogin(email, password)

        // Then
        assert(result.isFailure)
        assert(result.exceptionOrNull() is IllegalArgumentException)
        verify(exactly = 0) { authRepo.login(any(), any()) }
    }

    @Test
    fun `login should return failure result when password is empty`() {
        // Given
        val email = "test@example.com"
        val password = ""

        // When
        val result = authenticateUserLogin(email, password)

        // Then
        assert(result.isFailure)
        assert(result.exceptionOrNull() is IllegalArgumentException)
        verify(exactly = 0) { authRepo.login(any(), any()) }
    }

    @Test
    fun `login should return failure result when email is invalid format`() {
        // Given
        val email = "invalid-email"
        val password = "password123"

        // When
        val result = authenticateUserLogin(email, password)

        // Then
        assert(result.isFailure)
        assert(result.exceptionOrNull() is IllegalArgumentException)
        verify(exactly = 0) { authRepo.login(any(), any()) }
    }

    @Test
    fun `login should return success result when credentials are valid for admin`() {
        // Given
        val email = "admin@example.com"
        val password = "admin123"
        val user = User(UUID.randomUUID(), email, UserRole.ADMIN)
        every { authRepo.login(email, password) } returns user

        // When
        val result = authenticateUserLogin(email, password)

        // Then
        assertEquals(user, result.getOrNull())
        verify { authRepo.login(email, password) }
    }

    @Test
    fun `login should return failure result when both email and password are empty`() {
        // Given
        val email = ""
        val password = ""

        // When
        val result = authenticateUserLogin(email, password)

        // Then
        assert(result.exceptionOrNull() is IllegalArgumentException)
        verify(exactly = 0) { authRepo.login(any(), any()) }
    }
}