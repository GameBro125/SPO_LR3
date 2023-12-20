package org.example

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import cafe.adriel.bonsai.core.Bonsai
import cafe.adriel.bonsai.core.node.Branch
import cafe.adriel.bonsai.core.node.Leaf
import cafe.adriel.bonsai.core.tree.Tree
import cafe.adriel.bonsai.core.tree.TreeScope
import org.example.lexical_analyzer.AnalyzerResult
import org.example.lexical_analyzer.LexicalAnalyzer
import org.example.syntax_analyzer.BracketsAnalysis
import org.example.syntax_analyzer.SyntacticalAnalyzer
import org.example.syntax_analyzer.SyntaxTree
import org.example.tables.BinaryTreeTable
import tables.SimpleRehashTable
import kotlin.time.measureTime

val SOURCECODE = """
     b := (a + b) * (c - d);

    """.trimIndent()
fun main() = application {

    BracketsAnalysis(SOURCECODE).analyzeBrackets(SOURCECODE)
        Window(
            onCloseRequest = ::exitApplication,
            title = "Владислав 3",
            state = rememberWindowState(width = 1000.dp, height = 1000.dp)
        ) {
            MaterialTheme {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    testAnalyzer()
                }
            }
        }
    }


@Composable
fun testAnalyzer() {


    val lexicalResults = analyzeLexemes(SOURCECODE)
    analyzeSyntax(lexicalResults).onSuccess {
        drawNode(node = it)
    }.onFailure {
        println(it.message)
    }
}

@Composable fun TreeScope.drawBranch(node: SyntaxTree.Node) {
    if (node.children.isEmpty()) {
        Leaf(node.value)
    } else {
        Branch(node.value) {
            node.children.forEach { drawBranch(it) }
        }
    }
}

@Composable
fun drawNode(node: SyntaxTree.Node) {
    val tree = Tree<String> {
        drawBranch(node)
    }
    Bonsai(
        tree = tree,
        modifier = Modifier.wrapContentSize()
    )
}

/**
 * ЛР3, Партилов Д.М., Вариант 3
 * Синтаксический анализатор
 * Входная грамматика:
 * S -> F;
 * F -> if E then T else F | if E then F | a:= a
 * T -> if E then T else T | a := a
 * E -> a < a | a > a | a = a
 */
private fun analyzeSyntax(lexicalResults: List<AnalyzerResult>) = runCatching {
    val syntaxAnalyzer = SyntacticalAnalyzer()
    syntaxAnalyzer.analyze(lexicalResults)
}

/**
 * ЛР2, Партилов Д.М., Вариант 3
 * Лексический анализатор
 * Входной язык содержит операторы условия типа if ... then ... else ...  и if ... then,
 * разделённые символом ; (точка с запятой). Операторы условия содержат идентификаторы, знаки сравнения <, >, =,
 * десятичные числа с плавающей точкой (в обычной и логарифм. форме), знак присваивания (:=)
 */
private fun analyzeLexemes(sourceCode: String): List<AnalyzerResult> {
    val lexicalAnalyzer = LexicalAnalyzer()
    return lexicalAnalyzer.analyze(sourceCode)
}

/**
 * ЛР1, Партилов Д.М., ИВТ-424Б, Вариант 3
 * Метод 1: Простое рехэширование
 * Метод 2: Бинарное дерево
 */
private fun testTables() {
    val identifiers = readLinesFromInputStream(inputStream = getStreamFromResources(IDENTIFIERS_FILE_NAME))
    if (identifiers == null) {
        println("Не удалось прочесть файл. Завершаем исполнение программы...")
        return
    }

    // Создаём таблицы для дальнейшего заполнения
    val simpleRehashTable = SimpleRehashTable(TABLE_SIZE)
    val binaryTreeTable = BinaryTreeTable()

    // Измеряем время, необходимое для заполнения таблиц элементами из файла
    val timeToFillSimpleRehashTable =
        measureTime { identifiers.forEach { simpleRehashTable.insertElement(it) } }.inWholeMicroseconds
    val timeToFillBinaryTreeTable = measureTime { binaryTreeTable.fill(identifiers) }.inWholeMicroseconds
    println("Время, необходимое для заполнения таблицы \"Простое рехэширование\": $timeToFillSimpleRehashTable мкс")
    println("Время, необходимое для заполнения таблицы \"Бинарное дерево\": $timeToFillBinaryTreeTable мкс")

    // Тестируем поиск по таблице
    simpleRehashTable.testSearch()
    binaryTreeTable.testSearch()

    // Пользовательский поиск
    while (true) {
        print("Введите строку для поиска, или последовательность 'STOP_PROGRAM', чтобы завершить выполнение: ")
        val input: String = readln()
        if (input == "STOP_PROGRAM") return
        val simpleRehashTableAttempts = simpleRehashTable.findElement(input)
        if (simpleRehashTableAttempts != null) {
            println("Количество попыток найти элемент для таблицы с простым рехешированием: $simpleRehashTableAttempts")
        }
        val binaryTreeTableAttempts = binaryTreeTable.findElement(input)
        if (binaryTreeTableAttempts != null) {
            println("Количество попыток найти элемент для бинарного дерева: $binaryTreeTableAttempts")
        }
        println()
    }
}

private const val TABLE_SIZE = 2200
private const val IDENTIFIERS_FILE_NAME = "/identifiers2000.txt"