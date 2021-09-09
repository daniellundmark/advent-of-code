package year2019.computer.v09

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import java.math.BigInteger

sealed class Instruction {

    open suspend fun execute() {error("Not implemented")}

    data class Add(private val computer: IntCodeComputer, private val modes: List<ParameterMode>): Instruction() {
        override suspend fun execute() {
            val address = computer.ip
            val a = computer.memory.readParameter(address+1, modes[0], computer.relativeBaseOffset)
            val b = computer.memory.readParameter(address+2, modes[1], computer.relativeBaseOffset)
            val dst = computer.memory.readAddress(address+3, modes[2], computer.relativeBaseOffset)
            computer.memory[dst] = a+b
            computer.ip += 4
        }
    }

    data class Multiply(private val computer: IntCodeComputer, private val modes: List<ParameterMode>): Instruction() {
        override suspend fun execute() {
            val address = computer.ip
            val a = computer.memory.readParameter(address+1, modes[0], computer.relativeBaseOffset)
            val b = computer.memory.readParameter(address+2, modes[1], computer.relativeBaseOffset)
            val dst = computer.memory.readAddress(address+3, modes[2], computer.relativeBaseOffset)
            computer.memory[dst] = a*b
            computer.ip += 4
        }
    }

    data class Input(private val computer: IntCodeComputer, private val modes: List<ParameterMode>): Instruction() {
        override suspend fun execute() {
            val address = computer.ip
            val dst = computer.memory.readAddress(address+1, modes[0], computer.relativeBaseOffset)
            computer.memory[dst] = computer.input.receive()
            computer.ip += 2
        }
    }

    data class Output(private val computer: IntCodeComputer, private val modes: List<ParameterMode>): Instruction() {
        override suspend fun execute() {
            val address = computer.ip
            computer.output.send(computer.memory.readParameter(address +1, modes[0], computer.relativeBaseOffset))
            computer.ip += 2
        }
    }

    data class JumpIfTrue(private val computer: IntCodeComputer, private val modes: List<ParameterMode>): Instruction() {
        override suspend fun execute() {
            val (_, ip) = computer
            val a = computer.memory.readParameter(ip+1, modes[0], computer.relativeBaseOffset)
            val b = computer.memory.readParameter(ip+2, modes[1], computer.relativeBaseOffset)
            computer.ip = if(a != BigInteger.ZERO) b else ip + 3
        }
    }

    class JumpIfFalse(private val computer: IntCodeComputer, private val modes: List<ParameterMode>): Instruction() {
        override suspend fun execute() {
            val (memory, ip) = computer
            val a = computer.memory.readParameter(ip+1, modes[0], computer.relativeBaseOffset)
            val b = computer.memory.readParameter(ip+2, modes[1], computer.relativeBaseOffset)
            computer.ip = if(a == BigInteger.ZERO) b else ip + 3
        }
    }

    data class LessThan(private val computer: IntCodeComputer, private val modes: List<ParameterMode>): Instruction() {
        override suspend fun execute() {
            val (memory, ip) = computer
            val a = memory.readParameter(ip+1, modes[0], computer.relativeBaseOffset)
            val b = memory.readParameter(ip+2, modes[1], computer.relativeBaseOffset)
            val dst = memory.readAddress(ip+3, modes[2], computer.relativeBaseOffset)
            computer.memory[dst] = if(a < b) BigInteger.ONE else BigInteger.ZERO
            computer.ip += 4
        }
    }

    data class Equals(private val computer: IntCodeComputer, private val modes: List<ParameterMode>): Instruction() {
        override suspend fun execute() {
            val (memory, ip) = computer
            val a = memory.readParameter(ip+1, modes[0], computer.relativeBaseOffset)
            val b = memory.readParameter(ip+2, modes[1], computer.relativeBaseOffset)
            val dst = memory.readAddress(ip+3, modes[2], computer.relativeBaseOffset)
            computer.memory[dst] = if(a == b) BigInteger.ONE else BigInteger.ZERO
            computer.ip += 4
        }
    }

    data class AdjustRelativeBase(private val computer: IntCodeComputer, private val modes: List<ParameterMode>): Instruction() {
        override suspend fun execute() {
            val (memory, ip) = computer
            val a = memory.readParameter(ip+1, modes[0], computer.relativeBaseOffset)
            computer.relativeBaseOffset += a
            computer.ip += 2
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
            val modes = listOf(op/100%10, op/1000%10, op/10000%10).map {
                when(it) {
                    BigInteger.ZERO -> ParameterMode.POSITION
                    BigInteger.ONE -> ParameterMode.IMMEDIATE
                    BigInteger.valueOf(2) -> ParameterMode.RELATIVE
                    else -> error("Invalid parameter mode $it")
                }
            }
            return when(op%100) {
                BigInteger.valueOf(1) -> Add(computer, modes)
                BigInteger.valueOf(2) -> Multiply(computer, modes)
                BigInteger.valueOf( 3) -> Input(computer, modes)
                BigInteger.valueOf(4) -> Output(computer, modes)
                BigInteger.valueOf(5) -> JumpIfTrue(computer, modes)
                BigInteger.valueOf(6) -> JumpIfFalse(computer, modes)
                BigInteger.valueOf(7) -> LessThan(computer, modes)
                BigInteger.valueOf(8) -> Equals(computer, modes)
                BigInteger.valueOf(9) -> AdjustRelativeBase(computer, modes)
                BigInteger.valueOf(99) -> Terminate(computer, modes)
                else -> error("Invalid opcode: $op")
            }
        }
    }
}

typealias Memory = MutableMap<BigInteger, BigInteger>
fun Memory.read(address: BigInteger) = this[address] ?: BigInteger.ZERO
fun Memory.readParameter(address: BigInteger, mode: ParameterMode, baseOffset: BigInteger = BigInteger.ZERO): BigInteger {
    return when(mode) {
        ParameterMode.IMMEDIATE -> this.read(address)
        ParameterMode.POSITION -> this.read(this.read(address))
        ParameterMode.RELATIVE -> this.read(this.read(address)+baseOffset)
    }
}
fun Memory.readAddress(address: BigInteger, mode: ParameterMode, baseOffset: BigInteger = BigInteger.ZERO): BigInteger {
    return when(mode) {
        ParameterMode.IMMEDIATE -> error("Addresses should not be in immiediate mode")
        ParameterMode.POSITION -> this.read(address)
        ParameterMode.RELATIVE -> this.read(address)+baseOffset
    }
}

fun Memory.write(address: BigInteger, value: BigInteger) = this.put(address, value)
fun romFrom(string: String): ROM =
    string.split(",").map(String::toBigInteger).mapIndexed { index, value -> index.toBigInteger() to value }.toMap()

fun Memory.dump(): String = this.values.joinToString(",")

typealias ROM = Map<BigInteger, BigInteger>

enum class ParameterMode{ POSITION, IMMEDIATE, RELATIVE }


class IntCodeComputer {

    constructor(program: String, id: String = randomString(4)): this(romFrom(program))

    constructor(initalMemory: ROM, id: String = randomString(4)) {
        this.id = id
        this.memory = initalMemory.toMutableMap()
    }

    val id: String

    val memory: Memory
    var ip: BigInteger = BigInteger.ZERO // instruction pointer
    var relativeBaseOffset = BigInteger.ZERO
    var terminated = false

    var input: Channel<BigInteger> = Channel(Channel.UNLIMITED)
    var output: Channel<BigInteger> = Channel(Channel.UNLIMITED)

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

operator fun BigInteger.plus(value: Int): BigInteger = this.plus(value.toBigInteger())
operator fun BigInteger.plus(value: Long): BigInteger = this.plus(value.toBigInteger())

fun BigInteger.equals(value: Int) = this == value.toBigInteger()
fun BigInteger.equals(value: Long) = this == value.toBigInteger()

operator fun BigInteger.div(value: Int) = this / value.toBigInteger()
operator fun BigInteger.rem(value: Int) = this % value.toBigInteger()

suspend fun <E> ReceiveChannel<E>.receiveAvailable(): List<E> {
    val allMessages = mutableListOf<E>()
    allMessages.add(receive())
    var next = poll()
    while (next != null) {
        allMessages.add(next)
        next = poll()
    }
    return allMessages
}