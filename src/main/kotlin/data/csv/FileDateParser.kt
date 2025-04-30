package org.damascus.data.csv

import kotlinx.datetime.LocalDateTime
import logic.model.Admin
import logic.model.History
import logic.model.Mate
import logic.model.Project
import logic.model.State
import logic.model.Task
import logic.model.User
import org.damascus.logic.model.Role
import java.util.*

object FileDataParser {

    const val SEPARATOR = ","
    const val LIST_SEPARATOR = ";"
    const val USER_FIELD_COUNT = 4
    const val PROJECT_FIELD_COUNT = 4
    const val TASK_FIELD_COUNT = 7
    const val STATE_FIELD_COUNT = 2
    const val HISTORY_FIELD_COUNT = 8
    
    fun parseUser(line: String): User {
        val tokens = line.split(SEPARATOR)
        if (tokens.size < USER_FIELD_COUNT) throw CsvParsingException("Invalid user line: $line")

        val id = UUID.fromString(tokens[0].trim())
        val username = tokens[1].trim()
        val password = tokens[2].trim()
        val role = Role.fromString(tokens[3].trim())

        return when (role) {
            Role.ADMIN -> Admin(id, username, password, role)
            Role.MATE -> Mate(id, username, password, role)
        }
    }

    fun parseProject(line: String): Project {
        val tokens = line.split(SEPARATOR)
        if (tokens.size < PROJECT_FIELD_COUNT) throw CsvParsingException("Invalid project line: $line")

        val id = UUID.fromString(tokens[0].trim())
        val name = tokens[1].trim()
        val assignedMates = tokens[2].trim().split(LIST_SEPARATOR)
            .filter { it.isNotBlank() }
            .map { Mate(UUID.fromString(it), "", "", Role.MATE) }
            .toMutableList()
        val creationDate = LocalDateTime.parse(tokens[3].trim())

        return Project(id, name, assignedMates, creationDate)
    }

    fun parseTask(line: String): Task {
        val tokens = line.split(SEPARATOR)
        if (tokens.size < TASK_FIELD_COUNT) throw CsvParsingException("Invalid task line: $line")

        val id = UUID.fromString(tokens[0].trim())
        val projectId = UUID.fromString(tokens[1].trim())
        val title = tokens[2].trim()
        val description = tokens[3].trim()
        val assignee = tokens[4].trim().takeIf { it.isNotBlank() }
            ?.let { Mate(UUID.fromString(it), "", "", Role.MATE) }
        val state = State(UUID.fromString(tokens[5].trim()), "")
        val creationDate = LocalDateTime.parse(tokens[6].trim())

        return Task(id, projectId, title, description, assignee, state, creationDate)
    }

    fun parseState(line: String): State {
        val tokens = line.split(SEPARATOR)
        if (tokens.size < STATE_FIELD_COUNT) throw CsvParsingException("Invalid state line: $line")

        val id = UUID.fromString(tokens[0].trim())
        val name = tokens[1].trim()
        return State(id, name)
    }

    fun parseHistory(line: String): History {
        val tokens = line.split(SEPARATOR)
        if (tokens.size < HISTORY_FIELD_COUNT) throw CsvParsingException("Invalid history line: $line")

        return History(
            id = UUID.fromString(tokens[0].trim()),
            projectId = UUID.fromString(tokens[1].trim()),
            taskId = UUID.fromString(tokens[2].trim()),
            actionType = tokens[3].trim(),
            changedBy = UUID.fromString(tokens[4].trim()),
            oldState = tokens[5].trim().takeIf { it.isNotBlank() }?.let { State(UUID.fromString(it), "") },
            newState = State(UUID.fromString(tokens[6].trim()), ""),
            timestamp = LocalDateTime.parse(tokens[7].trim())
        )
    }

}