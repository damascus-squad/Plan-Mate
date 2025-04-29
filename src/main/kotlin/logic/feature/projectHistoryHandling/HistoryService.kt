package org.damascus.logic.feature.projectHistoryHandling

import org.damascus.logic.entities.ActionLog
import org.damascus.logic.repositories.HistoryRepository

class HistoryService(historyRepository: HistoryRepository) {
    fun getAllLogs() : List<ActionLog>{
        TODO()
    }
    fun saveLog(actionLog: ActionLog){
    }

}