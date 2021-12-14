package year2021.day11

import util.readInput

data class Pos(val x: Int, val y: Int)
typealias Grid<T> =  Map<Pos, T>

fun Grid<Int>.neighbours(pos: Pos): Set<Pos> =
    listOf(
        Pos(pos.x-1, pos.y-1), Pos(pos.x, pos.y-1), Pos(pos.x+1, pos.y-1),
        Pos(pos.x-1, pos.y), Pos(pos.x+1, pos.y),
        Pos(pos.x-1, pos.y+1), Pos(pos.x, pos.y+1), Pos(pos.x+1, pos.y+1)
    ).filter { this.containsKey(it) }.toSet()

fun<T> Grid<T>.toDisplayString(): String {
    val maxX = this.keys.maxOf { it.x }
    val maxY = this.keys.maxOf { it.y }

    return (0..maxY).joinToString(separator = "\n") { y ->
        (0..maxX).map { x -> this[Pos(x, y)] ?: 0 }.joinToString(separator = "")
    }
}

fun step(grid: Grid<Int>): Grid<Int> {
    // First increase each number 1 step
    val next = grid.map {e -> e.key to e.value + 1 }.toMap().toMutableMap()

    // Then go through and flash all the ones with a value > 9
    val flashing: MutableSet<Pos> = next.filter{ e -> e.value > 9 }.keys.toMutableSet()
    //println("Initial flashing: $flashing")
    while(flashing.isNotEmpty()) {
        val flash = flashing.first()
        flashing.remove(flash)
        next[flash] = 0

        // Get all the neighbours and increase them by 1 (unless they have already flashed)
        val neighbours = next.neighbours(flash)
        neighbours.forEach { n ->
            val neighbourValue = next[n]!!
            if(neighbourValue > 0) { // Skip the ones that have flashed already
                next[n] = neighbourValue + 1
            }
            // Add it if it has flashed
            if(neighbourValue + 1 > 9) {
                flashing.add(n)
            }
        }

    }

    //println("After step $num:")
    //println(next.toDisplayString())
    return next
}

fun flashes(grid: Grid<Int>) {
    //println("Before any steps:")
    //println(grid.toDisplayString())

    var cur = grid
    var flashes = 0L
    (1..100).forEach {
        cur = step(cur)
        flashes += cur.values.count { value -> value == 0 }
        if(cur.values.all { value -> value == 0 }) {
            println("Part 2: $it")
        }
    }
    println("Part 1: $flashes")
    var i = 100
    while(!cur.values.all { it == 0 }) {
        i++
        cur = step(cur)
    }
    println("Part 2: $i")
}

fun main() {
    val input: Grid<Int> = readInput("year2021/day11.input")
        .flatMapIndexed { y, line -> line.mapIndexed { x, c -> Pos(x,y) to c.digitToInt() }}.toMap()

    flashes(input)
}