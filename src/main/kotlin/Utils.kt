package org.example

import java.io.InputStream
import kotlin.math.absoluteValue
import kotlin.math.pow

fun getStreamFromResources(fileName: String): InputStream? = object {}.javaClass.getResourceAsStream(fileName)
fun readLinesFromInputStream(inputStream: InputStream?) = inputStream?.bufferedReader()?.readLines()

fun Char.isRomanDigit(): Boolean {
    return when (this) {
        'I', 'V', 'X', 'L', 'C', 'D', 'M' -> true
        else -> false
    }
}
fun String.hash(): Int = foldIndexed(0) { index, acc, char ->
    acc + char.code * 32f.pow(length - index - 1).toInt()
}.absoluteValue