package util

import kotlinx.coroutines.channels.ReceiveChannel
import java.io.File
import java.math.BigInteger
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.pow

fun readInput(fileName: String): List<String>
        = File("src/main/resources/$fileName").useLines { it.toList() }

fun <T> List<T>.rest() = this.drop(1)
fun String.rest() = this.drop(1)

fun List<Int>.multiply() = this.reduce{ a, b -> a*b }
fun List<Long>.multiply() = this.reduce{ a, b -> a*b }

fun <T> Iterable<T>.split(predicate: (T) -> Boolean): List<List<T>> =
        this.fold(mutableListOf(mutableListOf<T>())) { acc, e ->
            if (!predicate(e)) acc.last() += e
            if (predicate(e)) acc += mutableListOf<T>()
            acc
        }


fun Int.pow(exp: Int): Int = this.toDouble().pow(exp).toInt()

fun Int.odd(): Boolean = this.and(1) == 1
fun Int.even(): Boolean = this.and(1) == 0

fun <T> measureTime(loggingFunction: ((Duration) -> Unit)? = null, function: () -> T): T {

    val startTime = LocalDateTime.now()
    val result: T = function.invoke()
    val duration = Duration.between(startTime, LocalDateTime.now())
    if(loggingFunction == null) {
        println("Time taken: $duration")
    } else {
        loggingFunction.invoke(duration)
    }

    return result
}

infix fun Int.mod(m: Int): Int {
    val r = this % m
    return if (r < 0) r + m else r
}

infix fun Long.mod(m: Long): Long {
    val r = this % m
    return if (r < 0) r + m else r
}

fun <T> Collection<T>.peek(function: (T) -> Unit): Collection<T> {
    return this.map { function.invoke(it); it }
}

fun <T, S> Collection<T>.cartesianProduct(other: Iterable<S>): List<Pair<T, S>> {
    return cartesianProduct(other) { first, second -> first to second }
}

fun <T, S, V> Collection<T>.cartesianProduct(other: Iterable<S>, transformer: (first: T, second: S) -> V): List<V> {
    return this.flatMap { first -> other.map { second -> transformer.invoke(first, second) } }
}


fun <T : Any> cartProd(vararg items: Iterable<T>): Sequence<List<T>> = sequence {
    if (items.all { it.iterator().hasNext() }) {
        val itemsIter = items.map { it.iterator() }.filter { it.hasNext() }.toMutableList()
        val currElement: MutableList<T> = itemsIter.map { it.next() }.toMutableList()
        loop@ while (true) {
            yield(currElement.toList())
            for (pos in itemsIter.count() - 1 downTo 0) {
                if (!itemsIter[pos].hasNext()) {
                    if (pos == 0) break@loop
                    itemsIter[pos] = items[pos].iterator()
                    currElement[pos] = itemsIter[pos].next()
                } else {
                    currElement[pos] = itemsIter[pos].next()
                    break
                }
            }
        }
    }
}

fun <E> List<List<E>>.transpose(): List<List<E>> {
    if (isEmpty()) return this

    val width = first().size
    if (any { it.size != width }) {
        throw IllegalArgumentException("All nested lists must have the same size, but sizes were ${map { it.size }}")
    }

    return (0 until width).map { col ->
        (0 until size).map { row -> this[row][col] }
    }
}

operator fun BigInteger.rangeTo(other: BigInteger) =
        BigIntegerRange(this, other)

class BigIntegerRange(
        override val start: BigInteger,
        override val endInclusive: BigInteger
) : ClosedRange<BigInteger>, Iterable<BigInteger> {
    override operator fun iterator(): Iterator<BigInteger> =
            BigIntegerRangeIterator(this)
}

class BigIntegerRangeIterator(
        private val range: ClosedRange<BigInteger>
) : Iterator<BigInteger> {
    private var current = range.start

    override fun hasNext(): Boolean =
            current <= range.endInclusive

    override fun next(): BigInteger {
        if (!hasNext()) {
            throw NoSuchElementException()
        }
        return current++
    }
}



fun BigInteger.primeFactors(): List<Pair<BigInteger, Int>> {
    val factors = mutableListOf<Pair<BigInteger, Int>>()
    var remainder = this

    for(i in BigInteger.TWO .. this) {
        if(remainder % i == BigInteger.ZERO) {
            var count = 0
            while(remainder % i == BigInteger.ZERO) {
                count++
                remainder /= i
            }
            factors += Pair(i, count)
        }
        if(remainder == BigInteger.ONE) {
            //println("Prime factors of $this: $factors")
            return factors
        }
    }

    error("Should have reached down to one as remainder, ended up with $remainder")

}


data class Point(val x: Int, val y: Int) {
    override fun toString(): String {
        return "($x,$y)"
    }
}

fun min(a: Int, b: Int) = if(a < b) a else b
fun max(a: Int, b: Int) = if(a > b) a else b


var showDebug = false
fun debug(msg: Any) {
    if (showDebug) {
        println(msg)
    }
}