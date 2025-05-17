package org.damascus.logic.usecase.auth

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.damascus.logic.exception.NoMatesAvailableException
import org.damascus.logic.exception.UserNotFoundException
import org.damascus.logic.model.User
import org.damascus.logic.model.UserRole
import org.damascus.logic.repo.AuthenticationRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class ManageMateUseCaseTest {

    private lateinit var authRepo: AuthenticationRepository
    private lateinit var manageMateUseCase: ManageMateUseCase

    @BeforeEach
    fun setUp() {
        authRepo = mockk(relaxed = true)
        manageMateUseCase = ManageMateUseCase(authRepo)
    }

    @Test
    fun `getMate should return mate when user exists`() = runTest{
        // Given
        val userId = UUID.randomUUID()
        val expectedMate = User(userId, "testMate", UserRole.MATE)
        coEvery { authRepo.getMateById(userId) } returns expectedMate

        // When
        val result = manageMateUseCase.getMate(userId)

        // Then
        coVerify { authRepo.getMateById(userId) }
        assertThat(result).isEqualTo(expectedMate)
    }

    @Test
    fun `getMate should throw UserNotFoundException when user does not exist`() =runTest {
        // Given
        val userId = UUID.randomUUID()
        coEvery { authRepo.getMateById(userId) } returns null

        // When/Then
        assertThrows<UserNotFoundException> {
            manageMateUseCase.getMate(userId)
        }
        coVerify { authRepo.getMateById(userId) }
    }

    @Test
    fun `getAllMates should return list of mates when mates exist`()=runTest {
        // Given
        val mates = listOf(
            User(UUID.randomUUID(), "mate1", UserRole.MATE),
            User(UUID.randomUUID(), "mate2", UserRole.MATE)
        )
        coEvery { authRepo.getAllMates() } returns mates

        // When
        val result = manageMateUseCase.getAllMates()

        // Then
        coVerify { authRepo.getAllMates() }
        assertThat(result).isEqualTo(mates)
    }

    @Test
    fun `getAllMates should throw NoMatesAvailableException when no mates exist`() =runTest{
        // Given
        coEvery { authRepo.getAllMates() } returns emptyList()

        // When/Then
        assertThrows<NoMatesAvailableException> { manageMateUseCase.getAllMates() }
        coVerify { authRepo.getAllMates() }
    }
}