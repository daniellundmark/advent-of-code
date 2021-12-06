package year2021.day05

import util.max
import util.min
import util.readInput
import year2020.day20.seaMonsterPixels

data class Pos(val x: Int, val y: Int)
class Grid(val grid: MutableMap<Pos, Int> = mutableMapOf()) {

    fun inc(pos: Pos) {
        grid[pos] = (grid[pos] ?: 0) + 1
    }

    fun toNiceString(): String {
        val maxX = grid.maxOf { it.key.x }
        val maxY = grid.maxOf { it.key.y }

        return (0..maxY).joinToString(separator = "\n") { y ->
            (0..maxX).map { x -> grid[Pos(x, y)] ?: '.' }.joinToString(separator = "")
        }
    }
}

val regex = Regex("(\\d+),(\\d+) -> (\\d+),(\\d+)")
fun parse(line: String): Pair<Pos, Pos> {
    return regex.matchEntire(line)
        ?.destructured
        ?.let { (x1,y1,x2,y2) -> Pair(Pos(x1.toInt(),y1.toInt()), Pos(x2.toInt(),y2.toInt())) } ?: error("Failed to parse $line")
}


fun Pair<Pos, Pos>.isHorizontal(): Boolean = this.first.y == this.second.y
fun Pair<Pos, Pos>.isVertical(): Boolean = this.first.x == this.second.x

operator fun Pos.rangeTo(goal: Pos): Sequence<Pos> = sequence {
    var pos = this@rangeTo

    yield(pos)

    while(pos != goal) {
        pos = Pos(
            x = pos.x + goal.x.compareTo(pos.x),
            y = pos.y + goal.y.compareTo(pos.y)
        )
        yield(pos)
    }
}

fun solution(lines: List<Pair<Pos, Pos>>, useDiagonals: Boolean) {
    val grid = Grid()

    // Add horizontal or vertical lines to the grid
    lines.forEach { line ->
        if(useDiagonals || line.isVertical() || line.isHorizontal())
        (line.first .. line.second).forEach { pos -> grid.inc(pos) }
    }

    //println("The grid is:")
    //println(grid.toNiceString())

    println("Solution: ${grid.grid.count { it.value >= 2 }}")
}


fun main() {
    val small = readInput("year2021/small.input")
    val input = readInput("year2021/day05.input")

    println("Part 1:")
    solution(input.map(::parse), false)

    println("Part 2:")
    solution(input.map(::parse), true)

}