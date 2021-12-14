package year2021.day12

import util.measureTime
import util.readInput
import java.util.*
import kotlin.time.measureTimedValue

fun String.isLowerCase() = this.all { it.isLowerCase() }

class Graph<T> {

    private val adjacencyMap = mutableMapOf<T, MutableSet<T>>()

    fun nodes(): Set<T> {
        return adjacencyMap.keys
    }

    fun neighbours(node: T): Set<T> {
        return adjacencyMap[node] ?: error("$node is not a member of the graph")
    }

    fun addBidirectionalEdge(sourceVertex: T, destinationVertex: T) {
        addEdge(sourceVertex, destinationVertex)
        addEdge(destinationVertex, sourceVertex)
    }

    fun addEdge(sourceVertex: T, destinationVertex: T) {
        adjacencyMap.computeIfAbsent(sourceVertex) { mutableSetOf()}.add(destinationVertex)
    }

    override fun toString() : String = StringBuffer().apply {
        for (key in adjacencyMap.keys) {
            append("$key -> ")
            append(adjacencyMap[key]?.joinToString(", ", "[", "]\n"))
        }
    }.toString()

}

fun parse(lines: List<String>): Graph<String> {
    // Construct a graph from the links
    val graph = Graph<String>()
    lines.forEach {
        val (a, b) = it.split("-")
        graph.addBidirectionalEdge(a, b)
    }
    return graph
}

data class BFSState (val node: String, val path: List<String>, val hasAllowedDouble: Boolean)

fun bfs(graph: Graph<String>, start: String, end: String, doubleAllowed: String? = null): Set<List<String>> {

    // Create a queue for BFS
    val queue: Queue<BFSState> = LinkedList()

    val paths = mutableSetOf<List<String>>()

    // Initial step -> add the startNode to the queue.
    queue.add(BFSState(node = start, path = emptyList(), hasAllowedDouble = false))


    while(queue.isNotEmpty()) {
        val state = queue.remove() ?: error("Queue should not be empty here")

        val (node, path, hasAllowedDouble) = state

        // If we found the target, save the path to it
        if(node == end) {
            paths.add((state.path + end))
            continue
        }

        graph.neighbours(node).forEach { n ->
            // Don't push a path containing an already visited node (except for possibly the one which allows a double)
            val visited = path.filter { it.isLowerCase() }.toSet()
            val isNewVisitToDoubleAllowed = n == doubleAllowed && visited.contains(n) && !hasAllowedDouble
            if(!visited.contains(n)) {
                queue.add(BFSState(node = n, path = path + node, hasAllowedDouble = hasAllowedDouble))
            } else if(visited.contains(n) && isNewVisitToDoubleAllowed) {
                queue.add(BFSState(node = n, path = path + node, hasAllowedDouble = true))
            }
        }

    }

    //println(paths)

    return paths

}

fun part1(graph: Graph<String>) {
    // Do a BFS-search through the graph from "start" to "end", save all paths
    val paths = bfs(graph, "start", "end")
    //println("Part 1, all paths:")
    //println(paths.joinToString(separator = "\n"))
    println("Part 1: ${paths.size}")
}

fun part2(graph: Graph<String>) {

    // Find all possible lower case nodes
    val lowers = graph.nodes()
        .filter { it.isLowerCase() }
        .filter { it != "start" && it != "end" }

    // Run a BFS for each node as an allowed double
    var paths = mutableSetOf<List<String>>()
    lowers.forEach { lowerAllowed ->
        paths.addAll( bfs(graph, "start", "end", lowerAllowed) )
    }

    //println("Part 2, all paths:")
    //println(paths.joinToString(separator = "\n"))
    println("Part 2: ${paths.size}")
}

fun main() {
    val graph = readInput("year2021/day12.input")?.let { parse(it) }

    measureTime {
        part1(graph)
    }

    measureTime {
        part2(graph)
    }

}