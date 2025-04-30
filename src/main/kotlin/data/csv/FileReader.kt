package org.damascus.data.csv

import java.io.File

class FileReader(private val file: File) {

    fun readLinesSkippingHeader(): List<String> {
        if (!file.exists()) return emptyList()
        return file.readLines()
            .drop(HEADER_LINE_COUNT)
            .filter { it.isNotBlank() }
    }

    companion object {
        private const val HEADER_LINE_COUNT = 1
    }
}