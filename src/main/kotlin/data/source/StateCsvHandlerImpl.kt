package data.source

import data.model.State
import org.damascus.domin.repository.CsvHandler

class StateCsvHandlerImpl(
    private val filePath: String = "assets/states.csv",
    private val fileProvider: (String) -> java.io.File = { path -> java.io.File(path) }
) : CsvHandler<State> {

    override fun read(filePath: String): List<State> {
        TODO("Implement reading states from CSV")
    }

    override fun write(filePath: String, data: List<State>) {
        TODO("Implement writing states to CSV")
    }
}
