package data.csvDataHelper

import data.source.CsvHandlerImpl
import logic.model.State
import org.damascus.data.csv.FileDataParser
import org.damascus.data.csv.FileDataSerializer
import java.util.*

object CreateStateHelper {
    fun createState(
        id: UUID = UUID.randomUUID(),
        name: String = "Test State"
    ) = State(
        id = id,
        name = name
    )

    const val FILE_PATH_STATE = "test_assets/states.csv"

    fun buildHandlerState(): CsvHandlerImpl<State> {
        return CsvHandlerImpl(
            filePath = FILE_PATH_STATE,
            header = "id,name",
            idSelector = { it.id.toString() },
            parser = FileDataParser::parseState,
            serializer = FileDataSerializer::serializeState
        )
    }
}