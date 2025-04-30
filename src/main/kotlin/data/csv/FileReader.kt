package org.damascus.data.csv

import org.damascus.utils.Constants.HEADER_LINE_COUNT
import java.io.File

class FileReader(private val file: File) {

    fun readLinesSkippingHeader(): List<String> {
        if (!file.exists()) return emptyList()
        return file.readLines()
            .drop(HEADER_LINE_COUNT)
            .filter { it.isNotBlank() }
    }
}