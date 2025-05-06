package data.dto

import logic.model.User
import org.damascus.logic.model.UserRole
import java.util.*

data class UserDTO(
    val id: UUID,
    val hashedPassword: String,
    val username: String,
    val userRole: UserRole
) {
    fun toUser(): User {
        return User(
            id = id,
            username = username,
            userRole = userRole
        )
    }
}
