package data.csv.helpers

import data.csv.CsvParsingException
import data.csv.utils.CsvConstants.COMMA_SEPARATOR
import data.dto.UserDTO
import logic.model.UserRole
import java.util.*

object UserCsvHelper {

    const val USER_FIELD_COUNT = 4

    fun parseUser(line: String): UserDTO {
        val tokens = line.split(COMMA_SEPARATOR)
        if (tokens.size != USER_FIELD_COUNT) throw CsvParsingException("Invalid user line: $line")

        val id = UUID.fromString(tokens[0].trim())
        val hashedPassword = tokens[1].trim()
        val username = tokens[2].trim()
        val userRole = UserRole.fromString(tokens[3].trim())

        return UserDTO(id, hashedPassword, username, userRole)
    }

    fun serializeUser(user: UserDTO): String {
        return listOf(
            user.id.toString(),
            user.hashedPassword,
            user.username,
            user.userRole
        ).joinToString(COMMA_SEPARATOR)
    }

}