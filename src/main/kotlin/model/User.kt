import java.util.*

abstract class User(
    val id: UUID,
    val username: String,
    val password: String,
)

class Admin(
    id: UUID,
    username: String,
    password: String
) : User(id = id, username = username, password = password)

class Mate(
    id: UUID,
    username: String,
    password: String
) : User(id = id, username = username, password = password)