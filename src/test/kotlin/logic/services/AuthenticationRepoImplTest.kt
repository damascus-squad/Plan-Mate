package logic.services

import User
import org.junit.jupiter.api.Assertions.*

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import model.Admin
import model.Mate
import org.damascus.logic.HashingService
import org.damascus.logic.exception.InvalidPasswordException
import org.damascus.logic.exception.UnauthorizedActionException
import org.damascus.logic.exception.UserNotFoundException
import org.damascus.logic.exception.UserAlreadyExistException
import org.damascus.logic.services.AuthenticationRepository
import org.damascus.logic.services.AuthenticationRepoImpl
import org.junit.jupiter.api.Assertions.*
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
    fun `login should return user when credentials are valid`(){
        val username = "abdo"
        val rawPassword = "pass123"
        val hashedPassword = "hashed-pass"
        val storedUser = Mate(id= UUID.randomUUID(),username, hashedPassword)
        users.add(storedUser)

        val inputUser = Mate(id= UUID.randomUUID(),username, rawPassword)
        every { hashingService.verifyData(rawPassword, hashedPassword) } returns true

        val result = authRepo.login(inputUser)

        assertEquals(storedUser, result)
    }

    @Test
    fun `login should throw UserNotFoundException when user does not exist`() {
        val username = "unknown"
        val user = Mate(id= UUID.randomUUID(),username, "somepass")

        val exception = assertFailsWith<UserNotFoundException> {
            authRepo.login(user)
        }

        assertEquals("User 'unknown' not found", exception.message)
    }

    @Test
    fun `login should throw InvalidPasswordException when password is incorrect`() {
        val username = "ahmed"
        val correctHashed = "hashed-pass"
        val wrongInput = "wrong-pass"
        val storedUser = Mate(id= UUID.randomUUID(),username, correctHashed)
        users.add(storedUser)

        val inputUser = Mate(id= UUID.randomUUID(),username, wrongInput)
        every { hashingService.verifyData(wrongInput, correctHashed) } returns false

        assertFailsWith<InvalidPasswordException> {
            authRepo.login(inputUser)
        }
    }

    @Test
    fun `createMate should fail if user already exists`() {
        val admin = Admin(id= UUID.randomUUID(),"admin1", "hashed123")
        val existingUsername = "mate1"
        val newMate= Mate(id= UUID.randomUUID(),existingUsername, "pass")
        users.add(newMate)

        assertFailsWith<UserAlreadyExistException> {
            authRepo.createMate(admin, existingUsername, "newPass")
        }
    }

    @Test
    fun `createMate should pass when admin is the creator`() {
        val admin = Admin(id= UUID.randomUUID(),"admin1", "hashed123")
        val username = "newMate"
        val rawPassword = "password"
        val expectedHash = "hashedPassword"

        every { hashingService.hashData(rawPassword) } returns expectedHash

        val mate = authRepo.createMate(admin, username, rawPassword)

        assertEquals(username, mate.username)
        assertEquals(expectedHash, mate.password)
        verify(exactly = 1) { hashingService.hashData(rawPassword) }

        assertTrue(users.contains(mate))
    }

    @Test
    fun `createMate should fail when mate is the creator`() {
        val mate = Mate(id= UUID.randomUUID(),"mate1", "hashedMatePass")
        val username = "newMate"
        val rawPassword = "123"

        assertFailsWith<UnauthorizedActionException> {
            authRepo.createMate(mate, username, rawPassword)
        }
    }

    @Test
    fun `findByUsername should return user when user exists`() {
        val user = Mate(id= UUID.randomUUID(),"testUser", "hashed123")
        users.add(user)

        val result = authRepo.findByUsername("testUser")

        assertEquals(user.username, result?.username)
    }

    @Test
    fun `findByUsername should return null when user does not exist`() {
        val user = Mate(id= UUID.randomUUID(),"testUser", "hashed123")
        users.add(user)

        val result = authRepo.findByUsername("nonExistingUser")

        assertNull(result)
    }

    @Test
    fun `findByUsername should return null when list is empty`() {
        val result = authRepo.findByUsername("nonExistingUser")

        assertNull(result)
    }

    @Test
    fun `createAdmin should succeed when requester is admin and username is new`() {
        val requester = Admin(id = UUID.randomUUID(),"admin1", "hashed123")
        val newUsername = "admin2"
        val rawPassword = "123456"
        val hashedPassword = "hashed456"

        every { hashingService.hashData(rawPassword) } returns hashedPassword

        val result = authRepo.createAdmin(requester, newUsername, rawPassword)

        assertEquals(newUsername, result.username)
        assertEquals(hashedPassword, result.password)
        assertTrue(users.contains(result))
    }

    @Test
    fun `createAdmin should throw UnauthorizedActionException when requester is a mate`() {
        val requester = Mate(id = UUID.randomUUID(),"mate1", "hashedPass")
        val newUsername = "admin2"
        val rawPassword = "123456"

        assertFailsWith<UnauthorizedActionException> {
            authRepo.createAdmin(requester, newUsername, rawPassword)
        }
    }


    @Test
    fun `createAdmin should throw UserAlreadyExistException when username already exists`() {
        val requester = Admin(id = UUID.randomUUID(),"admin1", "hashed123")
        val existingUsername = "admin2"
        users.add(Admin(id = UUID.randomUUID(),existingUsername, "hashedExist"))

        assertFailsWith<UserAlreadyExistException> {
            authRepo.createAdmin(requester, existingUsername, "any")
        }
    }

}
