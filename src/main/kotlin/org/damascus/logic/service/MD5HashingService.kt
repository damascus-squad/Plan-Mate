package org.damascus.logic.service

import java.security.MessageDigest

class MD5HashingService : HashingService {

    override fun <T> hashData(data: T): String {
        val messageDigest = MessageDigest.getInstance(MD5_HASHING_ALGORITHM)
        val hashedBytes = messageDigest.digest(data.toString().toByteArray())
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

    private companion object {
        const val MD5_HASHING_ALGORITHM = "MD5"
    }
}