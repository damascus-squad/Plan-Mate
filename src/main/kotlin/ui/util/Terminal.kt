package ui.util

import logic.model.Project

enum class TerminalColor(private val code: String) {
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

fun List<Project>.printProjectTable() {
    val headers = listOf("No", "Name", "ID", "Mates Count", "Created Date")
    val rows = this.mapIndexed { index, project ->
        listOf(
            (index + 1).toString(),
            project.name,
            project.id.toString().take(8),
            project.assignedMatesIds.size.toString(),
            project.creationDate.toString()
        )
    }
    printTable(headers, rows)
}

//fun TransactionReport.printColoredTable() {
//    val headers = listOf(
//        "Income",
//        "Expense",
//        "Balance"
//    )
//
//    val data = listOf(
//        income,
//        expenses,
//        getBalance()
//    )
//    printTable(headers, listOf(data))
//    categorySummaries.printColoredTable()
//}

//fun Map<Category, CategorySummary>.printColoredTable() {
//    val headers = listOf(
//        "Category",
//        "Amount",
//        "Transactions Count"
//    )
//
//    val data = map {
//        listOf(
//            it.key.name,
//            it.value.amount,
//            it.value.transactionsCount
//        )
//    }
//    printTable(headers, data)
//}

fun enableWindowsAnsi() {
    if (System.getProperty("os.name").contains("Windows"))
        System.setProperty("jansi.passthrough", "true")
}

//fun printTable(headers: List<String>, data: List<List<Any>>) {
//    val colWidths = headers.mapIndexed { i, header ->
//        maxOf(header.length, data.maxOfOrNull { row -> row[i].toString().length } ?: 0)
//    }
//
//    val lineLength = (colWidths.sum() + colWidths.size * 3) - 1
//    val footer = " " + "—".repeat(lineLength)
//    println(footer)
//
//    fun printRow(items: List<Any>) {
//        val color = when {
//            TransactionType.EXPENSE in items -> TerminalColor.Red
//            TransactionType.INCOME in items -> TerminalColor.Green
//            else -> TerminalColor.entries.filter {
//                it !in listOf(TerminalColor.Red, TerminalColor.Green)
//            }.random()
//        }
//
//        items.forEachIndexed { i, item ->
//            print("| ${item.toString().padEnd(colWidths[i])} ".withStyle(color))
//        }
//        println("|")
//    }
//
//    printRow(headers)
//    println("|${colWidths.joinToString("|") { "-".repeat(it + 2) }}|")
//    data.forEach { printRow(it) }
//    println(footer)
//}


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
