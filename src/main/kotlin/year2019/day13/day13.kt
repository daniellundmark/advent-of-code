package year2019.day13

import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import util.Point
import util.readInput
import year2019.computer.v09.IntCodeComputer
import year2019.computer.v09.receiveAvailable
import java.math.BigInteger
import java.math.BigInteger.ONE
import java.math.BigInteger.ZERO

fun Map<Point, Int>.display() {
    val width = this.maxOf { it.key.x }
    val height = this.maxOf { it.key.y }

    println("Points: ${this[Point(-1,0)]}")
    for (y in 0 .. height) {
        for (x in 0 .. width) {
            print( display(Point(x, y), this[Point(x, y)]))
        }
        println()
    }
}

fun display(pos: Point, tile: Int?): Char =
    when(tile) {

        0 -> ' '
        1 -> if(pos.y == 0) '-' else '|'
        2 -> '#'
        3 -> '='
        4 -> 'o'

        null -> '?'

        else -> error("Unexpected tile: $tile")
    }

suspend fun ReceiveChannel<BigInteger>.receiveScreen() =
    this.receiveAvailable().map { it.toInt() }.chunked(3)
        .map {
            val (x, y, tile) = it
            Point(x, y) to tile
        }.toMap()


fun Map<Point, Int>.countBlocks() = this.count { it.value == 2 }


fun part1(input: String) = runBlocking {
    val computer = IntCodeComputer(input)
    launch { computer.execute() }.join()

    val screen = computer.output.receiveScreen()

    println(screen.countBlocks())

    screen.display()

}

fun part2(input: String) = runBlocking {

    val left = BigInteger.valueOf(-1)
    val neutral = BigInteger.ZERO
    val right = BigInteger.ONE


    val computer = IntCodeComputer(input)
    computer.memory[ZERO] = BigInteger.valueOf(2)

    val (_, _, input, output) = computer

    launch { computer.execute() }

    input.send(neutral)

    val screen = output.receiveScreen().toMutableMap()

    while(!computer.terminated) {

        // Find the right direction to move the joystick
        val ball = screen.entries.first { it.value == 4 }.key
        val paddle = screen.entries.first { it.value == 3 }.key

        val direction = ball.x.compareTo(paddle.x)

        input.send(direction.toBigInteger())

        val delta = output.receiveScreen()
        screen.putAll(delta)

        //screen.display()
        //Thread.sleep(100)

    }


    screen.display()


}

fun main() {
    val input = readInput("year2019/day13.input").first()

    //part1(input)
    part2(input)

}