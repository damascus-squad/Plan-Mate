package org.damascus.data.csv

import org.damascus.annotation.KoverIgnore
import org.damascus.data.csv.utils.CsvConstants

@KoverIgnore("Inline function")
inline fun <reified T> generateCsvHeader(): String {
    return T::class.java.declaredFields.joinToString(CsvConstants.COMMA_SEPARATOR) { it.name }
}
