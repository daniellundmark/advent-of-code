package year2020.day20

import util.*
import kotlin.math.sqrt

data class Pos(val x: Int, val y: Int)

data class Edge(val tile: Tile, val data: List<Boolean>) {
    fun matches(edge: Edge) = this.data == edge.data && this.tile.id != edge.tile.id
    fun matchesAny(edges: Collection<Edge>) = edges.any { e -> this.matches(e) }
    fun flip() = Edge(tile, data.reversed())
}

data class Tile(val id: Int, val data: List<List<Boolean>>) {

    fun edges(): Set<Edge> {
        // current top/bottom, left/right normal and flipped
        //
        return setOf(
                this.top(),
                this.bottom(),
                this.left(),
                this.right()
        ).toSet()
    }

    fun top() = Edge(this, this.data.first())
    fun bottom() = Edge(this, this.data.last())
    fun left() = Edge(this, this.data.map { it.first() })
    fun right() = Edge(this, this.data.map { it.last() })

    // Returns this tile flipped (reverse all lists)
    fun flip() = Tile(id = id, data = data.map { it.reversed() })

    // Rotates this tile (transpose all lists)
    fun rotate(): Tile {
        return Tile(id = id, data = data.transpose().map { it.reversed() })
    }

    fun sizeColumns() = this.data.first().size
    fun sizeRows() = this.data.size

    // Returns all rotated/flipped variants of this tile
    fun variants(): Set<Tile> {

        val a = this
        val b = a.rotate()
        val c = b.rotate()
        val d = c.rotate()

        val e = this.flip()
        val f = e.rotate()
        val g = f.rotate()
        val h = g.rotate()
        val i = h.rotate()


        return setOf(
                a, b, c, d,
                e, f, g, h
        )

    }

    override fun toString(): String {
        return "Tile $id:\n" + data.map { row -> row.map { col -> if(col) "#" else "." }.joinToString ("") }.joinToString("\n")
    }

}

operator fun Set<Tile>.get(id: Int): Tile = this.find { it.id == id } ?: error("Failed to find Tile=$id")

fun Set<Edge>.matches(edge: Edge): Set<Edge> = this.filter { it.matches(edge) }.toSet()
fun Set<Edge>.hasMatch(edge: Edge): Boolean = this.any { it.matches(edge) }

fun Set<Edge>.findUniqueMatchingTile(edgeToMatch: Edge): Tile {
    val matching = this.matches(edgeToMatch)
    if(matching.size != 1) {
        error("Expected unique match for edge $edgeToMatch, found ${matching.size}")
    }
    return matching.first().tile
}

val seaMonster:List<List<Boolean>> = listOf(
        "                  # ",
        "#    ##    ##    ###",
        " #  #  #  #  #  #   "
).map{ list -> list.map { it == '#' } }

const val seaMonsterWidth = 20
const val seaMonsterHeight = 3
const val seaMonsterPixels = 15

fun checkForSeaMonster(image: Tile, offsetX: Int, offsetY: Int): Boolean {

    var hits = 0


    if(offsetY + seaMonsterHeight > image.data.size) {
        error("Attempt to look to far down, image is only ${image.data.size} rows")
    }
    if(offsetX + seaMonsterWidth > image.data.first().size) {
        error("Attempt to look to far to the right, image is only ${image.data.first().size} columns")
    }

    for (y in 0 until seaMonsterHeight) {
        for(x in 0 until seaMonsterWidth) {
            val hit = seaMonster[y][x] && image.data[y+offsetY][x+offsetX]
            //print(if(hit) "#" else ".")
            if(hit) hits++
        }
        //println()
    }

    val found = hits == seaMonsterPixels
    //println("Looking for sea monster at ($offsetX, $offsetY): $found")

    return found
}

fun countSeaMonsters(image: Tile): Int {
    var foundMonsters = 0
    for(offsetY in 0 .. image.sizeRows()- seaMonsterHeight) {
        for(offsetX in 0 .. image.sizeColumns() - seaMonsterWidth) {
            val found = checkForSeaMonster(image, offsetX, offsetY)
            if(found) foundMonsters++
        }
    }
    return foundMonsters
}

fun main() {

    val groups = readInput("year2020/day20.input").split(String::isBlank)

    val titleRegex = "Tile (\\d+):".toRegex()

    // Read in the tiles
    val tiles = groups.map { group ->
        val (id) = (titleRegex.matchEntire(group.first())?:error("Bad title: ${group.first()}")).destructured
        Tile(id = id.toInt(), data = group.rest().map { it.map { c -> c == '#' } })
    }.toSet()

    // A set of all possible Edges for tiles, considering rotation and flips
    val allEdges = tiles.flatMap { it.edges() }.toSet() + tiles.flatMap { it.edges() }.map { it.flip() }

    // First find a tile which can be top-left, needs two edges without matches
    val corners: MutableSet<Tile> = mutableSetOf()
    val frame: MutableSet<Tile> = mutableSetOf()
    for(tile in tiles) {
        val theseEdges = tile.edges().toList()
        // Count the number of edges that have no other match
        val edgesWithNoMatch = tile.edges().count { allEdges.matches(it).isEmpty() }
        //println("Tile ${tile.id} has $edgesWithNoMatch with no match")
        if(edgesWithNoMatch == 2) {
            //println("Tile ${tile.id} is a corner")
            corners += tile
        } else if(edgesWithNoMatch == 1) {
            //println("Tile ${tile.id} is part of the frame")
            frame += tile
        }
    }
    if(corners.isEmpty()) {
        println("FAILED to find top left tile!")
        return
    }

    val size = sqrt(tiles.size*1.0).toInt()
    println("Total tiles: ${tiles.size}, dimensions:${size}x${size}")
    println("Corners: ${corners.map { it.id }}")
    println("Frame: ${frame.map { it.id }}")

    // Answer to part 1
    println("Answer to part 1: ${corners.map { it.id.toLong()}.multiply()}")

    // Build the "jigsaw"

    // Find the top left piece, it is one that has a matching right and bottom edge
    val topLeft = corners.first { allEdges.hasMatch(it.right()) && allEdges.hasMatch(it.bottom()) }
    //val unused: MutableSet<Tile> = tiles.toMutableSet()
    val jigsaw = mutableMapOf<Pos, Tile>().apply { this[Pos(0, 0)] = topLeft }

    println("Top left is:\n$topLeft")

    // Build the left edge
    for(y in 1 until size) {
        // We know the previous piece is placed
        val prev = jigsaw[Pos(0, y-1)] ?: error("Expected piece (0,${y-1} to have been placed")
        var next = allEdges.findUniqueMatchingTile(prev.bottom())

        // We need to rotate/flip the tile so that it is the _top_ edge which matches prevs _bottom_
        val variant = next.variants().find { it.top().matches(prev.bottom()) } ?: error("Could not find variant with correct top edge")
        jigsaw[Pos(0, y)] = variant

        //unused -= next
    }
    // Build the rest of the jigsaw
    for(y in 0 until size) {
        for(x in 1 until size) {
            val prev = jigsaw[Pos(x-1, y)] ?: error("Expected piece (${x-1},${y} to have been placed")
            val next = allEdges.findUniqueMatchingTile(prev.right())
            // We need to rotate/flip the tile so that it is the _left_ edge which matches prevs _right_
            val variant = next.variants().find { it.left().matches(prev.right()) } ?: error("Could not find variant with correct left edge")

            jigsaw[Pos(x, y)] = variant
            //unused -= next
        }
    }

    println("Tile placements:")
    for (y in 0 until size) {
        for (x in 0 until size) {
            print("${jigsaw[Pos(x, y)]!!.id} ")
        }
        println()
    }

    // Construct the combined image
    println("\nEntire jigsaw:")
    val matrix = mutableListOf<MutableList<Boolean>>()
    for(y in 0 until size * 10) {
        // We don't need lists for first/last row of each tile
        if(y%10 != 0 && y%10 != 9) {
            matrix += mutableListOf<Boolean>()
        }
        for(x in 0 until size * 10) {
            // find the tile to read from
            val xTile = x / 10
            val yTile = y / 10
            val xx = x % 10 // x/col position within the tile
            val yy = y % 10 // y/row position within the tile

            val data = jigsaw[Pos(xTile,yTile)]!!.data[yy][xx]
            print(if(data) "#" else ".")

            // Skip the first/last row of each tile
            if(xx == 0 || xx == 9) {
                // Skip
            }
            // Skip the first/last column of each tile
            else if(yy == 0 || yy == 9) {

            } else {
                matrix.last() += data
            }

            if(xx == 9) print(" ")
        }
        println()
        if(y % 10 == 9) println()
    }
    println()

    // Stick it in a tile to use the rotate/flip functions
    val imageTile = Tile(0, matrix)

    println("Found in all variants:")
    val foundInAll = imageTile.variants().map { countSeaMonsters(it) }
    println(foundInAll)

    val totalPixelsInImage = imageTile.data.map { row -> row.count {it} }.sum()
    val monsterPixels = foundInAll.sum() * seaMonsterPixels
    println("Total pixels: $totalPixelsInImage")
    println("Total in monsters: $monsterPixels")
    println("Pixels that are not monsters: "+(totalPixelsInImage - monsterPixels))


}