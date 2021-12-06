package year2021.day04

import util.readInput
import util.rest

data class Pos(val x: Int, val y: Int)

data class BingoTile(val number: Int, var marked: Boolean = false)
fun List<BingoTile>.checkForWin() = this.all { it.marked }
data class BingoBoard(val idx: Int, val tiles: MutableMap<Pos, BingoTile> = mutableMapOf()) {
    fun findTile(number: Int): BingoTile? =
        tiles.values.find { it.number == number }
    fun mark(number: Int) {
        findTile(number)?.let { it.marked = true }
    }
    fun addTile(pos: Pos, tile: BingoTile) {
        assert(findTile(tile.number) == null) {"Tile $tile already exists in board"}
        assert(tiles[pos] == null){"Position $pos already used in board"}
        assert(pos.x in 0..4 && pos.y in 0..4){"Invalid position: $pos"}
        tiles[pos] = tile
    }
    fun unmarked(): List<BingoTile> = tiles.values.filter { !it.marked }
    fun row(y: Int): List<BingoTile> {
        return (0..4).map { Pos(it, y) }.map { tiles[it] ?: error("Failed to find tile with pos $it ") }
    }
    fun col(x: Int): List<BingoTile> {
        return (0..4).map { Pos(x, it) }.map { tiles[it] ?: error("Failed to find tile with pos $it ") }
    }
    fun checkForWin(): Boolean {
        val rowsAndCols = (0..4).map { row(it) } + (0..4).map { col(it) }
        return rowsAndCols.any { it.checkForWin() }
    }
}
data class BingoGame(val draws: List<Int>, val boards: List<BingoBoard>)

fun parseBoard(idx: Int, lines: List<String>): BingoBoard {
    val board = BingoBoard(idx)
    lines.mapIndexed{y, line ->
        line.trim().split(Regex("\\s+"))
            //.peek { println("ZZQ: $it") }
            .map { it.toInt() }
            .mapIndexed{ x, number ->
                board.addTile(Pos(x, y), BingoTile((number)))
    }}
    return board
}

fun parse(input: List<String>): BingoGame {

    // First row is the drawn numbers
    val draws = input.first().split(",").map { it.toInt() }
    val boardLines = input.rest().windowed(6, 6).map { it.rest() }

    val boards = boardLines.mapIndexed { idx, lines -> parseBoard(idx, lines) }
    return BingoGame(draws, boards)
}

fun part1(game: BingoGame) {

    // Draw each number until some board has won
    for(draw in game.draws) {
        game.boards.forEach { it.mark(draw) }

        val winner = game.boards.find { it.checkForWin() }
        if(winner != null) {
            val sum = winner.unmarked().sumOf { it.number }
            println("Part1: $draw $sum ${sum * draw}")
            break
        }
    }

}

fun part2(game: BingoGame) {

    println("Part2")

    val winners = mutableSetOf<Int>()

    // Play all the draws and order the winners
    for(draw in game.draws) {
        //println("Drawing $draw")
        game.boards.forEach { it.mark(draw) }
        game.boards.forEach { board ->
            if(board.checkForWin() && !winners.contains(board.idx)) {
                println("Board ${board.idx} is a winner after drawing $draw, we now have ${winners.size} winners")
                winners.add(board.idx)
                if(winners.size == game.boards.size) {
                    val sum = board.unmarked().sumOf { it.number }
                    println("Part2: $draw $sum ${sum * draw}")
                    return
                }

            }
        }

    }

}

fun main() {
    val input = readInput("year2021/day04.input")
    val small = readInput("year2021/small.input")

    part1(parse(input))
    part2(parse(input))
}


