package org.damascus.data.csv.helpers

import org.damascus.data.csv.CsvParsingException
import org.damascus.data.dto.UserDTO
import org.damascus.logic.model.UserRole
import org.damascus.data.csv.utils.CsvConstants
import java.util.*

object UserCsvHelper {

    const val USER_FIELD_COUNT = 4

    fun parseUser(line: String): UserDTO {
        val tokens = line.split(CsvConstants.COMMA_SEPARATOR)
        if (tokens.size != USER_FIELD_COUNT) throw CsvParsingException("Invalid user line: $line")

        val id = UUID.fromString(tokens[0].trim())
        val hashedPassword = tokens[1].trim()
        val username = tokens[2].trim()
        val userRole = UserRole.Companion.fromString(tokens[3].trim())

        return UserDTO(id, hashedPassword, username, userRole)
    }

    fun serializeUser(user: UserDTO): String {
        return listOf(
            user.id.toString(),
            user.hashedPassword,
            user.username,
            user.userRole
        ).joinToString(CsvConstants.COMMA_SEPARATOR)
    }

}