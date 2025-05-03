package data

import org.junit.jupiter.api.Assertions.*
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import logic.exception.InvalidCredentialsException
import logic.exception.UnauthorizedActionException
import logic.exception.UserAlreadyExistException
import logic.exception.UserNotFoundException
import logic.model.Admin
import logic.model.Mate
import logic.model.User
import logic.repo.DataSource
import org.damascus.logic.service.HashingService
import logic.repo.AuthenticationRepository
import org.damascus.data.authentication.AuthenticationRepoImpl
import org.damascus.logic.model.Role
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertFailsWith

@ExtendWith(MockKExtension::class)
class AuthenticationRepositoryImplTest {

    private lateinit var hashingService: HashingService
    private lateinit var authRepo: AuthenticationRepository
    private lateinit var usersDataSource: DataSource<User>

    @BeforeTest
    fun setup() {
        hashingService = mockk()
        usersDataSource = mockk()
        authRepo = AuthenticationRepoImpl(hashingService, usersDataSource)
    }

    @Test
    fun `login should return user when credentials are valid`() {
        // Given
        val username = "abdo"
        val rawPassword = "pass123"
        val hashedPassword = "hashed-pass"
        val storedUser = Mate(id = UUID.randomUUID(), username, hashedPassword, Role.MATE)

        every { usersDataSource.read() } returns listOf(storedUser)
        every { hashingService.verifyData(rawPassword, hashedPassword) } returns true

        // When
        val result = authRepo.login(username, rawPassword)

        // Then
        assertEquals(storedUser, result)
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
        val storedUser = Mate(id = UUID.randomUUID(), username, correctHashed, Role.MATE)
        every { usersDataSource.read() } returns listOf(storedUser)
        every { hashingService.verifyData(wrongInput, correctHashed) } returns false


        // When , Then
        assertFailsWith<InvalidCredentialsException> {
            authRepo.login(username, wrongInput)
        }
    }

    @Test
    fun `createMate should fail if user already exists`() {
        // Given
        val admin = Admin(id = UUID.randomUUID(), "admin1", "hashed123", Role.ADMIN)
        val existingUsername = "mate1"
        val newMate = Mate(id = UUID.randomUUID(), existingUsername, "pass", Role.MATE)
        every { usersDataSource.read() } returns listOf(newMate)

        // When , Then
        assertFailsWith<UserAlreadyExistException> {
            authRepo.createMate(admin, existingUsername, "newPass")
        }
    }

    @Test
    fun `createMate should hash password and return correct Mate`() {
        // Given
        val admin = Admin(UUID.randomUUID(), "admin1", "hashed123", Role.ADMIN)
        val username = "newMate"
        val rawPassword = "password"
        val expectedHash = "hashedPassword"
        every { usersDataSource.read() } returns emptyList()
        every { hashingService.hashData(rawPassword) } returns expectedHash
        every { usersDataSource.write(any<Mate>()) } returns Unit

        // When
        val mate = authRepo.createMate(admin, username, rawPassword)

        // Then
        assertEquals(username, mate.username)
        assertEquals(expectedHash, mate.password)
        verify(exactly = 1) { hashingService.hashData(rawPassword) }
    }

    @Ignore
    @Test
    fun `createMate should add mate to users list`() {
        // Given
        val admin = Admin(UUID.randomUUID(), "admin1", "hashed123", Role.ADMIN)
        val username = "newMate"
        val rawPassword = "password"
        val expectedHash = "hashedPassword"

        val capturedMate = slot<Mate>()
        var called = false

        every { usersDataSource.read() } answers  {
            if (!called) {
                called = true
                emptyList()
            } else {
                listOf(capturedMate.captured)
            }
        }
        every { hashingService.hashData(rawPassword) } returns expectedHash
        every { usersDataSource.write(capture(capturedMate)) } returns Unit

        // When
        val mate = authRepo.createMate(admin, username, rawPassword)

        // Then
        assertTrue(usersDataSource.read().contains(mate))
    }


    @Test
    fun `createMate should fail when mate is the creator`() {
        val mate = Mate(id = UUID.randomUUID(), "mate1", "hashedMatePass", Role.MATE)
        val username = "newMate"
        val rawPassword = "123"

        assertFailsWith<UnauthorizedActionException> {
            authRepo.createMate(mate, username, rawPassword)
        }
    }

    @Test
    fun `findByUsername should return user when user exists`() {
        // Given
        val user = Mate(id = UUID.randomUUID(), "testUser", "hashed123", Role.MATE)
        every { usersDataSource.read() } returns listOf(user)

        // When
        val result = authRepo.getUserByUsername("testUser")

        // Then
        assertEquals(user.username, result?.username)
    }

    @Test
    fun `findByUsername should return null when user does not exist`() {
        // Given
        val user = Mate(id = UUID.randomUUID(), "testUser", "hashed123", Role.MATE)
        every { usersDataSource.read() } returns listOf(user)

        // When
        val result = authRepo.getUserByUsername("nonExistingUser")

        // Then
        assertNull(result)
    }

    @Test
    fun `findByUsername should return null when list is empty`() {
        // Given : empty list
        every { usersDataSource.read() } returns emptyList()
        // When
        val result = authRepo.getUserByUsername("nonExistingUser")

        // Then
        assertNull(result)
    }
}
