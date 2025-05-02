package org.damascus.data.csv

import java.io.FileNotFoundException

class CsvParsingException(message: String) : RuntimeException(message)
class CsvFileNotFound(message: String): FileNotFoundException(message)
class CsvEntryNotFound(message: String): NoSuchElementException(message)