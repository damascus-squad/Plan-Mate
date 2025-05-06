package logic.model

import org.damascus.logic.model.UserRole
import java.util.*

data class User(
    val id: UUID,
    val username: String,
    val userRole: UserRole
)