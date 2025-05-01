package org.damascus.data.csv

import java.io.File

class CsvFileReader(private val file: File) {

    fun readLinesSkippingHeader(): List<String> {
        if (!file.exists()) throw CsvFileNotFound("File ${file.name} does not exist")
        return file.readLines()
            .drop(HEADER_LINE_COUNT)
            .filter { it.isNotBlank() }
    }

    private companion object {
        const val HEADER_LINE_COUNT = 1
    }
}