package org.damascus.data.csv.helpers

import kotlinx.datetime.LocalDateTime
import org.damascus.data.csv.CsvParsingException
import org.damascus.data.csv.utils.CsvConstants
import org.damascus.data.dto.TaskDTO
import java.util.*

object TaskCsvHelper {

    const val TASK_FIELD_COUNT = 7

    fun parseTask(line: String): TaskDTO {
        val tokens = line.split(CsvConstants.COMMA_SEPARATOR)
        if (tokens.size != TASK_FIELD_COUNT) throw CsvParsingException("Invalid task line: $line")

        val id = UUID.fromString(tokens[0].trim())
        val projectId = UUID.fromString(tokens[1].trim())
        val title = tokens[2].trim()
        val description = tokens[3].trim()
        val assigneeId: UUID? = runCatching { UUID.fromString(tokens[4].trim()) }.getOrNull()
        val stateId = UUID.fromString(tokens[5].trim())
        val creationDate = LocalDateTime.parse(tokens[6].trim())

        return TaskDTO(id, projectId, title, description, assigneeId, stateId, creationDate)
    }

    fun serializeTask(task: TaskDTO): String {
        val assigneeId = task.assigneeId?.toString()
        return listOf(
            task.id.toString(),
            task.projectId.toString(),
            task.title,
            task.description,
            assigneeId ?: "",
            task.stateId.toString(),
            task.creationDate.toString()
        ).joinToString(CsvConstants.COMMA_SEPARATOR)

    }
}