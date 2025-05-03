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
    return this.replace(Regex("\u001B\\[[;\\d]*m"), "")
}

fun printTable(headers: List<String>, rows: List<List<Any>>, color: TerminalColor = TerminalColor.Yellow) {
    val colWidths = headers.indices.map { i ->
        (listOf(headers[i]) + rows.mapNotNull { it.getOrNull(i)?.toString() }).maxOf { it.length }
    }

    fun drawLine(left: String, mid: String, right: String): String {
        return left + colWidths.joinToString(mid) { "═".repeat(it + 2) } + right
    }


    val topBorder = drawLine("╔", "╦", "╗")
    val midBorder = drawLine("╠", "╬", "╣")
    val bottomBorder = drawLine("╚", "╩", "╝")


    println(topBorder.withStyle(TerminalColor.Magenta))

    val headerRow = headers.mapIndexed { i, h -> " ${h.padEnd(colWidths[i])} " }
    println("║" + headerRow.joinToString("║") + "║".withStyle(TerminalColor.Cyan))

    println(midBorder.withStyle(TerminalColor.Magenta))

    rows.forEach { row ->
        val dataRow = headers.indices.map { i ->
            val cell = row.getOrNull(i)?.toString() ?: "-"
            " ${cell.padEnd(colWidths[i])} "
        }
        println("║" + dataRow.joinToString("║") + "║".withStyle(color))
    }

    println(bottomBorder.withStyle(TerminalColor.Magenta))
}
