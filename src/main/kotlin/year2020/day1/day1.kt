package year2020.day1

import util.multiply
import util.readInput
import util.rest

fun main() {
    val input = readInput("year2020/day1.input").map { it.toInt() }
    val triple = input.findTripleThatSumsTO(2020) ?: error("Could not find triple")
    println("$triple, ${triple.toList().sum()}, ${triple.toList().multiply()}")
}

// A triple may be:
// * The first element plus a pair with the util.rest of the sum in the util.rest of the list
// * Or the triple can be found in the util.rest of the list
tailrec fun List<Int>.findTripleThatSumsTO(sum: Int): Triple<Int, Int, Int>? =
    when(val pair = this.rest().findPairThatSumsTo(sum - this.first())) {
        null -> this.rest().findTripleThatSumsTO(sum)
        else -> Triple(this.first(), pair.first, pair.second)
    }

// A pair in the list that sums to the given sum may be:
// * For an empty list, we can find it (base case)
// * The first element and an element from the util.rest of the list which sums with the first one
// * Or a pair found in the util.rest of the list
tailrec fun List<Int>.findPairThatSumsTo(sum: Int): Pair<Int, Int>?  =
    when {
        this.isEmpty() -> null
        else -> {
            when(val second = this.rest().find { it == sum - this.first() }) {
                null -> this.rest().findPairThatSumsTo(sum)
                else -> Pair(this.first(), second)
            }
        }
    }

// For-loop based solution
fun iterative(list: List<Int>, sum: Int): Triple<Int, Int, Int>? {
    for (i in list.indices) {
        for (j in i + 1 until list.size) {
            for(k in j + 1 until list.size) {
                if(list[i] + list[j] + list[k] == sum) {
                    return Triple(list[i], list[j], list[k])
                }
            }
        }
    }
    return null
}

