package year2020.day9

import util.readInput

fun inSums(preamble: List<Long>, number: Long): Boolean {
    for(i in preamble.indices) {
        for(j in i + 1 until preamble.size) {
            if(preamble[i] + preamble[j] == number) return true
        }
    }
    return false
}

fun part1(numbers: List<Long>) {

    val preambleLength = 25
    for(i in preambleLength until numbers.size) {
        if(!inSums(numbers.subList(i - preambleLength, i), numbers[i])) {
            println("$i: ${numbers[i]}")
            return
        }
    }
}

fun part2(numbers: List<Long>) {
    val target = 26134589L

    for(i in numbers.indices) {
        for(j in i+1 until numbers.size) {
            val sublist = numbers.subList(i, j)
            if(sublist.sum() == target) {
                println("$i, $j, ${sublist.minOrNull()}, ${sublist.maxOrNull()}")

                return
            }

        }
    }

}

fun main() {
    val lines = readInput("year2020/day9.input").map(String::toLong)
    part2(lines)
}