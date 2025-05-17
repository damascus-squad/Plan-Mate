package org.damascus.ui.views.user

import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.damascus.logic.model.User
import org.damascus.logic.model.UserRole
import org.damascus.logic.usecase.auth.AuthenticateUserLoginUseCase
import org.damascus.ui.io.Display
import org.damascus.ui.io.InputReader
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

class LoginViewTest {

    private lateinit var authenticateUserLoginUseCase: AuthenticateUserLoginUseCase
    private lateinit var inputReader: InputReader
    private lateinit var loginView: LoginView
    private lateinit var display: Display

    @BeforeEach
    fun setup() {
        authenticateUserLoginUseCase = mockk(relaxed = true)
        inputReader = mockk(relaxed = true)
        display = mockk(relaxed = true)
        loginView = LoginView(authenticateUserLoginUseCase, inputReader, display)
    }

    @Test
    fun `should return user when authentication succeeds`() = runTest {
        // Given
        val username = "testUser"
        val password = "password123"
        val expectedUser = User(id = UUID.randomUUID(), username = username, userRole = UserRole.MATE)

        every { inputReader.readString("👤 Enter your name ") } returns username
        every { inputReader.readString("🔒 Enter Your Password ") } returns password
        coEvery { authenticateUserLoginUseCase(username, password) } returns Result.success(expectedUser)

        // When
        val actualUser = loginView.getLoggedUser()

        // Then
        assertEquals(expectedUser, actualUser)
        verify(exactly = 1) { inputReader.readString("👤 Enter your name ") }
        verify(exactly = 1) { inputReader.readString("🔒 Enter Your Password ") }
        coVerify(exactly = 1) { authenticateUserLoginUseCase(username, password) }
    }

    @Test
    fun `should prompt again when authentication fails`() = runTest {
        // Given
        val failUsername = "wrongUser"
        val failPassword = "wrongPass"
        val correctUsername = "correctUser"
        val correctPassword = "correctPass"
        val expectedUser = User(id = UUID.randomUUID(), username = correctUsername, userRole = UserRole.MATE)

        every { inputReader.readString("👤 Enter your name ") } returns failUsername andThen correctUsername
        every { inputReader.readString("🔒 Enter Your Password ") } returns failPassword andThen correctPassword
        coEvery {
            authenticateUserLoginUseCase(
                failUsername,
                failPassword
            )
        } returns Result.failure(Exception("Authentication failed"))
        coEvery { authenticateUserLoginUseCase(correctUsername, correctPassword) } returns Result.success(expectedUser)

        // When
        val actualUser = loginView.getLoggedUser()

        // Then
        assertEquals(expectedUser, actualUser)
        verify(exactly = 2) { inputReader.readString("👤 Enter your name ") }
        verify(exactly = 2) { inputReader.readString("🔒 Enter Your Password ") }
        coVerify(exactly = 1) { authenticateUserLoginUseCase(failUsername, failPassword) }
        coVerify(exactly = 1) { authenticateUserLoginUseCase(correctUsername, correctPassword) }
    }

    @Test
    fun `should attempt multiple logins until success`() = runTest {
        // Given
        val firstUsername = "user1"
        val firstPassword = "pass1"
        val secondUsername = "user2"
        val secondPassword = "pass2"
        val thirdUsername = "user3"
        val thirdPassword = "pass3"

        val expectedUser = User(id = UUID.randomUUID(), username = thirdUsername, userRole = UserRole.MATE)

        every { inputReader.readString("👤 Enter your name ") } returns firstUsername andThen secondUsername andThen thirdUsername
        every { inputReader.readString("🔒 Enter Your Password ") } returns firstPassword andThen secondPassword andThen thirdPassword
        coEvery {
            authenticateUserLoginUseCase(
                firstUsername,
                firstPassword
            )
        } returns Result.failure(Exception("Authentication failed"))
        coEvery {
            authenticateUserLoginUseCase(
                secondUsername,
                secondPassword
            )
        } returns Result.failure(Exception("Authentication failed"))
        coEvery { authenticateUserLoginUseCase(thirdUsername, thirdPassword) } returns Result.success(expectedUser)

        // When
        val actualUser = loginView.getLoggedUser()

        // Then
        assertEquals(expectedUser, actualUser)
        verify(exactly = 3) { inputReader.readString("👤 Enter your name ") }
        verify(exactly = 3) { inputReader.readString("🔒 Enter Your Password ") }
        coVerify(exactly = 1) { authenticateUserLoginUseCase(firstUsername, firstPassword) }
        coVerify(exactly = 1) { authenticateUserLoginUseCase(secondUsername, secondPassword) }
        coVerify(exactly = 1) { authenticateUserLoginUseCase(thirdUsername, thirdPassword) }
    }

}