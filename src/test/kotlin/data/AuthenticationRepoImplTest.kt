package data

import org.junit.jupiter.api.Assertions.*
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import logic.model.Admin
import logic.model.Mate
import logic.model.User
import org.damascus.logic.HashingService
import org.damascus.logic.exception.InvalidPasswordException
import org.damascus.logic.exception.UnauthorizedActionException
import org.damascus.logic.exception.UserNotFoundException
import org.damascus.logic.exception.UserAlreadyExistException
import org.damascus.logic.AuthenticationRepository
import org.damascus.data.authentication.AuthenticationRepoImpl
import org.damascus.logic.model.Role
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

@ExtendWith(MockKExtension::class)
class AuthenticationRepositoryImplTest {

    private lateinit var hashingService: HashingService
    private lateinit var authRepo: AuthenticationRepository
    private lateinit var users: MutableList<User>

    @BeforeTest
    fun setup() {
        hashingService = mockk()
        users = mutableListOf()
        authRepo = AuthenticationRepoImpl(hashingService, users)
    }

    @Test
    fun `login should return user when credentials are valid`() {
        // Given
        val username = "abdo"
        val rawPassword = "pass123"
        val hashedPassword = "hashed-pass"
        val storedUser = Mate(id = UUID.randomUUID(), username, hashedPassword, Role.MATE)
        users.add(storedUser)
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

        // When, Then
        val exception = assertFailsWith<UserNotFoundException> {
            authRepo.login(username, password)
        }
        assertEquals("User 'unknown' not found", exception.message)
    }

    @Test
    fun `login should throw InvalidPasswordException when password is incorrect`() {
        // Given
        val username = "ahmed"
        val correctHashed = "hashed-pass"
        val wrongInput = "wrong-pass"
        val storedUser = Mate(id = UUID.randomUUID(), username, correctHashed, Role.MATE)
        users.add(storedUser)
        every { hashingService.verifyData(wrongInput, correctHashed) } returns false


        // When , Then
        assertFailsWith<InvalidPasswordException> {
            authRepo.login(username, wrongInput)
        }
    }

    @Test
    fun `createMate should fail if user already exists`() {
        // Given
        val admin = Admin(id = UUID.randomUUID(), "admin1", "hashed123", Role.ADMIN)
        val existingUsername = "mate1"
        val newMate = Mate(id = UUID.randomUUID(), existingUsername, "pass", Role.MATE)
        users.add(newMate)

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
        every { hashingService.hashData(rawPassword) } returns expectedHash

        // When
        val mate = authRepo.createMate(admin, username, rawPassword)

        // Then
        assertEquals(username, mate.username)
        assertEquals(expectedHash, mate.password)
        verify(exactly = 1) { hashingService.hashData(rawPassword) }
    }

    @Test
    fun `createMate should add mate to users list`() {
        // Given
        val admin = Admin(UUID.randomUUID(), "admin1", "hashed123", Role.ADMIN)
        val username = "newMate"
        val rawPassword = "password"
        val expectedHash = "hashedPassword"
        every { hashingService.hashData(rawPassword) } returns expectedHash

        // When
        val mate = authRepo.createMate(admin, username, rawPassword)

        // Then
        assertTrue(users.contains(mate))
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
        users.add(user)

        // When
        val result = authRepo.findByUsername("testUser")

        // Then
        assertEquals(user.username, result?.username)
    }

    @Test
    fun `findByUsername should return null when user does not exist`() {
        // Given
        val user = Mate(id = UUID.randomUUID(), "testUser", "hashed123", Role.MATE)
        users.add(user)

        // When
        val result = authRepo.findByUsername("nonExistingUser")

        // Then
        assertNull(result)
    }

    @Test
    fun `findByUsername should return null when list is empty`() {
        // Given : empty list

        // When
        val result = authRepo.findByUsername("nonExistingUser")

        // Then
        assertNull(result)
    }

    @Test
    fun `createAdmin should succeed when requester is admin and username is new`() {
        // Given
        val requester = Admin(id = UUID.randomUUID(), "admin1", "hashed123", Role.ADMIN)
        val newUsername = "admin2"
        val rawPassword = "123456"
        val hashedPassword = "hashed456"
        every { hashingService.hashData(rawPassword) } returns hashedPassword

        // When
        val result = authRepo.createAdmin(requester, newUsername, rawPassword)

        // Then
        assertEquals(newUsername, result.username)
        assertEquals(hashedPassword, result.password)
        assertTrue(users.contains(result))
    }

    @Test
    fun `createAdmin should throw UnauthorizedActionException when requester is a mate`() {
        // Given
        val requester = Mate(id = UUID.randomUUID(), "mate1", "hashedPass", Role.MATE)
        val newUsername = "admin2"
        val rawPassword = "123456"

        // When, Then
        assertFailsWith<UnauthorizedActionException> {
            authRepo.createAdmin(requester, newUsername, rawPassword)
        }
    }

    @Test
    fun `createAdmin should throw UserAlreadyExistException when username already exists`() {
        // Given
        val requester = Admin(id = UUID.randomUUID(), "admin1", "hashed123", Role.ADMIN)
        val existingUsername = "admin2"
        users.add(Admin(id = UUID.randomUUID(), existingUsername, "hashedExist", Role.ADMIN))

        // When, Then
        assertFailsWith<UserAlreadyExistException> {
            authRepo.createAdmin(requester, existingUsername, "any")
        }
    }
}
