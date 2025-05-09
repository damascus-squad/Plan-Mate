package org.damascus.ui.views.admin

import logic.exception.NoMatesAvailableException
import org.damascus.logic.usecase.auth.GetAllMatesUseCase
import ui.io.InputReader
import ui.util.printTable
import java.util.*
import kotlin.system.exitProcess

class SelectMateUi (
    private val getAllMatesUseCase: GetAllMatesUseCase,
    private val inputReader: InputReader
){

    operator fun invoke(): UUID {
        val availableMates = try {
            getAllMatesUseCase()
        } catch (e: NoMatesAvailableException) {
            println("❌ ${e.message}")
            exitProcess(1)
        }

        println("\n👥 Available Mates:")
        val headers = listOf("ID", "Name")
        val rows = availableMates.mapIndexed { index, mate ->
            listOf((index + 1).toString(), mate.username)
        }
        printTable(headers, rows)

        val selectedIndex = inputReader.readInt(
            prompt = "Enter the number of the mate to assign: ",
            min = 1,
            max = availableMates.size
        )

        return availableMates[selectedIndex - 1].id
    }
}