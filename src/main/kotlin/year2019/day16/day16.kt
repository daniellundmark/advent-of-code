package year2019.day16

import util.Point
import util.cartesianProduct
import util.measureTime
import util.readInput
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.absoluteValue
import kotlin.math.sign
import kotlin.system.exitProcess

operator fun <T> List<T>.times(t: Int): List<T> {
    val result = ArrayList<T>(this.size * t)
    repeat(t) {result.addAll(this)}
    return result
}

fun pattern(index: Int): Sequence<Int> = sequence {
    val base = listOf(0, 1, 0, -1)
    val repetitions = index + 1
    val pattern = base.flatMap {b -> List<Int>(repetitions){b} }

    var i = 0
    while(true) {
        i = (i + 1) % pattern.size
        yield(pattern[i])
    }

}

fun phase(list: List<Int>): List<Int> {

    return list.mapIndexed { index, _ ->
        val pattern = pattern(index).take(list.size).toList()

        pattern.forEach { print("%3d".format(it)) }
        println()

        val products = list.zip(pattern){a, b -> a * b}
        val sum = products.sum().rem(10).absoluteValue
        //println("Products: $products, sum: $sum")
        sum
    }

}


fun phases(signalLength: Int): List<List<Int>> {
    println("Phases:")
    return (0 until signalLength).map { index ->
        //println("Generating phase $index")
        val pattern =  pattern(index).take(signalLength).toList().drop(index)

        print("%2d: ".format(index))
        repeat(index){ print("   ") }
        pattern.forEach { print("%3d".format(it)) }
        println()

        pattern
    }

}

fun patternForIndex(index: Int): List<Int> {
    val base = listOf(0, 1, 0, -1)
    val repetitions = index + 1

    return base.flatMap {b -> List<Int>(repetitions){b} }
}

fun phaseArray(index: Int, length: Int): ByteArray {
    val base = listOf(0, 1, 0, -1)
    val repetitions = index + 1

    val pattern = base.flatMap {b -> List<Int>(repetitions){b} }
    var i = 0
    var array = ByteArray(length)
    for(i in array.indices) {
        array[i] = pattern[(i+1)%pattern.size].toByte()
    }

    return array
}

fun phaseArrays(signalLength: Int): List<ByteArray> {
    return (0 until signalLength).map { index ->
        println("Generating phase $index")
        val phase =  phaseArray(index, signalLength)
        /*
        print("%2d: ".format(index))
        repeat(index){ print("   ") }
        pattern.forEach { print("%3d".format(it)) }
        println()
         */
        phase
    }
}

fun sparsePhaseArray(index: Int, length: Int): Map<Int, Byte> {
    val base = listOf(0, 1, 0, -1)
    val repetitions = index + 1

    val pattern = base.flatMap {b -> List<Int>(repetitions){b} }
    return (0 until length).mapNotNull {
        val ptn = pattern[(it+1)%pattern.size].toByte()
        if(ptn.toInt() != 0) {
            it to ptn
        } else {
            null
        }
    }.toMap()

}

fun sparsePhaseMatrix(signalLength: Int): Pair<List<Point>, List<Point>> {

    val positives = LinkedList<Point>()
    val negatives = LinkedList<Point>()

    (0 until signalLength).forEach { y ->
        println("Generating phase $y")

        val pattern = patternForIndex(y)

        (1 .. signalLength).forEach { x ->
            val ptn = pattern[(x+1)%pattern.size]
            if(ptn < 0) {
                negatives += Point(x, y)
            } else if (ptn > 0) {
                positives += Point(x, y)
            }
        }
        println("Sizes: ${positives.size}, ${negatives.size}")
    }

    return Pair(negatives, positives)
}

fun transform(signal: List<Int>, phases: List<List<Int>>): List<Int> {
    return phases.map { phase ->
        val offset = signal.size - phase.size
        val sum = phase.foldIndexed(0) {index, acc, it ->
            acc + when(it) {
                0 -> 0
                1 -> signal[offset+index]
                else -> -signal[offset+index]
            }
        }.rem(10).absoluteValue

        //val products = phase.zip(signal.subList(signal.size - phase.size, signal.size)){a,b->a*b}
        //val sum = products.sum().rem(10).absoluteValue
        //println("sum: $sum")
        sum
    }
}

val base = listOf(0, 1, 0, -1)
fun phase(x: Int, y: Int): Int {
    val repetitions = y+1
    // y+1 = repetitions of each base pattern
    // (x+1)/repetitions = which pattern to use
    return base[(x+1)/(y+1) % base.size]
   //return if(phase == 0) null else phase == 1
}

fun fromPhase(phase: Boolean?): Byte {
    return when(phase) {
        null -> 0
        false -> -1
        true -> 1
    }
}

fun transformInMemory(signal: IntArray, buf: IntArray) {

   // println("Copying to buffer")
    System.arraycopy(signal, 0, buf, 0, signal.size)

   // println("Calculating")
    for(y in signal.indices) {
        //println("Calculating step $y/${signal.size}")
        signal[y] = 0
        for(x in y until signal.size) {
            when(phase(x,y)) {
                -1 -> signal[y] -= buf[x]
                1 -> signal[y] += buf[x]
                else -> {}
            }
        }
        //println()
        signal[y] = signal[y].rem(10).absoluteValue
    }

}

fun printPhases(len: Int) {
    print("   ")
    repeat(len) { x -> print("%3d".format(x)) }
    println()
    for(y in 0 until len) {
        print("%2d:".format(y))
        repeat(len) { x ->
            print("%3d".format(phase(x, y)))
        }
        print(" :%-2d".format(y))
        println()
    }
    print("   ")
    repeat(len) { x -> print("%3d".format(x)) }
    println()
}

fun iterative(signal: IntArray) {
    val buf = IntArray(signal.size)
    repeat(10) {
        transformInMemory(signal, buf)
        if((it + 1) % 1 == 0){
            println("After ${it+1} phases: ${signal.joinToString("")}")
        }
    }
    println("First 8: ${signal.asSequence().take(8).joinToString("")}")
}

/*

A number in a phase is constructed by numbers with equal or higher indices in the previous phase

     0  1  2  3  4  5  6  7
 0:  1  0 -1  0  1  0 -1  0 :0
 1:  0  1  1  0  0 -1 -1  0 :1
 2:  0  0  1  1  1  0  0  0 :2
 3:  0  0  0  1  1  1  1  0 :3
 4:  0  0  0  0  1  1  1  1 :4
 5:  0  0  0  0  0  1  1  1 :5
 6:  0  0  0  0  0  0  1  1 :6
 7:  0  0  0  0  0  0  0  1 :7
     0  1  2  3  4  5  6  7
 */

val memo = mutableMapOf<Pair<Int, Int>, Int>()

fun number(signal: IntArray, n: Int, phase: Int): Int {

    val memoized = memo[Pair(n, phase)]
    if(memoized != null) {
        //println("memo: number($n, $phase)")
        return memoized
    }

    //println("calc: number($n, $phase)")

    var result: Int
    if(phase == 0) {
        result = signal[n]
    }
    /*
    //var sum = 0
    val parts = (n until signal.size)
    val phases = parts.map { phase(it, n) }
    val prevs = parts.map { number(signal, it, phase-1) }

    val products = phases.zip(prevs){a,b->a*b}
    val sum = products.sum().rem(10).absoluteValue
    println("Building ($n,$phase) using $parts from $phases <- $prevs as $sum")
    return sum
     */

    else {
        result = (n until signal.size).fold(0) { acc, i ->
            val phi = phase(i, n)
            if (phi == 0) {
                return@fold acc
            }
            val prev = memo[Pair(i, phase -1)] ?: number(signal, i, phase - 1)
            if (phi < 0) {
                return@fold acc - prev
            } else {
                return@fold acc + prev
            }
            //return@fold if(phi == 0) acc else acc + phi * number(signal, i, phase-1)
        }//.rem(10).absoluteValue
    }

    memo[Pair(n, phase)] = result

    return result

}

fun finalFormula(size: Int, n: Int, p: Int): Map<Int, Int> {
    if(p == 0) {
        return mapOf(n to 1)
    }

    var result = mutableMapOf<Int, Int>()
    val parts = (n until size)
    val phases = parts.map { phase(it, n) }
    val prevs = parts.map { finalFormula(size, it, p-1) }

    for(i in prevs.indices) {
        val prev = prevs[i]
        prev.forEach {
            val cur = result[it.key] ?: 0
            if(phases[i] < 0) {
                result[it.key] = cur - it.value
            } else if(phases[i] > 0) {
                result[it.key] = cur + it.value
            }

        }
    }

    return result

}

fun finalFormulaAsString(signal: IntArray, n: Int, p: Int): String {
    val formula = finalFormula(signal.size, n, p)

    val result = number(signal, n, p)

    val string = formula.keys.sorted().joinToString(" + ") { key ->
        val value = formula[key] ?: error("Expected value for key $key")
        val multiplier = if (value > 1) "${value}*" else ""
        "${multiplier}f(${key},0)"
    }

    return "$string = $result"

}

fun Map<Int, Int>.addValues(other: Map<Int, Int>): Map<Int, Int> {
    val result = this.toMutableMap()
    other.forEach { k, v ->
        val cur = result[k] ?: 0
        result[k] = cur + v
    }
    return result
}

// This assumes only the upper-triangular part of the phase matrix is used
// (Assumes that n >= size/2)
fun formula(size: Int, n: Int, p: Int): List<Pair<Int, Int>> {

    // In the initial phase, we use 1 times the input number
    if(p == 0) {
        return listOf(Pair(n, 0))
    }

    val parts = (n until size).map { nn ->
        Pair(nn, p-1)
    }

    return parts
}

fun main() {

    measureTime {

        val input = readInput("year2019/day16.input").first().map(Character::getNumericValue )

        val multiple = 1

        val signal = IntArray(input.size * multiple)
        repeat(input.size * multiple) {
            signal[it] = input[it%input.size]
        }
        println("Input initialized")

        printPhases(signal.size)

        // Figure out the index to start at
        val startIndex = signal.asSequence().take(7).toList().joinToString("").toInt()
        //println(startIndex)

/*
        val p = 4
        println("Formula for phase $p:")
        for (x in signal.size/2 until  signal.size) {
            println("f($x,$p) = ${finalFormulaAsString(signal, x, p)}")
        }

        val n = 7
        println("Formulas for n=$n")
        for(pp in p downTo 0) {
            println("f($n,$pp) = ${finalFormulaAsString(signal, n, pp)}")
        }

 */

        println("Final formula for 7,4")
        println(finalFormulaAsString(signal, 7, 4))

        println("Formula for 7,4")
        println(formula(signal.size, 7, 4))

        /*
        println("After phase 100:")
        for (x in startIndex..(startIndex+8)) {
            print(number(signal, x, 100))
        }
        println()

         */

        //println("Input signal: ${signal.joinToString("")}")


    }

}