package year2021.day10

import util.*

sealed interface ParseResult {
    object Success: ParseResult
    data class Error(val illegal: Char): ParseResult
    data class Incomplete(val stack: Stack<Char>): ParseResult
}

fun Char.isOpener() = this in listOf('(', '[', '{', '<')

fun Char.matchingCharacter() = when(this) {
    ')' -> '('
    ']' -> '['
    '}' -> '{'
    '>' -> '<'
    '(' -> ')'
    '[' -> ']'
    '{' -> '}'
    '<' -> '>'
    else -> error("$this is not a valid delimiter")
}

fun Char?.illegalScore() = when(this) {
    ')' -> 3
    ']' -> 57
    '}' -> 1197
    '>' -> 25137
    else -> 0
}

fun Char?.autoCompleteScore() = when(this) {
    ')' -> 1
    ']' -> 2
    '}' -> 3
    '>' -> 4
    else -> 0
}

fun parse(line: String, stack: Stack<Char> = ArrayDeque()): ParseResult {

    //println("Parsing $line")
    if(line.isEmpty() && stack.isEmpty()) {
        return ParseResult.Success
    } else if(line.isEmpty() && stack.isNotEmpty()) {
        return ParseResult.Incomplete(stack)
    } else if(line.first().isOpener()) {
        stack.push(line.first())
    } else if(stack.pop() != line.first().matchingCharacter()) {
        return ParseResult.Error(line.first())
    }
    return parse(line.rest(), stack)
}

fun part1(lines: List<String>) {
    val errors = lines.map(::parse).filterIsInstance<ParseResult.Error>()
    println("Part 1: "+ errors.sumOf { it.illegal.illegalScore() })
}

fun part2(lines: List<String>) {
    val incompleteLines = lines.map(::parse).filterIsInstance<ParseResult.Incomplete>()
    val completions = incompleteLines.map { line -> line.stack.map { it.matchingCharacter() }.reversed() }
    val scores = completions.map { line ->
        line.fold(0L) { acc, c -> acc * 5 + c.autoCompleteScore() }
    }.sorted()
    //println("Autocompletes: $scores")
    println("Part 2: "+ scores[scores.size / 2])
}

fun main() {
    val input = readInput("year2021/day10.input")
    part1(input)
    part2(input)
}