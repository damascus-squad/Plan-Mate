package org.damascus.data.mongodb

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.github.cdimascio.dotenv.Dotenv
import org.bson.UuidRepresentation
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.configuration.CodecRegistry
import org.bson.codecs.pojo.PojoCodecProvider
import org.damascus.annotation.KoverIgnore
import org.koin.core.annotation.Single
import java.util.*

@KoverIgnore("Mongo Connection Handler")
object MongoConnector {
    private fun getConnectionString(): String {
        val dotenv = Dotenv.load()
        val mongoUsername = dotenv["MONGO_USERNAME"] ?: throw CredentialsNotFound("MONGO_USERNAME not found in .env")
        val mongoPassword = dotenv["MONGO_PASSWORD"] ?: throw CredentialsNotFound("MONGO_PASSWORD not found in .env")

        return "mongodb+srv://$mongoUsername:$mongoPassword@cluster0.cshlend.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0"
    }

    private val pojoCodecProvider = PojoCodecProvider.builder()
        .automatic(true)
        .register(UUID::class.java)
        .build()

    private val codecRegistry: CodecRegistry = CodecRegistries.fromRegistries(
        MongoClientSettings.getDefaultCodecRegistry(),
        CodecRegistries.fromCodecs(KotlinLocalDateTimeCodec()),
        CodecRegistries.fromProviders(pojoCodecProvider)
    )

    fun getClientSettings(
        connectionString: String = getConnectionString()
    ): MongoClientSettings = MongoClientSettings.builder()
        .applyConnectionString(ConnectionString(connectionString))
        .uuidRepresentation(UuidRepresentation.STANDARD)
        .codecRegistry(codecRegistry)
        .build()

    private val client by lazy {
        MongoClient.create(getClientSettings())
    }

    fun getDatabase(databaseName: String): MongoDatabase {
        return client.getDatabase(databaseName)
    }

    fun close() {
        try {
            client.close()
        } catch (e: Exception) {
            throw e
        }
    }
}