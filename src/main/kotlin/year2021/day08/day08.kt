package year2021.day08

import util.readInput
import util.splitOnWhitespace

data class SignalEntry (val signal: List<Set<Char>>, val output: List<Set<Char>>)

fun parse(line: String): SignalEntry =
    line.split("|").let {
            (left, right) -> SignalEntry(
                signal = left.splitOnWhitespace().map { it.toSet() },
                output = right.splitOnWhitespace().map { it.toSet() }
    ) }

fun analyse(entry: SignalEntry): Int {
    val signals = mutableMapOf<Int, Set<Char>?>()

    // The simple length based rules
    signals[1] = entry.signal.find { it.size == 2 }
    signals[4] = entry.signal.find { it.size == 4 }
    signals[7] = entry.signal.find { it.size == 3 }
    signals[8] = entry.signal.find { it.size == 7 }

    val four = signals[4] ?: error("Could not find four")
    val seven = signals[7] ?: error("Could not find seven")

    // All other digits can be found by comparing with seven and four
    signals[9] = entry.signal.find { it.size == 6 && it.intersect(four).size == 4 }
    signals[0] = entry.signal.find { it.size == 6 && it.intersect(four).size == 3 && it.intersect(seven).size == 3 }
    signals[6] = entry.signal.find { it.size == 6 && it.intersect(four).size == 3 && it.intersect(seven).size == 2 }

    signals[3] = entry.signal.find { it.size == 5 && it.intersect(seven).size == 3 }
    signals[5] = entry.signal.find { it.size == 5 && it.intersect(seven).size == 2 && it.intersect(four).size == 3 }
    signals[2] = entry.signal.find { it.size == 5 && it.intersect(seven).size == 2 && it.intersect(four).size == 2 }

    //println(signals.map{entry -> "${entry.key}: ${entry.value}"})
    //println(entry.output)

    // Find the digits of the output
    val outputDigits = entry.output.map { digit -> signals.entries.first { it.value == digit }.key}
    //println(outputDigits)

    return outputDigits.reduce { acc, i -> 10 * acc + i }
}

fun main() {
    val tiny = readInput("year2021/tiny.input").map(::parse)
    val small = readInput("year2021/small.input").map(::parse)
    val input = readInput("year2021/day08.input").map(::parse)

    println("Part 1: "+input.sumOf{ entry -> entry.output.count { output -> output.size in listOf(2, 3, 4, 7) } })

    println("Part 2: "+input.sumOf { analyse(it) })
}