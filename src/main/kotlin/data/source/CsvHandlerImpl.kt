package data.source

import org.damascus.data.csv.CsvParsingException
import org.damascus.data.csv.FileReader
import org.damascus.data.csv.CsvHandler
import java.io.File

class CsvHandlerImpl<T>(
    filePath: String,
    private val header: String,
    private val idSelector: (T) -> String,
    private val parser: (String) -> T?,
    private val serializer: (T) -> String,
) : CsvHandler<T> {

    private val file = File(filePath)

    init {
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.writeText("$header\n")
        }
    }

    override fun read(): List<T> {
        val lines = FileReader(file).readLinesSkippingHeader()
        return lines.mapNotNull {
            try {
                parser(it)
            } catch (e: CsvParsingException) {
                println("Skipping line due to parse error: ${e.message}")
                null
            }
        }
    }

    override fun write(data: List<T>) {
        file.writer().use { writer ->
            writer.appendLine(header)
            data.forEach { writer.appendLine(serializer(it)) }
        }
    }

    override fun update(id: String, updatedData: T) {
        val data = read().toMutableList()
        val index = data.indexOfFirst { idSelector(it) == id }
        if (index != INDEX_NOT_FOUND) {
            data[index] = updatedData
            write(data)
        }
    }

    override fun delete(id: String) {
        val updated = read().filter { idSelector(it) != id }
        write(updated)
    }


    companion object {
        private const val INDEX_NOT_FOUND = -1
    }
}
