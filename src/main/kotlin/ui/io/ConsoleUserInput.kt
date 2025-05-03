package org.damascus.ui.io

import org.damascus.ui.exception.InputException
import org.damascus.ui.util.TerminalColor
import org.damascus.ui.util.withStyle


class ConsoleUserInput : InputReader {
    override fun readString(prompt: String): String {
        print("$prompt ".withStyle(TerminalColor.Blue))
        while (true) {
            val input = readln().trim()

            if (input.isEmpty()) {
                println("❌ Input cannot be empty. Please try again.".withStyle(TerminalColor.Red))
                print("$prompt ".withStyle(TerminalColor.Blue))
            } else return input
        }
    }

    override fun readInt(prompt: String, min: Int?, max: Int?): Int {
        print("$prompt ".withStyle(TerminalColor.Blue))
        while (true) {
            val input = readlnOrNull()?.trim()?.toIntOrNull() ?: throw InputException("Wrong input: ")

            if ((min != null && input < min) || (max != null && input > max)) {
                println("❌ Invalid input. Try again.\n".withStyle(TerminalColor.Red))
            } else return input
        }
    }

    override fun readBoolean(): Boolean {
        while (true) {
            print("Do you like it? (y/n): ".withStyle(TerminalColor.Magenta))
            when (readlnOrNull()?.trim()?.lowercase()) {
                "y" -> return true
                "n" -> return false
                else -> println("❌ Invalid input. Please enter 'y' or 'n'.".withStyle(TerminalColor.Red))
            }
        }
    }

    override fun readDouble(prompt: String): Double {
        println(prompt.withStyle(TerminalColor.Blue))
        while (true) {
            val input = readlnOrNull()?.toDoubleOrNull()

            if (input == null) {
                println("❌ Invalid number. Please try again.".withStyle(TerminalColor.Red))
                println(prompt.withStyle(TerminalColor.Blue))
            } else {
                return input
            }
        }
    }
}