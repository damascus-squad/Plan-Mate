package org.damascus.data.dto

import kotlinx.datetime.LocalDateTime
import org.bson.codecs.pojo.annotations.BsonId
import org.damascus.data.mongodb.MongoDocument
import org.damascus.logic.model.ActionType
import java.util.*

data class HistoryLogDTO(
    @BsonId
    val id: UUID,
    val projectId: UUID,
    val taskId: UUID,
    val actionType: ActionType,
    val userId: UUID,
    val currentState: String?,
    val newState: String?,
    val actionDate: LocalDateTime,
) : MongoDocument