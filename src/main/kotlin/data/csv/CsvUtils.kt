package org.damascus.data.csv

/**
 * Usage of inline with reified here is to avoid type erasure when creating headers for CSVs
 *
 * Code coverage wise, such functions don't play nice with coverage tools, it's only one line,
 * but it shows 1/2 lines covered, please ignore its coverage.
 */

inline fun <reified T> generateCsvHeader(): String {
    return T::class.java.declaredFields.joinToString(",") { it.name }
}
