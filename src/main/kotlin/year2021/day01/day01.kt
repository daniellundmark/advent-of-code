package year2021.day01

import util.measureTime
import util.readInput
import util.rest

tailrec fun increases(input: List<Int>, prev: Int? = null): Int {
    if(input.isEmpty()) {
        return 0
    }

    val first = input.first()
    val rest = input.rest()
    val thisIncreased = prev?.let { prev < first } ?: false

    return (if(thisIncreased) 1 else 0) + increases(rest, first)
}

tailrec fun threes(input: List<Int>, prev3Sum: Int? = null): Int {
    if(input.size < 3) {
        return 0
    }
    val thisSum = input[0] + input[1] + input[2]
    val thisIncreased = prev3Sum?.let { prev3Sum < thisSum } ?: false

    return (if(thisIncreased) 1 else 0) + threes(input.rest(), thisSum)
}


fun listBased(input: List<Int>, size: Int): Int {

    // Generate the windows of the list
    val windows = input.windowed(size, 1)

    // Map each window to a sum
    val sums = windows.map { it.sum() }

    // Get pairs of subsequent elements
    val pairs = sums.zipWithNext()

    // See how many subsequent sums are increasing (this is shorthand for filter + count)
    return pairs.count { pair -> pair.first < pair.second }
}


fun main() {
    val input = readInput("year2021/day01.input").map { it.toInt() }
    val small = readInput("year2021/small.input").map { it.toInt() }


    println( measureTime({d -> println("Part 1: list based solution: $d")}) {
        listBased(input, 1)
    } )
    println( measureTime({d -> println("Part 1: recursive solution: $d")}) {
        increases(input)
    } )

    println( measureTime({d -> println("Part 2: list based solution: $d")}) {
        listBased(input, 3)
    } )
    println ( measureTime({d -> println("Part 2: recursive solution: $d")}) {
        threes(input)
    })

}
