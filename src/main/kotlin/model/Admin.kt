package model

import java.util.*

class Admin(
    id: UUID,
    username: String,
    password: String
) : User(id = id, username = username, password = password)