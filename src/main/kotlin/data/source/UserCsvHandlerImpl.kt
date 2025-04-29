package data.source

import data.model.User
import org.damascus.domin.repository.CsvHandler
import java.io.File

class UserCsvHandlerImpl(
    private val filePath: String = "assets/users.csv",
    private val fileProvider: (String) -> File = { path -> File(path) }
) : CsvHandler<User> {

    override fun read(filePath: String): List<User> {
        TODO()
    }

    override fun write(filePath: String, data: List<User>) {
        TODO()

    }
}
