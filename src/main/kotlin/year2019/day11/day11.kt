package year2019.day11

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import util.Point
import util.readInput
import year2019.computer.v09.IntCodeComputer
import java.math.BigInteger

enum class Direction {
    UP, RIGHT, DOWN, LEFT;

    fun left(): Direction {
        return when(this) {
            UP -> LEFT
            LEFT -> DOWN
            DOWN -> RIGHT
            RIGHT -> UP
        }
    }

    fun right(): Direction {
        return when(this) {
            UP -> RIGHT
            RIGHT -> DOWN
            DOWN -> LEFT
            LEFT -> UP
        }
    }
}

fun Point.move(dir: Direction): Point =
    when(dir) {
        Direction.UP -> Point(this.x, this.y-1)
        Direction.RIGHT -> Point(this.x+1, this.y)
        Direction.DOWN -> Point(this.x, this.y+1)
        Direction.LEFT -> Point(this.x-1, this.y)
    }

fun part1(input: String): Int = runBlocking {
    val computer = IntCodeComputer(input)

    val job = launch { computer.execute() }

    var direction = Direction.UP
    var point = Point(0,0)
    val whitePlates = mutableMapOf<Point, Boolean>()


    for(i in 1..10000) {

        if(computer.terminated) {
            break
        }

        //println("$i: Now at $point, facing $direction")

        computer.input.send(if(whitePlates[point] == true) BigInteger.ONE else BigInteger.ZERO)
        val paintWhite = computer.output.receive() == BigInteger.ONE
        val turnRight = computer.output.receive() == BigInteger.ONE

        whitePlates[point] = paintWhite
        direction = if(turnRight) direction.right() else direction.left()
        point = point.move(direction)
    }

    return@runBlocking whitePlates.count()
}

fun part2(input: String) = runBlocking {

    val computer = IntCodeComputer(input)

    val job = launch { computer.execute() }

    var direction = Direction.UP
    var point = Point(0,0)
    val whitePlates = mutableMapOf<Point, Boolean>()
    whitePlates[point] = true

    for(i in 1..10000) {

        if(computer.terminated) {
            break
        }

        println("$i: Now at $point, facing $direction")

        computer.input.send(if(whitePlates[point] == true) BigInteger.ONE else BigInteger.ZERO)
        val paintWhite = computer.output.receive() == BigInteger.ONE
        val turnRight = computer.output.receive() == BigInteger.ONE

        whitePlates[point] = paintWhite
        direction = if(turnRight) direction.right() else direction.left()
        point = point.move(direction)
    }

    val height = whitePlates.maxOf { it.key.y }
    val width = whitePlates.maxOf { it.key.x }

    for(y in 0 .. height) {
        for(x in 0 .. width) {
            print(if(whitePlates[Point(x,y)] == true) '#' else ' ')
        }
        println()
    }

   // println(whitePlates)

}

fun main() = runBlocking {

    val input = readInput("year2019/day11.input").first()

    println(part1(input))

    part2(input)

}