package year2021.day09

import util.multiply
import util.readInput

data class Pos(val x: Int, val y: Int)
typealias Grid<T> =  Map<Pos, T>

fun Grid<Int>.neighbours(pos: Pos): Set<Pos> =
    listOf(Pos(pos.x-1, pos.y), Pos(pos.x, pos.y-1), Pos(pos.x+1, pos.y), Pos(pos.x, pos.y+1))
        .filter { this.containsKey(it) }
        .toSet()

fun Grid<Int>.lowPoints(): List<Pos> {
    val lows = this.keys.filter { p ->
        this.neighbours(p).none { n -> this[n]!! <= this[p]!! }
    }
    return lows
}

fun basin(point: Pos, grid: Grid<Int>, basinPoints: MutableSet<Pos> = mutableSetOf(point)): Set<Pos> {
    val newNeighbours = (grid.neighbours(point) - basinPoints).filter { grid[it]!! < 9 }
    basinPoints += newNeighbours
    newNeighbours.forEach { n ->
        basinPoints += basin(n, grid, basinPoints)
    }
    return basinPoints
}

fun part2(grid: Grid<Int>): Int {
    val lowPoints = grid.lowPoints()

    // A basin is recursively defined as a point in the basin (starting with the low point)
    // and all its neighbours that are not already in the basin
    val basins = lowPoints.map { point -> basin(point, grid) }.sortedByDescending { it.size }

    //println(basins)
    //println(basins.map { it.size })

    return basins.take(3).map { it.size }.multiply()

}

fun part1(grid: Grid<Int>): Int {
    return grid.lowPoints().sumOf { grid[it]!! + 1 }
}

fun main() {
    val grid = readInput("year2021/day09.input")
        .flatMapIndexed{ y, line -> line.mapIndexed { x, c -> Pos(x, y) to c.digitToInt() } }
        .toMap()

    println("Part 1: "+part1(grid))
    println("Part 2: "+part2(grid))
}