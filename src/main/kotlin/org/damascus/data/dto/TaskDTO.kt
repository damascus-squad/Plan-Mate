package org.damascus.data.dto

import kotlinx.datetime.LocalDateTime
import org.bson.codecs.pojo.annotations.BsonId
import org.damascus.data.mongodb.MongoDocument
import java.util.*

data class TaskDTO(
    @BsonId val id: UUID,
    val projectId: UUID,
    val title: String,
    val description: String,
    var assigneeId: UUID?,
    val stateId: UUID,
    val creationDate: LocalDateTime
) : MongoDocument
