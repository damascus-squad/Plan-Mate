package org.damascus.ui.util

    import kotlinx.datetime.LocalDateTime
    import kotlinx.datetime.toJavaLocalDateTime
    import org.damascus.logic.model.Project
    import org.damascus.logic.model.Task
    import org.damascus.logic.model.User
    import java.time.format.DateTimeFormatter

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
        val headers = listOf("No", "Name", "Mates Count", "Created Date")
        val rows = this.mapIndexed { index, project ->
            listOf(
                (index + 1).toString(),
                project.name,
                project.assignedMatesIds.size.toString(),
                formatDateTime(project.creationDate)
            )
        }
        printTable(headers, rows)
    }

    fun Project.printProjectDetails() {
        val headers = listOf("Name", "Mates Count", "Created Date")
        val rows = listOf(
            name,
            assignedMatesIds.size.toString(),
            formatDateTime(creationDate)
        )
        printTable(headers, listOf(rows))
    }

    fun List<User>.printMateTable() {
        val headers = listOf("No", "Username")
        val rows = this.mapIndexed { index, user ->
            listOf(
                (index + 1).toString(),
                user.username,
            )
        }
        printTable(headers, rows)
    }

    fun Task.printTaskDetails(assignee: String, state:String) {
        val headers = listOf("Title", "Description", "Assigned to", "State","Created Date")
        val rows = listOf(
            title,
            description,
            assignee,
            state,
            formatDateTime(creationDate)
        )
        printTable(headers, listOf(rows))
    }

    fun enableWindowsAnsi() {
        if (System.getProperty("os.name").contains("Windows"))
            System.setProperty("jansi.passthrough", "true")
    }

    fun printTable(headers: List<String>, rows: List<List<Any>>, color: TerminalColor = TerminalColor.Reset) {
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
        println("║" + headerRow.joinToString("║") + "║".withStyle(TerminalColor.Reset))

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

    fun formatDateTime(dateTime: LocalDateTime, pattern: String = "yyyy-MM-dd HH:mm"): String {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return dateTime.toJavaLocalDateTime().format(formatter)
    }
