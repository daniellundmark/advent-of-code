package year2020.day8

import util.readInput

data class Op(val lineNum: Int, val op: String, val value: Int)
data class Result(val terminated: Boolean, val lastAcc: Int)

val regex =  "(\\w+) ([+-]\\d+)".toRegex()
fun parse(lineNum: Int, line: String): Op {
    val (op, value) = regex.matchEntire(line)!!.destructured
    return Op(lineNum, op, value.toInt())
}

// tries executing the program, see if it terminates
fun execute(program: List<Op>): Result {
    var acc = 0
    var cur = 0

    val visited = mutableSetOf<Int>()

    while(cur < program.size) {
        val op = program[cur]

       // println("(cur:$cur,acc:$acc),: $year2019.day02.op")

        // This is the naive solution
        if(visited.contains(cur)) {
            //println("Already visited!")
            return Result(false, acc)
        } else {
            visited += cur
        }

        when(op.op) {
            "nop" -> { cur++ }
            "acc" -> { acc += op.value; cur++ }
            "jmp" -> { cur += op.value }
            else -> error("Bad year2019.day02.op: $op")
        }
    }
    return Result(true, acc)
}

fun tryToModify(program: List<Op>, fromOp: String, toOp: String): Result? {

    for (curLine in program.indices) {
        println("Trying to change $fromOp to $toOp on line $curLine")

        var attempt = program.toMutableList()

        if(program[curLine].op == fromOp) {
            val op = program[curLine]
            attempt[curLine] = Op(lineNum = op.lineNum, op = toOp, value = op.value)
            val result = execute(attempt)
            if(result.terminated) {
                return result
            }
        }
    }

    return null
}

fun main() {
    var program = readInput("year2020/day8.input").mapIndexed{ lineNum, line -> parse(lineNum, line) }
    program.forEach(::println)

    // Try changing each jmp to nop, see if the program terminates
    val jmpToNop = tryToModify(program, "jmp", "nop")
    val nopToJmp = tryToModify(program, "nop", "jmp")

    println("jmpToNop:$jmpToNop, nopToJmp:$nopToJmp")

}