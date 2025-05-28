package org.damascus.data.csv.helpers

import org.damascus.data.csv.CsvParsingException
import org.damascus.data.csv.utils.CsvConstants
import org.damascus.data.dto.UserDTO
import org.damascus.logic.model.UserRole
import org.damascus.logic.model.UserRole.ADMIN
import org.damascus.logic.model.UserRole.MATE

object UserCsvHelper {

    const val USER_FIELD_COUNT = 4

    private enum class FieldPosition { ID, USERNAME, HASHED_PASSWORD, USER_ROLE }

    fun parseUser(line: String): UserDTO {
        val tokens = line.split(CsvConstants.COMMA_SEPARATOR)
        if (tokens.size != USER_FIELD_COUNT) throw CsvParsingException("Invalid user line: $line")

        return UserDTO(
            id = tokens[FieldPosition.ID.ordinal].toCsvUuid(),
            username = tokens[FieldPosition.USERNAME.ordinal].trim(),
            hashedPassword = tokens[FieldPosition.HASHED_PASSWORD.ordinal].trim(),
            userRole = UserRole.entries[tokens[FieldPosition.USER_ROLE.ordinal].toCsvInt()]
        )
    }

    fun serializeUser(user: UserDTO): String {
        val fields = Array(USER_FIELD_COUNT) { "" }

        fields[FieldPosition.ID.ordinal] = user.id.toString()
        fields[FieldPosition.USERNAME.ordinal] = user.username
        fields[FieldPosition.HASHED_PASSWORD.ordinal] = user.hashedPassword
        fields[FieldPosition.USER_ROLE.ordinal] = user.userRole.ordinal.toString()

        return fields.joinToString(CsvConstants.COMMA_SEPARATOR)
    }

}