package org.damascus.data.csv

import java.io.File

class FileReader(private val file: File) {
    fun readLinesSkippingHeader(): List<String> {
        if (!file.exists()) return emptyList()
        return file.readLines()
            .drop(1)
            .filter { it.isNotBlank() }
    }
}