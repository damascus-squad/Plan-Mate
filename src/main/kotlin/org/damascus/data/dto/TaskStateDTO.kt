package org.damascus.data.dto

import org.bson.codecs.pojo.annotations.BsonId
import org.damascus.data.mongodb.MongoDocument
import java.util.*

data class TaskStateDTO(
    @BsonId
    val id: UUID,
    val name: String,
    val projectReferencesCount: Int
) : MongoDocument
