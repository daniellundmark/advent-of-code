package year2019.day02

import util.readInput

enum class OpCode(code: Int) {
    ADD(1), MULTIPLY(2), TERMINATE(99);

    companion object {
        fun fromCode(code: Int) = when(code) {
            1 -> ADD
            2 -> MULTIPLY
            99 -> TERMINATE
            else -> error("Error parsing op code $code")
        }
    }

}

typealias Memory = MutableMap<Int, Int>
typealias ROM = Map<Int, Int>

fun op(code: Int): OpCode {
    return OpCode.fromCode(code) ?: error("Parse error for op code $code")
}

fun Memory.read(key: Int): Int {
    return this[key] ?: error("Error reading memory at $key")
}

private fun Memory.process(op: OpCode, address: Int): Memory {
    return when(op) {
        OpCode.TERMINATE -> this
        else -> {
            val a = this.read(address+1)
            val b = this.read(address+2)
            val dst = this.read(address+3)
            when(op) {
                OpCode.ADD -> this[dst] = this.read(a)+this.read(b)
                OpCode.MULTIPLY -> this[dst] = this.read(a)*this.read(b)
            }
            this
        }
    }
}

fun Memory.handle(address: Int): Int? {
    val code = this[address] ?: error("Failed to read op code from $address")
    val op = op(code)
    if(op == OpCode.TERMINATE) return null
    this.process(op, address)
    return address+4
}

fun execute(mem: Memory): Memory {
    var address: Int? = 0
    while(address != null) {
        address = mem.handle(address)
    }
    return mem
}

fun executeNounVerb(mem: Memory, noun: Int, verb: Int): Int {
    mem[1] = noun
    mem[2] = verb
    execute(mem)
    return mem[0]!!
}

fun findNounAndVerb(initial: ROM, target: Int): Pair<Int, Int> {
    for(i in 0..99) {
        for(j in 0..99) {
            val result = executeNounVerb(initial.toMutableMap(), i, j)
            if(result == target) {
                return Pair(i, j)
            }
        }
    }
    error("Failed to find pair for target $target")
}

fun main() {
    val numbers = readInput("year2019/day02.input").first().split(",").map(String::toInt)
    val initial = numbers.mapIndexed { index, value -> index to value }.toMap()


    val pair = findNounAndVerb(initial, 19690720)

    println(100*pair.first + pair.second  )

}