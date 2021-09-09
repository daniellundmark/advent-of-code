package year2020.day17

import util.measureTime
import util.readInput
import java.lang.StringBuilder

data class Point(val x: Int, val y: Int, val z: Int = 0, val w:Int = 0) {
    fun neighbors() =
            (-1..1).flatMap { dw ->
                (-1..1).flatMap { dz ->
                    (-1..1).flatMap { dy ->
                        (-1..1).map { dx ->
                            Point(x + dx, y + dy, z + dz, w + dw)
                        }
                    }
                }
            } - this

    fun countActiveNeighbours(activePoints: Set<Point>): Int =
            this.neighbors().filter { activePoints.contains(it) }.count()

}

// Print a 2D representation of a specific slice
fun Set<Point>.toString(zz: Int, ww: Int): String {
    val slice = this.filter { it.z == zz && it.w == ww}
    val x0 = this.minOf { it.x }
    val xn = this.maxOf { it.x }
    val y0 = this.minOf { it.y }
    val yn = this.maxOf { it.y }

    val sb = StringBuilder()
    for(x in x0 .. xn) {
        for(y in y0 .. yn) {
            sb.append(if(slice.contains(Point(x, y, zz, ww))) '#' else '.')
        }
        sb.append("\n")
    }

    return sb.toString()
}

fun main() {

    val max = 6

    measureTime {

        var lines = readInput("year2020/day17.input").map { line -> line.map { it == '#' } }

        // Read in the initial points
        var actives = lines.flatMapIndexed{ x, line ->
            line.mapIndexedNotNull{ y, value ->
                if(value) Point(x, y) else null
            }
        }.toSet()

       // println("z=0:\n${actives.toString(0)}")

        for(iteration in 0 until max) {

            // Collect all active points _and all neighbors_ from the previous iteration
            val candidates = actives.toSet() + actives.flatMap { it.neighbors() }

            // Calculate what points are active in the next iteration
            val nextActives = candidates.mapNotNull { point ->
                val willBeActive = when (actives.contains(point)) {
                    true -> point.countActiveNeighbours(actives) in 2..3
                    else -> point.countActiveNeighbours(actives) == 3
                }
                if(willBeActive) point else null
            }.toSet()

            println("After ${iteration+1} cycles: prev:${actives.count()} next:${nextActives.count()}")

            actives = nextActives

            //for(z in actives.minOf { it.z } .. actives.maxOf { it.z }) {
            //   println("z=$z\n${actives.toString(z)}")
            //}
        }
        println(actives.count())
    }

}