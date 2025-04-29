package data.source

import data.model.Task
import org.damascus.domin.repository.CsvHandler

class TaskCsvHandlerImpl(
    private val filePath: String = "assets/tasks.csv",
    private val fileProvider: (String) -> java.io.File = { path -> java.io.File(path) }
) : CsvHandler<Task> {

    override fun read(filePath: String): List<Task> {
        TODO("Implement reading tasks from CSV")
    }

    override fun write(filePath: String, data: List<Task>) {
        TODO("Implement writing tasks to CSV")
    }
}