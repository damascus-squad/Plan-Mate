package logic.useCase


import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import logic.model.Admin
import logic.model.Mate
import logic.repo.AuthenticationRepository
import logic.exception.*
import org.damascus.logic.model.Role
import org.damascus.logic.service.MD5HashingService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class CreateMateUseCaseTest {

    private lateinit var authRepo: AuthenticationRepository
    private lateinit var hashingService: MD5HashingService
    private lateinit var useCase: CreateMateUseCase

    @BeforeEach
    fun setup() {
        authRepo = mockk()
        hashingService = mockk()
        useCase = CreateMateUseCase(authRepo, hashingService)
    }

    @Test
    fun `should create mate successfully when requester is admin`() {
        // Given
        val admin = Admin(UUID.randomUUID(), "admin1", "hash123", Role.ADMIN)
        val username = "mate1"
        val password = "pass123"
        val hashedPassword = "hashed-pass"
        val expectedMate = Mate(UUID.randomUUID(), username, hashedPassword, Role.MATE)

        every { authRepo.getUserByUsername(username) } returns null
        every { hashingService.hashData(password) } returns hashedPassword
        every { authRepo.createMate(admin, username, hashedPassword) } returns expectedMate

        // When
        val result = useCase(admin, username, password)

        // Then
        assertEquals(expectedMate, result)
        verify(exactly = 1) { authRepo.createMate(admin, username, hashedPassword) }
    }

    @Test
    fun `should throw UnauthorizedActionException when requester is not admin`() {
        // Given
        val mateRequester = Mate(UUID.randomUUID(), "mate1", "hash123", Role.MATE)
        val username = "mate2"
        val password = "pass123"

        // When/Then
        assertThrows(UnauthorizedActionException::class.java) {
            useCase(mateRequester, username, password)
        }
    }

    @Test
    fun `should throw UserAlreadyExistException when username exists`() {
        // Given
        val admin = Admin(UUID.randomUUID(), "admin1", "hash123", Role.ADMIN)
        val existingUser = Mate(UUID.randomUUID(), "existing", "hash456", Role.MATE)

        every { authRepo.getUserByUsername("existing") } returns existingUser

        // When/Then
        assertThrows(UserAlreadyExistException::class.java) {
            useCase(admin, "existing", "anypass")
        }
    }

    @Test
    fun `should throw InvalidUserNameInputException when username contains spaces`() {
        // Given
        val admin = Admin(UUID.randomUUID(), "admin1", "hash123", Role.ADMIN)

        // When/Then
        assertThrows(InvalidUserNameInputException::class.java) {
            useCase(admin, "user name", "pass123")
        }
    }

    @Test
    fun `should throw BlankInputException when username is blank`() {
        // Given
        val admin = Admin(UUID.randomUUID(), "admin1", "hash123", Role.ADMIN)

        // When/Then
        assertThrows(BlankInputException::class.java) {
            useCase(admin, " ", "pass123")
        }
    }

    @Test
    fun `should throw BlankInputException when password is blank`() {
        // Given
        val admin = Admin(UUID.randomUUID(), "admin1", "hash123", Role.ADMIN)
        every { authRepo.getUserByUsername("user1") } returns null

        // When/Then
        assertThrows(BlankInputException::class.java) {
            useCase(admin, "user1", "")
        }
    }

    @Test
    fun `should throw InvalidPasswordException when password is already hashed`() {
        // Given
        val admin = Admin(UUID.randomUUID(), "admin1", "hash123", Role.ADMIN)
        val hashedPassword = "5f4dcc3b5aa765d61d8327deb882cf99" // MD5 of "password"

        every { authRepo.getUserByUsername("user1") } returns null

        // When/Then
        assertThrows(InvalidPasswordException::class.java) {
            useCase(admin, "user1", hashedPassword)
        }
    }

    @Test
    fun `should preserve spaces in password`() {
        // Given
        val admin = Admin(UUID.randomUUID(), "admin1", "hash123", Role.ADMIN)
        val username = "mate1"
        val passwordWithSpaces = " pass with spaces "
        val hashedPassword = "hashed-with-spaces"
        val expectedMate = Mate(UUID.randomUUID(), username, hashedPassword, Role.MATE)

        every { authRepo.getUserByUsername(username) } returns null
        every { hashingService.hashData(passwordWithSpaces) } returns hashedPassword
        every { authRepo.createMate(admin, username, hashedPassword) } returns expectedMate

        // When
        val result = useCase(admin, username, passwordWithSpaces)

        // Then
        assertEquals(expectedMate, result)
        verify { hashingService.hashData(passwordWithSpaces) }
    }
}