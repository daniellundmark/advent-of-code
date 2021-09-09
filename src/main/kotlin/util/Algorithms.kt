package util

import java.math.BigInteger
import java.util.*
import kotlin.collections.ArrayDeque

fun <T> MutableList<T>.push(item: T) = this.add(this.count(), item)
fun <T> MutableList<T>.pop(): T = if(this.count() > 0) this.removeAt(this.count() - 1) else error("Empty stack")
fun <T> MutableList<T>.peek(): T = if(this.count() > 0) this[this.count() - 1] else error("Empty stack")
fun <T> MutableList<T>.hasMore() = this.count() > 0
typealias Stack<T> = MutableList<T>

class Graph<T> {

    private val adjacencyMap = mutableMapOf<T, MutableSet<T>>()

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

    // perform a breadth first search, record the distance to each node
    fun bfs(start:T, end: T): Map<T, Int>? {
        // Avoid visiting nodes multiple times
        val visitedNodes = mutableSetOf<T>()

        // Create a queue for BFS
        val queue: Queue<T> = LinkedList<T>()

        // Keep track of the "distance" to each visited node
        val distance = mutableMapOf<T, Int>()

        // Initial step -> add the startNode to the queue.
        queue.add(start)
        distance[start] = 0

        while(queue.isNotEmpty()) {
            val currentNode = queue.remove() ?: error("Queue should not be empty here")

            if(visitedNodes.contains(currentNode)) {
                continue
            }

            // If we found the target, return the path to it
            if(currentNode == end) {
                return distance
            }

            visitedNodes.add(currentNode)
            this.adjacencyMap[currentNode]?.forEach(queue::add)
            adjacencyMap[currentNode]?.forEach{
                if(distance[it] == null) distance[it] = distance[currentNode]!! + 1
            }
        }

        // We reached the end of all reachable paths without finding the target
        return null
    }

    // Perform a depth-first search, find the path to the end
    fun dfs(start:T, end: T): List<T>? {
        // Avoid visiting nodes multiple times
        val visitedNodes = mutableSetOf<T>()

        // Create a stack for DFS
        val stack: Stack<T> = mutableListOf()

        // Initial step -> add the startNode to the stack.
        stack.push(start)

        // Store the sequence in which nodes are visited, for return value.
        val traversalList = mutableListOf<T>()

        while(stack.isNotEmpty()) {
            val currentNode = stack.pop() ?: error("Stack should not be empty here")

            if(visitedNodes.contains(currentNode)) {
                continue
            }

            traversalList.push(currentNode)

            // If we found the target, return the path to it
            if(currentNode == end) {
                return traversalList
            }

            visitedNodes.add(currentNode)
            this.adjacencyMap[currentNode]?.forEach(stack::push)
        }

        // We reached the end of all reachable paths without finding the target
        return null
    }

}


// Find the multiplicate inverse of a modulo b, so the number so that a*inv = 1 modulo b
fun multInv(a: BigInteger, b: BigInteger): BigInteger {
    if (b == BigInteger.ONE) return BigInteger.ONE
    var aa = a
    var bb = b
    var x0 = BigInteger.ZERO
    var x1 = BigInteger.ONE
    while (aa > BigInteger.ONE) {
        val q = aa / bb
        var t = bb
        bb = aa % bb
        aa = t
        t = x0
        x0 = x1 - q * x0
        x1 = t
    }
    if (x1 < BigInteger.ZERO) x1 += b
    return x1
}

fun chineseRemainder(n: List<BigInteger>, a: List<BigInteger>): BigInteger {
    val prod = n.fold(BigInteger.ONE) { acc, i -> acc * i }
    var sum = BigInteger.ZERO
    for (i in n.indices) {
        val p = prod / n[i]
        sum += a[i] * multInv(p, n[i]) * p
    }
    return sum % prod
}

data class ModPair<T>(val modulo: T, val remainder: T)

fun sieve(pairs: List<ModPair<BigInteger>>): BigInteger {

    // Try to find a number which has the right remainder in each modulo
    // Increase step size each time we get a match

    var found = 0
    var current = BigInteger.ZERO
    var stepSize = BigInteger.ONE

    var iterations = BigInteger.ZERO

    while(found < pairs.size) {
        iterations++

        //println("Examining $current")

        // See how many match for this number
        for(i in found until pairs.size) {
            if (current.remainder(pairs[i].modulo) == pairs[i].remainder) {
                println("Found match $found: $current ${pairs[i]}")
                found++
                stepSize *= pairs[i].modulo
            } else {
                current += stepSize
                break
            }
        }

    }

    println("Iterations: $iterations")

    return current

}

fun <T> permute(input: List<T>): List<List<T>> {
    if (input.size == 1) return listOf(input)
    val perms = mutableListOf<List<T>>()
    val toInsert = input[0]
    for (perm in permute(input.drop(1))) {
        for (i in 0..perm.size) {
            val newPerm = perm.toMutableList()
            newPerm.add(i, toInsert)
            perms.add(newPerm)
        }
    }
    return perms
}

fun greatestCommonDivisor(a: Long, b: Long): Long {

    var aa = a
    var bb = b

    while (bb > 0) {
        val tmp = bb
        bb = aa % bb
        aa = tmp
    }
    return aa
}

fun leastCommonMultiple(a: Long, b: Long): Long {
    return a * (b / greatestCommonDivisor(a, b))
}

fun leastCommonMultiple(values: List<Long>): Long {
    var result = values[0]
    for(i in 1 until values.size) {
        result = leastCommonMultiple(result, values[i])
    }
    return result
}