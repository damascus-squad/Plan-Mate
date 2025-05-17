package org.damascus.data.mongodb

import com.mongodb.MongoException
import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import org.bson.Document
import org.damascus.data.mongodb.MongoConstants.MONGO_ID
import org.damascus.data.mongodb.MongoConstants.MONGO_SET_OPERATOR
import java.util.*

class MongoDataSource<T : MongoDocument>(
    mongoDatabase: MongoDatabase,
    collectionName: String,
    documentClass: Class<T>,
    private val dispatcher: CoroutineDispatcher
) {

    private val collection = mongoDatabase.getCollection(collectionName, documentClass)

    suspend fun read(): List<T> = withContext(dispatcher) {
        collection.find().toList()
    }

    suspend fun write(entry: T) = withContext(dispatcher) {
        try {
            collection.insertOne(entry)
        } catch (e: MongoException) {
            throw e
        }
    }

    suspend fun write(entriesList: List<T>) = withContext(dispatcher) {
        try {
            collection.insertMany(entriesList)
        } catch (e: MongoException) {
            throw e
        }
    }

    suspend fun update(id: UUID, updatedData: T) = withContext(dispatcher) {
        collection.findOneAndUpdate(
            Filters.eq(MONGO_ID, id),
            Document(
                MONGO_SET_OPERATOR,
                updatedData.toUpdateDocument()
            )
        ).apply {
            if (this == null) {
                throw MongoDocumentNotFound()
            }
        }
    }

    suspend fun delete(id: UUID) = withContext(dispatcher) {
        collection.findOneAndDelete(Filters.eq(MONGO_ID, id)).apply {
            if (this == null) {
                throw MongoDocumentNotFound()
            }
        }
    }
}