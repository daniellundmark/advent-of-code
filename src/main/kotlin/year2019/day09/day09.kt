package year2019.day09

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import util.measureTime
import util.readInput
import year2019.computer.v09.IntCodeComputer
import year2019.computer.v09.receiveAvailable
import java.math.BigInteger

fun main() {
    val input = readInput("year2019/day09.input").first()
    val computer = IntCodeComputer(input)

    measureTime {
        runBlocking {
            val job = launch { computer.execute() }
            computer.input.send(BigInteger.valueOf(2))
            job.join()
            println(computer.output.receiveAvailable().joinToString (","))
        }
    }

}