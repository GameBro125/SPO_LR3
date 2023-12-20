package org.example.syntax_analyzer

class BracketAnalysisException(message: String, val position: Int) : Exception(message)
class BracketsAnalysis(inputString: String) {
    private val openBrackets = setOf('(', '[', '{')
    private val closeBrackets = setOf(')', ']', '}')

    init {
        try {
            analyzeBrackets(inputString)
            println("Лексический анализ успешен")
        } catch (e: BracketAnalysisException) {
            println("Ошибка: ${e.message} на позиции ${e.position}")
        }
    }

    fun analyzeBrackets(input: String): Boolean {
        val stack = mutableListOf<Pair<Char, Int>>()

        for ((index, char) in input.withIndex()) {
            when {
                openBrackets.contains(char) -> stack.add(char to index)
                closeBrackets.contains(char) -> {
                    if (stack.isEmpty() || !isMatchingPair(stack.removeAt(stack.size - 1).first, char)) {
                        throw BracketAnalysisException("Несбалансированные скобки", index)
                        return false
                    }
                }
            }
        }

        if (stack.isNotEmpty()) {
            val position = stack.last().second
            throw BracketAnalysisException("Несбалансированные скобки", position)

        }
        return false
    }

    private fun isMatchingPair(openBracket: Char, closeBracket: Char): Boolean {
        return (openBracket == '(' && closeBracket == ')') ||
                (openBracket == '[' && closeBracket == ']') ||
                (openBracket == '{' && closeBracket == '}')
    }
}
