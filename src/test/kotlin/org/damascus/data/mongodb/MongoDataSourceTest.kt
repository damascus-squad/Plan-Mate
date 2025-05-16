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
     * kind of type we will be storing to & retrieving from the mongo db
     */

    data class TestDocument(
        @BsonId
        val id: UUID = UUID.randomUUID(),
        val nullableId: UUID?,
        val name: String,
        val value: Int,
        val list: MutableList<UUID>,
        val enum: UserRole,
        val date: LocalDateTime
    ) : MongoDocument

    val testDocument = TestDocument(
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
        private lateinit var database: MongoDatabase

        @BeforeAll
        @JvmStatic
        fun setup() {
            mongoDBContainer.start()

            val clientSettings = MongoConnector.getClientSettings(
                connectionString = mongoDBContainer.connectionString
            )

            mongoClient = MongoClient.create(clientSettings)
            database = mongoClient.getDatabase("testdb")
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            mongoClient.close()
            mongoDBContainer.stop()
        }
    }

    private lateinit var mongoDataSource: MongoDataSource<TestDocument>

    @BeforeEach
    fun init() {
        mongoDataSource = MongoDataSource(
            database,
            "testCollection",
            TestDocument::class.java,
            Dispatchers.IO
        )
    }

    @Test
    fun `should throw CollectionIsEmptyException when reading from an empty collection`() = runTest {
        // Given: No items inserted

        // When && Then
        assertThrows<MongoException> {
            mongoDataSource.read()
        }
    }

    @Test
    fun `should read a document after writing it to db`() = runTest {
        // Given
        val testDoc = testDocument.copy(id = UUID.randomUUID())

        // When
        mongoDataSource.write(testDoc)
        val result = mongoDataSource.read()

        // Then
        assertThat(testDoc.id).isEqualTo(result.first().id)
    }

    @Test
    fun `should write multiple documents when accepting a list`() = runTest {
        // Given
        val docs = listOf(
            testDocument.copy(id = UUID.randomUUID()), testDocument.copy(id = UUID.randomUUID())
        )

        // When
        mongoDataSource.write(docs)

        // Then
        val result = mongoDataSource.read().map { it.id }
        assertThat(result).containsAtLeast(docs[0].id, docs[1].id)
    }

    @Test
    fun `should update a document when found in the db`() = runTest {
        // Given
        val originalDoc = testDocument.copy(id = UUID.randomUUID(), name = "Original")
        mongoDataSource.write(originalDoc)

        // When
        val updatedDoc = originalDoc.copy(name = "Updated")
        mongoDataSource.update(originalDoc.id, updatedDoc)

        // Then
        val result = mongoDataSource.read().first { it.id == originalDoc.id }
        assertThat(result.name).isEqualTo("Updated")
    }

    @Test
    fun `should delete a document when found in the db`() = runTest {
        // Given
        val doc = testDocument.copy(UUID.randomUUID())
        mongoDataSource.write(doc)

        // When
        mongoDataSource.delete(doc.id)

        // Then
        val result = mongoDataSource.read().firstOrNull { it.id == doc.id }
        assertThat(result).isNull()
    }
}