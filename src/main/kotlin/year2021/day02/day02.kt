package year2021.day02

import util.readInput

enum class Direction {
    FORWARD, UP, DOWN;
    companion object {
        fun parse(line: String) = valueOf(line.uppercase())
    }
}

data class Instruction (val dir: Direction, val dist: Int) {

    companion object {
        private val regex = Regex("(\\w+) (\\d+)")
        fun parse(line: String): Instruction {
            return regex.matchEntire(line)
                ?.destructured
                ?.let { (dir, dist) -> Instruction(Direction.parse(dir), dist.toInt()) }
                ?: error("Could not parse $line")
        }
    }
}

data class PosPart1 (val x: Int, val z: Int) {
    fun move(line: String): PosPart1 {
        val inst = Instruction.parse(line)
        return when(inst.dir) {
            Direction.FORWARD -> PosPart1(x + inst.dist, z)
            Direction.DOWN -> PosPart1(x, z + inst.dist)
            Direction.UP -> PosPart1(x, z - inst.dist)
        }
    }
}

data class PosPart2 (val x: Int, val z: Int, val aim: Int) {

    fun move(line: String): PosPart2 {
        val inst = Instruction.parse(line)
        return when(inst.dir) {
            Direction.FORWARD -> PosPart2(x + inst.dist, z + inst.dist * aim, aim)
            Direction.DOWN -> PosPart2(x, z, aim + inst.dist)
            Direction.UP -> PosPart2(x, z, aim - inst.dist)
        }
    }

}

fun main() {
    val input = readInput("year2021/day02.input")
    val small = readInput("year2021/small.input")

    val p1 = input.fold(PosPart1(0, 0)) { p, line -> p.move(line) }
    println("Part 1: End: $p1, Result: ${p1.x * p1.z}")

    val p2 = input.fold(PosPart2(0, 0, 0)) {p, line -> p.move(line)}
    println("Part 2: End: $p2, Result: ${p2.x * p2.z}")
}