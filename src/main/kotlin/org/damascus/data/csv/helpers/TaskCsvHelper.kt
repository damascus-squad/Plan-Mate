package org.damascus.data.csv.helpers

import org.damascus.data.csv.CsvParsingException
import org.damascus.data.csv.utils.CsvConstants
import org.damascus.data.dto.TaskDTO

object TaskCsvHelper {

    const val TASK_FIELD_COUNT = 7

    private enum class FieldPosition {
        ID,
        PROJECT_ID,
        TITLE,
        DESCRIPTION,
        ASSIGNEE_ID,
        STATE_ID,
        CREATION_DATE
    }

    fun parseTask(line: String): TaskDTO {
        val tokens = line.split(CsvConstants.COMMA_SEPARATOR)
        if (tokens.size != TASK_FIELD_COUNT) throw CsvParsingException("Invalid task line: $line")

        return TaskDTO(
            id = tokens[FieldPosition.ID.ordinal].toCsvUuid(),
            projectId = tokens[FieldPosition.PROJECT_ID.ordinal].toCsvUuid(),
            title = tokens[FieldPosition.TITLE.ordinal].trim(),
            description = tokens[FieldPosition.DESCRIPTION.ordinal].trim(),
            assigneeId = runCatching { tokens[FieldPosition.ASSIGNEE_ID.ordinal].toCsvUuid() }.getOrNull(),
            stateId = tokens[FieldPosition.STATE_ID.ordinal].toCsvUuid(),
            creationDate = tokens[FieldPosition.CREATION_DATE.ordinal].toCsvDate()
        )
    }

    fun serializeTask(task: TaskDTO): String {
        val fields = Array(TASK_FIELD_COUNT) { "" }

        fields[FieldPosition.ID.ordinal] = task.id.toString()
        fields[FieldPosition.PROJECT_ID.ordinal] = task.projectId.toString()
        fields[FieldPosition.TITLE.ordinal] = task.title
        fields[FieldPosition.DESCRIPTION.ordinal] = task.description
        fields[FieldPosition.ASSIGNEE_ID.ordinal] = task.assigneeId?.toString() ?: ""
        fields[FieldPosition.STATE_ID.ordinal] = task.stateId.toString()
        fields[FieldPosition.CREATION_DATE.ordinal] = task.creationDate.toString()

        return fields.joinToString(CsvConstants.COMMA_SEPARATOR)

    }
}