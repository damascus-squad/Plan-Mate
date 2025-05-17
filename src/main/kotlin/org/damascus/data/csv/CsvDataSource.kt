package org.damascus.data.csv

import org.damascus.logic.repo.DataSource
import java.io.File
import java.util.*

class CsvDataSource<T>(
    filePath: String,
    generateHeader: () -> String,
    private val extractId: (T) -> UUID,
    private val parser: (String) -> T?,
    private val serializer: (T) -> String,
) : DataSource<T> {

    private val file = File(filePath)
    private val header = generateHeader()

    override suspend fun read(): List<T> {
        val lines = try {
            readLinesSkippingHeader()
        } catch (_: CsvFileNotFound) {
            return emptyList()
        }

        return lines.mapNotNull { runCatching { parser(it) }.getOrNull() }
    }

    override suspend fun write(entry: T) {
        createCsvFileIfNotFound()
        appendEntry(entry)
    }

    override suspend fun write(entriesList: List<T>) {
        createCsvFileIfNotFound()
        entriesList.forEach { entry -> write(entry) }
    }

    override suspend fun update(id: UUID, updatedData: T) {
        val data = read().toMutableList()
        val index = data.indexOfFirst { extractId(it) == id }
        if (index != INDEX_NOT_FOUND) {
            data[index] = updatedData
            overwriteAll(data)
        } else {
            throw CsvEntryNotFound("Entry for id $id doesn't exist, hence can't be updated")
        }
    }

    override suspend fun delete(id: UUID) {
        val data = read()

        val entryNotFound = data.find { extractId(it) == id } == null
        if (entryNotFound) {
            throw CsvEntryNotFound("Entry for id $id doesn't exist, hence can't be deleted")
        }

        val updated = data.filter { extractId(it) != id }

        println("updated")
        println(updated)

        overwriteAll(updated)
    }

    private fun csvFileFound() = file.exists()
    private fun csvFileNotFound() = file.exists().not()

    private fun createCsvFileIfNotFound() {
        if (csvFileFound()) return

        file.parentFile.mkdirs()
        file.writeText("$header\n")
    }

    private fun appendEntry(item: T) {
        file.appendText(serializer(item) + "\n")
    }

    private fun overwriteAll(data: List<T>) {
        file.writer().use { writer ->
            writer.appendLine(header)
            data.forEach { writer.appendLine(serializer(it)) }
        }
    }

    private fun readLinesSkippingHeader(): List<String> {
        if (csvFileNotFound()) throw CsvFileNotFound("File ${file.name} does not exist")
        return file.readLines()
            .drop(HEADER_LINE_COUNT)
            .filter { it.isNotBlank() }
    }

    private companion object {
        const val HEADER_LINE_COUNT = 1
        const val INDEX_NOT_FOUND = -1
    }
}