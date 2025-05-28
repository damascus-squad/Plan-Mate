package org.damascus.data.csv.helpers

import kotlinx.datetime.LocalDateTime
import org.damascus.data.csv.utils.CsvConstants
import java.util.*

internal fun String.toCsvUuid() = UUID.fromString(this.trim())

internal fun String.toCsvInt() = this.trim().toInt()

internal fun String.toCsvDate() = LocalDateTime.parse(this.trim())

internal fun String.toCsvUuidMutableList() = this.trim()
    .split(CsvConstants.LIST_SEPARATOR)
    .filter { it.isNotBlank() }
    .map { UUID.fromString(it) }
    .toMutableList()

