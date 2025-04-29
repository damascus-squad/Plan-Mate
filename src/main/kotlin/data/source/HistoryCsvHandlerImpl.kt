package data.source

import data.model.History
import org.damascus.domin.repository.CsvHandler

class HistoryCsvHandlerImpl(
    private val filePath: String = "assets/history.csv",
    private val fileProvider: (String) -> java.io.File = { path -> java.io.File(path) }
) : CsvHandler<History> {

    override fun read(filePath: String): List<History> {
        TODO("Implement reading history records from CSV")
    }

    override fun write(filePath: String, data: List<History>) {
        TODO("Implement writing history records to CSV")
    }
}