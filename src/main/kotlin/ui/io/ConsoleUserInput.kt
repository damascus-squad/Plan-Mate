package ui.io

import ui.exception.InputException
import ui.util.TerminalColor
import ui.util.withStyle

class ConsoleUserInput : InputReader {
    override fun readString(prompt: String): String {
        print("👉 $prompt: ".withStyle(TerminalColor.Yellow))
        while (true) {
            val input = readln().trim()

            if (input.isEmpty()) {
                println("❌ Input cannot be empty. Please try again.".withStyle(TerminalColor.Red))
                print("👉 $prompt: ".withStyle(TerminalColor.Yellow))
            } else return input
        }
    }

    override fun readInt(prompt: String, min: Int?, max: Int?): Int {
        print("👉 $prompt: ".withStyle(TerminalColor.Yellow))
        while (true) {
            val input = readlnOrNull()?.trim()?.toIntOrNull() ?: throw InputException("Wrong input: ")

            if ((min != null && input < min) || (max != null && input > max)) {
                println("❌ Invalid input. Try again.\n".withStyle(TerminalColor.Red))
            } else return input
        }
    }

    override fun readBoolean(): Boolean {
        while (true) {
            print("👉 Do you like it? (yes/no): ".withStyle(TerminalColor.Yellow))
            when (readlnOrNull()?.trim()?.lowercase()) {
                "yes" -> return true
                "no" -> return false
                else -> println("❌ Invalid input. Please enter 'yes' or 'no'.".withStyle(TerminalColor.Red))
            }
        }
    }

    override fun readDouble(prompt: String): Double {
        println("👉 $prompt: ".withStyle(TerminalColor.Yellow))
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