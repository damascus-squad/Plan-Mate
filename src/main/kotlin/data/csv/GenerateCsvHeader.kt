package data.csv

import data.csv.utils.CsvConstants.COMMA_SEPARATOR

inline fun <reified T> generateCsvHeader(): String {
    return T::class.java.declaredFields.joinToString(COMMA_SEPARATOR) { it.name }
}
