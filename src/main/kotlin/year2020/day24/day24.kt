package year2020.day24

import util.measureTime
import util.readInput
import year2020.day24.HexDirection.*

enum class HexDirection { E, SE, SW, W, NW, NE }

// A hex tile using axial coordinates
// https://www.redblobgames.com/grids/hexagons/
//    (0,-1)  (+1,-1)
//  (-1,0) (q,r) (+1,0)
//    (-1,+1)  (0,+1)
data class Hex(val q: Int, val r: Int) {
    fun direction(dir: HexDirection): Hex {
        return when (dir) {
            E -> Hex(q + 1, r)
            SE -> Hex(q, r + 1)
            SW -> Hex(q - 1, r + 1)
            W -> Hex(q - 1, r)
            NW -> Hex(q, r - 1)
            NE -> Hex(q + 1, r - 1)
        }
    }

    fun neighbours() = listOf(E, SE, SW, W, NW, NE).map(this::direction)

}

tailrec fun parseDirections(line: String): List<HexDirection> {

    if (line.isBlank()) return emptyList()

    return when (line[0]) {
        'e' -> listOf(E) + parseDirections(line.substring(1))
        'w' -> listOf(W) + parseDirections(line.substring(1))
        's' -> when (line[1]) {
            'e' -> listOf(SE) + parseDirections(line.substring(2))
            'w' -> listOf(SW) + parseDirections(line.substring(2))
            else -> error("parse error: $line")
        }
        'n' -> when (line[1]) {
            'e' -> listOf(NE) + parseDirections(line.substring(2))
            'w' -> listOf(NW) + parseDirections(line.substring(2))
            else -> error("parse error: $line")
        }
        else -> error("parse error: $line")
    }

}

fun main() {

    measureTime {

        val lines = readInput("year2020/day24.input").map(::parseDirections)

        var blacks = mutableSetOf<Hex>()

        val start = Hex(0, 0)

        // Fix initial
        for (directions in lines) {
            var cur = start
            for (dir in directions) {
                cur = cur.direction(dir)
            }
            if (blacks.contains(cur)) {
                blacks.remove(cur)
            } else {
                blacks.add(cur)
            }
        }

        println("Day 0: ${blacks.size}")

        // Play game of tiles
        for (i in 1..100) {

            var next = mutableSetOf<Hex>()

            // Coordinates of being blacks are current blacks, plus their neighbours
            val candidates = blacks + blacks.flatMap { it.neighbours() }.toSet()

            for (tile in candidates) {
                val neighbours = tile.neighbours().count { blacks.contains(it) }
                if (blacks.contains(tile) && !(neighbours == 0 || neighbours > 2)) {
                    next.add(tile)
                }
                if (!blacks.contains(tile) && neighbours == 2) {
                    next.add(tile)
                }
            }
            blacks = next

            println("Day $i: ${blacks.size}")

        }

        println("Final: ${blacks.size}")

    }
}