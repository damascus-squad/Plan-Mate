package data.source

import data.model.Project
import org.damascus.domin.repository.CsvHandler

class ProjectCsvHandlerImpl(
    private val filePath: String = "assets/projects.csv",
    private val fileProvider: (String) -> java.io.File = { path -> java.io.File(path) }
) : CsvHandler<Project> {

    override fun read(filePath: String): List<Project> {
        TODO("Implement reading projects from CSV")
    }

    override fun write(filePath: String, data: List<Project>) {
        TODO("Implement writing projects to CSV")
    }
}