package org.damascus.data.csv

import org.damascus.data.DataSource
import org.damascus.data.csv.CsvDataSource.Companion.HEADER_LINE_COUNT
import org.damascus.data.csv.CsvDataSource.Companion.INDEX_NOT_FOUND
import java.io.File
import java.util.*
import java.util.Map.entry
import kotlin.collections.indexOfFirst

class CsvDataSource<T>(
    filePath: String,
    generateHeader: () -> String,
    private val extractId: (T) -> UUID,
    private val parser: (String) -> T?,
    private val serializer: (T) -> String,
) : DataSource<T> {

    private val file = File(filePath)
    private val header = generateHeader()

    private fun append(item: T) {
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.writeText("$header\n")
        }
        file.appendText(serializer(item) + "\n")
    }

    override fun read(): List<T> {
        val lines = readLinesSkippingHeader()
        return lines.mapNotNull { runCatching { parser(it) }.getOrNull() }
    }

    override fun write(entry: T) {
        append(entry)
    }

    override fun write(entriesList: List<T>) {
        entriesList.forEach { entry -> write(entry) }
    }

    private fun overwriteAll(data: List<T>) {
        file.writer().use { writer ->
            writer.appendLine(header)
            data.forEach { writer.appendLine(serializer(it)) }
        }
    }

    override fun update(id: UUID, updatedData: T) {
        val data = read().toMutableList()
        val index = data.indexOfFirst { extractId(it) == id }
        if (index != INDEX_NOT_FOUND) {
            data[index] = updatedData
            overwriteAll(data)
        } else {
            throw CsvEntryNotFound("Entry for id $id doesn't exist, hence can't be updated")
        }
    }

    override fun delete(id: UUID) {
        val data = read()

        val idExistsOrNull = data.find { extractId(it) == id }
        if (idExistsOrNull == null) {
            throw CsvEntryNotFound("Entry for id $id doesn't exist, hence can't be deleted")
        }

        val updated = data.filter { extractId(it) != id }
        overwriteAll(updated)
    }

    private fun readLinesSkippingHeader(): List<String> {
        if (!file.exists()) throw CsvFileNotFound("File ${file.name} does not exist")
        return file.readLines()
            .drop(HEADER_LINE_COUNT)
            .filter { it.isNotBlank() }
    }

    private companion object {
        const val HEADER_LINE_COUNT = 1
        const val INDEX_NOT_FOUND = -1
    }
}