package data.csvDataHelper

import data.model.Admin
import data.model.Mate
import data.model.User
import java.util.UUID


fun createUser(
    id: UUID = UUID.randomUUID(),
    username: String = "defaultUser",
    password: String = "defaultPass",
    role: String = "mate"
): User {
    return when (role.lowercase()) {
        "admin" -> Admin(id = id, username = username, password = password, role = "admin")
        else -> Mate(id = id, username = username, password = password, role = "mate")
    }
}
