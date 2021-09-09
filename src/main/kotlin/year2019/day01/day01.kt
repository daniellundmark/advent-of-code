package year2019.day01

import util.measureTime
import util.readInput

tailrec fun fuel(mass: Long, sum: Long = 0): Long {
    val f = mass / 3 - 2
    return if(f <=0 ) sum else fuel(f, sum+f)
}


fun main() {
    measureTime {
        var lines = readInput("year2019/day01.input").map(String::toLong)
        println(lines.map(::fuel).sum())
    }
}