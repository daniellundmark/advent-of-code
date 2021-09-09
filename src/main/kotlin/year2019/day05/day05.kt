package year2019.day05

import kotlinx.coroutines.channels.receiveOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import util.readInput
import year2019.computer.v07.IntCodeComputer
import year2019.computer.v07.dump


fun main() = runBlocking {

    val program = readInput("year2019/day05.input").first()
    val computer = IntCodeComputer(program)

    launch{ computer.execute() }
    computer.input.send(5)

    println(computer.memory.dump())
    println(computer.output.receiveOrNull())
}

