package data.csvDataHelper

import logic.model.Admin
import logic.model.Mate
import logic.model.User
import org.damascus.logic.model.Role
import java.util.UUID


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
