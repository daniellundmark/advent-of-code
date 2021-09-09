package year2020.day11

import util.measureTime
import util.readInput

// Representation of a day11.Seat
//  L = empty = false, # = occupied = true, . = floor = null
// Save alternating iterations as odd/even to maybe avoid some garbage collection
class Seat(occupied: Char, pos: Position, tolerance: Int, grid: Grid) {
    private val grid = grid
    private val pos = pos
    private var isEven = true
    private val tolerance = tolerance
    private var even: Boolean? = when(occupied) {
        'L' -> false
        '#' -> true
        else -> null
    }
    private var odd: Boolean? = null

    private var neighbours: List<Seat?>? = null

    private fun isChair() = getValue() != null
    fun isOccupied() = if(isEven) even == true else odd == true

    private fun getValue(): Boolean? = if(isEven) even else odd
    private fun getNextValue(): Boolean? = if(isEven) odd else even
    private fun setNextValue(value: Boolean?) = if(isEven) odd = value else even = value

    override fun toString(): String {
        return when(getValue()) {
            true -> "#"
            false -> "L"
            else -> "."
        }
    }

    private fun neighbours(): List<Seat?> {
        if(neighbours == null) {
           initNeighbours(false)
        }
        return neighbours as List<Seat?>
    }
    private fun countNeighbours() = neighbours().count { it?.isOccupied() == true }

    fun iterate() {
        val countNeighbours = countNeighbours()
        setNextValue(getValue())
        when(getValue()) {
            false -> if(countNeighbours == 0) setNextValue(true)
            true -> if(countNeighbours >= tolerance) setNextValue(false)
            null -> { } // Nothing happens to empty floor space
        }
    }

    fun willChange(): Boolean = odd != even

    fun move() {
        isEven = !isEven
    }


    // We want 8 lists of neighbour "directions"
    fun initNeighbours(onlyFirst: Boolean) {

        // Hmm... this was over complicated, they can only see the first day5.seat, regardless of whether it is occupied...
        val nw = mutableListOf<Seat>()
        val n = mutableListOf<Seat>()
        val ne= mutableListOf<Seat>()
        val w= mutableListOf<Seat>()
        val e= mutableListOf<Seat>()
        val sw= mutableListOf<Seat>()
        val s= mutableListOf<Seat>()
        val se= mutableListOf<Seat>()

        var xx: Int; var yy: Int

        // Populate nw
        for(pos in (pos.x - 1 downTo 0).zip(pos.y - 1 downTo 0).map { Position(it.first, it.second) }) {
            nw.add(grid.get(pos)!!)
        }

        // Populate n
        for(pos in (pos.y - 1 downTo 0).map { Position(pos.x, it) }) {
            n.add(grid.get(pos)!!)
        }

        // Populate ne
        for(pos in (pos.x + 1 until grid.sizex).zip(pos.y - 1 downTo 0).map { Position(it.first, it.second) }) {
            ne.add(grid.get(pos)!!)
        }

        // Populate w
        for(pos in (pos.x - 1 downTo 0).map { Position(it, pos.y) }) {
            w.add(grid.get(pos)!!)
        }

        // Populate e
        for(pos in (pos.x + 1 until grid.sizex).map { Position(it, pos.y) }) {
            e.add(grid.get(pos)!!)
        }

        // Populate sw
        for(pos in (pos.x - 1 downTo 0).zip(pos.y + 1 until grid.sizey).map { Position(it.first, it.second) }) {
            sw.add(grid.get(pos)!!)
        }

        // Populate s
        for(pos in (pos.y + 1 until grid.sizey).map { Position(pos.x, it) }) {
            s.add(grid.get(pos)!!)
        }

        // Populate se
        for(pos in (pos.x + 1 until grid.sizex).zip(pos.y + 1 until grid.sizey).map { Position(it.first, it.second) }) {
            se.add(grid.get(pos)!!)
        }

        val fullRange = listOf(
            nw, n, ne,
            w, e,
            sw, s, se)

        this.neighbours =
            fullRange.map {
                if(onlyFirst) it.firstOrNull()
                else it.firstOrNull (Seat::isChair)
            }

    }

}

data class Position(val x: Int, val y: Int)

class Grid(x: Int, y: Int)  {
    val sizex = x
    val sizey = y
    private val seats = mutableMapOf<Position, Seat>()
    fun put(pos: Position, seat: Seat) { seats[pos] = seat }
    fun get(pos: Position): Seat? = seats[pos]

    override fun toString(): String {
        val builder = StringBuilder()
        for(y in 0 until sizey) {
            for(x in 0 until sizex) {
                builder.append(this.get(Position(x, y)).toString())
            }
            builder.append("\n")
        }
        return builder.toString()
    }

    fun initNeighbours(onlyFirst: Boolean) {
        for(y in 0 until sizey) {
            for (x in 0 until sizex) {
                seats[Position(x, y)]?.initNeighbours(onlyFirst)
            }
        }
    }

    fun iterate(): Position? {
        for(y in 0 until sizey) {
            for (x in 0 until sizex) {
                seats[Position(x, y)]?.iterate()
            }
        }
        val anyChanged = anyWillChange()
        for(y in 0 until sizey) {
            for (x in 0 until sizex) {
                seats[Position(x, y)]?.move()
            }
        }
        return anyChanged
    }

    private fun anyWillChange(): Position? {
        for(y in 0 until sizey) {
            for (x in 0 until sizex) {
                if(seats[Position(x, y)]!!.willChange()) return Position(x, y)
            }
        }
        return null
    }

    fun occupied() = seats.values.count { it.isOccupied() }
}

fun main() {

    measureTime()
    {

        val lines = readInput("year2020/day11.input")
        val grid = Grid(lines[0].length, lines.size)

        // Parse the initial grid
        for (y in lines.indices) {
            for (x in lines[y].indices) {
                grid.put(Position(x, y), Seat(lines[y][x], Position(x, y), 4, grid))
            }
        }

        grid.initNeighbours(true)

        // println(grid)

        var i = 0
        var changed: Position? = Position(0, 0)
        while (changed != null) {
            changed = grid.iterate()
            i += 1
            //println("Iteration: $i $changed")
            //println(grid)
        }


        println(grid.occupied())

    }
}