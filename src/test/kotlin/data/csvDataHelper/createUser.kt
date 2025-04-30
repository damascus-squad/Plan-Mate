package data.csvDataHelper

import data.source.CsvHandlerImpl
import logic.model.Admin
import logic.model.Mate
import logic.model.User
import org.damascus.data.csv.FileDataParser
import org.damascus.data.csv.FileDataSerializer
import org.damascus.logic.model.Role
import java.util.UUID


object CreateUserHelper{

    fun createUser(
        id: UUID = UUID.randomUUID(),
        username: String = "defaultUser",
        password: String = "defaultPass",
        role: String = "mate"
    ): User {
        return when (role.lowercase()) {
            "admin" -> Admin(id = id, username = username, password = password, role = Role.ADMIN)
            else -> Mate(id = id, username = username, password = password, role = Role.MATE)
        }
    }

    const val FILE_PATH = "test_assets/users.csv"

    fun buildHandlerUser(): CsvHandlerImpl<User> {
        return CsvHandlerImpl(
            filePath = FILE_PATH,
            header = "id,username,password,role",
            idSelector = { it.id.toString() },
            parser = FileDataParser::parseUser,
            serializer = FileDataSerializer::serializeUser
        )
    }
}