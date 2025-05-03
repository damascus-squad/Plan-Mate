package org.damascus.presentation

enum class TerminalColor(val code: String) {
    Red("\u001B[31m"),
    Green("\u001B[32m"),
    Yellow("\u001B[33m"),
    Blue("\u001B[34m"),
    Magenta("\u001B[35m"),
    Cyan("\u001B[36m"),
    Reset("\u001B[0m");

    fun wrap(text: String) = "$code$text${Reset.code}"
}

fun String.withStyle(color: TerminalColor) = color.wrap(this)

fun String.center(width: Int): String {
    val padSize = (width - this.length).coerceAtLeast(0)
    val left = padSize / 2
    val right = padSize - left
    return " ".repeat(left) + this + " ".repeat(right)
}

fun String.removeStyle(): String {
    return this.replace(Regex("\u001B\\[[;\\d]*m"), "") // Remove ANSI colors
}

fun printTable(headers: List<String>, meals: List<List<Any>>) {
    val keyCol = "Key"
    val valueCol = "Value"

    val keyWidth = (listOf(keyCol) + headers).maxOf { it.length }
    val valueWidth = maxOf(valueCol.length, meals.flatten().maxOfOrNull { it.toString().length } ?: 0)

    val topBorder = "╔${"═".repeat(keyWidth + 2)}╦${"═".repeat(valueWidth + 2)}╗"
    val midBorder = "╠${"═".repeat(keyWidth + 2)}╬${"═".repeat(valueWidth + 2)}╣"
    val bottomBorder = "╚${"═".repeat(keyWidth + 2)}╩${"═".repeat(valueWidth + 2)}╝"

    println(topBorder.withStyle(TerminalColor.Magenta))
    println("║ ${keyCol.padEnd(keyWidth)} ║ ${valueCol.padEnd(valueWidth)} ║".withStyle(TerminalColor.Cyan))
    println(midBorder.withStyle(TerminalColor.Magenta))

    meals.forEachIndexed { i, row ->
        headers.forEachIndexed { j, header ->
            val valueStr = row.getOrNull(j)?.toString() ?: "-"
            println("║ ${header.padEnd(keyWidth)} ║ ${valueStr.padEnd(valueWidth)} ║".withStyle(TerminalColor.Yellow))
        }
        if (i != meals.lastIndex) {
            println(midBorder.withStyle(TerminalColor.Magenta))
        }
    }

    println(bottomBorder.withStyle(TerminalColor.Magenta))
}
