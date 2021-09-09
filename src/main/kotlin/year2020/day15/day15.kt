package year2020.day15

import util.measureTime
import util.readInput

fun main() {

    measureTime {

        val numbers = readInput("year2020/day15.input").first().split(",").map(String::toInt)

        val max = 30000000

        val lastSeen = IntArray(max){-1}

        // Set the first numbers
        numbers.forEachIndexed{i,number -> lastSeen[number] = i}

        var last = numbers.last()

        // calculate van eck numbers
        for(i in numbers.size-1 until max-1) {

            val vanEck = if(lastSeen[last] != -1) i - lastSeen[last] else 0
            lastSeen[last] = i
           // println("${i+1} $last $vanEck")
            last = vanEck
        }

        println(last)

    }

}