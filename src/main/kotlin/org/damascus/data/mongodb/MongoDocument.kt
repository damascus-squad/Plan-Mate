package org.damascus.data.mongodb

import org.bson.Document
import org.bson.codecs.pojo.annotations.BsonId
import org.damascus.data.mongodb.MongoConstants.MONGO_ID
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

interface MongoDocument {
    fun toDocument(): Document {
        val doc = Document()
        this::class.memberProperties.forEach { prop ->
            val name = if (prop.findAnnotation<BsonId>() != null) MONGO_ID else prop.name
            doc[name] = prop.call(this)
        }
        return doc
    }

    fun toUpdateDocument(): Document {
        return toDocument().apply { remove(MONGO_ID) }
    }
}