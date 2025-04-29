package data.model

import java.util.*

abstract class User(
    val id: UUID = UUID.randomUUID(),
    val username: String,
    val password: String,
    val role: String
)