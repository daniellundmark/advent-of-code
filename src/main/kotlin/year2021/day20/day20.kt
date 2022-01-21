package year2021.day20

import util.readInput
import util.rest

const val DARK = '.'
const val LIGHT = '#'

private data class Pixel(val x: Int, val y: Int) {
    fun neighbours(): List<Pixel> =
        listOf(
            Pixel(this.x-1, this.y-1), Pixel(this.x, this.y-1), Pixel(this.x+1, this.y-1),
            Pixel(this.x-1, this.y), Pixel(this.x, this.y), Pixel(this.x+1, this.y),
            Pixel(this.x-1, this.y+1), Pixel(this.x, this.y+1), Pixel(this.x+1, this.y+1))
}
private data class Grid(val pixels: Map<Pixel, Char>, val default: Char, val translation: String) {

    fun getOrDefault(pos: Pixel) = this.pixels[pos] ?: default

    fun binary(pixel: Pixel): Char = if(this.getOrDefault(pixel) == LIGHT) '1' else '0'

    fun binary(pp: List<Pixel>): String =
        pp
            .map { binary(it) }
            .joinToString(separator = "")

    fun binaryInt(pp: List<Pixel>): Int = binary(pp).toInt(2)

    fun nextPixelValue(p: Pixel): Char {
        return translation[binaryInt(p.neighbours())]
    }

    fun pixelsWithBorder(): Set<Pixel> = pixels.keys.flatMap { it.neighbours().toSet() }.toSet()

    fun nextGrid(): Grid {
        val nextImage = pixelsWithBorder().associateWith { nextPixelValue(it) }
        return Grid(nextImage, if(default == DARK) LIGHT else DARK, translation)
    }

    fun toDisplayString(): String {
        val minX = this.pixels.keys.minOf { it.x }
        val maxX = this.pixels.keys.maxOf { it.x }
        val minY = this.pixels.keys.minOf { it.y }
        val maxY = this.pixels.keys.maxOf { it.y }

        return (minY..maxY).joinToString(separator = "\n") { y ->
            (minX..maxX).map { x -> this.pixels[Pixel(x, y)] ?: DARK }.joinToString(separator = "")
        }
    }

}

private fun printlnGrid(grid: Grid, iteration: Int) {
    println("Iteration $iteration")
    println(grid.toDisplayString())
    println()
}

private fun solve(input: Grid) {

    var grid = input

    //printlnGrid(grid, 0)

    (1..50).forEach {
        grid = grid.nextGrid()
        //printlnGrid(grid, it)

        if(it == 2) {
            println("Part 1: ${grid.pixels.values.count { it == LIGHT }}")
        }

    }

    println("Part 2: ${grid.pixels.values.count { it == LIGHT }}")
}

fun main() {
    val lines = readInput("year2021/day20.input")
    val translation = lines.first()
    val grid = lines.rest().rest().flatMapIndexed { y, line ->
        line.mapIndexed { x, c ->
               Pixel(x, y) to c
        }
    }.toMap().let { Grid(it, DARK, translation) }

    solve(grid)

}