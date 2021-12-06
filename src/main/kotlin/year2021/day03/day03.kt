package year2021.day03

import util.peek
import util.readInput

data class Common(val most: List<Char>, val least: List<Char>)

fun toInt(c: Char) = if(c == '1') 1 else if(c == '0') -1 else error("Unexpected char $c")
fun fromInt(i: Int) = if(i >= 0) '1' else if(i < 0) '0' else '1'
fun inv(c: Char) = if(c == '1') '0' else if(c == '0') '1' else error("Unexpected char $c")

fun analyse(lines: List<String>): Common {

    val mostCommon = lines.map { it.map(::toInt) }
        .reduce {acc, list -> acc.zip(list){a,b -> a+b} }
        //.peek { println(it) }
        .map(::fromInt)

    val leastCommon = mostCommon.map(::inv)

    return Common(most = mostCommon, least = leastCommon)
}

// Finds the most common bit
fun findMostCommon(lines: List<String>, idx: Int): Char {
    return lines.map { toInt(it[idx]) }.sum().let { fromInt(it) }
}

fun part1(lines: List<String>): Int {

    val (mostCommon, leastCommon) = analyse(lines)

    val gamma = Integer.parseInt(mostCommon.joinToString(separator = ""), 2)
    val epsilon = Integer.parseInt(leastCommon.joinToString(separator = ""), 2)

    println("gamma: $gamma, epsilon: $epsilon, mc: $mostCommon, lc: $leastCommon")

    return gamma*epsilon
}

// Filter away those strings that do not obey the pattern at index i
fun filterOnIndex(pattern: Char, lines: List<String>, index: Int): List<String> =
    lines.filter { it[index] == pattern }


fun filterUntilSingleItem(lines: List<String>, patternFn: (lines: List<String>, idx: Int) -> Char): String {
    var tmp = lines
    var idx = 0

    while(tmp.size > 1) {
        // Find the pattern character
        val pattern = patternFn.invoke(tmp, idx)

        // Keep only the ones that match the pattern
        tmp = filterOnIndex(pattern, tmp, idx)

        idx++
    }

    assert(tmp.size == 1)
    return tmp.first()
}


fun part2(lines: List<String>): Int {

    // Find the one matching most common
    val byMost = filterUntilSingleItem(lines){lines, idx -> findMostCommon(lines, idx)}
    val byLeast = filterUntilSingleItem(lines){lines, idx -> inv(findMostCommon(lines, idx)) }

    val oxygenRating = Integer.parseInt(byMost, 2)
    val co2Rating = Integer.parseInt(byLeast, 2)

    println("byMost: $byMost, byLeast: $byLeast, ox: $oxygenRating, co2: $co2Rating")

    return oxygenRating*co2Rating
}

fun main() {
    val input = readInput("year2021/day03.input")
    val small = readInput("year2021/small.input")

    println("Part1")
    println(part1(input))

    println("Part2")
    println(part2(input))
}