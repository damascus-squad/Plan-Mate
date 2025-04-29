package org.damascus.logic

import org.damascus.utils.MD5_HASHING_ALGORITHM
import java.security.MessageDigest

class MD5HashingService : HashingService {

    override fun <T> hashData(data: T): String {
        val md = MessageDigest.getInstance(MD5_HASHING_ALGORITHM)
        val hashedBytes = md.digest(data.toString().toByteArray())
        return bytesToHex(hashedBytes)
    }

    override fun <T> verifyData(inputData: T, storedHash: String): Boolean {
        val inputHash = hashData(inputData)
        return inputHash == storedHash
    }

    private fun bytesToHex(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (b in bytes) {
            sb.append(String.format("%02x", b))
        }
        return sb.toString()
    }
}