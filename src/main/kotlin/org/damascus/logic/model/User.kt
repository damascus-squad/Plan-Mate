package org.damascus.logic.model

import java.util.*

data class User(
    val id: UUID,
    val username: String,
    val userRole: UserRole
)