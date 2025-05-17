package org.damascus.data.dto

import kotlinx.datetime.LocalDateTime
import org.bson.codecs.pojo.annotations.BsonId
import org.damascus.data.mongodb.MongoDocument
import java.util.*

data class ProjectDTO(
    @BsonId
    val id: UUID,
    val name: String,
    val assignedMatesIds: MutableList<UUID>,
    val allowedStatesIds: MutableList<UUID>,
    val creationDate: LocalDateTime
) : MongoDocument
