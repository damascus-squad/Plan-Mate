package org.damascus.logic.repositories

import org.damascus.logic.entities.ActionLog


interface HistoryRepository {
    fun saveLog(actionLog: ActionLog){
    }
    fun getLogByProjectId(projectId: String): List<ActionLog> {
        TODO()
    }
    fun getLogByTaskId(taskId: String): List<ActionLog> {
        TODO()
    }
    fun getAllLogs(): List<ActionLog>{
        TODO()
    }
}