package org.damascus.logic.usecase.auth

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.damascus.logic.exception.InvalidCredentialsException
import org.damascus.logic.model.User
import org.damascus.logic.model.UserRole
import org.damascus.logic.repo.AuthenticationRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class CreateMateUseCaseTest {

    private lateinit var authRepo: AuthenticationRepository
    private lateinit var createMateUseCase: CreateMateUseCase

    @BeforeEach
    fun setUp() {
        authRepo = mockk(relaxed = true)
        createMateUseCase = CreateMateUseCase(authRepo)
    }

    @Test
    fun `createMate should successfully create mate when admin credentials are valid`() {
        // Given
        val adminUser = User(UUID.randomUUID(), "admin", UserRole.ADMIN)
        val newUsername = "newMate"
        val newPassword = "password123"
        val newMateUser = User(UUID.randomUUID(), newUsername, UserRole.MATE)
        every { authRepo.createMate(adminUser, newUsername, newPassword) } returns newMateUser

        // When
        val result = createMateUseCase(adminUser, newUsername, newPassword)

        // Then
        verify { authRepo.createMate(adminUser, newUsername, newPassword) }
        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `createMate should return failure when repository throws exception`() {
        // Given
        val adminUser = User(UUID.randomUUID(), "admin", UserRole.ADMIN)
        val newUsername = "newmate"
        val newPassword = "password123"
        every { authRepo.createMate(adminUser, newUsername, newPassword) } throws InvalidCredentialsException()

        // When
        val result = createMateUseCase(adminUser, newUsername, newPassword)

        // Then
        verify { authRepo.createMate(adminUser, newUsername, newPassword) }
        assertThat(result.isFailure).isTrue()
    }
}