package year2019.day06

import util.Graph
import util.readInput

tailrec fun length(orbits: Map<String, String>, from: String, acc: Int = 0): Int {
    return when(val orbit = orbits[from]) {
        null -> error("Missing orbit for $from")
        "COM" -> acc + 1
        else -> length(orbits, orbit, acc + 1)
    }
}

fun part1(orbits: Map<String, String>) {
    println(orbits.keys.map { length(orbits, it) }.sum())
}

fun part2(orbits: Map<String, String>) {

    val graph = Graph<String>()
    orbits.forEach{ (a, b) -> graph.addBidirectionalEdge(a, b)}

    println(graph)

    val from = orbits["YOU"] ?: error("Could not find orbit for you")
    val to = orbits["SAN"] ?: error("Could not find orbit for Santa")

    val dist = graph.bfs("YOU", "SAN") ?: error("Failed BFS in graph")

    println((dist["SAN"] ?: error("")) -2)


}

fun main() {
    val orbits = readInput("year2019/day06.input")
        .map {
            val (left, right) = it.split(")")
            right to left
        }.toMap()

    println(orbits)

    part1(orbits)

    part2(orbits)

}
