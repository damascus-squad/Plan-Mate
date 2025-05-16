package org.damascus.data.mongodb

import com.mongodb.MongoBulkWriteException
import com.mongodb.MongoException
import com.mongodb.MongoWriteException
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

    suspend fun read(): List<T> {
        val result = withContext(dispatcher) {
            try {
                collection.find().toList()
            } catch (e: MongoException) {
                throw e
            }
        }

        if (result.isEmpty())
            throw CollectionIsEmptyException()

        return result
    }

    suspend fun write(entry: T) = withContext(dispatcher) {
        try {
            collection.insertOne(entry)
        } catch (e: MongoWriteException) {
            throw e
        }
    }


    suspend fun write(entriesList: List<T>) = withContext(dispatcher) {
        try {
            collection.insertMany(entriesList)
        } catch (e: MongoBulkWriteException) {
            throw e
        }
    }

    suspend fun update(id: UUID, updatedData: T) = withContext(dispatcher) {
        try {
            collection.findOneAndUpdate(
                Filters.eq(MONGO_ID, id),
                Document(
                    MONGO_SET_OPERATOR,
                    updatedData.toUpdateDocument()
                )
            )
        } catch (e: MongoWriteException) {
            throw e
        }
    }

    suspend fun delete(id: UUID) = withContext(dispatcher) {
        try {
            collection.findOneAndDelete(Filters.eq(MONGO_ID, id))
        } catch (e: MongoWriteException) {
            throw e
        }
    }
}