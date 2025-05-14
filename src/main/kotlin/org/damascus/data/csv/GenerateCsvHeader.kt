package org.damascus.data.csv

import org.damascus.data.csv.utils.CsvConstants

inline fun <reified T> generateCsvHeader(): String {
    return T::class.java.declaredFields.joinToString(CsvConstants.COMMA_SEPARATOR) { it.name }
}
