package year2019.day07

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import util.measureTime
import util.permute
import util.readInput
import year2019.computer.v07.IntCodeComputer

suspend fun part1(program: String, phases: List<Int>): Int {
    val amps = (0..4).map { IntCodeComputer(program) }
    for(i in 0 until amps.size-1) {
        amps[i+1].input = amps[i].output
    }

    val jobs = amps.map { GlobalScope.launch { it.execute() } }

    for(i in amps.indices) {
        amps[i].input.send(phases[i])
    }
    amps[0].input.send(0)

    jobs.map { it.join() }

    return amps[4].output.receive()
}

suspend fun part2(program: String, phases: List<Int>): Int {
    val amps = (0..4).map { IntCodeComputer(program) }
    for(i in amps.indices) {
        //println("Setting ${(i+1) % amps.size}.input = ${i}.output")
        amps[(i+1) % amps.size].input = amps[i].output
    }

    val jobs = amps.map { GlobalScope.launch { it.execute() } }

    for(i in amps.indices) {
        amps[i].input.send(phases[i])
    }

    amps[0].input.send(0)

    jobs.map { it.join() }

    return amps[4].output.receive()
}

fun main() {

    val input = readInput("year2019/day07.input").first()

    measureTime {
        runBlocking {
            val part1Permutations = permute(listOf(0, 1, 2, 3, 4))
            val phasesPart1 = part1Permutations.map { val out = part1(input, it); out to it }.toMap()
            println("Max output part 1: ${phasesPart1.maxByOrNull { it.key }}")
        }
    }


    measureTime {
        runBlocking {
            val part2Permutations = permute(listOf(5, 6, 7, 8, 9))
            val phasesPart2 = part2Permutations.map { val out = part2(input, it); out to it }.toMap()
            println("Max output part 2: ${phasesPart2.maxByOrNull { it.key }}")
        }
    }

}

