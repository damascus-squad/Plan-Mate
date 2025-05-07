package logic.model

import java.util.*

data class TaskState(
    val id: UUID,
    val name: String,
    val projectReferences: Int
)
