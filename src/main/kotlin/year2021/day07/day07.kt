package year2021.day07

import util.readInput
import kotlin.math.absoluteValue

fun distance(numbers: List<Int>, distanceFn: (a: Int, b: Int) -> Int): Int {
    // Take the min/max number
    val min = numbers.minOf { it }
    val max = numbers.maxOf { it }

    // For each integer in the range, check the sum of distances to each number in the list and return the minimum sum
    return (min..max).minOf { m -> numbers.sumOf { distanceFn.invoke(m, it) } }
}

fun main() {
    val small = readInput("year2021/small.input").first().split(",").map(String::toInt)
    val input = readInput("year2021/day07.input").first().split(",").map(String::toInt)

    val part1 = distance(input){a, b -> (a-b).absoluteValue}
    println("Part 1: $part1")

    val part2 = distance(input){a, b ->
        val diff = (a-b).absoluteValue
        (diff*(diff+1))/2
    }
    println("Part 2: $part2")

}