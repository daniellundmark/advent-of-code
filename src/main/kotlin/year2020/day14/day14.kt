package year2020.day14

import util.cartesianProduct
import util.readInput
import util.rest

fun masks(input: String): Pair<Long, Long> {
    var zeroMask = 0L
    var oneMask = 0L

    for(c in input) {
        zeroMask = zeroMask.shl(1)
        oneMask = oneMask.shl(1)
        when(c) {
            '0' -> zeroMask = zeroMask.or(1L)
            '1' -> oneMask = oneMask.or(1L)
        }
    }

    return Pair(zeroMask, oneMask)
}

fun withMasks(value: Long, zeroMask: Long, oneMask: Long) = value.and(zeroMask.inv()).or(oneMask)

fun or(a: Char, b: Char) = if(a == '1' || b == '1') '1' else '0'

// Generate permutations of address + mask with each X replaced with 0/1
tailrec fun expandAddress(address: String, mask: String): List<String> {
    if(address.isEmpty()) {
        return listOf("")
    }

    val rest = expandAddress(address.rest(), mask.rest())
    val prefix = if(mask.first() == 'X') listOf('0', '1') else listOf(or(mask.first(), address.first()))

    return prefix.cartesianProduct(rest) { a, b -> a + b }
}

fun part1() {

    val lines = readInput("year2020/day14.input")

    val regex = "(mask|mem)(\\[(\\d+)])? = (\\w+)".toRegex()
    val memory = mutableMapOf<String, Long>()
    var masks = Pair(0L, 0L)

    lines.forEach{ line ->
        regex.matchEntire(line)?.destructured?.let { (op, _, addr, value) ->
            when(op) {
                "mask" -> masks = masks(value)
                "mem" -> memory[addr] = withMasks(value.toLong(), masks.first, masks.second)
                else -> error("Invalid line: $line")
            }
        }
    }

    println(memory.values.sum())

}

fun part2() {
    val lines = readInput("year2020/day14.input")

    val regex = "(mask|mem)(\\[(\\d+)])? = (\\w+)".toRegex()
    val memory = mutableMapOf<Long, Long>()
    var mask = "000000000000000000000000000000000000"

    lines.forEach{ line ->
        regex.matchEntire(line)?.destructured?.let { (op, _, addr, value) ->
            when(op) {
                "mask" -> mask = value
                "mem" -> {
                    // Generate all combinations of address+mask
                    val addresses = expandAddress(addr.toLong().toString(2).padStart(36, '0'), mask)

                    // For each 0/1 mask find the resulting address and write value to it
                    addresses.forEach{address ->
                        //println("Writing $address <- $value (decimal ${address.toLong(2)})")
                        memory[address.toLong(2)] = value.toLong()
                    }
                }
                else -> error("Invalid line: $line")
            }
        }
    }

    //println(memory)

    println(memory.values.sum())

}

fun main() {
    part2()
}