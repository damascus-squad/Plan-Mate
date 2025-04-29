package data.csvDataHelper

import data.model.State
import java.util.UUID

fun createState(
    id: UUID = UUID.randomUUID(),
    name: String = "Test State"
) = State(
    id = id,
    name = name
)