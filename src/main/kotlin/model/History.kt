package org.damascus.model

import java.util.UUID

data class History(
    val id: UUID = UUID.randomUUID(),
    val projectID: UUID
)
