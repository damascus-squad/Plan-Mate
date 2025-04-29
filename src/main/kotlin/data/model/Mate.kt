package data.model

import java.util.UUID

class Mate(
    id: UUID = UUID.randomUUID(),
    username: String,
    password: String,
    role: String
) : User(id, username, password, role)