package year2020.day12

import util.measureTime
import util.mod
import util.readInput
import kotlin.math.absoluteValue

data class Part1(val x: Int, val y: Int, val d: Int = 0) {

    fun process(op: Char, value: Int): Part1 =
        when(op) {
            'N' -> Part1(x, y + value, d)
            'S' -> Part1(x, y - value, d)
            'E' -> Part1(x + value, y, d)
            'W' -> Part1(x - value, y, d)
            'L' -> Part1(x, y, (d + (value / 90)) mod 4)
            'R' -> Part1(x, y, (d - (value / 90)) mod 4)
            'F' -> when (d) {
                0 -> process('E', value)
                1 -> process('N', value)
                2 -> process('W', value)
                3 -> process('S', value)
                else -> error("Wrong direction $d")
            }
            else -> error("Wrong year2019.day02.op code $op")
        }

    fun manhattan() = x.absoluteValue+y.absoluteValue
}


data class Waypoint(val x: Int, val y: Int) {
    fun process(op: Char, value: Int): Waypoint =
        when(op) {
            'N' -> Waypoint(x, y + value)
            'S' -> Waypoint(x, y - value)
            'E' -> Waypoint(x + value, y)
            'W' -> Waypoint(x - value, y)
            'L' -> (value / 90).downTo(1).fold(this, { acc, _ -> acc.ccw() })
            'R' -> (value / 90).downTo(1).fold(this, { acc, _ -> acc.cw() })
            else -> error("Wrong year2019.day02.op code $op")
        }

    fun ccw() = Waypoint(-y, x)
    fun cw() = Waypoint(y, -x)
}


data class Part2(val x: Int, val y: Int, val wp: Waypoint) {

    fun process(op: Char, value: Int): Part2 =
        when(op) {
            'F' -> Part2(x + value * wp.x, y + value * wp.y, wp)
            else -> Part2(x, y, wp.process(op, value))
        }

    fun manhattan() = x.absoluteValue+y.absoluteValue

}


fun main() {

    measureTime {

        val lines = readInput("year2020/day12.input")
        val regex = "(\\w)(\\d+)".toRegex()

        val end = lines.fold(Part2(0, 0, Waypoint(10, 1))) { p, line ->
            regex.matchEntire(line)!!
                .destructured.let { (op, value) ->
                    p.process(op.first(), value.toInt())
                        //.also { println("$year2019.day02.op:$value $it") }
                }
        }

        println(end.manhattan())
    }
}