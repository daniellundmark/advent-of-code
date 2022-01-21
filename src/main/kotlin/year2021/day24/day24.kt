package year2021.day24

import util.readInput
import java.util.*

enum class InstType {inp, add, mul, div, mod, eql}
data class Instruction(val type: InstType, val lhs: Char, val rhs: Char?, val number: Int?) {
    companion object {
        private val re = Regex("(\\w+) (\\w) ?([-\\w]+)?( *//.*)?")
        fun parse(line: String): Instruction {
            return re.matchEntire(line)?.destructured?.let { (type, lhs, rhs) ->
                val number = rhs.toIntOrNull()
                Instruction(
                    type = InstType.valueOf(type),
                    lhs.first(),
                    rhs = if(number == null && rhs.isNotEmpty()) rhs.first() else null,
                    number
                )
            }?: error("Failed to parse $line")
        }
    }

    override fun toString() = "($type $lhs ${rhs ?: number ?: ""})"
}

class ALU() {

    constructor(input: List<Int>) : this() {
        this.input.addAll(input)
    }

    constructor(input: String): this(input.map { it.digitToInt() })

    private var registers = IntArray(4) {0}
    fun w() = registers[0]
    fun x() = registers[1]
    fun y() = registers[2]
    fun z() = registers[3]
    private operator fun IntArray.get(c: Char) = registers[c.code - 'w'.code]
    private operator fun IntArray.set(c: Char, number: Int) = registers.set(c.code - 'w'.code, number)

    fun rhs(registers: IntArray, instruction: Instruction) =
        instruction.number ?: instruction.rhs?.let { registers.get(instruction.rhs) }

    val input: Queue<Int> = LinkedList()

    override fun toString() = "(w=${w()}, x=${x()}, y=${y()}, z=${z()})"

    fun process(instruction: Instruction) {

        val before = this.toString()

        val lhs = registers[instruction.lhs]
        val rhs = rhs(registers, instruction) ?: input.remove()
        registers[instruction.lhs] = when(instruction.type) {
            InstType.inp -> rhs
            InstType.add -> lhs + rhs
            InstType.mul -> lhs * rhs
            InstType.div -> lhs / rhs
            InstType.mod -> lhs % rhs
            InstType.eql -> if(lhs == rhs) 1 else 0
        }

        val after = this.toString()

        println("$before ->\t$instruction ->\t$after")
    }

}

fun main() {
    val instructions = readInput("year2021/small.input").map(Instruction::parse)
    println(instructions)
    val input = "12345678912345"
    val alu = ALU(input)
    println(alu)
    instructions.forEach { alu.process(it) }
    println(alu)
}