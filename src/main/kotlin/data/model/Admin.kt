package data.model

import java.util.*

class Admin(
    id: UUID,
    username: String,
    password: String,
    role: String
) : User(id, username, password, role)