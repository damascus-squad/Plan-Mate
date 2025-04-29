package data.model

import java.util.*

class Admin(
    id: UUID = UUID.randomUUID(),
    username: String,
    password: String,
    role: String
) : User(id, username, password, role)