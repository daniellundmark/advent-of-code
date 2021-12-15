package year2021.day15

import util.measureTime
import util.readInput
import java.util.*

data class Pos(val x: Int, val y: Int)
data class DistancePos(val pos: Pos, val distance: Int): Comparable<DistancePos> {
    override fun compareTo(other: DistancePos): Int {
        return this.distance.compareTo(other.distance)
    }
}

fun Map<Pos, Int>.toDisplayString(): String {
    val maxX = this.keys.maxOf { it.x }
    val maxY = this.keys.maxOf { it.y }

    return (0..maxY).joinToString(separator = "\n") { y ->
        (0..maxX).map { x -> this[Pos(x,y)] }.joinToString(separator = "")
    }
}


fun Map<Pos, Int>.neighbours(pos: Pos): Set<Pos> =
    listOf(Pos(pos.x-1, pos.y), Pos(pos.x, pos.y-1), Pos(pos.x+1, pos.y), Pos(pos.x, pos.y+1))
        .filter { this.containsKey(it) }
        .toSet()

// Dijkstra's algorithm
fun dijkstra(grid: Map<Pos, Int>): Int {

    val start = Pos(0,0)
    val end = Pos(grid.keys.maxOf { it.x }, grid.keys.maxOf { it.y })

    val settled = mutableSetOf<Pos>()

    // Settled minimum distances
    val distance = mutableMapOf<Pos, Int>()
    distance[start] = 0

    val unsettled = PriorityQueue<DistancePos>()
    unsettled.add(DistancePos(pos = start, distance = 0))

    while(unsettled.isNotEmpty()) {

        // Find the unsettled node with the lowest distance
        val next = unsettled.remove()

        // In case of duplicates, we may already have settled this node
        if(settled.contains(next.pos)){
            continue
        }
        settled.add(next.pos)
        distance[next.pos] = next.distance

        // See if we have new distances to the neighbours of next
        val newNeighbours = grid.neighbours(next.pos)
            .filter { !settled.contains(it) }

        newNeighbours.forEach { neighbour ->
            val newDistance = DistancePos(neighbour, distance = next.distance + grid[neighbour]!!)
            unsettled.add(newDistance)
        }

    }

    return distance[end]!!

}

fun getValue(smallValue: Int, steps: Int): Int {
    var value = smallValue
    repeat(steps) {
        value++
        if(value > 9) value = 1
    }
    return value
}

fun buildBigGrid(smallGrid: Map<Pos, Int>, xx: Int, yy: Int): Map<Pos, Int> {

    val sizeX = smallGrid.keys.maxOf { it.x } + 1
    val sizeY = smallGrid.keys.maxOf { it.y } + 1

    // First fill in the top horizontal
    val xGrid = (0 until xx).flatMap { xMult ->
        //println("Building xGrid for $xMult")
        smallGrid.map { (smallPos, smallVal) ->
            //println("${Pos(smallPos.x + (xMult * sizeX), smallPos.y) to (smallVal + xMult) % 10}")
            Pos(smallPos.x + (xMult * sizeX), smallPos.y) to getValue(smallVal, xMult)
        }
    }.toMap()

    // Then multiply that vertically
    val bigGrid = (0 until yy).flatMap { yMult ->
        xGrid.map{ (smallPos, smallVal) ->
            Pos(smallPos.x, smallPos.y + (yMult * sizeY)) to  getValue(smallVal, yMult)
        }
    }.toMap()

    return bigGrid
}

fun main() {
    val grid = readInput("year2021/day15.input").flatMapIndexed { y, line ->
        line.mapIndexed { x, c ->
            Pos(x, y) to c.digitToInt()
        }
    }.toMap()

    measureTime {
        println("Part 1: " + dijkstra(grid))
    }

    val bigGrid = buildBigGrid(grid, 5, 5)

    measureTime {
        println("Part 2: "+dijkstra(bigGrid))
    }


}