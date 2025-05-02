package logic.model

import org.damascus.logic.model.Role
import java.util.UUID

class Mate(
    id: UUID,
    username: String,
    password: String,
    role: Role
) : User(id, username, password, role)