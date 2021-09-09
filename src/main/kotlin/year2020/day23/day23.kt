package year2020.day23

import util.measureTime
import util.rest

// Circular list
class Cup {
    var value: Int
        private set
    var next: Cup
        private set

    // Initialize a new circular list of size 1
    constructor(value: Int) {
        this.value = value
        this.next = this
        Cup.cups[value] = this
    }

    private constructor(value: Int, next: Cup) {
        this.value = value
        this.next = next
        Cup.cups[value] = this
    }

    fun push(cw: Int): Cup {
        val next = this.next
        this.next = Cup(cw, next)
        return this.next
    }

    fun pushValues(values: List<Int>): List<Cup> {
        return values.reversed().map { this.push(it) }
    }

    fun push(cw: Cup): Cup {
        val next = this.next
        this.next = cw
        cw.next = next
        return this.next
    }

    fun pushCups(values: List<Cup>): List<Cup> {
        return values.reversed().map { this.push(it) }
    }

    fun pop(): Cup {

        val removed = this.next
        this.next = removed.next

        return removed
    }

    fun pop(num: Int): List<Cup> {
        return (1..num).map { this.pop() }
    }

    fun find(num: Int): Cup {
        var tmp = this
        while(tmp.value != num) tmp = tmp.next
        return tmp
    }

    override fun toString(): String {
        val maxDepth: Int = 100

        val thisNode = this
        var tmp = this
        var iterations = 0

        val sb = StringBuilder("[")
        sb.append("(${tmp.value})")
        tmp = tmp.next

        while (tmp != thisNode && iterations++ < maxDepth) {
            sb.append(",${tmp.value}")
            tmp = tmp.next
        }

        sb.append("]")
        return sb.toString()
    }

    companion object {
        lateinit var cups: Array<Cup?>
        fun cup(value: Int) = cups[value] ?: error("Expected cup $value to exist")
    }

}

fun circularLinkedListWithLookupTable() {
    measureTime({println("Time taken using circular linked list: $it")}) {
        val input =
                "496138527".toList().map(Character::getNumericValue) + (10..1_000_000)

        // Need the max value later
        val max = input.maxOrNull() ?: error("Expects a max value here")

        // Holds all cups for fast direct access by "value"
        Cup.cups = arrayOfNulls<Cup>(max + 1)

        // Build a circular list of the cups
        Cup.cups[input.first()] = Cup(input.first())
        var cur = Cup.cup(input.first())

        // Build the rest of the list
        cur.pushValues(input.rest())

        //println(cur)

        for (i in 1..10_000_000) {

            //println("-- move $i --")
            //println("cups: $cur")

            // Pick up three cups
            val pickup = cur.pop(3)

            var target = cur.value - 1
            while (pickup.map { it.value }.contains(target) || target < 1) {
                target--; if (target < 1) target = max
            }
            val dst = Cup.cup(target)

            //println("pick up: ${pickup.map { it.value }}")
            //println("destination: ${dst.value}")

            dst.pushCups(pickup)

            cur = cur.next

        }

        //println("-- final --")
        //println("cups: $cur")

        val one = Cup.cup(1)
        //println(one)

        val (a, b) = one.pop(2)

        println("${a.value} ${b.value} ${a.value.toLong() * b.value.toLong()}")

        /*
        var tmp = one
        var res = ""
        while (tmp.next != one) {
            res += tmp.next.value
            tmp = tmp.next
        }
        println(res)
        */

    }
}

fun Array<Int?>.toString(cur: Int): String {
    val sb = StringBuilder("[")
    sb.append("($cur)")
    var c = this[cur]!!
    while (c != cur) {
        sb.append(" $c")
        c = this[c]!!
    }
    sb.append("]")
    return sb.toString()
}

fun arrayBased() {

    measureTime({println("Time taken using array: $it")}) {

        val input = "496138527".toList().map(Character::getNumericValue) + (10..1_000_000)
        val cups = arrayOfNulls<Int>(input.size + 1)
        val max = input.maxOrNull() ?: error("Failed to find max value")

        // Initialize the array with the "pointer" to the next value
        for (i in input.indices) {
            cups[input[i]] = input[(i + 1) % input.size]
        }

        var cur = input.first()
        for (i in 1..10_000_000) {

            val a = cups[cur]!!
            val b = cups[a]!!
            val c = cups[b]!!

            // Point cur to the next cup in the circle
            cups[cur] = cups[c]!!

            // Find where to insert the cups
            var dst = cur - 1
            while (a == dst || b == dst || c == dst || dst < 1) {
                dst--
                if (dst < 1) dst = max
            }

            // Insert them there
            cups[c] = cups[dst]!!
            cups[dst] = a

            // Move on to the next
            cur = cups[cur]!!

        }

        val a = cups[1]!!
        val b = cups[a]!!

        println("$a $b ${a.toLong() * b.toLong()}")

    }

}

fun main() {
    arrayBased()
    circularLinkedListWithLookupTable()
}