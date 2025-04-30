package logic.model

import org.damascus.logic.model.Role
import java.util.*

abstract class User(
    val id: UUID,
    val username: String,
    val password: String,
    val role: Role
)