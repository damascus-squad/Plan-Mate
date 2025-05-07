package ui.io

interface InputReader {
    fun readString(prompt: String): String
    fun readInt(prompt: String, min: Int? = null, max: Int? = null): Int
    fun readBoolean(): Boolean
    fun readDouble(prompt: String): Double
}