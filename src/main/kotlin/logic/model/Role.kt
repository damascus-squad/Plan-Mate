package org.damascus.logic.model

import org.damascus.data.csv.CsvParsingException

enum class Role {
    ADMIN,
    MATE;

    companion object {
        fun fromString(value: String): Role {
            return when (value.lowercase()) {
                "admin" -> ADMIN
                "mate" -> MATE
                else -> throw CsvParsingException("Unknown role: $value")
            }
        }
    }
}
