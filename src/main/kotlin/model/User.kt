package model

import java.util.*

abstract class User(
    val id: UUID = UUID.randomUUID(),
    val username: String,
    val password: String,
)