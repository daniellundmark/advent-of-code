package year2021.day17

import util.max
import util.min
import util.readInput
import kotlin.math.floor
import kotlin.math.sqrt

data class Pos(val x: Int, val y: Int)
data class Area(val minX: Int, val maxX: Int, val minY: Int, val maxY: Int) {
    fun contains(pos: Pos) = (pos.x in minX..maxX) && (pos.y in minY..maxY)
}

fun parse(line: String): Area {
    return Regex("target area: x=(-?\\d+)..(-?\\d+), y=(-?\\d+)..(-?\\d+)")
        .matchEntire(line)?.destructured?.let { (a, b, c, d) ->
            Area(
                minX = min(a.toInt(), b.toInt()), maxX = max(a.toInt(), b.toInt()),
                minY = min(c.toInt(), d.toInt()), maxY = max(c.toInt(), d.toInt())
            )
        } ?: error("Failed to parse $line as Area")
}

// Returns the path a probe follows using initialVel until it passes the target area
// Note: the minY/maxY goes downwards, so the probe will pass minY last
fun path(initialVelocity: Pos, target: Area): List<Pos> {

    val path = sequence<Pos> {
        var position = Pos(0,0)
        var velocity = initialVelocity
        yield(position)
        while (position.x < target.maxX && position.y > target.minY) {
            position = Pos(
                x = position.x + velocity.x,
                y = position.y + velocity.y
            )
            velocity = Pos(
                x = velocity.x - velocity.x.compareTo(0),
                y = velocity.y - 1
            )
            yield(position)
        }
    }
    return path.toList()
}

// Tries an initial velocity and returns the maximum position.y in case the probe hits the target
fun tryVelocity(initialVelocity: Pos, target: Area): Int? {
    val path = path(initialVelocity, target)
    return if(path.any { target.contains(it) }) path.maxOf { it.y } else null
}

// A minor (unnecessary) optimisation instead of taking minX = 0
// solving (x * (x + 1))/2 = t.minX for x gives this formula for the first velocity.X which hits the target
fun minVelX(targetMinX: Int): Int = floor(0.5 * (sqrt(8*targetMinX.toDouble() + 1) - 1)).toInt()

fun trials(target: Area) {
    // The maximum theoretical dx is aiming for the end of the target area
    val attempts = (minVelX(target.minX)..target.maxX).flatMap { x ->
        (target.minY..(-target.minY)).map { y->
            Pos(x, y)
        }
    }
    val successes = attempts.mapNotNull { tryVelocity(it, target) }
    println("Part 1: (${attempts.size} total attempts) "+successes.maxOf { it })
    println("Part 2: (${attempts.size} total attempts) ${successes.size}")
}

fun main() {
    val target = parse(readInput("year2021/day17.input").first())
    trials(target)
}