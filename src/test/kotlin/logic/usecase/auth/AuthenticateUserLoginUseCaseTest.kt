package logic.usecase.auth

import com.google.common.truth.ExpectFailure.assertThat
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import logic.exception.InvalidCredentialsException
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
        verify { authRepo.login(username, password) }
        assertThat(result.isSuccess).isEqualTo(true)

    }

    @Test
    fun `login should return success result when credentials are valid for admin`() {
        // Given
        val username = "admin"
        val password = "admin123"
        every { authRepo.login(username, password) } throws InvalidCredentialsException()

        // When
        val result = authenticateUserLogin(username, password)

        // Then
        verify { authRepo.login(username, password) }
        assertThat(result.isFailure).isEqualTo(true)
    }
}