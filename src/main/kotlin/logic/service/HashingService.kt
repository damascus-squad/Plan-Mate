package logic.service

interface HashingService {
    fun <T> hashData(data: T): String
    fun <T> verifyData(inputData: T, storedHash: String): Boolean
}