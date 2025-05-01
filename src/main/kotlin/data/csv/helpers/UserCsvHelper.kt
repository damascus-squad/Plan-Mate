package data.csv.helpers

import logic.model.Admin
import logic.model.Mate
import logic.model.User
import org.damascus.data.csv.CsvParsingException
import org.damascus.logic.model.Role
import org.damascus.utils.Constants.SEPARATOR
import java.util.UUID

object UserCsvHelper {

    const val USER_FIELD_COUNT = 4

    fun parseUser(line: String): User {
        val tokens = line.split(SEPARATOR)
        if (tokens.size != USER_FIELD_COUNT) throw CsvParsingException("Invalid user line: $line")

        val id = UUID.fromString(tokens[0].trim())
        val username = tokens[1].trim()
        val password = tokens[2].trim()
        val role = Role.fromString(tokens[3].trim())

        return when (role) {
            Role.ADMIN -> Admin(id, username, password, role)
            Role.MATE -> Mate(id, username, password, role)
        }
    }

    fun serializeUser(user: User): String {
        return listOf(
            user.id.toString(),
            user.username,
            user.password,
            user.role
        ).joinToString(",")
    }

}