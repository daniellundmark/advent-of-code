package year2020.day13

import util.*
import java.math.BigInteger

fun part1() {
    val lines = readInput("year2020/day13.input")

    val time = lines[0].toInt()
    val buses = lines[1].split(",").filter { it != "x" }.map (String::toInt)

    println(buses)

    var iterator = time
    var found = false
    var max = 1000000
    while(!found && (iterator - time < max)) {
        buses.forEach {
            if(iterator % it == 0) {
                println("$iterator $it ${iterator - time} ${(iterator - time)*it}")
                found = true
            }
        }
        iterator++
    }
}


fun main(args: Array<String>) {
    //val n = listOf(7L, 13L, 19L, 31L, 59L).map {BigInteger.valueOf(it)} // modulos
    //val a = listOf(0L, 12L, 12L, 25L, 55L).map {BigInteger.valueOf(it)} // remainders


    val input = readInput("year2020/day13.input")[1].split(",")
            .mapIndexed{ index, number -> Pair(index, number) }.filter { it.second != "x" }.map { Pair(it.first.toLong(), it.second.toLong()) }
            .sortedBy { it.second } // Sort by modulos
            .map { Pair((it.second - it.first) mod it.second, it.second) } // Get the remainder as a positive number

    println(input)

    measureTime {
        val n = input.map { BigInteger.valueOf(it.second) } // Modulos
        val a = input.map { BigInteger.valueOf(it.first) } // Remainders in those modulos

        // println("$n $a")
        println(chineseRemainder(n, a))
    }

    measureTime {
        val pairs = input.map { ModPair(BigInteger.valueOf(it.second), BigInteger.valueOf(it.first)) }
       // println(pairs)
        println(sieve(pairs))
    }
}

