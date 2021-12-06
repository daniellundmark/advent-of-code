package year2021.day06

import util.debug
import util.measureTime
import util.readInput
import util.showDebug


fun LongArray.toDisplayString(): String = this.mapIndexed { idx, it -> "$it" }.joinToString ( "\t" )

fun LongArray.grow(): LongArray {
    val nextAges = LongArray(9) { 0 }
    (8.downTo(1)).forEach { c ->
        val cur = this[c]
        nextAges[c-1] = cur
    }
    nextAges[6] = nextAges[6]  + this[0]
    nextAges[8] = this[0]

    return nextAges
}

fun evolve(fish: List<Int>, iterations: Int): Long {

    // Collect how many fish there are of each age
    var ages = fish.foldRight(LongArray(9) { 0 }) { age, ages -> ages[age]++; ages }

    //println("  \t${(0..8).joinToString("\t")}")
    //println("0:\t${ages.toDisplayString()}")
    repeat(iterations) {
        ages = ages.grow()
        //println("$it:\t${ages.toDisplayString()}")
    }
    return ages.sum()
}

fun evolveInMemory(fish: List<Int>, iterations: Int): Long {
    // Collect how many fish there are of each age, this creates an array with fishes of each age
    val ages: LongArray = fish.foldRight(LongArray(9) { 0 }) { age, ages -> ages[age]++; ages }

    // There is one index each iteration which "grows" into index - 2, it starts off at 0
    var grows = 0
    repeat(iterations) {
        ages[(grows - 2).mod(9)] += ages[grows.mod(9)]
        grows++
    }

    return ages.sum()
}

fun main() {
    val small = readInput("year2021/small.input").first().split(",").map(String::toInt)
    val input = readInput("year2021/day06.input").first().split(",").map(String::toInt)

    showDebug=true

    // Warm up
    measureTime {
        evolve(input, 80)
    }

    measureTime({println("Part 1, copying: $it")}) {
        debug(evolve(input, 80))
    }
    measureTime({println("Part 1, in-memory: $it")}) {
        debug(evolveInMemory(input, 80))
    }

    measureTime({println("Part 2, copying: $it")}) {
        debug(evolve(input, 256))
    }
    measureTime({println("Part 2, in-memory: $it")}) {
        debug(evolveInMemory(input, 256))
    }

}