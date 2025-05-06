package ui.views

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import logic.model.User
import org.damascus.logic.model.UserRole
import logic.usecase.auth.AuthenticateUserLoginUseCase
import org.damascus.ui.io.InputReader
import org.damascus.ui.views.LoginView
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals

class LoginViewTest {

    private lateinit var authenticateUserLoginUseCase: AuthenticateUserLoginUseCase
    private lateinit var inputReader: InputReader
    private lateinit var loginView: LoginView

    @BeforeEach
    fun setup() {
        authenticateUserLoginUseCase = mockk(relaxed = true)
        inputReader = mockk(relaxed = true)
        loginView = LoginView(authenticateUserLoginUseCase, inputReader)
    }

    @Test
    fun `should return user when authentication succeeds`() {
        // Given
        val username = "testUser"
        val password = "password123"
        val expectedUser = User(id = UUID.randomUUID(), username = username, userRole = UserRole.MATE)

        every { inputReader.readString("Username:") } returns username
        every { inputReader.readString("Password:") } returns password
        every { authenticateUserLoginUseCase(username, password) } returns Result.success(expectedUser)

        // When
        val actualUser = loginView.getLoggedUser()

        // Then
        assertEquals(expectedUser, actualUser)
        verify(exactly = 1) { inputReader.readString("Username:") }
        verify(exactly = 1) { inputReader.readString("Password:") }
        verify(exactly = 1) { authenticateUserLoginUseCase(username, password) }
    }

    @Test
    fun `should prompt again when authentication fails`() {
        // Given
        val failUsername = "wrongUser"
        val failPassword = "wrongPass"
        val correctUsername = "correctUser"
        val correctPassword = "correctPass"
        val expectedUser = User(id = UUID.randomUUID(), username = correctUsername, userRole = UserRole.MATE)

        every { inputReader.readString("Username:") } returns failUsername andThen correctUsername
        every { inputReader.readString("Password:") } returns failPassword andThen correctPassword
        every { authenticateUserLoginUseCase(failUsername, failPassword) } returns Result.failure(Exception("Authentication failed"))
        every { authenticateUserLoginUseCase(correctUsername, correctPassword) } returns Result.success(expectedUser)

        // When
        val actualUser = loginView.getLoggedUser()

        // Then
        assertEquals(expectedUser, actualUser)
        verify(exactly = 2) { inputReader.readString("Username:") }
        verify(exactly = 2) { inputReader.readString("Password:") }
        verify(exactly = 1) { authenticateUserLoginUseCase(failUsername, failPassword) }
        verify(exactly = 1) { authenticateUserLoginUseCase(correctUsername, correctPassword) }
    }

    @Test
    fun `should attempt multiple logins until success`() {
        // Given
        val firstUsername = "user1"
        val firstPassword = "pass1"
        val secondUsername = "user2"
        val secondPassword = "pass2"
        val thirdUsername = "user3"
        val thirdPassword = "pass3"

        val expectedUser = User(id = UUID.randomUUID(), username = thirdUsername, userRole = UserRole.MATE)

        every { inputReader.readString("Username:") } returns firstUsername andThen secondUsername andThen thirdUsername
        every { inputReader.readString("Password:") } returns firstPassword andThen secondPassword andThen thirdPassword
        every { authenticateUserLoginUseCase(firstUsername, firstPassword) } returns Result.failure(Exception("Authentication failed"))
        every { authenticateUserLoginUseCase(secondUsername, secondPassword) } returns Result.failure(Exception("Authentication failed"))
        every { authenticateUserLoginUseCase(thirdUsername, thirdPassword) } returns Result.success(expectedUser)

        // When
        val actualUser = loginView.getLoggedUser()

        // Then
        assertEquals(expectedUser, actualUser)
        verify(exactly = 3) { inputReader.readString("Username:") }
        verify(exactly = 3) { inputReader.readString("Password:") }
        verify(exactly = 1) { authenticateUserLoginUseCase(firstUsername, firstPassword) }
        verify(exactly = 1) { authenticateUserLoginUseCase(secondUsername, secondPassword) }
        verify(exactly = 1) { authenticateUserLoginUseCase(thirdUsername, thirdPassword) }
    }


}