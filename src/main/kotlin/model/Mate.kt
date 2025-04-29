package model

import java.util.UUID

class Mate(
    id: UUID,
    username: String,
    password: String
) : User(id = id, username = username, password = password)