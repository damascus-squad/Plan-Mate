package org.damascus.logic.model

import org.junit.jupiter.params.provider.CsvParsingException

enum class Role {
    ADMIN,
    MATE;

    companion object {
        fun fromString(value: String): Role {
            return when (value.lowercase()) {
                ADMIN.name.lowercase() -> ADMIN
                MATE.name.lowercase() -> MATE
                else -> throw CsvParsingException("Unknown role: $value")
            }
        }
    }
}