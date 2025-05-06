package data

import data.dto.UserDTO
import data.repo.AuthenticationRepoImpl
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import logic.exception.InvalidCredentialsException
import logic.exception.UnauthorizedActionException
import logic.exception.UserAlreadyExistException
import logic.exception.UserNotFoundException
import logic.model.User
import logic.model.UserRole
import logic.repo.AuthenticationRepository
import logic.repo.DataSource
import logic.service.HashingService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.*
import java.util.stream.Stream
import kotlin.test.Test
import kotlin.test.assertFailsWith

@ExtendWith(MockKExtension::class)
class AuthenticationRepositoryImplTest {

    private lateinit var hashingService: HashingService
    private lateinit var authRepo: AuthenticationRepository
    private lateinit var usersDataSource: DataSource<UserDTO>

    @BeforeEach
    fun setup() {
        hashingService = mockk(relaxed = true)
        usersDataSource = mockk(relaxed = true)
        authRepo = AuthenticationRepoImpl(hashingService, usersDataSource)
    }

    @Test
    fun `login should return user when credentials are valid`() {
        // Given
        val username = "abdo"
        val rawPassword = "pass123"
        val hashedPassword = "hashed-pass"
        val storedUser = UserDTO(id = UUID.randomUUID(), hashedPassword, username, UserRole.MATE)

        every { usersDataSource.read() } returns listOf(storedUser)
        every { hashingService.verifyData(rawPassword, hashedPassword) } returns true

        // When
        val result = authRepo.login(username, rawPassword)

        // Then
        assertEquals(storedUser.toUser(), result)
    }

    @Test
    fun `login should throw UserNotFoundException when user does not exist`() {
        // Given
        val username = "unknown"
        val password = "somepass"
        every { usersDataSource.read() } returns emptyList()

        // When, Then
        assertFailsWith<UserNotFoundException> {
            authRepo.login(username, password)
        }
    }

    @Test
    fun `login should throw InvalidPasswordException when password is incorrect`() {
        // Given
        val username = "ahmed"
        val correctHashed = "hashed-pass"
        val wrongInput = "wrong-pass"
        val storedUser = UserDTO(id = UUID.randomUUID(), correctHashed, username, UserRole.MATE)

        every { usersDataSource.read() } returns listOf(storedUser)
        every { hashingService.verifyData(wrongInput, correctHashed) } returns false

        // When , Then
        assertFailsWith<InvalidCredentialsException> {
            authRepo.login(username, wrongInput)
        }
    }

    @ParameterizedTest
    @MethodSource("existingUsersLists")
    fun `createMate should fail if user already exists`(matesList: List<UserDTO>) {
        // Given
        val admin = User(UUID.randomUUID(), "hashed123", UserRole.ADMIN)
        val existingUsername = "mate17"
        every { usersDataSource.read() } returns matesList

        // When , Then
        assertFailsWith<UserAlreadyExistException> {
            authRepo.createMate(admin, existingUsername, "newPass")
        }
    }

    @Test
    fun `createMate should hash password and return correct Mate`() {
        // Given
        val admin = User(UUID.randomUUID(), "admin1", UserRole.ADMIN)
        val username = "newMate"
        val rawPassword = "password"

        every { usersDataSource.read() } returns emptyList()
        every { usersDataSource.write(any<UserDTO>()) } returns Unit

        // When
        val mate = authRepo.createMate(admin, username, rawPassword)

        // Then
        assertEquals(username, mate.username)
        verify(exactly = 1) { hashingService.hashData(rawPassword) }
    }

    @Test
    fun `createMate should fail when mate is the creator`() {
        val mate = User(id = UUID.randomUUID(), "mate1", UserRole.MATE)
        val username = "newMate"
        val rawPassword = "123"

        assertFailsWith<UnauthorizedActionException> {
            authRepo.createMate(mate, username, rawPassword)
        }
    }

    companion object {
        @JvmStatic
        fun existingUsersLists(): Stream<List<UserDTO>> {
            val existingUsername = "mate17"
            val newMate = UserDTO(UUID.randomUUID(), "pass", existingUsername, UserRole.MATE)

            return Stream.of(
                listOf(newMate),
                listOf(
                    UserDTO(UUID.randomUUID(), "pass", "mate1", UserRole.MATE),
                    UserDTO(UUID.randomUUID(), "pass", "mate2", UserRole.MATE),
                    newMate
                )
            )
        }
    }
}
