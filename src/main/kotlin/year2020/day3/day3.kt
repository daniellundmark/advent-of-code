package year2020.day3

import util.readInput
import util.rest

fun main() {
    val lines = readInput("year2020/day3.input")

    println(lines.run(1, 1))
    println(lines.run(3, 1))
    println(lines.run(5, 1))
    println(lines.run(7, 1))
    println(lines.run(1, 2))

}

fun String.curValue(index: Int) =
    if(this[index] == '#') 1 else 0

fun <T> List<T>.step(steps: Int): List<T> =
    when(steps == 0 || this.isEmpty()) {
        true -> this
        else -> this.rest().step(steps-1)
    }

fun List<String>.trees(down: Int, right: Int, pos: Int) : Int =
  if(this.isEmpty()) 0 else this.first().curValue(pos) + this.step(down).trees(down, right, (pos + right) % this.first().length)

fun List<String>.run(right: Int, down: Int) : Int =
    this.step(down).trees(down, right, right)
