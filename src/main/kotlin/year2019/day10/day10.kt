package year2019.day10

import util.readInput
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.sqrt

data class Point(val x: Int, val y: Int) {
    override fun toString(): String {
        return "($x,$y)"
    }
}

data class PolarCoord(val phi: Double, val r: Double) {
    override fun toString(): String {
        return "($phi:$r)"
    }
    companion object {
        fun fromPoints(azimuth: Point, other: Point): PolarCoord {
            val dy = other.y - azimuth.y
            val dx = other.x - azimuth.x
            return PolarCoord(
                phi = atan2(dy, dx),
                r = sqrt(dx*dx + dy*dy)
            )
        }
    }
}

fun atan2(y: Int, x: Int) = atan2(y.toDouble(), x.toDouble())
fun sqrt(x: Int) = sqrt(x.toDouble())

fun main() {
    val lines = readInput("year2019/day10.input").map { it.toList().map { c -> c == '#' } }

    val points = lines.flatMapIndexed{ y, line -> line.mapIndexed{ x, c ->
        if(c) Point(x, y) else null
    } }.filterNotNull()

    val counts = points.map { point ->
        val count = (points - point).map { other -> atan2(other.y - point.y, other.x - point.x) }.toSet().count()
        point to count
    }.toMap()

    val azimuth = counts.maxByOrNull { it.value }?.key ?: error("Failed to find station")
    println("Part1: $azimuth: "+counts.values.maxOrNull())

    // Transform all other points to polar coordinates
    val polar = (points-azimuth).map { PolarCoord.fromPoints(azimuth, it) to it }.toMap()

    // Rotate them by 90 degrees (PI/2 radians) as "upwards" is where the laser starts and make it start at that point as 0
    fun rotate(phi: Double): Double = (phi + 0.5*PI + 2.0*PI) % (2*PI)
    val rotated = polar.map { PolarCoord(rotate(it.key.phi), it.key.r) to it.value }.toMap()

    val byAngle = Comparator.comparing<Pair<PolarCoord, Any>, Double> { it.first.phi }
    val byDistance = Comparator.comparing<Pair<PolarCoord, Any>, Double> { it.first.r }

    println(points)

    // Make a list which we go through and pick only the first by each angle
    val sortedByAngle = rotated.toList().sortedWith(byAngle).toMutableList()
    val perRotation = mutableListOf<Pair<PolarCoord, Point>>()

    while (sortedByAngle.isNotEmpty()) {
        var prevAngle = Double.NEGATIVE_INFINITY
        for(pair in sortedByAngle) {
            if(pair.first.phi != prevAngle) {
                // Find the one with this angle and the lowest distance
                val min = sortedByAngle.filter { it.first.phi == pair.first.phi }.minByOrNull { it.first.r } ?: error("Failed to find minimum point")

                perRotation += min
                prevAngle = pair.first.phi
            }
        }
        sortedByAngle.removeAll(perRotation)
    }

    println(perRotation)

    val num200 = perRotation[199]

    println(num200)
    println(num200.second.x * 100 + num200.second.y)

}