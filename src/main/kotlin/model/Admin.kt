package model

import java.util.*

class Admin(
    id: UUID,
    username: String,
    password: String
) : User(username = username, password = password)