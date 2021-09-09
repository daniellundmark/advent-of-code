package year2019.computer.v07

import kotlinx.coroutines.channels.Channel

sealed class Instruction {

    open suspend fun execute() {error("Not implemented")}

    data class Add(private val computer: IntCodeComputer, private val modes: List<ParameterMode>): Instruction() {
        override suspend fun execute() {
            val address = computer.ip
            val a = computer.memory.parameter(address+1, modes[0])
            val b = computer.memory.parameter(address+2, modes[1])
            val dst = computer.memory.parameter(address+3)
            computer.memory[dst] = a+b
            computer.ip += 4
        }
    }

    data class Multiply(private val computer: IntCodeComputer, private val modes: List<ParameterMode>): Instruction() {
        override suspend fun execute() {
            val address = computer.ip
            val a = computer.memory.parameter(address+1, modes[0])
            val b = computer.memory.parameter(address+2, modes[1])
            val dst = computer.memory.parameter(address+3)
            computer.memory[dst] = a*b
            computer.ip += 4
        }
    }

    data class Input(private val computer: IntCodeComputer, private val modes: List<ParameterMode>): Instruction() {
        override suspend fun execute() {
            val address = computer.ip
            val dst = computer.memory.parameter(address+1)
            computer.memory[dst] = computer.input.receive()
            computer.ip += 2
        }
    }

    data class Output(private val computer: IntCodeComputer, private val modes: List<ParameterMode>): Instruction() {
        override suspend fun execute() {
            val address = computer.ip
            computer.output.send(computer.memory.parameter(address +1, modes[0]))
            computer.ip += 2
        }
    }

    data class JumpIfTrue(private val computer: IntCodeComputer, private val modes: List<ParameterMode>): Instruction() {
        override suspend fun execute() {
            val (memory, ip) = computer
            val a = computer.memory.parameter(ip+1, modes[0])
            val b = computer.memory.parameter(ip+2, modes[1])
            computer.ip = if(a != 0) b else ip + 3
        }
    }

    class JumpIfFalse(private val computer: IntCodeComputer, private val modes: List<ParameterMode>): Instruction() {
        override suspend fun execute() {
            val (memory, ip) = computer
            val a = computer.memory.parameter(ip+1, modes[0])
            val b = computer.memory.parameter(ip+2, modes[1])
            computer.ip = if(a == 0) b else ip + 3
        }
    }

    data class LessThan(private val computer: IntCodeComputer, private val modes: List<ParameterMode>): Instruction() {
        override suspend fun execute() {
            val (memory, ip) = computer
            val a = memory.parameter(ip+1, modes[0])
            val b = memory.parameter(ip+2, modes[1])
            val dst = memory.parameter(ip+3)
            computer.memory[dst] = if(a < b) 1 else 0
            computer.ip += 4
        }
    }

    data class Equals(private val computer: IntCodeComputer, private val modes: List<ParameterMode>): Instruction() {
        override suspend fun execute() {
            val (memory, ip) = computer
            val a = memory.parameter(ip+1, modes[0])
            val b = memory.parameter(ip+2, modes[1])
            val dst = memory.parameter(ip+3)
            computer.memory[dst] = if(a == b) 1 else 0
            computer.ip += 4
        }
    }

    data class Terminate(private val computer: IntCodeComputer, private val modes: List<ParameterMode>): Instruction() {
        override suspend fun execute(){
            computer.terminated = true
        }
    }

    companion object {
        fun parse(computer: IntCodeComputer): Instruction {
            val address = computer.ip
            val op = computer.memory.read(address)
            val modes = listOf(op/100%10, op/1000%10, op/10000%10).map { if(it==0) ParameterMode.POSITION else ParameterMode.IMMEDIATE }
            return when(op%100) {
                1 -> Add(computer, modes)
                2 -> Multiply(computer, modes)
                3 -> Input(computer, modes)
                4 -> Output(computer, modes)
                5 -> JumpIfTrue(computer, modes)
                6 -> JumpIfFalse(computer, modes)
                7 -> LessThan(computer, modes)
                8 -> Equals(computer, modes)
                99 -> Terminate(computer, modes)
                else -> error("Invalid opcode: $op")
            }
        }
    }
}

typealias Memory = MutableMap<Int, Int>
fun Memory.read(address: Int) = this[address] ?: error("Failed to read memory at $address")
fun Memory.parameter(address: Int, mode: ParameterMode): Int {
    return when(mode) {
        ParameterMode.IMMEDIATE -> this.read(address)
        ParameterMode.POSITION -> this.read(this.read(address))
    }
}
fun Memory.parameter(address: Int) = this.parameter(address, ParameterMode.IMMEDIATE)
fun Memory.write(address: Int, value: Int) = this.put(address, value)
fun romFrom(string: String): ROM =
    string.split(",").map(String::toInt).mapIndexed { index, value -> index to value }.toMap()

fun Memory.dump(): String = this.values.joinToString(",")

typealias ROM = Map<Int, Int>

enum class ParameterMode{ POSITION, IMMEDIATE }


class IntCodeComputer {

    constructor(program: String, id: String = randomString(4)): this(romFrom(program))

    constructor(initalMemory: ROM, id: String = randomString(4)) {
        this.id = id
        this.memory = initalMemory.toMutableMap()
    }

    val id: String

    val memory: Memory
    var ip: Int = 0 // instruction pointer
    var terminated = false

    var input: Channel<Int> = Channel(Channel.UNLIMITED)
    var output: Channel<Int> = Channel(Channel.UNLIMITED)

    suspend fun execute() {

        // Process instructions until reading a Terminate
        //val instruction = Instruction.parse(memory, ip)
        while(!terminated) {
            val instruction = Instruction.parse(this)
            //println("$ip ($id): Executing $instruction")
            instruction.execute()
        }

    }

    operator fun component1() = this.memory
    operator fun component2() = this.ip
    operator fun component3() = this.input
    operator fun component4() = this.output
    operator fun component5() = this.terminated
}


private val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
fun randomString(length: Int) = (1..length)
    .map { i -> kotlin.random.Random.nextInt(0, charPool.size) }
    .map(charPool::get)
    .joinToString("")