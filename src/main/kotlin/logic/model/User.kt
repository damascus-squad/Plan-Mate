package logic.model

import org.damascus.logic.model.Role
import java.util.*

abstract class User(
    val id: UUID,
    val username: String,
    val password: String,
    val role: Role
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is User) return false

        return id == other.id &&
                username == other.username &&
                password == other.password &&
                role == other.role
    }

    override fun hashCode(): Int {
        return listOf(id, username, password, role).hashCode()
    }

    override fun toString(): String {
        return "Mate(id=$id, username=$username, password=$password, role=$role)"
    }
}