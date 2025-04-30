package org.damascus.data.csv

import data.model.*
import java.time.LocalDateTime
import java.util.*

object FileDataParser {

    private const val separator = ","
    private const val listSeparator = ";"

    fun parseUser(line: String): User {
        val tokens = line.split(separator)
        if (tokens.size < 4) throw CsvParsingException("Invalid user line: $line")

        val id = UUID.fromString(tokens[0].trim())
        val username = tokens[1].trim()
        val password = tokens[2].trim()
        val role = tokens[3].trim()

        return when (role.lowercase()) {
            "admin" -> Admin(id, username, password, role)
            "mate" -> Mate(id, username, password, role)
            else -> throw CsvParsingException("Unknown role in user line: $line")
        }
    }

    fun parseProject(line: String): Project {
        val tokens = line.split(separator)
        if (tokens.size < 4) throw CsvParsingException("Invalid project line: $line")

        val id = UUID.fromString(tokens[0].trim())
        val name = tokens[1].trim()
        val assignedMates = tokens[2].trim().split(listSeparator)
            .filter { it.isNotBlank() }
            .map { Mate(UUID.fromString(it), "", "", "mate") }
            .toMutableList()
        val creationDate = LocalDateTime.parse(tokens[3].trim())

        return Project(id, name, assignedMates, creationDate)
    }

    fun parseTask(line: String): Task {
        val tokens = line.split(separator)
        if (tokens.size < 7) throw CsvParsingException("Invalid task line: $line")

        val id = UUID.fromString(tokens[0].trim())
        val projectId = UUID.fromString(tokens[1].trim())
        val title = tokens[2].trim()
        val description = tokens[3].trim()
        val assignee = tokens[4].trim().takeIf { it.isNotBlank() }
            ?.let { Mate(UUID.fromString(it), "", "", "mate") }
        val state = State(UUID.fromString(tokens[5].trim()), "")
        val creationDate = LocalDateTime.parse(tokens[6].trim())

        return Task(id, projectId, title, description, assignee, state, creationDate)
    }

    fun parseState(line: String): State {
        val tokens = line.split(separator)
        if (tokens.size < 2) throw CsvParsingException("Invalid state line: $line")

        val id = UUID.fromString(tokens[0].trim())
        val name = tokens[1].trim()
        return State(id, name)
    }

    fun parseHistory(line: String): History {
        val tokens = line.split(separator)
        if (tokens.size < 8) throw CsvParsingException("Invalid history line: $line")

        return History(
            id = UUID.fromString(tokens[0].trim()),
            projectID = UUID.fromString(tokens[1].trim()),
            entityId = UUID.fromString(tokens[2].trim()),
            entityType = tokens[3].trim(),
            changedBy = UUID.fromString(tokens[4].trim()),
            oldState = tokens[5].trim().ifBlank { null },
            newState = tokens[6].trim().ifBlank { null },
            timestamp = LocalDateTime.parse(tokens[7].trim())
        )
    }

}