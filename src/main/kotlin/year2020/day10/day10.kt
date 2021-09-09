package year2020.day10

import util.measureTime
import util.readInput

val memo = mutableMapOf<Int, Long>()

fun countWaysToEnd(jolts: List<Int>): Long {

    if(jolts.isEmpty()) {
        return 0
    }
    if(jolts.first() == 0) {
        //println("reached end")
        return 1
    }

    val memoized = memo[jolts.first()]
    if(memoized != null) {
        //println("Found $memoized in memoized cache")
        return memoized
    }

    // All the reachable possible "paths" from this node
    val thisJolt = jolts.first()
    val reachablePaths = listOf(thisJolt - 1, thisJolt - 2, thisJolt - 3)
        .filter { jolts.contains(it) }.map { jolts.indexOf(it) }.map { index -> jolts.subList(index, jolts.size)}


    // Count the ways we reach the end by all reachable paths
    return reachablePaths
        .map {
           val ways = countWaysToEnd(it)
            //println("Found $ways for ${it.first()}")
            memo[it.first()] = ways
            ways
        }
        .sum()

}

fun main() {

    measureTime {

        var jolts = readInput("year2020/day10.input").map(String::toInt).sorted()
        val withZero = listOf(0) + jolts
        val withDevice = jolts + (jolts.last() + 3)

        // Count steps of 1 and 3
        val diffs = withZero.zip(withDevice) { a, b -> b - a }
        println("${diffs.count { it == 1 }} ${diffs.count { it == 3 }}  ${diffs.count { it == 1 } * diffs.count { it == 3 }}")

        // Follow the jolts from the end to the beginning, keep memoization to avoid unnecessary recursion
        val result = countWaysToEnd(withZero.reversed())

        println(result)

    }

}