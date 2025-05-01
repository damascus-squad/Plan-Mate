package org.damascus.logic.model

import java.util.*

abstract class User(
    val id: UUID,
    val username: String,
    val password: String,
)