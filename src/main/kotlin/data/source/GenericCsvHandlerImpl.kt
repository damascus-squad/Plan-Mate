package data.source

import org.damascus.data.csv.CsvParsingException
import org.damascus.data.csv.FileReader
import org.damascus.logic.domin.repository.CsvHandler
import java.io.File

class GenericCsvHandlerImpl<T>(
    private val filePath: String,
    private val header: String,
    private val idSelector: (T) -> String,
    private val parser: (String) -> T?,
    private val serializer: (T) -> String,
    private val fileProvider: (String) -> File = { File(it) }
) : CsvHandler<T> {

    init {
        val file = fileProvider(filePath)
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.writeText("$header\n")
        }
    }

    override fun read(filePath: String): List<T> {
        val lines = FileReader(fileProvider(filePath)).readLinesSkippingHeader()
        return lines.mapNotNull { line ->
            try {
                parser(line)
            } catch (e: CsvParsingException) {
                println("Skipping line due to parse error: ${e.message}")
                null
            }
        }
    }

    override fun write(filePath: String, data: List<T>) {
        val file = fileProvider(filePath)
        file.writer().use { writer ->
            writer.appendLine(header)
            data.forEach { writer.appendLine(serializer(it)) }
        }
    }

    override fun update(filePath: String, id: String, updatedData: T) {
        val data = read(filePath).toMutableList()
        val index = data.indexOfFirst { idSelector(it) == id }
        if (index != -1) {
            data[index] = updatedData
            write(filePath, data)
        }
    }

    override fun delete(filePath: String, id: String) {
        val updated = read(filePath).filter { idSelector(it) != id }
        write(filePath, updated)
    }
}
