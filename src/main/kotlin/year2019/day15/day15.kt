package year2019.day15

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import util.measureTime
import util.readInput
import year2019.computer.v09.IntCodeComputer
import year2019.computer.v09.receiveAvailable
import year2019.day13.display
import java.lang.Thread.sleep
import java.util.*

const val NORTH = 1
const val SOUTH = 2
const val WEST = 3
const val EAST = 4

const val WALL = 0
const val FLOOR = 1
const val GOAL = 2

data class Point(val x: Int, val y: Int) {
    override fun toString(): String {
        return "($x,$y)"
    }

    fun intCodeTo(other: Point): Int {
        val (xx, yy) = other
        if(xx == x-1 && yy == y) {
            return WEST
        } else if(xx == x+1 && yy == y) {
            return EAST
        } else if(xx == x && yy == y - 1) {
            return NORTH
        } else if(xx == x && yy == y + 1) {
            return SOUTH
        } else {
            error("Not neighbours: $this and $other")
        }
    }

    fun neighbours() = listOf<Point>(
        Point(x-1, y), Point(x+1, y), Point(x, y-1), Point(x, y+1)
    )


}

// Return candidates that are still unvisited
fun candidates(point: Point, visited: Collection<Point>): Set<Point> {
    return (point.neighbours() - visited).toSet()
}

fun MutableMap<Point, Char>.display() {
    val minX = this.minOf { it.key.x }
    val maxX = this.maxOf { it.key.x }
    val minY = this.minOf { it.key.y }
    val maxY = this.maxOf { it.key.y }

    for (y in minY .. maxY) {
        for (x in minX .. maxX) {
            print(this[Point(x, y)] ?: ' ')
        }
        println()
    }
}

fun status(path: List<Int>, input: String): Int = runBlocking {
    val computer = IntCodeComputer(input)

    val job = launch { computer.execute() }
    path.forEach{ computer.input.send(it.toBigInteger()) }
    val output = computer.output.receiveAvailable().map { it.toInt() }

    assert(output.size == path.size) {"Expected input/output to have same size: $input : $output"}

    job.cancel()

    output.last()
}

fun main() = runBlocking {
    val input = readInput("year2019/day15.input").first()



    val maze = mutableMapOf<Point,Char>(Point(0,0) to 'S')

    var goalPoint: Point? = null

    // The BFS queue
    val queue: Queue<Point> = LinkedList<Point>()
    queue.add(Point(0,0))

    // The IntCode inputs that got us to each point
    val paths = mutableMapOf<Point, List<Int>>(Point(0,0) to emptyList<Int>())

    measureTime {
        while(queue.isNotEmpty()) {

            val cur = queue.remove() ?: error("Queue should not be empty here")
            val pathToCur = paths[cur] ?: error("Should have had path to $cur")

            //println("Doing BFS search from $cur")

            // Check all candidate nodes
            val candidates = candidates(cur, maze.keys)
            candidates.forEach { candidate ->

                // Ask the computer what node this candidate is
                val candidatePath = pathToCur + cur.intCodeTo(candidate)
                paths[candidate] = candidatePath

                when(val status = status(candidatePath, input)) {
                    WALL -> { maze[candidate] = '#' } // Just skip walls
                    FLOOR -> { maze[candidate] = '.'; queue.add(candidate) } // Keep searching with this node
                    GOAL -> {  maze[candidate] = 'G'; goalPoint = candidate; queue.add(candidate)  } // Also keep searching so we explore the entire maze
                    else -> error("Unexpected IntCode response: $status")
                }
            }
        }

        maze.display()
        println("Found goal at $goalPoint, path is: ${paths[goalPoint]}, length: ${paths[goalPoint]?.size}")
    }



    // Do a reverse BFS from the goal to find the distance to the furthest point
    val oxygen = mutableSetOf<Point>(goalPoint!!)
    val minutes = mutableMapOf<Point, Int>(goalPoint!! to 0)
    queue.add(goalPoint!!)

    measureTime {
        while(queue.isNotEmpty()) {

            val cur = queue.remove() ?: error("Queue should not be empty here")
            val pathToCur = paths[cur] ?: error("Should have had path to $cur")

            //println("Doing BFS search from $cur")

            // Check all candidate nodes
            val candidates = candidates(cur, oxygen)
            candidates.forEach { candidate ->

                val type = maze[candidate] ?: error("Should have found $candidate in maze")
                val minutesToCandidate = minutes[cur]!! + 1

                when(type) {
                    '.', 'S' -> {
                        oxygen += candidate
                        minutes[candidate] = minutesToCandidate
                        queue += candidate
                    }
                }

            }
        }

        println("Maximum minutes: ${minutes.maxOf { it.value }}")

    }

}