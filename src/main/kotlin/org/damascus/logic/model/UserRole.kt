package org.damascus.logic.model

import org.damascus.data.csv.CsvParsingException

enum class UserRole {
    ADMIN,
    MATE;

    companion object {
        fun fromString(value: String): UserRole {
            return when (value.lowercase()) {
                ADMIN.name.lowercase() -> ADMIN
                MATE.name.lowercase() -> MATE
                else -> throw CsvParsingException("Unknown role: $value")
            }
        }
    }
}
