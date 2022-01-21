package year2021.day22

import util.max
import util.min
import util.readInput

data class Point(val x: Int, val y: Int, val z: Int)
data class Action(val on: Boolean, val from: Point, val to: Point) {
    fun points(): Set<Point> {
        return (from.x..to.x).flatMap { x ->
            (from.y..to.y).flatMap { y ->
                (from.z..to.z).map {  z ->
                    Point(x, y, z)
                }
            }
        }.toSet()
    }
    fun points(min: Point, max: Point): Set<Point> {
        return (max(from.x, min.x)..min(to.x, max.x)).flatMap { x ->
                (max(from.y, min.y)..min(to.y, max.y)).flatMap { y ->
                    (max(from.z, min.z)..min(to.z, max.z)).map {  z ->
                    Point(x, y, z)
                }
            }
        }.toSet()
    }
    fun valid(min: Point, max: Point): Boolean =
        (min.x < to.x || max.x > from.x) &&
                (min.y < to.y || max.y > from.y) &&
                (min.z < to.z || max.z > from.z)
}
fun Collection<Point>.within(min: Point, max: Point): Collection<Point> =
    this.filter {
        it.x >= min.x && it.x <= max.x && it.y >= min.y && it.y <= max.y && it.z >= min.z && it.z <= max.z
    }

data class Grid(val enabled: MutableSet<Point> = mutableSetOf()) {
    fun on(points: Set<Point>): Grid {
        enabled += points
        return this
    }
    fun off(points: Set<Point>): Grid {
        enabled -= points
        return this
    }
}


// on x=10..12,y=10..12,z=10..12
fun parse(line: String): Action {
    val re = Regex("(on|off) x=(-?\\d+)..(-?\\d+),y=(-?\\d+)..(-?\\d+),z=(-?\\d+)..(-?\\d+)")
    return re.matchEntire(line)?.destructured?.let { (action, x1, x2, y1, y2, z1, z2) ->
        Action(
            action == "on",
            from = Point(x1.toInt(), y1.toInt(), z1.toInt()),
            to = Point(x2.toInt(), y2.toInt(), z2.toInt())
        ).also { (_, from, to ) ->
            // Make sure we are not making the wrong assumption
            assert(from.x < to.x && from.y < to.y && from.z < to.z)
        }
    } ?: error("Failed to parse $line")
}

fun part1(actions: List<Action>) {
    val initial = Grid()
    val validMin = Point(-50, -50, -50)
    val validMax = Point(50, 50, 50)
    val result = actions.fold(initial){grid, action ->
        val points = action.points()
        //val points = action.points(validMin, validMax)
        if(action.on) grid.on(points) else grid.off(points)
    }

    println("Part 1: ${result.enabled.count()}")

}

fun main() {
    val actions = readInput("year2021/small.input").map(::parse)
    part1(actions)
}