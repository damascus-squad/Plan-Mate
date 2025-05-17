package org.damascus.data.mongodb

import com.google.common.truth.Truth.assertThat
import com.mongodb.MongoException
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.bson.codecs.pojo.annotations.BsonId
import org.damascus.logic.model.UserRole
import org.junit.jupiter.api.*
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.*


@Testcontainers
class MongoDataSourceTest {

    /**
     * Members doesn't make sense together, but the point is testing every
     * data type we will be storing to & retrieving from the mongo db
     */

    data class TestDocument(
        @BsonId val id: UUID = UUID.randomUUID(),
        val nullableId: UUID?,
        val name: String,
        val value: Int,
        val list: MutableList<UUID>,
        val enum: UserRole,
        val date: LocalDateTime
    ) : MongoDocument

    val baseTestDocument = TestDocument(
        name = "Test",
        value = 42,
        nullableId = null,
        list = mutableListOf(UUID.randomUUID(), UUID.randomUUID()),
        enum = UserRole.ADMIN,
        date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    )

    companion object {
        @Container
        val mongoDBContainer = MongoDBContainer("mongo:6.0")

        private lateinit var mongoClient: MongoClient

        @BeforeAll
        @JvmStatic
        fun setup() {
            mongoDBContainer.start()
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            mongoDBContainer.stop()
        }
    }

    private lateinit var database: MongoDatabase
    private lateinit var mongoDataSource: MongoDataSource<TestDocument>
    private val collectionName = "testCollection"

    @BeforeEach
    fun init() {
        val clientSettings = MongoConnector.getClientSettings(
            connectionString = mongoDBContainer.connectionString
        )

        mongoClient = MongoClient.create(clientSettings)
        database = mongoClient.getDatabase("testdb_${UUID.randomUUID()}")

        mongoDataSource = MongoDataSource(
            database,
            collectionName,
            TestDocument::class.java,
            Dispatchers.IO
        )
    }

    @AfterEach
    fun cleanup() {
        runTest {
            database.getCollection<TestDocument>(collectionName).drop()
        }

        mongoClient.close()
    }

    @Test
    fun `read should return a document list after writing it to db`() = runTest {
        // Given
        val testDoc = baseTestDocument.copy(id = UUID.randomUUID())

        // When
        mongoDataSource.write(testDoc)
        val result = mongoDataSource.read()

        // Then
        assertThat(testDoc.id).isEqualTo(result.first().id)
    }

    @Test
    fun `read should return an empty list when reading empty collection`() = runTest {
        // Given no entries inserted

        // When
        val result = mongoDataSource.read()

        // Then
        assertThat(result).isEmpty()
    }


    @Test
    fun `write should insert a single document when accepting a single entry`() = runTest {
        // Given
        val testDoc = baseTestDocument.copy(id = UUID.randomUUID())

        // When
        mongoDataSource.write(testDoc)

        // Then
        val result = mongoDataSource.read()
        assertThat(result.first().id).isEqualTo(testDoc.id)
    }


    @Test
    fun `write should insert multiple documents when accepting a list`() = runTest {
        // Given
        val docs = listOf(
            baseTestDocument.copy(id = UUID.randomUUID()), baseTestDocument.copy(id = UUID.randomUUID())
        )

        // When
        mongoDataSource.write(docs)

        // Then
        val result = mongoDataSource.read().map { it.id }
        assertThat(result).containsExactlyElementsIn(docs.map { it.id })
    }


    @Test
    fun `write should throw a MongoException when writing a document with same id`() = runTest {
        // Given
        val testDoc = baseTestDocument.copy(id = UUID.randomUUID())

        // When && Then
        assertThrows<MongoException> {
            mongoDataSource.write(testDoc)
            mongoDataSource.write(testDoc.copy(name = "Second"))
        }
    }

    @Test
    fun `update should modify a document when found in the db`() = runTest {
        // Given
        val originalDoc = baseTestDocument.copy(id = UUID.randomUUID(), name = "Original")
        mongoDataSource.write(originalDoc)

        // When
        val updatedDoc = originalDoc.copy(name = "Updated")
        mongoDataSource.update(originalDoc.id, updatedDoc)

        // Then
        val result = mongoDataSource.read().first { it.id == originalDoc.id }
        assertThat(result.name).isEqualTo("Updated")
    }

    @Test
    fun `update should throw MongoDocumentNotFound when updating non-existent document`() = runTest {
        val nonExistentId = UUID.randomUUID()
        val updatedDoc = baseTestDocument.copy(id = nonExistentId)

        assertThrows<MongoDocumentNotFound> {
            mongoDataSource.update(nonExistentId, updatedDoc)
        }
    }

    @Test
    fun `delete should remove a document when found in the db`() = runTest {
        // Given
        val doc = baseTestDocument.copy(UUID.randomUUID())
        mongoDataSource.write(doc)

        // When
        mongoDataSource.delete(doc.id)

        // Then
        val result = mongoDataSource.read().firstOrNull { it.id == doc.id }
        assertThat(result).isNull()
    }

    @Test
    fun `delete should throw MongoDocumentNotFound when removing non-existent document`() = runTest {
        val nonExistentId = UUID.randomUUID()
        val updatedDoc = baseTestDocument.copy(id = nonExistentId)

        assertThrows<MongoDocumentNotFound> {
            mongoDataSource.delete(nonExistentId)
        }
    }
}