package org.damascus.logic.model

import kotlinx.datetime.LocalDateTime
import java.util.*

data class Task(
    val id: UUID,
    val projectId: UUID,
    val title: String,
    val description: String,
    var assignee: Mate? = null,
    val state: State,
    val creationDate: LocalDateTime
)
