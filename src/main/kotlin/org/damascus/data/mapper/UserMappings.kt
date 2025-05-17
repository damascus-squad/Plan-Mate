package org.damascus.data.mapper

import org.damascus.data.dto.UserDTO
import org.damascus.logic.model.User

fun UserDTO.toModel() = User(
    id = id,
    username = username,
    userRole = userRole,
)