package year2021.day13

import util.measureTime
import util.readInput
import util.split

data class Dot(val x: Int, val y: Int)
data class Fold(val x: Int, val y: Int)

fun Set<Dot>.toDisplayString(): String {
    val maxX = this.maxOf { it.x }
    val maxY = this.maxOf { it.y }

    return (0..maxY).joinToString(separator = "\n") { y ->
        (0..maxX).map { x -> if(this.contains(Dot(x,y))) '#' else ' ' }.joinToString(separator = "")
    }
}

fun foldByInstruction(dot: Dot, fold: Fold): Dot =
    Dot(
        x = if(fold.x < dot.x) 2*fold.x - dot.x else dot.x,
        y = if(fold.y < dot.y) 2*fold.y - dot.y else dot.y
    )

fun foldByInstruction(grid: Set<Dot>, instruction: Fold): Set<Dot> = grid.map { foldByInstruction(it, instruction) }.toSet()

fun main() {

    val lines = readInput("year2021/day13.input")
    val (dots, instructions) = lines.split { it.isBlank() }
    val grid = dots.map { it.split(",").let { (x, y) -> Dot(x.toInt(), y.toInt()) } }.toSet()
    val folds = instructions.mapNotNull { Regex("fold along ([x,y])=(\\d+)").matchEntire(it)?.destructured?.let { (axis, pos) ->
        Fold(
            x = if(axis == "x") pos.toInt() else Int.MAX_VALUE,
            y = if(axis == "y") pos.toInt() else Int.MAX_VALUE
        )
    } }

    println("Part 1: ${foldByInstruction(grid, folds.first()).size}")

    measureTime {
        val result = folds.fold(grid){acc, instruction -> foldByInstruction(acc, instruction) }
        println("Part 2: ")
        println(result.toDisplayString())
    }

}